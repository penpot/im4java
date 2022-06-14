/**************************************************************************
/* This class implements a test of writing BufferedImages.
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
import java.awt.image.*;
import javax.imageio.ImageIO;
import org.im4java.core.*;

/**
   This class implements a test of writing BufferedImages.

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase13 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "Writing BufferedImage";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase13 tc = new TestCase13();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("13. Testing writing BufferedImages ...");

    // use first parameter as output-format (default: png)
    String outputFormat = "png";
    if (iArgs != null && iArgs.length > 0) {
      outputFormat = iArgs[0];
    }

    IMOperation op = new IMOperation();
    op.addImage(iImageDir+"tulip2.jpg");     // input
    op.blur(2.0).paint(10.0);
    op.addImage(outputFormat+":-");          // output: stdout


    // set up command
    ConvertCmd convert = new ConvertCmd();
    Stream2BufferedImage s2b = new Stream2BufferedImage();
    convert.setOutputConsumer(s2b);
    convert.run(op);

    // save result to disk
    BufferedImage img = s2b.getImage();
    ImageIO.write(img,"PNG",new File(iImageDir+"tmpfile.png"));


    // show result
    DisplayCmd.show(iImageDir+"tmpfile.png");
    (new File(iImageDir+"tmpfile.png")).delete();
  }
}