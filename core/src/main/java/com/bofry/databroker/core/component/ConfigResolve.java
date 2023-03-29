package com.bofry.databroker.core.component;

import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigResolve {

    private String conf;
    private Configuration configuration;
    private KubernetesConfiguration kubernetesConfiguration;
    private List<SourceConfiguration> sourceConfiguration;
    private List<SinkConfiguration> sinkConfiguration;

    public ConfigResolve(String conf) {
        // TODO 要檢核各個欄位字串合法性、長度...等等，在各個Configuration裡面自行檢核即可。
        this.conf = conf;

        this.genConfiguration(this.formatEnvVars());
        this.genKubernetesConfiguration();

        this.genConfiguration(this.formatEnvVars());
        this.genSourceConfigurationElement();
        this.genSinkConfigurationElement();
    }

    @SneakyThrows
    private void genConfiguration(String conf) {
        this.configuration = JacksonUtils.YAML_OBJECT_MAPPER.readValue(conf, Configuration.class);
    }

    @SneakyThrows
    private void genKubernetesConfiguration() {
        this.kubernetesConfiguration = this.configuration.getKubernetes();
    }

    @SneakyThrows
    private void genSourceConfigurationElement() {
        this.sourceConfiguration = new ArrayList<>(this.configuration.getSource());
    }

    @SneakyThrows
    private void genSinkConfigurationElement() {
        List<SinkConfiguration> result = new ArrayList<>();
        for (SinkConfiguration sink : this.configuration.getSink()) {
            sink.config(this.configuration);
            result.add(sink);
        }
        this.sinkConfiguration = result;
    }

    private String formatEnvVars() {
        Environment env = Environment.getInstance();
        env._add(new KubernetesConfigMapProvider(this.kubernetesConfiguration));
        return env.expand(this.conf);
    }

}
