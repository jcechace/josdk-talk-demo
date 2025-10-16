package net.cechacek.demo.operator.dependents;

import io.fabric8.kubernetes.api.model.*;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import net.cechacek.demo.operator.ResourceUtils;
import net.cechacek.demo.operator.model.Website;

import java.util.Map;

public class HtmlPodDependent extends CRUDKubernetesDependentResource<Pod, Website> {

    public static String name(Website primary) {
        return ResourceUtils.createName(primary, null);
    }

    public static Map<String, String> labels(Website primary) {
        return Map.of(
                "demo.cechacek.net/site", ResourceUtils.createName(primary, null)
        );
    }

    @Override
    protected Pod desired(Website primary, Context<Website> context) {
        var labels = labels(primary);

        return new PodBuilder()
                .withMetadata(ResourceUtils.createMetadata(primary, HtmlPodDependent::name))
                .editMetadata().addToLabels(labels).endMetadata()
                .withSpec(new PodSpecBuilder()
                        .withVolumes(new VolumeBuilder()
                                .withName("content")
                                .withConfigMap(new ConfigMapVolumeSourceBuilder()
                                        .withName(ContentConfigMapDependent.name(primary))
                                        .build())
                                .build())
                        .withContainers(new ContainerBuilder()
                                .withName("caddy")
                                .withImage("caddy:latest")
                                .withPorts(new ContainerPortBuilder()
                                        .withName("http")
                                        .withContainerPort(80)
                                        .build())
                                .withVolumeMounts(new VolumeMountBuilder()
                                        .withName("content")
                                        .withMountPath("/usr/share/caddy")
                                        .withReadOnly()
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
