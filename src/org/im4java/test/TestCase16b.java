/**************************************************************************
/* This class implements a test of asynchronous execution. 
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
import java.util.concurrent.*;

import org.im4java.core.*;
import org.im4java.process.*;

/**
   This class implements a test of asynchronous execution. It will create
   three images, display them and cancel all running processes after
   at most 10 seconds. It tests the {@link ThreadPoolExecutor#awaitTermination}
   and {@link ProcessExecutor#shutdownNow} methods.

   @version $Revision: 1.2 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase16b extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "asynchronous execution";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase16b tc = new TestCase16b();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("16b. Testing asynchronous execution ...");

    // create three images
    IMOperation op = new IMOperation();
    op.size(400,200);
    op.addImage("gradient:red",iImageDir+"red-gradient.jpg");
    ConvertCmd convert = new ConvertCmd();
    convert.run(op);

    op = new IMOperation();
    op.size(400,200);
    op.addImage("gradient:green",iImageDir+"green-gradient.jpg");
    convert.run(op);

    op = new IMOperation();
    op.size(400,200);
    op.addImage("gradient:blue",iImageDir+"blue-gradient.jpg");
    convert.run(op);

    // show all images using a display-command (max two at a time)
    ProcessExecutor exec = new ProcessExecutor(2);

    DisplayCmd display = new DisplayCmd();
    IMOperation dispOp = new IMOperation();
    dispOp.addImage(iImageDir+"red-gradient.jpg");
    exec.execute(display.getProcessTask(dispOp));

    display = new DisplayCmd();
    dispOp = new IMOperation();
    dispOp.addImage(iImageDir+"green-gradient.jpg");
    exec.execute(display.getProcessTask(dispOp));

    display = new DisplayCmd();
    dispOp = new IMOperation();
    dispOp.addImage(iImageDir+"blue-gradient.jpg");
    exec.execute(display.getProcessTask(dispOp));

    exec.shutdown();

    // now wait 10 seconds to finish
    System.err.println("waiting 10 seconds for processes to terminate ...");
    if (exec.awaitTermination(10,TimeUnit.SECONDS)) {
      System.err.println("processes terminated on their own");
    } else {
      System.err.println("trying to cancel all running processes ...");
      exec.shutdownNow();
    }
  }
}

