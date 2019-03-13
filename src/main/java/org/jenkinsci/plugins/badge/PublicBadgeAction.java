/*
 * The MIT License
 *
 * Copyright 2013 Dominik Bartholdi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.PluginWrapper;
import static hudson.model.Item.PERMISSIONS;
import static hudson.model.Item.READ;

import hudson.model.BallColor;
import hudson.model.HealthReport;
import hudson.model.Job;
import hudson.model.UnprotectedRootAction;
import hudson.plugins.clover.CloverBuildAction;
import hudson.plugins.cobertura.CoberturaBuildAction;
import hudson.plugins.cobertura.targets.CoverageMetric;
import hudson.plugins.jacoco.JacocoBuildAction;
import static hudson.security.ACL.SYSTEM;
import static hudson.security.ACL.impersonate;
import hudson.security.Permission;
import static hudson.security.PermissionScope.ITEM;
import hudson.tasks.test.AbstractTestResultAction;
import java.io.IOException;

import static jenkins.model.Jenkins.getInstance;
import org.acegisecurity.context.SecurityContext;
import static org.acegisecurity.context.SecurityContextHolder.setContext;
import static org.jenkinsci.plugins.badge.Messages._ViewStatus_Permission;
import org.kohsuke.stapler.HttpResponse;
import static org.kohsuke.stapler.HttpResponses.notFound;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Exposes the build status badge via unprotected URL.
 *
 * The status of a job can be checked like this:
 *
 * <li>http://localhost:8080/buildstatus/icon?job=[JOBNAME] <li>
 * e.g. http://localhost:8080/buildstatus/icon?job=free1 <br/>
 * <br/>
 *
 * The status of a particular build can be checked like this:
 *
 *
 * Even though the URL is unprotected, the user does still need the 'ViewStatus'
 * permission on the given Job. If you want the status icons to be public
 * readable/accessible, just grant the 'ViewStatus' permission globally to
 * 'anonymous'.
 *
 * @author Dominik Bartholdi (imod)
 */
@Extension
public class PublicBadgeAction implements UnprotectedRootAction {

    /**
     * TO DO
     */
    public final static Permission VIEW_STATUS = new Permission(PERMISSIONS, "ViewStatus", _ViewStatus_Permission(), READ, ITEM);
    /**
     * TO DO
     */
    private final ImageResolver iconResolver;

    /**
     * TO DO
     * @throws IOException
     */
    public PublicBadgeAction() throws IOException {
        iconResolver = new ImageResolver();
    }

    /**
     * TO DO
     * @return
     */
    @Override
    public String getUrlName() {
        return "buildStatus";
    }

    /**
     * TO DO
     * @return
     */
    @Override
    public String getIconFileName() {
        return null;
    }

    /**
     * TO DO
     * @return
     */
    @Override
    public String getDisplayName() {
        return null;
    }

    /**
     * Serves the codeCoverage badge image.
     *
     * @param req
     * @param rsp
     * @param job
     * @return
     */
    public HttpResponse doCoverageIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job) {
        Job<?, ?> project = getProject(job);
        Integer codeCoverage = null;

        if (project.getLastSuccessfulBuild() != null) {
            PluginWrapper jacocoInstalled = getInstance().pluginManager.getPlugin("jacoco");
            // Checks for Jacoco
            if (jacocoInstalled != null && jacocoInstalled.isActive()) {
                JacocoBuildAction jacocoAction = project.getLastSuccessfulBuild().getAction(JacocoBuildAction.class);
                if (jacocoAction != null) {
                    if (jacocoAction.getInstructionCoverage() != null){
						codeCoverage = jacocoAction.getInstructionCoverage().getPercentage();
                    }
                }
            }
            PluginWrapper coberturaInstalled = getInstance().pluginManager.getPlugin("cobertura");
            // Checks for Cobertura
            if (coberturaInstalled != null && coberturaInstalled.isActive()) {
                CoberturaBuildAction coberturaAction = project.getLastSuccessfulBuild().getAction(CoberturaBuildAction.class);
                if (coberturaAction != null) {
                	codeCoverage = coberturaAction.getResults().get(CoverageMetric.LINE).getPercentage();
                }
            }
            PluginWrapper cloverInstalled = getInstance().pluginManager.getPlugin("clover");
            // Checks for Clover
            if (cloverInstalled != null && cloverInstalled.isActive()) {
                CloverBuildAction cloverAction = project.getLastSuccessfulBuild().getAction(CloverBuildAction.class);
                if (cloverAction != null){
                    codeCoverage = cloverAction.getElementCoverage().getPercentage();
                }
            }
        }

        return iconResolver.getCoverageImage(codeCoverage);
    }

    /**
     * Serves the testCoverage badge image. TO DO
     * @param req
     * @param rsp
     * @param job
     * @return
     */
    @SuppressWarnings("rawtypes")
	public HttpResponse doTestIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job) {
        Job<?, ?> project = getProject(job);
        Integer testPass = null;
        Integer testTotal = null;

        if (project.getLastCompletedBuild() != null) {
        	AbstractTestResultAction testAction =  project.getLastCompletedBuild().getAction(AbstractTestResultAction.class);
			if(testAction != null){
				int total = testAction.getTotalCount();
				int pass = total - testAction.getFailCount() - testAction.getSkipCount();
				
				testTotal = total;
				testPass = pass;
			}
        }
        return iconResolver.getTestResultImage(testPass, testTotal);
    }

    /**
     * Serves the buildResult badge image.
     * @param req
     * @param rsp
     * @param job
     * @return
     */
    public HttpResponse doBuildIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job) {
        Job<?, ?> project = getProject(job);
        BallColor lastBuildColor;
        if(project.getLastCompletedBuild() != null) {
        	lastBuildColor = project.getLastCompletedBuild().getIconColor();
        } else {
        	lastBuildColor = BallColor.NOTBUILT;
        }
        return iconResolver.getImage(project.getIconColor(), lastBuildColor);
    }
    
    /**
     * Serves the Build Description badge image.
     * @param req
     * @param rsp
     * @param job
     * @return
     */
    public HttpResponse doBuildDescriptionIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job) {
        Job<?, ?> project = getProject(job);
        String buildDescription = null;
        
        /*if (project.getLastSuccessfulBuild() != null) {
            buildDescription = project.getLastSuccessfulBuild().getDescription();
        }*/
        
        if (project.getLastBuild() != null) {
            buildDescription = project.getLastBuild().getDescription();
        }
        
        if (buildDescription == null && project.getLastCompletedBuild() != null) {
            buildDescription = project.getLastCompletedBuild().getDescription();
        }
        
        return iconResolver.getBuildDescriptionImage(buildDescription);
    }
    
    /**
     * Serves the Weather badge image.
     * @param req
     * @param rsp
     * @param job
     * @return
     */
    public HttpResponse doWeatherIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job) {
        Job<?, ?> project = getProject(job);
        HealthReport healthReport = project.getBuildHealth();
        
        return iconResolver.getWeatherImage(healthReport);
    }
    
    /** 
     * TO DO
     * @param job
     * @return
     */
    private Job<?, ?> getProject(String job) {
        Job<?, ?> p;

        // as the user might have ViewStatus permission only (e.g. as anonymous)
        // we get get the project impersonate and 
        // check for permission after getting the project
        SecurityContext orig = impersonate(SYSTEM);
        try {
            p = getInstance().getItemByFullName(job, Job.class);
        } finally {
            setContext(orig);
        }

        // check if user has permission to view the status
        if (p == null || !(p.hasPermission(VIEW_STATUS))) {
            throw notFound();
        }

        return p;
    }
}