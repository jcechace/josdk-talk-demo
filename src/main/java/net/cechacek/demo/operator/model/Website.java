package net.cechacek.demo.operator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("demo.cechacek.net")
@Version("v1alpha1")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Website
        extends CustomResource<WebsiteSpec, WebsiteStatus>
        implements Namespaced {
    public Website() {
    }
}
