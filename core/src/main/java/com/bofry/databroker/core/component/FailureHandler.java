package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FailureHandler implements IFailureHandler, IAgent {

    private final IFailureDocumentWriter writer;
    private final WriterConfiguration conf;

    public FailureHandler(WriterConfiguration conf) {
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
        // log.info("FailureHandler: ", e);
        if (e instanceof Failure) {
            Failure f = (Failure) e;
            if (f.isFailure()) {
                FailureContext ctx = FailureContext.builder()
                        .action(FailureActionEnum.Fail)
                        .content(f.getContent())
                        .build();
                this.writer.write(ctx);
                return true;
            }
        }
        return false;
    }

    @Override
    public IConfiguration getConfiguration() {
        return this.conf;
    }

    @Override
    public void throwFailure(Exception e) {
        FailureManager failureManager = new FailureManager();
        failureManager.process(e);
    }
}
