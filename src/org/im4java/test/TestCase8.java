/**************************************************************************
/* This class implements a test of the Info-class.
/*
/* Copyright (c) 2009-2013 by Bernhard Bablok (mail@bablokb.de)
/*
/* This program is free software; you can redistribute it and/or modify
/* it under the terms of the GNU Library General Public License as published
/* by  the Free Software Foundation; either version 2 of the License or
/* (at your option) any later version.
/*
/* This program is distributed in the hope that it will be useful, but
/* WITHOUT ANY WARRANTY; without even the implied warranty of
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/* GNU Library General Public License for more details.
/*
/* You should have received a copy of the GNU Library General Public License
/* along with this program; see the file COPYING.LIB.  If not, write to
/* the Free Software Foundation Inc., 59 Temple Place - Suite 330,
/* Boston, MA  02111-1307 USA
/**************************************************************************/

package org.im4java.test;

import java.util.*;
import org.im4java.core.*;

/**
   This class implements a test of the Info-class.

   The first argument is an image filename. If the image-name is "-" or
   "format:-", this test-case expects the file from stdin.

   The second argument is either true or false and controls if the program
   prints only basic image-information or the complete set available.

   @version $Revision: 1.11 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase8 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "info";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase8 tc = new TestCase8();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 8. Testing info ...");

    // use first parameter as input image
    String imgName = iImageDir+"multi-scene.gif";
    if (iArgs != null && iArgs.length > 0) {
      imgName = iArgs[0];
    }

    // check if basic-info is requested
    boolean onlyBasic=false;
    if (iArgs != null && iArgs.length > 1) {
      onlyBasic = Boolean.parseBoolean(iArgs[1]);
    }

    // create and output complete information
    if (!onlyBasic) {
      showCompleteInfo(imgName);
      if (imgName.equals("-") || imgName.endsWith(":-")) {
	return;
      }
    }

    // create and output basic information
    showBasicInfo(imgName);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Show complete-info for a file.

     @since 1.4.0
  */

  private void showCompleteInfo(String pImgName) throws InfoException {
    Info imageInfo;
    if (pImgName.equals("-") || pImgName.endsWith(":-")) {
      imageInfo = new Info(pImgName,System.in);
    } else {
      imageInfo = new Info(pImgName);
    }

    // dump all that is available
    Enumeration<String> props = imageInfo.getPropertyNames();
    if (props == null) {
      return;
    }

    System.out.println("\n==================================");
    System.out.println("complete info (all attributes)");
    System.out.println("==================================\n");

    while (props.hasMoreElements()) {
      String prop=props.nextElement();
      System.out.println(prop+"="+imageInfo.getProperty(prop));
    }
    System.out.println("\n==================================");
    System.out.println("some attributes for every scene");
    System.out.println("==================================\n");

    int n = imageInfo.getSceneCount();
    for (int i=0; i<n; ++i) {
      System.out.println(i+": geometry=" + imageInfo.getProperty("Geometry",i));
      System.out.println(i+": page geometry=" + 
			 imageInfo.getProperty("Page geometry",i));
    }

    System.out.println("\n==================================");
    System.out.println("basic information from complete info");
    System.out.println("==================================\n");

    // check that basic information is also available
    dumpBasicInfo(imageInfo);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Show basic-info for a file.

     @since 1.4.0
  */

  private void showBasicInfo(String pImgName) throws InfoException {
    System.out.println("\n=================");
    System.out.println("basic information");
    System.out.println("=================\n");
    Info imageInfo;
    if (pImgName.equals("-") || pImgName.endsWith(":-")) {
      imageInfo = new Info(pImgName,System.in,true);
    } else {
      imageInfo = new Info(pImgName,true);
    }
    dumpBasicInfo(imageInfo);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Dump all the basic image-information
  */

  private void dumpBasicInfo(Info pImageInfo) throws InfoException {
    System.out.println("Format: " + pImageInfo.getImageFormat());
    System.out.println("Width: " + pImageInfo.getImageWidth());
    System.out.println("Height: " + pImageInfo.getImageHeight());
    System.out.println("Geometry: " + pImageInfo.getImageGeometry());
    System.out.println("PageWidth: " + pImageInfo.getPageWidth());
    System.out.println("PageHeight: " + pImageInfo.getPageHeight());
    System.out.println("PageGeometry: " + pImageInfo.getPageGeometry());
    System.out.println("Depth: " + pImageInfo.getImageDepth());
    System.out.println("Class: " + pImageInfo.getImageClass());

    // and the same for all scenes
    int n = pImageInfo.getSceneCount();
    for (int i=0; i<n; ++i) {
      System.out.println("--------------- " + i + " -------------------");
      System.out.println("Format: " + pImageInfo.getImageFormat(i));
      System.out.println("Width: " + pImageInfo.getImageWidth(i));
      System.out.println("Height: " + pImageInfo.getImageHeight(i));
      System.out.println("Geometry: " + pImageInfo.getImageGeometry(i));
      System.out.println("PageWidth: " + pImageInfo.getPageWidth(i));
      System.out.println("PageHeight: " + pImageInfo.getPageHeight(i));
      System.out.println("PageGeometry: " + pImageInfo.getPageGeometry(i));
      System.out.println("Depth: " + pImageInfo.getImageDepth(i));
      System.out.println("Class: " + pImageInfo.getImageClass(i));
    }
  }
}