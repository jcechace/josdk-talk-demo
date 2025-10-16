package net.cechacek.demo.operator.dependents;

import io.fabric8.kubernetes.api.model.*;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import net.cechacek.demo.operator.ResourceUtils;
import net.cechacek.demo.operator.model.Website;

public class ServiceDependent extends CRUDKubernetesDependentResource<Service, Website> {

    public static String name(Website primary) {
        return ResourceUtils.createName(primary, null);
    }

    @Override
    protected Service desired(Website primary, Context<Website> context) {
        return new ServiceBuilder()
                .withMetadata(ResourceUtils.createMetadata(primary, ServiceDependent::name))
                .withSpec(new ServiceSpecBuilder()
                        .withSelector(HtmlPodDependent.labels(primary))
                        .withPorts(new ServicePortBuilder()
                                .withName("http")
                                .withPort(80)
                                .withTargetPort(new IntOrString(80))
                                .build())
                        .build())
                .build();
    }
}
