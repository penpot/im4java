/**************************************************************************
/* This class implements a test of the dcraw-command.
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

import java.io.*;
import org.im4java.core.*;
import org.im4java.process.*;

/**
   This class implements a test of the dcraw-command.

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase19 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "dcraw";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase19 tc = new TestCase19();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("19. Testing dcraw ...");
    String outfile=iImageDir+"rawfile.tif";
    String infile=System.getProperty("im4java.testDcraw.infile");
    if (infile == null) {
      System.err.println(
         "\nSkipping this test since input-file is not defined.\n" +
         "Set the system-property im4java.testDcraw.infile to\n" +
         "your input-file for dcraw:\n" +
         "\tpass JAVA_OPTS=-Dim4java.testDcraw.infile=... to \"make test\" or\n" +
         "\texport JAVA_OPTS=-Dim4java.testDcraw.infile=...\n\n"
      );
      return;
    }

    DCRAWOperation op = new DCRAWOperation();
    op.halfSize();
    op.createTIFF();
    op.setGamma(2.4,12.92);         // use sRGB gamma
    op.write2stdout();
    op.addImage(infile);            // input-filename

    // create pipe for output
    FileOutputStream fos = new FileOutputStream(outfile);
    Pipe pipeOut = new Pipe(null,fos);

    // set up and run command
    DcrawCmd dcraw = new DcrawCmd();
    dcraw.setOutputConsumer(pipeOut);
    dcraw.run(op);
    fos.close();

    DisplayCmd.show(outfile);
    (new File(outfile)).delete();
  }
}