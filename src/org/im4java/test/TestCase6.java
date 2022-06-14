/**************************************************************************
/* This class implements a test of the identify command.
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
   This class implements a test of the identify command.

   @version $Revision: 1.2 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase6 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "identify";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase6 tc = new TestCase6();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 6. Testing identify ...");
    IMOperation op = new IMOperation();
    // op.verbose();
    op.addImage(2);

    IdentifyCmd identify = new IdentifyCmd();
    System.out.println("   first run:");
    identify.run(op,iImageDir+"rose1.jpg",iImageDir+"rose2.jpg");

    System.out.println("   second run:");
    ArrayListOutputConsumer output = new ArrayListOutputConsumer();
    identify.setOutputConsumer(output);
    identify.run(op,iImageDir+"tulip1.jpg",iImageDir+"tulip2.jpg");
    ArrayList<String> cmdOutput = output.getOutput();
    for (String line:cmdOutput) {
      System.out.println(line);
    }
  }
}