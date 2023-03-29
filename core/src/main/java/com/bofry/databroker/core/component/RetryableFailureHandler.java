package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RetryableFailureHandler implements IFailureHandler, IAgent {

    private final IFailureDocumentWriter writer;
    private final WriterConfiguration conf;

    public RetryableFailureHandler(WriterConfiguration conf) {
        if (conf == null) {
            throw new IllegalArgumentException();
        }
        this.conf = conf;
        IWriterBuilder writerBuilder = FailureFactory.create(conf.getType());
        IDocumentWriter documentWriter = writerBuilder.buildWriter(this);
        this.writer = (IFailureDocumentWriter) documentWriter;
    }

    @Override
    public boolean process(Exception e) {
        // log.info("RetryableFailureHandler: ", e);
        if (e instanceof Failure) {
            Failure f = (Failure) e;
            if (f.isRetryable()) {
                FailureContext ctx = FailureContext.builder()
                        .action(FailureActionEnum.RETRY)
                        .content(f.getContent())
                        .build();
                this.writer.write(ctx);
                return true;
            }
        }
        return false;
    }

    @Override
    public WriterConfiguration getConfiguration() {
        return this.conf;
    }

    @Override
    public void throwFailure(Exception e) {
        FailureManager failureManager = new FailureManager();
        failureManager.process(e);
    }
}
