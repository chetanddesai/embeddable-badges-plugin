package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.BallColor;
import hudson.model.Job;
import java.io.IOException;
import java.util.Collection;
import static java.util.Collections.singleton;
import jenkins.model.TransientActionFactory;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("rawtypes")
@Extension
public class BadgeActionFactory extends TransientActionFactory<Job> {

    /**
     * TO DO
     */
    private final ImageResolver iconResolver;

    /**
     * TO DO
     * @throws IOException 
     */
    public BadgeActionFactory() throws IOException {
        iconResolver = new ImageResolver();
    }

    /**
     * TO DO
     * @return
     */
    @Override
    public Class<Job> type() {
        return Job.class;
    }

    /**
     * TO DO
     * @param target
     * @return
     */
    @Override
    public Collection<? extends Action> createFor(Job target) {
        return singleton(new BadgeAction(this,target));
    }
    /**
     * TO DO
     * @param color
     * @return
     */
    public StatusImage getImage(BallColor color, BallColor lastBuildColor) {
        return iconResolver.getImage(color, lastBuildColor);
    }

}
