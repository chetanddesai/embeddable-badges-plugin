package org.jenkinsci.plugins.badge;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import static jenkins.model.Jenkins.RESOURCE_PATH;
import static jenkins.model.Jenkins.getInstance;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.toByteArray;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Status image as an {@link HttpResponse}, with proper cache handling.
 *
 * <p>
 * Originally we used 302 redirects to map the status URL to a proper permanent
 * image URL, but it turns out that some browsers cache 302 redirects in
 * violation of RFC (see
 * http://code.google.com/p/chromium/issues/detail?id=103458)
 *
 * <p>
 * So this version directly serves the image at the status URL. Since the status
 * can change any time, we use ETag to skip the actual data transfer if
 * possible.
 *
 * @author Kohsuke Kawaguchi
 */
class StatusImage implements HttpResponse {

    /**
     * TO DO
     */
    public final byte[] payload;

    /**
     * To improve the caching, compute unique ETag.
     *
     * This needs to differentiate different image types and possible future
     * image changes in newer versions of this plugin.
     */
    private final String etag;
    /**
     * TO DO?
     */
    private final String length;

    /**
     * TO DO
     * @param fileName
     * @throws IOException
     */
    StatusImage(String fileName) throws IOException {
        etag = '"' + RESOURCE_PATH + '/' + fileName + '"';

        URL image;
        image = new URL(
                getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                "status/" + fileName);
        InputStream s = image.openStream();
        try {
            payload = toByteArray(s);
        } finally {
            closeQuietly(s);
        }
        length = Integer.toString(payload.length);
    }

    /**
     * TO DO
     * @param etag
     * @param s
     * @throws IOException
     */
    StatusImage(String etag, InputStream s) throws IOException {
        this.etag = etag;
        try {
            payload = toByteArray(s);
        } finally {
            closeQuietly(s);
        }
        length = Integer.toString(payload.length);
    }
    
    /**
     * TO DO
     * @param req
     * @param rsp
     * @param node
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        /*String v = req.getHeader("If-None-Match");
        if (etag.equals(v)) {
            rsp.setStatus(SC_NOT_MODIFIED);
            return;
        }*/

        rsp.setHeader("ETag", etag);
        rsp.setHeader("Expires", "Fri, 01 Jan 1984 00:00:00 GMT");
        rsp.setHeader("Cache-Control", "no-cache, private");
        rsp.setHeader("Content-Type", "image/svg+xml;charset=utf-8");
        rsp.setHeader("Content-Length", length);
        rsp.getOutputStream().write(payload);
    }
}