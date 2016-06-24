/*
* The MIT License
*
* Copyright 2013 Kohsuke Kawaguchi, Dominik Bartholdi
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

import hudson.model.BallColor;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import static jenkins.model.Jenkins.getInstance;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.toInputStream;
/**
 * TO DO
 * @author dkersch
 */
public class ImageResolver {
    /**
     * TO DO
     */
    private final HashMap<String, StatusImage[]> styles;
    /**
     * TO DO
     */
    private final StatusImage[] defaultStyle;
    /**
     * TO DO
     */
    public static final String RED = "#cc0000";
    /**
     * TO DO
     */
    public static final String YELLOW = "#b2b200";
    /**
     * TO DO
     */
    public static final String GREEN = "#008000";
    /**
     * TO DO
     */
    public static final String GREY = "#808080";
    /**
     * TO DO
     */
    public static final String BLUE = "#007ec6";

    /**
     * TO DO
     * @throws IOException
     */
    public ImageResolver() throws IOException {
        styles = new HashMap<String, StatusImage[]>();
        // shields.io "flat" style (new default from Feb 1 2015)
        StatusImage[] flatImages;
        flatImages = new StatusImage[]{
            new StatusImage("build-failing-red-flat.svg"),
            new StatusImage("build-unstable-yellow-flat.svg"),
            new StatusImage("build-passing-brightgreen-flat.svg"),
            new StatusImage("build-running-blue-flat.svg"),
            new StatusImage("build-aborted-lightgrey-flat.svg"),
            new StatusImage("build-unknown-lightgrey-flat.svg")
        };
        defaultStyle = flatImages;
        styles.put("default", defaultStyle);
    }

    /**
     * TO DO
     * @param codeCoverage
     * @return
     */
    public StatusImage getCoverageImage(Integer codeCoverage) {

        // TODO don't read file everytime, store this as a static variable in
        // TODO memory with the constructor
        URL image = null;
        try {
            image = new URL(
                    getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    "status/build-coverage-flat.svg");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(image.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String replacedImage = replaceCodeCoverageSVG(sb.toString(), codeCoverage);
        InputStream is = toInputStream(replacedImage);
        String etag = "status/build-coverage-flat.svg" + codeCoverage;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    /**
     * TO DO
     * @param image
     * @param codeCoverage
     * @return
     */
    private String replaceCodeCoverageSVG(String image, Integer codeCoverage) {

        if (codeCoverage == null) {
            String modifiedColor = image.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{code-coverage-to-change}", "0");

        } else if (codeCoverage < 20) {
            String modifiedColor = image.replace("{hex-color-to-change}", RED);
            return modifiedColor.replace("{code-coverage-to-change}", codeCoverage.toString());
        } else if (codeCoverage < 80) {
            String modifiedColor = image.replace("{hex-color-to-change}", YELLOW);
            return modifiedColor.replace("{code-coverage-to-change}", codeCoverage.toString());
        } else {
            String modifiedColor = image.replace("{hex-color-to-change}", GREEN);
            return modifiedColor.replace("{code-coverage-to-change}", codeCoverage.toString());
        }

    }
    /**
     * TO DO
     * @param testPass
     * @param testTotal
     * @return
     */
    public StatusImage getTestResultImage(Integer testPass, Integer testTotal) {

        // TODO don't read file everytime
        // TODO store this as a static variable in memory with the constructor
        URL image = null;
        try {
            image = new URL(
                    getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    "status/build-test-result-flat.svg");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(image.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String replacedImage = replaceTestResultSVG(sb.toString(), testPass, testTotal);
        InputStream is = toInputStream(replacedImage);
        String etag = "status/build-test-result-flat.svg" + testPass + testTotal + YELLOW;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    /**
     * TO DO
     * @param image
     * @param testPass
     * @param testTotal
     * @return
     */
    private String replaceTestResultSVG(String image, Integer testPass, Integer testTotal) {

        if (testTotal == null || testPass == null) {
            String modifiedColor = image.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{passed-tests}/{total-tests}", "n/a");
        } 
        else {
                double passTest = (double) (testPass);
                double passTotal = (double) (testTotal);
        	double passPercent = (passTest / passTotal) * 100.0;
        	if (passPercent < 20) {
	            String modifiedColor = image.replace("{hex-color-to-change}", RED);
	            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
	            return modifiedPass.replace("{total-tests}", testTotal.toString());
	        } else if (passPercent < 80) {
	            String modifiedColor = image.replace("{hex-color-to-change}", YELLOW);
	            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
	            return modifiedPass.replace("{total-tests}", testTotal.toString());
	        } else {
	            String modifiedColor = image.replace("{hex-color-to-change}", GREEN);
	            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
	            return modifiedPass.replace("{total-tests}", testTotal.toString());
	        }
        }

    }
    /**
     * TO DO
     * @param color
     * @return
     */
    public StatusImage getImage(BallColor color) {
        StatusImage[] images = styles.get("default");

        if (color.isAnimated()) {
            return images[3];
        }

        switch (color) {
            case RED:
                return images[0];
            case YELLOW:
                return images[1];
            case BLUE:
                return images[2];
            case ABORTED:
                return images[4];
            default:
                return images[5];

        }
    }
    /**
     * TO DO
     * @param image
     * @param testPass
     * @param testTotal
     * @return
     */
    private String replaceBuildDescriptionSVG(String image, String buildDescription) {

        if (buildDescription == null) {
            String modifiedColor = image.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{description_text}", "n/a");
        }
        else {
            String modifiedColor = image.replace("{hex-color-to-change}", BLUE);
            String modifiedPass = modifiedColor.replace("{description_text}", buildDescription);
            return modifiedPass;
	        }
        }
    
    
    public StatusImage getBuildDescriptionImage(String buildDescription) {
         // TODO don't read file everytime
        // TODO store this as a static variable in memory with the constructor
        URL image = null;
        try {
            image = new URL(
                    getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    "status/build-description-flat.svg");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(image.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String replacedImage = replaceBuildDescriptionSVG(sb.toString(), buildDescription);
        InputStream is = toInputStream(replacedImage);
        String etag = "status/build-description-flat.svg" + buildDescription;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
