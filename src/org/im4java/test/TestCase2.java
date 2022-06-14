/**************************************************************************
/* This class implements a test of operations and suboperations.
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
import org.im4java.core.*;

/**
   This class implements a test of operations and suboperations.

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase2 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "operation and sub-operations";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase2 tc = new TestCase2();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 2. Testing operations and suboperations ...");
    IMOperation top = new IMOperation();

    // first (top) line
    top.addImage(iImageDir+"rose1.jpg").addImage(iImageDir+"rose2.jpg");
    top.appendHorizontally();

    // second (bottom) line
    IMOperation bottom = new IMOperation();
    bottom.addImage(iImageDir+"tulip1.jpg").addImage(iImageDir+"tulip2.jpg");
    bottom.appendHorizontally();

    // assemble lines
    IMOperation op = new IMOperation();
    op.addSubOperation(top);
    op.addSubOperation(bottom);
    op.appendVertically();

    op.addImage(iTmpImage);
    ConvertCmd convert = new ConvertCmd();
    convert.run(op);
    DisplayCmd.show(iTmpImage);
  }
}