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
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.UnprotectedRootAction;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.security.PermissionScope;
import hudson.util.HttpResponses;

import hudson.plugins.clover.CloverBuildAction;
import hudson.plugins.cobertura.CoberturaBuildAction;
import hudson.plugins.jacoco.JacocoBuildAction;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Exposes the build status badge via unprotected URL.
 *
 * The status of a job can be checked like this:
 *
 * <li>http://localhost:8080/buildstatus/icon?job=[JOBNAME] <li>e.g. http://localhost:8080/buildstatus/icon?job=free1 <br/>
 * <br/>
 *
 * The status of a particular build can be checked like this:
 *
 * <li>http://localhost:8080/buildstatus/icon?job=[JOBNAME]&build=[BUILDNUMBER] <li>e.g. http://localhost:8080/buildstatus/icon?job=free1&build=5<br/>
 * <br/>
 *
 * Even though the URL is unprotected, the user does still need the 'ViewStatus' permission on the given Job. If you want the status icons to be public readable/accessible, just grant the 'ViewStatus'
 * permission globally to 'anonymous'.
 *
 * @author Dominik Bartholdi (imod)
 */
@Extension
public class PublicBadgeAction implements UnprotectedRootAction {

    public final static Permission VIEW_STATUS = new Permission(Item.PERMISSIONS, "ViewStatus", Messages._ViewStatus_Permission(), Item.READ, PermissionScope.ITEM);
    private final ImageResolver iconResolver;

    public PublicBadgeAction() throws IOException {
        iconResolver = new ImageResolver();
    }

    public String getUrlName() {
        return "buildStatus";
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }


    /**
     * Serves the codeCoverage badge image.
     */
    public HttpResponse doCoverageIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, @QueryParameter String build, @QueryParameter String style) {
    	if(build != null) {
            Run run = getRun(job, build);
            Integer codeCoverage = null;
            
            // Checks for Cobertura
            CoberturaBuildAction coverageAction = run.getAction(CoberturaBuildAction.class);
            if(coverageAction != null){
              codeCoverage = new Integer(coverageAction.getBuildHealth().getScore());
            }
            // Checks for Clover
            CloverBuildAction cloverAction = run.getAction(CloverBuildAction.class);
            if (cloverAction != null){
              codeCoverage = new Integer(cloverAction.getBuildHealth().getScore());
            }
            // Checks for Jacoco
            JacocoBuildAction jacocoAction = run.getAction(JacocoBuildAction.class);
            if (jacocoAction != null) {
              codeCoverage = new Integer(jacocoAction.getInstructionCoverage().getPercentage());
            }
            return iconResolver.getCoverageImage(codeCoverage);
        } else {
            Job<?, ?> project = getProject(job);
            Integer codeCoverage = null;
            
            // Checks for Cobertura
            CoberturaBuildAction coverageAction = project.getLastSuccessfulBuild().getAction(CoberturaBuildAction.class);
            if(coverageAction != null){
              codeCoverage = new Integer(coverageAction.getBuildHealth().getScore());
            }
            // Checks for Clover
            CloverBuildAction cloverAction = project.getLastSuccessfulBuild().getAction(CloverBuildAction.class);
            if (cloverAction != null){
              codeCoverage = new Integer(cloverAction.getBuildHealth().getScore());
            }
            // Checks for Jacoco
            JacocoBuildAction jacocoAction = project.getLastSuccessfulBuild().getAction(JacocoBuildAction.class);
            if (jacocoAction != null) {
              codeCoverage = new Integer(jacocoAction.getInstructionCoverage().getPercentage());
            }
            
            return iconResolver.getCoverageImage(codeCoverage);
        }
    }
    /**
     * Serves the buildResult badge image.
     */
    public HttpResponse doBuildIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, @QueryParameter String build, @QueryParameter String style) {
        if(build != null) {
            Run run = getRun(job, build);
            return iconResolver.getImage(run.getIconColor(), style);
        } else {
            Job<?, ?> project = getProject(job);
            return iconResolver.getImage(project.getIconColor(), style);
        }
    }

    private Job<?, ?> getProject(String job) {
        Job<?, ?> p;

        // as the user might have ViewStatus permission only (e.g. as anonymous) we get get the project impersonate and check for permission after getting the project
        SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
        try {
            p = Jenkins.getInstance().getItemByFullName(job, Job.class);
        } finally {
            SecurityContextHolder.setContext(orig);
        }

        // check if user has permission to view the status
        if(p == null || !(p.hasPermission(VIEW_STATUS))){
            throw HttpResponses.notFound();
        }

        return p;
    }
    private Run<?, ?> getRun(String job, String build) {
        Run<?, ?> run;

        // as the user might have ViewStatus permission only (e.g. as anonymous) we get get the project impersonate and check for permission after getting the project
        SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
        try {
            run = Jenkins.getInstance().getItemByFullName(job, Job.class).getBuildByNumber(Integer.parseInt(build));
        } finally {
            SecurityContextHolder.setContext(orig);
        }

        // check if user has permission to view the status
        if(run == null || !(run.hasPermission(VIEW_STATUS))){
            throw HttpResponses.notFound();
        }

        return run;
    }

}
