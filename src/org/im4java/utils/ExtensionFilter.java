/**************************************************************************
/* This utility-class implements a FilenameFilter based on extensions.
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
   This utility-class implements a FilenameFilter based on extensions.
   The comparison ignores the case of the extension.

   @version $Revision: 1.6 $
   @author  $Author: bablokb $
 
   @since 1.1.0
*/

public class ExtensionFilter extends BaseFilter {

  //////////////////////////////////////////////////////////////////////////////

  /**
     A filter for standard-image types (jpg, tif, png, gif and bmp).
  */

  public static final ExtensionFilter STANDARD_IMAGES =
    new ExtensionFilter("jpg","jpeg","tif","tiff","png","gif","bmp");

  //////////////////////////////////////////////////////////////////////////////

  /**
     A filter for raw-image types (the list is taken from Wikipedia, see
     http://en.wikipedia.org/wiki/Raw_image_format).
  */

  public static final ExtensionFilter RAW_IMAGES =
    new ExtensionFilter(
			"3fr", // (Hasselblad)
			"arw", // (Sony)
			"srf", // (Sony)
			"sr2", // (Sony)
			"bay", // (Casio)
			"crw", // (Canon) 
			"cr2", // (Canon)
			"cap", // (Phase_One) 
			"tif", // (Phase_One), (Kodak)
			"iiq", // (Phase_One)
			"eip", // (Phase_One)
			"dcs", // (Kodak) 
			"dcr", // (Kodak) 
			"drf", // (Kodak) 
			"k25", // (Kodak) 
			"kdc", // (Kodak) 
			"dng", // (Adobe), (Leica)
			"erf", // (Epson)
			"fff", // (Imacon)
			"mef", // (Mamiya)
			"mos", // (Leaf)
			"mrw", // (Minolta)
			"nef", // (Nikon) 
			"nrw", // (Nikon)
			"orf", // (Olympus)
			"ptx", // (Pentax)
			"pef", // (Pentax)
			"pxn", // (Logitech)
			"R3D", // (RED)
			"raf", // (Fuji)
			"raw", // (Panasonic), (Leica) 
			"rw2", // (Panasonic)
			"rwl", // (Leica) 
			"rwz", // (Rawzor)
			"x3f"  // (Sigma)
			);

  //////////////////////////////////////////////////////////////////////////////

  /**
     The HashSet of all extensions.
  */

  private HashSet<String> iExtensions = null;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Constructor (pass the extensions as array or as String-arguments).
  */

  public ExtensionFilter(String... pExt) {
    iExtensions=new HashSet<String>(pExt.length);
    for (String ext:pExt) {
      iExtensions.add(ext.toUpperCase(Locale.ENGLISH));
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Constructor (pass the extensions as a collection);
  */

  public ExtensionFilter(Collection<String> pExt) {
    iExtensions=new HashSet<String>(pExt.size());
    Iterator<String> it = pExt.iterator();
    while (it.hasNext()) {
      String ext = it.next();
      iExtensions.add(ext.toUpperCase(Locale.ENGLISH));
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Implement the accect-method of FilenameFilter

     @see java.io.FilenameFilter#accept
  */

  public boolean accept(File dir, String name) {
    File f = new File(dir.getPath()+File.separatorChar+name);
    if (f.isDirectory()) {
      return acceptDir(dir,name);
    } else {
      int extIndex = name.lastIndexOf('.');
      if (extIndex == -1) {
	return false;
      }
      String ext = name.substring(extIndex+1).toUpperCase(Locale.ENGLISH);
      return iExtensions.contains(ext);
    }
  }

}