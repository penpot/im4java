/**************************************************************************
/* This utility-class loads selected filenames of a directory into a list.
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
   This utility-class loads selected filenames of a directory into a list.

   @version $Revision: 1.8 $
   @author  $Author: bablokb $
 
   @since 1.1.0
*/

public class FilenameLoader {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The FilenameFilter for this FilenameLoader.
  */

  private FilenameFilter iFilter = null;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The default constructor (no filters, no recursion).
  */

  public FilenameLoader() {
    this(null);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Create an instance with given filter and recursion-mode

     @param pFilter  The FilenameFilter to use
  */

  public FilenameLoader(FilenameFilter pFilter) {
    iFilter  = pFilter;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Set the filter for this instance.

     @param pFilter The FilenameFilter to use
  */

  public void setFilter(FilenameFilter pFilter) {
    iFilter = pFilter;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Load filenames in the given directory.

     @param   pDir The directory to search
     @return       List of filenames
  */

  public List<String> loadFilenames(String pDir) {
    List<String> list = new LinkedList<String>();
    load(new File(pDir),list);
    return list;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Load files in the given directory and store the results in the
     given list.

     @param pDir    The directory to search
     @param pNames  The result-list 
  */

  public void load(File pDir, List<String> pNames) {
    String[] names = pDir.list(iFilter);
    String path = pDir.getPath()+File.separatorChar;
    File file;

    // add files of current directory
    for (String name:names) {
      file = new File(path+name);
      if (!file.isDirectory()) {
	pNames.add(file.getPath());
      }
    }

    // now add files in subdirs
    for (String name:names) {
      file = new File(path+name);
      if (file.isDirectory()) {
	load(file,pNames);
      }
    }
  }
}