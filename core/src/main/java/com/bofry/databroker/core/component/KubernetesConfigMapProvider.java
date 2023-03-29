package com.bofry.databroker.core.component;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class KubernetesConfigMapProvider implements IConfigProvider {

    private final Map<String, String> configMap;

    public KubernetesConfigMapProvider(KubernetesConfiguration kubernetesConfiguration) {
        this.configMap = getConfigMap(kubernetesConfiguration);
    }

    @Override
    public String get(String key) {
        return configMap.get(key);
    }

    private Map<String, String> getConfigMap(KubernetesConfiguration kubernetesConfiguration) {
        Map<String, String> configMap = new HashMap<>();

        if (kubernetesConfiguration == null ||
                kubernetesConfiguration.getNamespace() == null ||
                kubernetesConfiguration.getName() == null ||
                kubernetesConfiguration.getConfigFile() == null) {
            return configMap;
        }

        KubernetesClient client = new DefaultKubernetesClient();
        try {
            ConfigMap k8sConfigMap = client.configMaps().inNamespace(kubernetesConfiguration.getNamespace()).withName(kubernetesConfiguration.getName()).get();
            configMap = new Yaml().load(new ByteArrayInputStream(k8sConfigMap.getData().get(kubernetesConfiguration.getConfigFile()).getBytes()));
        } catch (Exception e) {
             e.printStackTrace();
        }
        return configMap;
    }

}
