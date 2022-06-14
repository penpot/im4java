/**************************************************************************
/* This class implements a test of the composite command.
/*
/* Copyright (c) 2009-2010 by Bernhard Bablok (mail@bablokb.de)
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
   This class implements a test of the composite command.

   @version $Revision: 1.4 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase7 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "composite";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase7 tc = new TestCase7();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 7. Testing composite ...");
    IMOperation op = new IMOperation();
    op.blend(50);
    op.addImage("[300x200+0+0]");  // read and crop first image
    op.addImage("[300x200+0+0]");  // read and crop second image
    op.addImage();                 // output image

    CompositeCmd composite = new CompositeCmd();
    composite.createScript(iImageDir+"composite.sh",op);
    composite.run(op,iImageDir+"rose1.jpg",iImageDir+"rose2.jpg",iTmpImage);
    DisplayCmd.show(iTmpImage);
  }
}