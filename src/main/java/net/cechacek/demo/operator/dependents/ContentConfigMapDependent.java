package net.cechacek.demo.operator.dependents;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import net.cechacek.demo.operator.ResourceUtils;
import net.cechacek.demo.operator.model.Website;

public class ContentConfigMapDependent extends CRUDKubernetesDependentResource<ConfigMap, Website > {
    public static String name(Website primary) {
        return ResourceUtils.createName(primary, "content");
    }

    @Override
    protected ConfigMap desired(Website primary, Context<Website> context) {
        return new ConfigMapBuilder()
                .withMetadata(ResourceUtils.createMetadata(primary, ContentConfigMapDependent::name))
                .withData(primary.getSpec().getHtml().getPages())
                .build();
    }
}
