/**************************************************************************
/* This is the base class of all FilenameFilters in this package.
/*
/* Copyright (c) 2010 by Bernhard Bablok (mail@bablokb.de)
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

package org.im4java.utils;

import java.io.*;
import java.util.*;

/**
   This is the base class of all FilenameFilters in this package. It provides
   basic directory-management (recursion, dot-dir-policy).

   @version $Revision: 1.2 $
   @author  $Author: bablokb $
 
   @since 1.1.0
*/

public abstract class BaseFilter implements FilenameFilter {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The recursion-mode for this FilenameFilter. The default is false, i.e.
     don't recurse into subdirectories.
  */

  private boolean iRecMode=false;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Ignore directories beginning with a dot. The default is false, i.e.
     don't ignore directories starting with a dot.
  */

  private boolean iIgnoreDotDirs=false;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Set the recursion-mode for this instance.

     @param pRecMode If true, recursively load filenames
  */

  public void setRecursion(boolean pRecMode) {
    iRecMode = pRecMode;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Ignore directories beginning with a dot.

     @param pIgnoreDotDirs If true, ignore hidden directories
  */

  public void ignoreDotDirs(boolean pIgnoreDotDirs) {
    iIgnoreDotDirs = pIgnoreDotDirs;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Check if we should accept the given directory.

  */

  protected boolean acceptDir(File dir, String name) {
    File f = new File(dir.getPath()+File.separatorChar+name);
    if (! f.isDirectory()) {
      throw new IllegalStateException();
    }

    // don't accept directories if recursion is turned off
    if (!iRecMode) {
      return false;
    }

    // check if directory starts with a dot
    if (iIgnoreDotDirs && name.charAt(0) == '.') {
      return false;
    } else {
      return true;
    }
  }
}