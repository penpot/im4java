/**************************************************************************
/* This utility-class implements a FilenameFilter based on shell-globbing.
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
import java.util.regex.*;

/**
   This utility-class implements a FilenameFilter based on shell-globbing.
   This filter is a simple variant of the {@link RegexFilter} and just
   replaces a "*" with ".*" and a "." with "\.".

   @version $Revision: 1.2 $
   @author  $Author: bablokb $
 
   @since 1.1.0
*/

public class GlobbingFilter extends BaseFilter {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The regular expression for this filter.
  */

  private Pattern iPattern;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Construct the filter using the given pattern.

     @param pPattern the regular expression to use during filtering
  */

  public GlobbingFilter(String pPattern) throws PatternSyntaxException {
    // replace globbing-syntax with regex-syntax
    String glob = pPattern.replaceAll("\\.","\\\\.");
    glob=glob.replaceAll("\\*",".*");
    iPattern = Pattern.compile(glob);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Construct the filter using the given pattern and match-flags

     @param pPattern the regular expression to use during filtering
     @param pFlags   the match-flags, 
                     see {@link java.util.regex.Pattern#Pattern(String,int)}
  */

  public GlobbingFilter(String pPattern, int pFlags)
                       throws PatternSyntaxException, IllegalArgumentException {
    // replace globbing-syntax with regex-syntax
    String glob = pPattern.replaceAll("\\.","\\\\.");
    glob.replaceAll("\\*",".*");
    iPattern = Pattern.compile(glob,pFlags);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}
  */

  public boolean accept(File dir, String name) {
    File f = new File(dir.getPath()+File.separatorChar+name);
    if (f.isDirectory()) {
      return acceptDir(dir,name);
    } else {
      return iPattern.matcher(name).matches();
    }
  }
}