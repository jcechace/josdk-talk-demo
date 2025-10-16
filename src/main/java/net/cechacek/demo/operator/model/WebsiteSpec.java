package net.cechacek.demo.operator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebsiteSpec {
    @JsonPropertyDescription("Simple HTML site spec")
    @JsonProperty(required = true)
    private HtmlSpec html = null;

    public HtmlSpec getHtml() {
        return html;
    }

    public void setHtml(HtmlSpec html) {
        this.html = html;
    }
}
