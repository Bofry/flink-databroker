package com.bofry.databroker.core.component;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import lombok.SneakyThrows;
import org.apache.flink.cassandra.shaded.com.google.common.util.concurrent.FutureCallback;
import org.apache.flink.cassandra.shaded.com.google.common.util.concurrent.Futures;
import org.apache.flink.cassandra.shaded.com.google.common.util.concurrent.ListenableFuture;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class CassandraWriter {

    public static final int DEFAULT_MAX_CONCURRENT_REQUESTS = Integer.MAX_VALUE;
    public static final Duration DEFAULT_MAX_CONCURRENT_REQUESTS_TIMEOUT = Duration.ofMillis(Long.MAX_VALUE);
    private final int maxConcurrentRequests;
    private final Duration maxConcurrentRequestsTimeout;
    protected transient Cluster cluster;
    protected transient Session session;
    private AtomicReference<Throwable> throwable;
    private FutureCallback<ResultSet> callback;
    private Semaphore semaphore;
    private Map<String, Object> props;

    @SneakyThrows
    public CassandraWriter(Map<String, Object> props) {
        maxConcurrentRequests = DEFAULT_MAX_CONCURRENT_REQUESTS;
        maxConcurrentRequestsTimeout = DEFAULT_MAX_CONCURRENT_REQUESTS_TIMEOUT;
        this.props = props;
        this.callback = new FutureCallback<ResultSet>() {

            public void onSuccess(ResultSet ignored) {
                CassandraWriter.this.semaphore.release();
            }

            @SneakyThrows
            public void onFailure(Throwable t) {
                System.out.println(CassandraWriter.this.semaphore.availablePermits());
                CassandraWriter.this.throwable.compareAndSet(null, t);
                CassandraWriter.this.semaphore.release();
                CassandraWriter.this.throwFailure(t);
            }
        };
        ClusterBuilder builder = new ClusterBuilder() {
            protected Cluster buildCluster(com.datastax.driver.core.Cluster.Builder builder) {
                String host = CassandraWriter.this.props.get("host").toString();
                String[] hosts = host.split(",");
                Integer port = Integer.valueOf(CassandraWriter.this.props.get("port").toString());
                return builder.addContactPoints(hosts).withPort(port).build();
            }
        };
        try {
            this.cluster = builder.getCluster();
            this.session = this.createSession();
            this.throwable = new AtomicReference();
            this.semaphore = new Semaphore(maxConcurrentRequests);
        } catch (Exception e) {
            this.throwFailure(e);
        }
    }

    public void write(Statement stmt) throws TimeoutException, InterruptedException, Failure {
        this.checkAsyncErrors();
        this.tryAcquire(1);

        ListenableFuture result = null;
        try {
            result = this.session.executeAsync(stmt);
        } catch (Throwable t) {
            this.semaphore.release();
            this.throwFailure(t);
        }
        Futures.addCallback(result, this.callback);
    }

    protected Session createSession() {
        return this.cluster.connect();
    }

    public void close() throws Exception {
        try {
            this.checkAsyncErrors();
            this.flush();
            this.checkAsyncErrors();
        } finally {
            try {
                if (this.session != null) {
                    this.session.close();
                }
            } catch (Exception e) {
                this.throwFailure(e);
            }

            try {
                if (this.cluster != null) {
                    this.cluster.close();
                }
            } catch (Exception e) {
                this.throwFailure(e);
            }
        }
    }

    private void checkAsyncErrors() throws Failure {
        Throwable currentError = this.throwable.getAndSet(null);
        if (currentError != null) {
            this.throwFailure(currentError);
        }
    }

    private void flush() throws InterruptedException, TimeoutException {
        this.tryAcquire(this.maxConcurrentRequests);
        this.semaphore.release(this.maxConcurrentRequests);
    }

    private void tryAcquire(int permits) throws InterruptedException, TimeoutException {
        if (!this.semaphore.tryAcquire(permits, this.maxConcurrentRequestsTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
            throw new TimeoutException(String.format("Failed to acquire %d out of %d permits to send value in %s.", permits, this.maxConcurrentRequests, this.maxConcurrentRequestsTimeout));
        }
    }

    public Session getSession() {
        return this.session;
    }

    private void throwFailure(Throwable t) throws Failure {
        Failure f = new CassandraWriterFailure((Exception) t);
        throw f;
    }
}
