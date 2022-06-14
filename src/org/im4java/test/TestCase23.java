/**************************************************************************
/* This class implements a test for chaining commands via im4java
/*
/* Copyright (c) 2011 by Bernhard Bablok (mail@bablokb.de)
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
import java.util.*;
import java.util.concurrent.*;

import org.im4java.core.*;
import org.im4java.process.*;

/**
   This class implements a test for chaining commands via im4java (i.e.
   piping the output of one im4java-command to a second).

   <p>Note that chaining is inherent complex, because you have to take care
   of closing the pipe between commands yourself in case of errors. If you
   pipe commands on the commandline, the shell takes care of this and you
   don't have to care about it, but with java, it's different.</p>

   @version $Revision: 1.3 $
   @author  $Author: bablokb $
 
   @since 1.2.0
 */

public class TestCase23 extends AbstractTestCase implements ProcessEventListener {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The first command in the chain.
  */

  private ConvertCmd iCmd1;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The second command in the chain.
  */

  private ConvertCmd iCmd2;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The first operation in the chain.
  */

  private IMOperation iOp1;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The second operation in the chain.
  */

  private IMOperation iOp2;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The PipedInputStream used by the pipe.
  */

  private PipedInputStream iPis;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The PipedOutputStream used by the pipe.
  */

  private PipedOutputStream iPos;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "chaining of commands";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase23 tc = new TestCase23();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println("23. Testing chaining of commands ...");

    // setup optional control-variables
    String syncType = "sync1";
    if (iArgs != null && iArgs.length > 0) {
      syncType = iArgs[0];
    }
    int induceError = 0;
    if (iArgs != null && iArgs.length > 1) {
      induceError = Integer.parseInt(iArgs[1]);
    }
    
    // set up pipes
    // in handcrafted code we could do better and save one read-write cycle:
    // read directly from the InputStream of the first process (i.e. read the 
    // output of the first process) and write to the OutputStream of the second
    // process (which is the input of the second process)

    iPis = new PipedInputStream();
    iPos = new PipedOutputStream(iPis);

    Pipe pipeOut = new Pipe(null,iPos);
    Pipe pipeIn  = new Pipe(iPis,null);

    // set up first operation and command

    iOp1 = new IMOperation();
    iOp1.addImage(iImageDir+"ipomoea.jpg");
    iOp1.resize(200,null,'%');
    if (induceError == 1) {
      iOp1.rotate();
    }
    iOp1.addImage("tif:-");               // write to stdout in tif-format

    iCmd1 = new ConvertCmd();
    iCmd1.setOutputConsumer(pipeOut);
    iCmd1.addProcessEventListener(this);  // needed if cmd1 runs asynchronously

    // set up second operation and command

    iOp2 = new IMOperation();
    iOp2.addImage("tif:-");               // read from stdin
    iOp2.rotate(90.0);
    if (induceError == 2) {
      iOp2.resize();
    }
    iOp2.addImage(iImageDir+"ipomoea-large.jpg");

    iCmd2 = new ConvertCmd();
    iCmd2.setInputProvider(pipeIn);

    // test different methods
    int rc;
    if (syncType.equals("sync1")) {
      rc = sync1();
    } else if (syncType.equals("sync2")) {
      rc = sync2();
    } else {
      System.err.println("error: illegal sync-type");
      rc = 1;
    }

    if (rc == 0) {
      DisplayCmd.show(iImageDir+"ipomoea-large.jpg");
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run cmd1 synchronously, cmd2 asynchronously.
  */

  private int sync1() throws Exception {
    iCmd2.setAsyncMode(true);
    ProcessTask pt = iCmd2.getProcessTask(iOp2);
    ExecutorService exec = Executors.newSingleThreadExecutor();

    // run commands

    exec.execute(pt);     // second command runs asynchronously but has to
    exec.shutdown();
    try {
      iCmd1.run(iOp1);        // wait for the output of the first command
    } catch (Exception e) {
      e.printStackTrace();
      iPos.close();
      iPis.close();
    }

    int rc = pt.get().getReturnCode();

    if (rc > 0) {
      System.err.println("Problem with cmd2. Trying to shutdown execution...");
      pt.cancel(true);
      exec.shutdownNow();
      System.err.println("process cmd2 terminated");
    }
    return rc;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run cmd1 asynchronously, cmd2 synchronously.
  */

  private int sync2() throws Exception {
    iCmd1.setAsyncMode(true);
    ProcessTask pt = iCmd1.getProcessTask(iOp1);
    ExecutorService exec = Executors.newSingleThreadExecutor();

    // run commands

    exec.execute(pt);     // second command runs synchronously but has to
    exec.shutdown();
    iCmd2.run(iOp2);      // wait for the output of the first command

    int rc = pt.get().getReturnCode();
    if (rc > 0) {
      System.err.println("Problem with cmd1. Trying to shutdown execution...");
      pt.cancel(true);
      exec.shutdownNow();
      System.err.println("process cmd1 terminated");
    }
    return rc;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public void processInitiated(ProcessEvent pEvent) {
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public void processStarted(ProcessEvent pEvent) {
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public void processTerminated(ProcessEvent pEvent) {
    if (pEvent.getReturnCode() != 0) {
      System.err.println("problem with cmd1");
      ImageCommand cmd = (ImageCommand) pEvent.getProcessStarter();
      ArrayList<String> errors=cmd.getErrorText();
      if (errors != null && errors.size()>0) {
	System.err.println("  printing errors:");
	for (String err:errors) {
          System.err.println("  " + err);
	}
      }  

      // close pipes to allow cmd2 to terminate
      try {
	iPos.close();
	iPis.close();
      } catch (Exception ex) {
      }
    }
  }
}