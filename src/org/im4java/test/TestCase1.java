/**************************************************************************
/* This class implements the basic test of convert.
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
   This class implements the basic test of convert.

   @version $Revision: 1.7 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase1 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "simple use of convert";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase1 tc = new TestCase1();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 1. Testing convert ...");
 
    // setup optional control-variables
    boolean induceError = false;
    if (iArgs != null && iArgs.length > 0) {
      induceError = Boolean.parseBoolean(iArgs[0]);
    }

    IMOperation op = new IMOperation();
    op.addImage();
    if (!induceError) {
      // with induceError == true, we will have more images than placeholders
      op.addImage();
    }
    op.bordercolor("darkgray");
    op.border(10,10);
    op.appendHorizontally();
    op.addImage(iTmpImage);

    String[] images = new String[] {
        iImageDir+"tulip1.jpg",
        iImageDir+"tulip 2.jpg"
    };

    ConvertCmd convert = new ConvertCmd();
    convert.createScript(iImageDir+"append.sh",op);
    convert.run(op,(Object[]) images);
    DisplayCmd.show(iTmpImage);
  }
}
