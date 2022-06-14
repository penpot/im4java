/**************************************************************************
/* The abstract base class of all test-cases.
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
import org.im4java.core.*;

/**
   This class implements an abstract base class for all test-cases.

   @version $Revision: 1.4 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public abstract class AbstractTestCase implements TestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The directory with test-images (platform-independent).
  */

  protected static final String iImageDir = "images"+File.separatorChar;

  //////////////////////////////////////////////////////////////////////////////

  /**
     A temporary file for display of results.
  */

  protected static final String iTmpImage = iImageDir+"tmpImage.miff";

  //////////////////////////////////////////////////////////////////////////////

  /**
     Program arguments.
  */

  protected String[] iArgs;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run test and catch execptions.

     @param pArgs Additional arguments for the test.
  */

  public void runTest(String[] pArgs) {
    iArgs=pArgs;
    try {
      run();
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