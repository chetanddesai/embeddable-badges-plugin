package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.BallColor;
import hudson.model.Job;
import jenkins.model.TransientActionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class BadgeActionFactory extends TransientActionFactory<Job> {

    /**
     *
     */
    private final ImageResolver iconResolver;

    /**
     *
     * @throws IOException 
     */
    public BadgeActionFactory() throws IOException {
        iconResolver = new ImageResolver();
    }

    /**
     *
     * @return
     */
    @Override
    public Class<Job> type() {
        return Job.class;
    }

    /**
     *
     * @param target
     * @return
     */
    @Override
    public Collection<? extends Action> createFor(Job target) {
        return Collections.singleton(new BadgeAction(this,target));
    }
    /**
     *
     * @param color
     * @return
     */
    public StatusImage getImage(BallColor color) {
        return iconResolver.getImage(color);
    }

}
