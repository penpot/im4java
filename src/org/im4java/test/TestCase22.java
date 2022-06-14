/**************************************************************************
/* This class tests the class BatchConverter.
/*
/* Copyright (c) 2010 by Bernhard Bablok (mail@bablokb.de)
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
import java.io.*;
import org.im4java.core.*;
import org.im4java.process.*;
import org.im4java.utils.*;

/**
   This class tests the class BatchConverter.

   @version $Revision: 1.6 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase22 extends TestCase21 {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The default constructor.
  */

  public TestCase22() {
    super();
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "batch conversion";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase22 tc = new TestCase22();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(
            "22. Testing batch conversion  ...");

    // get batch-conversion type from program-arguments
    // arg[0]: source-dir
    // arg[1]: recursion-mode
    // arg[2]: conversion-mode
    BatchConverter.Mode runMode = BatchConverter.Mode.SEQUENTIAL;
    if (iArgs != null && iArgs.length > 2) {
      if (iArgs[2].equals("par")) {
	runMode = BatchConverter.Mode.PARALLEL;
      } else if (iArgs[2].equals("batch")) {
	runMode = BatchConverter.Mode.BATCH;
      }
    }

    // load all files
    List<String> images = load(); 

    // create the target-directory for the thumbnails
    String targetDir = iImageDir+".thumbnails22" + File.separatorChar;
    (new File(targetDir)).mkdir();

    // now process all files
    MyBatchConverter bc = new MyBatchConverter(runMode,images);
    bc.run(iOp,images,targetDir+"%F");
  }

  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  /**
     Subclass of BatchConverter to make use of the terminated()-method.
  */

  class MyBatchConverter extends BatchConverter {

    ////////////////////////////////////////////////////////////////////////////

    /**
       Start time of execution.
    */
    
    private long iStartTime;

    ////////////////////////////////////////////////////////////////////////////

    /**
       List of images.
    */

    private List<String> iImages;

    ////////////////////////////////////////////////////////////////////////////

    /**
       Constructor (just pass argument to superclass).

       @param pMode    The conversion mode of this BatchConverter
       @param pImages  the images we are converting
    */

    MyBatchConverter(BatchConverter.Mode pMode, List<String> pImages) {
      super(pMode);
      iImages = pImages;
      iStartTime = System.nanoTime();
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
       The terminate-callback.
    */

    public void terminated() {
      double elapsedTime = (System.nanoTime()-iStartTime)/1000000000.0;
      System.err.println("Estimated elapsed time: " + elapsedTime);
      System.err.println("conversion terminated\n");
      List<BatchConverter.ConvertException> failed = getFailedConversions();
      if (failed != null && failed.size()>0) {
	System.err.println("Failed images:");
	for (BatchConverter.ConvertException ex:failed) {
	  System.err.println("  " + iImages.get(ex.getIndex()));
	  Throwable cause = ex.getCause();
	  if (cause != null && cause.getMessage() != null) {
	    System.err.println(" " + cause.getMessage());
	  }
	}
      }
    }
  }
}
