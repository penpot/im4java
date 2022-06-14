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

import org.im4java.core.*;
import org.im4java.process.*;

/**
   This class implements a test of asynchronous execution. 

   @version $Revision: 1.5 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase16 extends AbstractTestCase {

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
    TestCase16 tc = new TestCase16();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("16. Testing asynchronous execution ...");
    IMOperation op = new IMOperation();
    op.size(400,200);
    op.addImage("gradient:red",iTmpImage);

    ConvertCmd convert = new ConvertCmd();
    convert.run(op);

    DisplayCmd display = new DisplayCmd();
    display.setAsyncMode(true);

    // helper-class defined at the end of this file
    AsyncTestProcessEventListener pl = new AsyncTestProcessEventListener();
    display.addProcessEventListener(pl);
    IMOperation dispOp = new IMOperation();
    dispOp.addImage(iTmpImage);
    display.run(dispOp);

    // loop until the user finishes the operation
    for (int i=0; i<10; ++i) {
      System.err.println("sleeping for one second...");
      Thread.sleep(1000);
      if (!pl.isRunning()) {
	break;
      }
    }
    pl.destroy();
  }


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

/**
   Helper-class for method run. A real-life application would do
   something more sensible like update a GUI.
*/

class AsyncTestProcessEventListener implements ProcessEventListener {
  private Process iProcess     = null;
  private boolean isTerminated = false;

  // empty implementation
  public void processInitiated(ProcessEvent pEvent) {
    System.err.println("process initiated");
  }

  // save the started process
  public void processStarted(ProcessEvent pEvent) {
    System.err.println("process started");
    isTerminated=false;
    iProcess = pEvent.getProcess();
  }

  // print return-code or stack-trace
  public void processTerminated(ProcessEvent pEvent) {
    System.err.println("process terminated");
    synchronized(iProcess) {
      iProcess = null;
    }
    isTerminated = true;
    if (pEvent.getException() != null) {
      Exception e = pEvent.getException();
      System.err.println("Process terminated with: " + e.getMessage());
    } else {
      System.out.println("async process terminated with rc: " +
			 pEvent.getReturnCode());
    }
  }

  // check if thread is still running
  public boolean isRunning() {
    return !isTerminated;
  }

  // destroy running process (this will trigger an execption which
  // is passed to processTerminated())
  public void destroy() {
    try {
      synchronized(iProcess) {
	iProcess.destroy();
      }
    } catch (Exception e) {
    }
  }
}
}