/**************************************************************************
/* This class implements a test for the IM compare command
/*
/* Copyright (c) 2012 by Bernhard Bablok (mail@bablokb.de)
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
import org.im4java.utils.*;

/**
   This class implements a test for the IM compare command.

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.3.0
 */

public class TestCase24 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "compare";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase24 tc = new TestCase24();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 24. Testing compare ...");

    String metric="RMSE";   // root mean squared (normalized root mean squared)
    if (iArgs != null && iArgs.length > 0) {
      metric = iArgs[0];    // alternative metric passed in as argument
    }

    IMOperation op = new IMOperation();

    // sharpen the firelily
    op.addImage(iImageDir+"firelily.jpg");           // input image
    op.unsharp(0.0,1.0);
    op.addImage(iImageDir+"firelily-sharpen.jpg");   // output image
    ConvertCmd convert = new ConvertCmd();
    convert.run(op);

    // run the diff (metric-output goes to stderr)
    CompareCmd  compare = new CompareCmd();
    ArrayListErrorConsumer errorConsumer = new ArrayListErrorConsumer();
    compare.setErrorConsumer(errorConsumer);

    IMOperation cmpOp = new IMOperation();
    cmpOp.addImage();
    cmpOp.addImage();
    cmpOp.metric(metric);
    cmpOp.addImage();
    System.err.print(metric + ": ");
    compare.run(cmpOp,iImageDir+"firelily.jpg",
                      iImageDir+"firelily-sharpen.jpg",
                      iImageDir+"firelily-diff.jpg");

    // dump output
    ArrayList<String> cmdOutput = errorConsumer.getOutput();
    for (String line:cmdOutput) {
      System.out.println(line);
    }

    // show diff-image
    DisplayCmd.show(iImageDir+"firelily-diff.jpg");
  }
}