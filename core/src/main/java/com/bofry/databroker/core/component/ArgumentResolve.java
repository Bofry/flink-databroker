package com.bofry.databroker.core.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ArgumentResolve {

    private final MultipleParameterTool params;

    public ArgumentResolve(String[] args) {
        this.params = MultipleParameterTool.fromArgs(args);
    }

    public String getConfigurationContext() {
        String config = getContentByKey("conf");
        if (StringUtils.isEmpty(config)) {
            throw new IllegalArgumentException();
        }
        return config;
    }

    private String getContentByKey(String key) {
        String value = this.params.get(key);
        if (value == null) {
            return "";
        }
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return readWebContent(value);
        } else {
            return readFileContent(value);
        }
    }

    private String readFileContent(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    private String readWebContent(String url) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            return httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
