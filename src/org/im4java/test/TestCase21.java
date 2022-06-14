/**************************************************************************
/* This class tests the parallel processing features of im4java.
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
   This class tests the parallel processing features of im4java. It uses the
   class {@link ProcessExecutor} to limit resource-usage and implements
   the interface {@link ProcessEventListener} to control success or failure
   of individual conversions.

   @version $Revision: 1.21 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase21 extends AbstractTestCase implements ProcessEventListener {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The operation associated with this test-case.
  */

  protected IMOperation iOp;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The images processed in this test-case.
  */

  private List<String> iImages;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The default constructor.
  */

  public TestCase21() {
    // create a simple thumbnail operation
    iOp = new IMOperation();
    iOp.size(80);
    iOp.addImage();
    iOp.thumbnail(80);
    iOp.addImage();
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "parallel processes";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase21 tc = new TestCase21();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Load all files into a list.
  */

  protected List<String> load() {
    String dir=iImageDir;
    boolean recMode=false;
    if (iArgs != null && iArgs.length > 0) {
      dir = iArgs[0];
    }
    if (iArgs != null && iArgs.length > 1) {
      recMode = Boolean.parseBoolean(iArgs[1]);
    }

    // use strange ExtensionFilter to trigger some errors later on
    ExtensionFilter filter = new ExtensionFilter("jpg","sh","cmd");
    filter.setRecursion(recMode);
    filter.ignoreDotDirs(true);
    FilenameLoader loader =  new FilenameLoader(filter);
    return loader.loadFilenames(dir);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("21. Testing parallel processes ...");

    // load all files
    iImages = load(); 

    // create the target-directory for the thumbnails
    String targetDir = iImageDir+".thumbnails" + File.separatorChar;
    (new File(targetDir)).mkdir();

    // create FilenamePatternResolver
    FilenamePatternResolver fpr = 
      new FilenamePatternResolver(targetDir+"%F");

    // now process all files
    ProcessExecutor exec = new ProcessExecutor();
    ProcessStarter.setPIDCounter(0);     // this is the default
    for (String img:iImages) {
      System.out.println("processing " + img);
      ConvertCmd cmd = new ConvertCmd();
      cmd.addProcessEventListener(this);
      ProcessTask pt = cmd.getProcessTask(iOp,img,fpr.createName(img));
      exec.execute(pt);
    }
    exec.shutdown();
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public void processInitiated(ProcessEvent pEvent) {
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public void processStarted(ProcessEvent pEvent) {
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public void processTerminated(ProcessEvent pEvent) {
    if (pEvent.getReturnCode() != 0) {
      System.err.println("conversion failed for " + 
                                            iImages.get(pEvent.getPID()));
      ImageCommand cmd = (ImageCommand) pEvent.getProcessStarter();
      ArrayList<String> errors=cmd.getErrorText();
      if (errors != null && errors.size()>0) {
	System.err.println("  printing errors:");
	for (String err:errors) {
          System.err.println("  " + err);
	}
      }    
    } else {
      System.err.println("conversion successful for " +
                                            iImages.get(pEvent.getPID()));
    }
  }
}
