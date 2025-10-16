package net.cechacek.demo.operator.conditions;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;
import net.cechacek.demo.operator.model.Website;

import java.util.ArrayList;

import static net.cechacek.demo.operator.ResourceUtils.isValidHtml;

public class HtmlValidCondition  implements Condition<Void, Website> {

    @Override
    public boolean isMet(DependentResource<Void, Website> dependentResource, Website primary, Context<Website> context) {
        var pages = primary.getSpec().getHtml().getPages();
        var invalid = new ArrayList<String>();

        pages.forEach((name, content) -> {
            if (!isValidHtml(content)) {
                invalid.add(name);
            }
        });

        if (!pages.containsKey("index.html") || !invalid.isEmpty()) {
            throw new InvalidHtmlException(invalid);
        }

        return true;
    }
}