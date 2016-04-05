package org.jenkinsci.plugins.badge;

import java.io.IOException;

import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;


/**
* @author Kohsuke Kawaguchi
*/
public class BadgeAction implements Action {
    private final BadgeActionFactory factory;

    public final Job project;

    public BadgeAction(BadgeActionFactory factory, Job project) {
        this.factory = factory;
        this.project = project;
    }

    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH+"/plugin/embeddable-badges/images/24x24/shield.png";
    }

    public String getDisplayName() {
        return Messages.BadgeAction_DisplayName();
    }

    public String getUrlName() {
        return "badge";
    }

    /**
     * Serves the badge image.
     */
    public HttpResponse doIcon(@QueryParameter String style) {
        return factory.getImage(project.getIconColor(), style);
    }
    
    /**
     * Serves the badge image.
     * @throws IOException 
     */
    public HttpResponse doCoverage(@QueryParameter String style) throws IOException {
        return factory.getCoverageImage();
    }
}
