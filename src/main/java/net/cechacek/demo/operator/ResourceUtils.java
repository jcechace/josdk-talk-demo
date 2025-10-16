package net.cechacek.demo.operator;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import net.cechacek.demo.operator.model.Website;
import net.cechacek.demo.operator.model.WebsiteStatus;

import java.util.function.Function;

/**
 * Useful helper methods
 */
public final class ResourceUtils {
    private ResourceUtils() {
    }

    /**
     * Creates metadata for dependent resource
     * @param site primary website resource
     * @param namer name creator function
     * @return metadata
     */
    public static ObjectMeta createMetadata(Website site, Function<Website, String> namer) {
        var targetName = namer.apply(site);
        var targetNs = site.getMetadata().getNamespace();

        return new ObjectMetaBuilder()
                .withName(targetName)
                .withNamespace(targetNs)
                .build();
    }

    /**
     * Joins primary and secondary name using dash
     * @param site primary website resource
     * @param name secondary name
     * @return joined name
     */
    public static String createName(Website site, String name) {
        var targetName = site.getMetadata().getName();

        if (name != null &&  !name.isBlank()) {
            targetName += "-" + name;
        }

        return targetName;
    }

    /**
     * Dummy HTML validation
     * @return true or false depending on validity
     */
    public static boolean isValidHtml(String html) {
        var htmlLowerCase = html.toLowerCase();
        return htmlLowerCase.contains("<html") && htmlLowerCase.contains("</html>");
    }

    /**
     * Creates a fresh "empty" website resource with only identifiers and status for SSA
     * @param website website resource
     * @param ready whether site is ready
     * @param error error message or null
     * @return resource instance suitable for SSA patch
     */
    public static Website createWebsiteForStatusUpdate(Website website, boolean ready, String error) {
        var res = new Website();
        res.setMetadata(
                new ObjectMetaBuilder()
                        .withName(website.getMetadata().getName())
                        .withNamespace(website.getMetadata().getNamespace())
                        .build()
        );
        res.setStatus(createStatus(ready, error));
        return res;
    }

    /**
     * Creates status subresource
     * @param ready whether site is ready
     * @param error error message or null
     * @return website status instance
     */
    public static WebsiteStatus createStatus(boolean ready, String error) {
        var status = new WebsiteStatus();
        status.setReady(ready);
        status.setErrorMessage(error);
        return status;
    }
}
