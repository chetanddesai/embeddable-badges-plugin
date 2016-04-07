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
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;




public class ImageResolver {

    public final StatusImage[] buildResultImages;
    public final StatusImage[] codeCoverageImages;

    public ImageResolver() throws IOException{
        // shields.io "flat" style (new default from Feb 1 2015)
        buildResultImages = new StatusImage[] {
                new StatusImage("build-failing-red-flat.svg"),
                new StatusImage("build-unstable-yellow-flat.svg"),
                new StatusImage("build-passing-brightgreen-flat.svg"),
                new StatusImage("build-running-blue-flat.svg"),
                new StatusImage("build-aborted-lightgrey-flat.svg"),
                new StatusImage("build-unknown-lightgrey-flat.svg")
        };
        // shields.io code-coverage-badges
        codeCoverageImages = new StatusImage[] {
                new StatusImage("build-coverage-flat.svg")
        };
    }

    public void getCodeCoverage(){
        // TO DO:
        // check if they are using Clover or Cobertura or Junit
        // Get the results.
        // If the results is in a range set the replace hex color in modifiedColor
        // paste results to the modifiedPercentage
        // need to make the .replace continue to modify the same file. need to add a marker in the orig .svg
    }
    //Run Method
    public StatusImage getCoverageImage(Integer codeCoverage) {
    	// cobertura or clover
			return codeCoverageImages[0];
    }
    //Project Method
    public StatusImage getCoverageImage() {
      // cobertura or clover
      return codeCoverageImages[0];
    }



    public void modifySVG() throws FileNotFoundException, IOException {
        String filePath = new File("").getAbsolutePath();
        filePath = filePath + "/src/main/webapp/status/build-coverage-flat.svg";
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            String modifiedColor = everything.replace("{hex-color-to-change}", "#97CA00");
            String modifiedPercentage = modifiedColor.replace("{code-coverage-to-change}", "90");
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(modifiedPercentage);
            bw.close();
            System.out.println(modifiedPercentage);
        } finally {
            br.close();
        }
    }

    public StatusImage getBuildResultImage(BallColor color) {
        if (color.isAnimated())
            return buildResultImages[3];

        switch (color) {
        case RED:
            return buildResultImages[0];
        case YELLOW:
            return buildResultImages[1];
        case BLUE:
            return buildResultImages[2];
        case ABORTED:
            return buildResultImages[4];
        default:
            return buildResultImages[5];

        }
    }

}
