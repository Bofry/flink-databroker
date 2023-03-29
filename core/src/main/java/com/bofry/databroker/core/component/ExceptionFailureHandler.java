package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ExceptionFailureHandler implements IFailureHandler {
    @Override
    public boolean process(Exception e) {
        if (e instanceof Failure) {
            Failure f = (Failure) e;
            String value = FailureHelper.getValueToString(f.getContent());
            if (value == null) {
                value = FailureHelper.getSourceToString(f.getContent());
            }
            log.error("content: " + value, f);
            return true;
        }
        e.printStackTrace();
        return true;
    }
}
