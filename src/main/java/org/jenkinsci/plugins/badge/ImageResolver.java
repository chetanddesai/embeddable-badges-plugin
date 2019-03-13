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
import hudson.model.HealthReport;

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
    
    // Include the plugin's current version number in the ETag,...
	// ...so that cached responses from a previous version don't get served
    private final String thisPluginCurrentVersion = getClass().getPackage().getImplementationVersion();

    
    /**
     * TO DO
     * @throws IOException
     */
    public ImageResolver() throws IOException {
        styles = new HashMap<String, StatusImage[]>();
        // shields.io "flat" style (new default from Feb 1 2015)
        StatusImage[] flatImages;
        flatImages = new StatusImage[] {
            new StatusImage("build-failing-red-flat.svg"),
            new StatusImage("build-unstable-yellow-flat.svg"),
            new StatusImage("build-passing-brightgreen-flat.svg"),
            new StatusImage("build-running-blue-red-flat.svg"),
            new StatusImage("build-running-blue-yellow-flat.svg"),
            new StatusImage("build-running-blue-brightgreen-flat.svg"),
            new StatusImage("build-running-blue-lightgrey-aborted-flat.svg"),
            new StatusImage("build-running-blue-lightgrey-unknown-flat.svg"),
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
        String etag = thisPluginCurrentVersion + "-status/build-coverage-flat.svg-" + codeCoverage;

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
            //return modifiedColor.replace("{code-coverage-to-change}", "0");
            return modifiedColor.replace("{code-coverage-to-change}", "NaN");

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
    
    private Integer[] scaleBadge(Integer totalLength, String badgeType){
        //52 (x6), 70 (x12) and 82 (x3), 95 (x6)
        int defaultTextLength = 52; //default for test status. Old: 52
        int defaultBadgeLength = 70; //default for test status. Old: 70
        int defaultMultiplier = 3;	// Old: 6
        
        if ("description".equals(badgeType)) {
            defaultTextLength = 82;	// Old: 82
            defaultBadgeLength = 95;	// Old: 95
            defaultMultiplier = 3;	// Old: 3
        }
        
        if (totalLength > 3) {	// > "n/a"
            defaultTextLength = defaultTextLength + (defaultMultiplier * totalLength);
            defaultBadgeLength = defaultBadgeLength + ((defaultMultiplier * 2) * totalLength);
        }
        
        Integer[] svgLengths = new Integer[2];
        svgLengths[0] = defaultTextLength;
        svgLengths[1] = defaultBadgeLength;
        
        return svgLengths;
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
        String etag = thisPluginCurrentVersion + "-status/build-test-result-flat.svg-" + testPass + testTotal;

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
        int totalLength;
        String badgeType;
        badgeType = "tests";
        try {
        	totalLength = testPass.toString().length() + testTotal.toString().length() + 1; //include the "/" in the badge
        } catch (Exception ex) {
        	totalLength = 3;	// "n/a"
        }
        
        Integer[] svgLengths = scaleBadge(totalLength, badgeType);
        Integer defaultTextLength = svgLengths[0];
        Integer defaultBadgeLength = svgLengths[1];
        String modifiedTextLength = image.replace("{change-text-length}", String.valueOf(defaultTextLength));
        String modifiedBadgeLength = modifiedTextLength.replace("{change-badge-length}", String.valueOf(defaultBadgeLength));
        
        if (testTotal == null || testPass == null) {
            String modifiedColor = modifiedBadgeLength.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{passed-tests}/{total-tests}", "n/a");
        } 
        else {
                double passTest = (double) (testPass);
                double passTotal = (double) (testTotal);
        	double passPercent = (passTest / passTotal) * 100.0;
        	if (passPercent < 20) {
	            String modifiedColor = modifiedBadgeLength.replace("{hex-color-to-change}", RED);
	            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
	            return modifiedPass.replace("{total-tests}", testTotal.toString());
	        } else if (passPercent < 80) {
	            String modifiedColor = modifiedBadgeLength.replace("{hex-color-to-change}", YELLOW);
	            String modifiedPass = modifiedColor.replace("{passed-tests}", testPass.toString());
	            return modifiedPass.replace("{total-tests}", testTotal.toString());
	        } else {
	            String modifiedColor = modifiedBadgeLength.replace("{hex-color-to-change}", GREEN);
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
    public StatusImage getImage(BallColor color, BallColor lastBuildColor) {
        StatusImage[] images = styles.get("default");

        if (color.isAnimated()) {	// Running
        	return getRunningBuildImage(lastBuildColor);
        }

        switch (color) {
            case RED:
                return images[0];
            case YELLOW:
                return images[1];
            case BLUE:
                return images[2];
            case ABORTED:
                return images[8];
            default:
                return images[9];
        }
    }
    
    private StatusImage getRunningBuildImage(BallColor lastBuildColor) {
    	StatusImage[] images = styles.get("default");

        switch (lastBuildColor) {
            case RED:
                return images[3];
            case YELLOW:
                return images[4];
            case BLUE:
                return images[5];
            case ABORTED:
                return images[6];
            default:
                return images[7];
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
        int totalLength;
        String badgeType;
        badgeType = "description";
        try {
        	totalLength = buildDescription.length();
        } catch (Exception ex) {
        	totalLength = 3;	// "n/a"
        }
        
        Integer[] svgLengths = scaleBadge(totalLength, badgeType);
        Integer defaultTextLength = svgLengths[0];
        Integer defaultBadgeLength = svgLengths[1];
        
        String modifiedTextLength = image.replace("{change-text-length}", String.valueOf(defaultTextLength));
        String modifiedBadgeLength = modifiedTextLength.replace("{change-badge-length}", String.valueOf(defaultBadgeLength));
        
        if (buildDescription == null) {
            String modifiedColor = modifiedBadgeLength.replace("{hex-color-to-change}", GREY);
            return modifiedColor.replace("{description_text}", "n/a");
        }
        else {
            String modifiedColor = modifiedBadgeLength.replace("{hex-color-to-change}", BLUE);
            String modifiedPass = modifiedColor.replace("{description_text}", buildDescription);
            return modifiedPass;
        }
    }
    
    public StatusImage getBuildDescriptionImage(String buildDescription) {
        // TODO don't read file everytime
        // TODO store this as a static variable in memory with the constructor
        URL imageUrl = null;
        try {
            imageUrl = new URL(
                    getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    "status/build-description-flat.svg");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(imageUrl.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String replacedImage = replaceBuildDescriptionSVG(sb.toString(), buildDescription);
        InputStream is = toInputStream(replacedImage);
        String etag = thisPluginCurrentVersion + "-status/build-description-flat.svg-" + buildDescription;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public StatusImage getWeatherImage(HealthReport healthReport) {
    	// TODO don't read file everytime
        // TODO store this as a static variable in memory with the constructor
        URL imageUrl = null;
        String imageName;
        
        int score = healthReport.getScore();
        if (score <= 20) {
            imageName = "weather/health-00to19.svg";
        } else if (score <= 40) {
        	imageName = "weather/health-20to39.svg";
        } else if (score <= 60) {
        	imageName = "weather/health-40to59.svg";
        } else if (score <= 80) {
        	imageName = "weather/health-60to79.svg";
        } else {
        	imageName = "weather/health-80plus.svg";
        }
        
        try {
            imageUrl = new URL(
                    getInstance().pluginManager.getPlugin("embeddable-badges").baseResourceURL,
                    imageName);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    	
        StringBuilder sb = null;
        try {
            sb = new StringBuilder(IOUtils.toString(imageUrl.openStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        InputStream is = toInputStream(sb.toString());
        String etag = thisPluginCurrentVersion + "-" + imageName;

        try {
            return new StatusImage(etag, is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    	return null;
    }
}