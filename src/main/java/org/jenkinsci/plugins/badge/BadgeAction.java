package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.HttpResponse;

/**
* @author Kohsuke Kawaguchi
*/
public class BadgeAction implements Action {
    /**
     *
     */
    private final BadgeActionFactory factory;

    /**
     *
     */
    public final Job project;
    /**
     *
     * @param factory
     * @param project
     */
    public BadgeAction(BadgeActionFactory factory, Job project) {
        this.factory = factory;
        this.project = project;
    }
    /**
     *
     * @return
     */
    @Override
    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH+"/plugin/embeddable-badges/images/24x24/shield.png";
    }

    /**
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return Messages.BadgeAction_DisplayName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getUrlName() {
        return "badge";
    }

    /**
     * Serves the buildResult badge image.
     * @return
     */
    public HttpResponse doBuildIcon() {
        return factory.getImage(project.getIconColor());
    }
}
