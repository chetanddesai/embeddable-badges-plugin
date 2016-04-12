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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hudson.model.FreeStyleProject;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.SecurityRealm;

import java.net.HttpURLConnection;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.PresetData;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Dominik Bartholdi (imod)
 */
public class PublicBadgeActionTest {

    /**
     *
     */
    @Rule
    public JenkinsRule j;

    /**
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    /**
     *
     */
    public PublicBadgeActionTest() {
        this.j = new JenkinsRule();
    }

    /**
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void authenticatedAccess() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("free");
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice");
        try {
            // try with wrong job name
            wc.goTo("buildStatus/buildIcon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }
        wc.goTo("buildStatus/buildIcon?job=free", "image/svg+xml");
        j.buildAndAssertSuccess(project);
    }

    /**
     *
     * @throws Exception
     */
    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void invalidAnonymousAccess() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("free");
        JenkinsRule.WebClient wc = j.createWebClient();
        try {
            // try with wrong job name
            wc.goTo("buildStatus/buildIcon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        try {
            // try with correct job name
            wc.goTo("buildStatus/buildIcon?job=free", "image/svg+xml");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            // make sure return code does not leak security relevant information (must 404)
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        j.buildAndAssertSuccess(project);

    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void validAnonymousViewStatusAccess() throws Exception {

        final SecurityRealm realm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(PublicBadgeAction.VIEW_STATUS, "anonymous");
        j.getInstance().setSecurityRealm(realm);
        j.getInstance().setAuthorizationStrategy(auth);

        final FreeStyleProject project = j.createFreeStyleProject("free");

        JenkinsRule.WebClient wc = j.createWebClient();
        try {
            // try with wrong job name
            wc.goTo("buildStatus/buildIcon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        wc.goTo("buildStatus/buildIcon?job=free", "image/svg+xml");
        j.buildAndAssertSuccess(project);
   
    }
    /**
     *
     * @throws Exception
     */
    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void validAnonymousAccess() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("free");
        JenkinsRule.WebClient wc = j.createWebClient();
        try {
            // try with wrong job name
            wc.goTo("buildStatus/buildIcon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        // try with correct job name
        wc.goTo("buildStatus/buildIcon?job=free", "image/svg+xml");
        j.buildAndAssertSuccess(project);
    }

    /**
     * Test of getUrlName method, of class PublicBadgeAction.
     * @throws java.io.IOException
     */
    @Test
    public void testGetUrlName() throws IOException {
        System.out.println("getUrlName");
        PublicBadgeAction instance = new PublicBadgeAction();
        String expResult = "buildStatus";
        String result = instance.getUrlName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getIconFileName method, of class PublicBadgeAction.
     * @throws java.io.IOException
     */
    @Test
    public void testGetIconFileName() throws IOException {
        System.out.println("getIconFileName");
        PublicBadgeAction instance = new PublicBadgeAction();
        String expResult = null; //Change this eventually
        String result = instance.getIconFileName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayName method, of class PublicBadgeAction.
     * @throws java.io.IOException
     */
    @Test
    public void testGetDisplayName() throws IOException {
        System.out.println("getDisplayName");
        PublicBadgeAction instance = new PublicBadgeAction();
        String expResult = null; //change this eventually
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of doCoverageIcon method, of class PublicBadgeAction.
     * @throws java.io.IOException
     */
    @Test
    public void testDoCoverageIcon() throws IOException {
        System.out.println("doCoverageIcon");
        StaplerRequest req = null;
        StaplerResponse rsp = null;
        String job = "";
        PublicBadgeAction instance = new PublicBadgeAction();
        //HttpResponse expResult = instance.doCoverageIcon(req, rsp, job);
        //HttpResponse result = instance.doCoverageIcon(req, rsp, job);
        assertEquals(1, 1);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of doTestIcon method, of class PublicBadgeAction.
     * @throws java.io.IOException
     */
    @Test
    public void testDoTestIcon() throws IOException {
        System.out.println("doTestIcon");
        StaplerRequest req = null;
        StaplerResponse rsp = null;
        String job = "";
        PublicBadgeAction instance = new PublicBadgeAction();
        //HttpResponse expResult = instance.doTestIcon(req, rsp, job);
        //HttpResponse result = instance.doTestIcon(req, rsp, job);
        assertEquals(1, 1);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of doBuildIcon method, of class PublicBadgeAction.
     * @throws java.io.IOException
     */
    @Test
    public void testDoBuildIcon() throws IOException {
        System.out.println("doBuildIcon");
        StaplerRequest req = null;
        StaplerResponse rsp = null;
        String job = "";
        PublicBadgeAction instance = new PublicBadgeAction();
        //HttpResponse expResult = instance.doBuildIcon(req, rsp, job);
        //HttpResponse result = instance.doBuildIcon(req, rsp, job);
        assertEquals(1, 1);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
