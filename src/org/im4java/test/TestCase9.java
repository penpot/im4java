/**************************************************************************
/* This class implements a test of 
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
import org.im4java.utils.*;

/**
   This class implements a test of the NoiseFilter-class.

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase9 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "noise-filter";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase9 tc = new TestCase9();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 9. Testing noise-filter ...");
    IMOperation op = new IMOperation();

    // add image to operation
    op.addImage(iImageDir+"spathiphyllum.jpg");

    // set up NoiseFilter.Edge
    IMOperation filterOp = new IMOperation();
    filterOp.despeckle();
    NoiseFilter.Edge noiseFilter = new NoiseFilter.Edge(filterOp,2.0);

    // use NoiseFilter.Edge
    op.openOperation();
    op.clone(0);
    op.addOperation(noiseFilter);
    op.closeOperation();

    // append all images (same as p_append())
    op.appendHorizontally();

    op.addImage(iTmpImage);
    ConvertCmd convert = new ConvertCmd();
    convert.run(op);
    DisplayCmd.show(iTmpImage);
  }
}