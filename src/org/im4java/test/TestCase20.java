/**************************************************************************
/* This class implements a test of setting search PATHs.
/*
/* Copyright (c) 2009 by Bernhard Bablok (mail@bablokb.de)
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
import org.im4java.process.*;

/**
   This class implements a test of setting search PATHs.

   @version $Revision: 1.2 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase20 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "setting search PATHs";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase20 tc = new TestCase20();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("20. Testing search path ...");

    String searchPath=System.getProperty("im4java.testSearchPath.path");
    if (searchPath == null) {
      System.err.println(
         "\nSkipping this test since search path is not defined.\n" +
         "Set the system-property im4java.testSearchPath.path to\n" +
         "your search path:\n" +
         "\tpass JAVA_OPTS=-Dim4java.testSearchPath.path=... to \"make test\" or\n" +
         "\texport JAVA_OPTS=-Dim4java.testSearchPath.path=...\n\n"
      );
      return;
    }

    ETOperation op = new ETOperation();
    op.getTags("Filename","ImageWidth","ImageHeight","FNumber",
                                                           "ExposureTime","iso");
    op.addImage();

    // setup command and execute it (capture output)
    ArrayListOutputConsumer output = new ArrayListOutputConsumer();
    ExiftoolCmd et = new ExiftoolCmd();
    et.setSearchPath(searchPath);
    et.setOutputConsumer(output);
    et.run(op,iImageDir+"spathiphyllum.jpg");

    // dump output
    System.out.println("--- using per object search path ---");
    ArrayList<String> cmdOutput = output.getOutput();
    for (String line:cmdOutput) {
      System.out.println(line);
    }

    // use global path
    et.setSearchPath(null);
    ProcessStarter.setGlobalSearchPath(searchPath);
    cmdOutput.clear();
    et.run(op,iImageDir+"spathiphyllum.jpg");

    // dump output
    System.out.println("--- using global search path ---");
    cmdOutput = output.getOutput();
    for (String line:cmdOutput) {
      System.out.println(line);
    }
  }
}
