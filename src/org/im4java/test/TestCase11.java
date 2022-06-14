/**************************************************************************
/* This class implements a test of dynamic operations.
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
   This class implements a test of dynamic operations.

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase11 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "dynamic operation";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase11 tc = new TestCase11();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("11. Testing dynamic operations ...");

    IMOperation op = new IMOperation();
    // add image to operation
    op.addImage();

    // add -despeckle only if iso > 200
    op.openOperation();
    op.clone(0);
    op.addDynamicOperation(new DynamicOperation() {
      public Operation resolveOperation(Object... pImages)
      throws IM4JavaException {
        // we just care about the first image
        if (pImages.length>0) {

          // we use identify to query the iso-setting
          IMOperation iso = new IMOperation();
          iso.ping().format("%[EXIF:ISOSpeedRatings]\n");
          String img = (String) pImages[0];
          iso.addImage(img);
          IdentifyCmd identify = new IdentifyCmd();
          ArrayListOutputConsumer output = new ArrayListOutputConsumer();
          identify.setOutputConsumer(output);
          try {
            identify.run(iso);
          } catch (Exception e) {
            throw new IM4JavaException(e);
          }

          // now read the setting
          ArrayList<String> out = output.getOutput();
          int isoValue = Integer.parseInt(out.get(0));
          if (isoValue > 200) {
            IMOperation op = new IMOperation();
            op.despeckle();
            return op;
          } else {
            return null;
          }
        } else {
          return null;
        }
      }
    });
    op.closeOperation();
    op.appendHorizontally();

    // now run the command
    op.addImage();
    ConvertCmd convert = new ConvertCmd();
    convert.run(op,iImageDir+"firelily.jpg",iTmpImage);
    DisplayCmd.show(iTmpImage);
    convert.run(op,iImageDir+"tulip1.jpg",iTmpImage);
    DisplayCmd.show(iTmpImage);
  }
}