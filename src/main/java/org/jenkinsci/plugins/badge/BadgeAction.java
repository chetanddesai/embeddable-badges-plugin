package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.BallColor;
import hudson.model.Job;
import static jenkins.model.Jenkins.RESOURCE_PATH;
import static org.jenkinsci.plugins.badge.Messages.BadgeAction_DisplayName;
import org.kohsuke.stapler.HttpResponse;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("rawtypes")
public class BadgeAction implements Action {

    /**
     * TO DO
     */
    private final BadgeActionFactory factory;

    /**
     * TO DO
     */
	public final Job project;

    /**
     * TO DO
     *
     * @param factory
     * @param project
     */
    public BadgeAction(BadgeActionFactory factory, Job project) {
        this.factory = factory;
        this.project = project;
    }

    /**
     * TO DO
     *
     * @return
     */
    @Override
    public String getIconFileName() {
        return RESOURCE_PATH + "/plugin/embeddable-badges/images/24x24/shield.png";
    }

    /**
     * TO DO
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return BadgeAction_DisplayName();
    }

    /**
     * TO DO
     *
     * @return
     */
    @Override
    public String getUrlName() {
        return "badge";
    }

    /**
     * Serves the buildResult badge image.
     *
     * @return
     */
    public HttpResponse doBuildIcon() {
    	BallColor lastBuildColor;
        if(project.getLastCompletedBuild() != null) {
        	lastBuildColor = project.getLastCompletedBuild().getIconColor();
        } else {
        	lastBuildColor = BallColor.NOTBUILT;
        }
        return factory.getImage(project.getIconColor(), lastBuildColor);
    }
}
