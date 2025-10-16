package net.cechacek.demo.operator;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.config.informer.Informer;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.processing.dependent.workflow.WorkflowReconcileResult;
import io.quarkus.logging.Log;
import net.cechacek.demo.operator.conditions.HtmlValidCondition;
import net.cechacek.demo.operator.dependents.ContentConfigMapDependent;
import net.cechacek.demo.operator.dependents.HtmlPodDependent;
import net.cechacek.demo.operator.dependents.ServiceDependent;
import net.cechacek.demo.operator.model.Website;
import net.cechacek.demo.operator.model.WebsiteStatus;

import java.util.concurrent.TimeUnit;

import static net.cechacek.demo.operator.ResourceUtils.createWebsiteForStatusUpdate;

@Workflow(dependents = {
        @Dependent(name = "content", type = ContentConfigMapDependent.class, reconcilePrecondition = HtmlValidCondition.class),
        @Dependent(type = HtmlPodDependent.class, dependsOn = "content"),
        @Dependent(type = ServiceDependent.class)
})
@ControllerConfiguration(
        informer = @Informer(namespaces = Constants.WATCH_ALL_NAMESPACES)
)
public class WebsiteReconciler implements Reconciler<Website> {

    @Override
    public UpdateControl<Website> reconcile(Website website, Context<Website> context) throws Exception {
        var wrr = context.managedWorkflowAndDependentResourceContext().getWorkflowReconcileResult();

        if (wrr.map(WorkflowReconcileResult::allDependentResourcesReady).orElse(false)) {
            Log.infof("Site %s is ready", website.getMetadata().getName());
            return UpdateControl
                        .patchStatus(createWebsiteForStatusUpdate(website, true, null));
        }
        else {
            Log.infof("Site %s not ready yet", website.getMetadata().getName());
            return UpdateControl
                        .patchStatus(createWebsiteForStatusUpdate(website, false, null))
                        .rescheduleAfter(5, TimeUnit.SECONDS);
        }
    }

    @Override
    public ErrorStatusUpdateControl<Website> updateErrorStatus(Website website, Context<Website> context, Exception e) {
        Log.error("updateErrorStatus", e);
        return ErrorStatusUpdateControl.patchStatus(createWebsiteForStatusUpdate(website, false, e.getMessage()));
    }
}
