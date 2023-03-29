package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class ElasticsearchWriter implements Serializable {

    private final IElasticsearchSinkAgent agent;
    private final ElasticsearchProperties properties;

    public ElasticsearchWriter(IElasticsearchSinkAgent agent) {
        this.agent = agent;
        this.properties = agent.getElasticsearchProperties();
    }

    public void execute(ElasticsearchRequestModel requestModel) {
        // Elasticsearch 連線資訊
        // TODO 需要支援多台
        String host = properties.getHost();
        String user = properties.getUser();
        String pass = properties.getPassword();
        String method = properties.getMethod();

        Request request = new Request(method, requestModel.getEndpoint());
        request.setJsonEntity(requestModel.getJsonEntity());

        RestClientBuilder builder = creteRestClientBuilder(host, user, pass);
        RestClient restClient = builder.build();
        try {
            Response response = restClient.performRequest(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (!isSuccess(statusCode)) {
                // TODO 補充處理說明
                throwFailure(statusCode, requestModel);
            }
        } catch (IOException e) {
            // TODO 補充處理說明
            throwFailure(e, requestModel);
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                throwFailure(e);
            }
        }
    }

    @SneakyThrows
    private void throwFailure(int statusCode, ElasticsearchRequestModel requestModel) {
        Failure f = new ElasticsearchWriterFailure(statusCode);
        FailureHelper.setValue(f.getContent(), requestModel.getJsonEntity());
        throw f;
    }

    @SneakyThrows
    private void throwFailure(Exception e, ElasticsearchRequestModel requestModel) {
        Failure f = new ElasticsearchWriterFailure(e);
        FailureHelper.setValue(f.getContent(), requestModel.getJsonEntity());
        throw f;
    }

    protected void throwFailure(Exception e) {
        agent.throwFailure(e);
    }

    private boolean isSuccess(int statusCode) {
        return 200 <= statusCode && statusCode < 300;
    }

    public RestClientBuilder creteRestClientBuilder(String host, String user, String pass) {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));

        String[] hostNamesPort = host.split(",");
        List<HttpHost> httpHosts = new ArrayList<>();
        if (0 != hostNamesPort.length) {
            for (String hostPort : hostNamesPort) {
                URI requestURI = URI.create(hostPort);
                httpHosts.add(new HttpHost(requestURI.getHost(), requestURI.getPort(), requestURI.getScheme()));
            }
        }

        return RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()]))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider);
                });
    }

}
