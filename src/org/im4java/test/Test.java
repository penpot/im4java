/**************************************************************************
/* This class implements various tests of the im4java-package.
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
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

import org.im4java.core.*;
import org.im4java.process.Pipe;
import org.im4java.process.ArrayListOutputConsumer;
import org.im4java.process.ProcessEvent;
import org.im4java.process.ProcessStarter;
import org.im4java.utils.*;


/**
   This class implements various tests of the im4java-package.

   @version $Revision: 1.42 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class  Test {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The directory with test-images (platform-independent).
  */

  private static final String iImageDir = "images"+File.separatorChar;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Constructor.
   */

  public  Test() {
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main-method. You can either pass the number(s) of the test or the
     string "all" for all tests.
   */

  public static void main(String[] args) {
    if (args.length == 0 || args[0].equals("help")) {
      System.err.
      println("usage: java org.im4java.test.Test all | help | nr [...]\n\n" +
          "Available tests:\n"
      );
      try {
	int i=1;
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	while (true) {
	  String tcName="org.im4java.test.TestCase"+i;
	  Class tcClass=Class.forName(tcName);
	  TestCase tc = (TestCase) tcClass.newInstance();
	  System.err.printf("%2d: %s\n",i, tc.getDescription());
	  i++;
	}
      } catch (Exception e) {
      }
      System.err.println();
      System.exit(1);
    }

    try {
      if (args[0].equals("all")) {
	int i=1;
	while (true) {
	  String tcName="org.im4java.test.TestCase"+i;
	  Class tcClass=Class.forName(tcName);
	  TestCase tc = (TestCase) tcClass.newInstance();
	  tc.run();
	  System.err.println("\n--------------------------\n");
	  i++;
	}
      } else {
	for(String arg:args) {
	  String tcName="org.im4java.test.TestCase"+arg;
	  Class tcClass=Class.forName(tcName);
	  TestCase tc = (TestCase) tcClass.newInstance();
	  tc.run();
	}
      }
    } catch (ClassNotFoundException cfe) {
    } catch (CommandException ce) {
      ce.printStackTrace();
      ArrayList<String> cmdError = ce.getErrorText();
      for (String line:cmdError) {
        System.err.println(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}