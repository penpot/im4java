/**************************************************************************
/* This class implements a test of reading from stdin and piping to stdout.
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
   This class implements a test of reading from stdin and piping to stdout.

   @version $Revision: 1.2 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase10 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "piping";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase10 tc = new TestCase10();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("10. Testing pipes ...");

    IMOperation op = new IMOperation();
    op.addImage("-");                   // read from stdin
    op.addImage("tif:-");               // write to stdout in tif-format

    // set up pipe(s): you can use one or two pipe objects
    FileInputStream fis = new FileInputStream(iImageDir+"ipomoea.jpg");
    FileOutputStream fos = new FileOutputStream(iImageDir+"ipomoea.tif");
    // Pipe pipe = new Pipe(fis,fos);
    Pipe pipeIn  = new Pipe(fis,null);
    Pipe pipeOut = new Pipe(null,fos);

    // set up command
    ConvertCmd convert = new ConvertCmd();
    convert.setInputProvider(pipeIn);
    convert.setOutputConsumer(pipeOut);
    convert.run(op);
    fis.close();
    fos.close();

    // show result
    DisplayCmd.show(iImageDir+"ipomoea.tif");
  }
}