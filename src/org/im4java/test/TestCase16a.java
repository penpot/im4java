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
   This class implements a test of asynchronous execution. 

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase16a extends AbstractTestCase {

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
    TestCase16a tc = new TestCase16a();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("16a. Testing asynchronous execution ...");
    IMOperation op = new IMOperation();
    op.size(400,200);
    op.addImage("gradient:red",iTmpImage);

    ConvertCmd convert = new ConvertCmd();
    convert.run(op);

    DisplayCmd display = new DisplayCmd();
    IMOperation dispOp = new IMOperation();
    dispOp.addImage(iTmpImage);
    ProcessTask pt = display.getProcessTask(dispOp);

    // get executor
    ExecutorService exec = Executors.newSingleThreadExecutor();
    exec.execute(pt);
    exec.shutdown();

    String waitType = "sleep";
    if (iArgs != null && iArgs.length > 0) {
      waitType = iArgs[0];
    }

    // three wait-and-terminate implementations
    if (waitType.equals("sleep")) {
      waitSleep(exec,pt);
    } else if (waitType.equals("get")) {
      waitGet(pt);
    } else {
      waitWait(exec,pt);
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Use sleep-loop and cancel to wait-and-terminate.
  */

  private void waitSleep(ExecutorService pExec, ProcessTask pPT) 
                                                   throws InterruptedException {
    // loop until the user finishes the operation
    for (int i=0; i<10; ++i) {
      System.err.println("sleeping for one second...");
      Thread.sleep(1000);
      if (pExec.isTerminated()) {
	break;
      }
    }

    // kill process and terminate
    if (!pExec.isTerminated()) {
      System.err.println("Trying to shutdown execution...");
      pPT.cancel(true);
      pExec.shutdownNow();
    } else {
      System.err.println("already terminated");
    }
    System.err.println("Terminated execution");
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Wait using the get-method of the ProcessTask.
  */

  private void waitGet(ProcessTask pPT) throws Exception {
    try {
      System.err.println("waiting 10 seconds for process to finish with get ...");
      ProcessEvent pe = pPT.get(10,TimeUnit.SECONDS);
      System.err.println("process ended with " + pe.getReturnCode());
    } catch (TimeoutException toe) {
      System.err.println("canceling process ...");
      pPT.cancel(true);
      System.err.println("process terminated");
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Wait using the awaitTermination-method of the ExecutorService.
  */

  private void waitWait(ExecutorService pExec, ProcessTask pPT) throws Exception {
    System.err.
    println("waiting 10 seconds for process to finish with awaitTermination ...");
    if (pExec.awaitTermination(10,TimeUnit.SECONDS)) {
      System.err.println("process ended with " + pPT.get().getReturnCode());
    } else {
      System.err.println("Trying to shutdown execution...");
      pPT.cancel(true);
      pExec.shutdownNow();
      System.err.println("process terminated");
    }
  }
}

