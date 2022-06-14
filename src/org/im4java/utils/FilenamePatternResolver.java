/**************************************************************************
/* This utility-class creates filenames from an input file and a template.
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

/**
   This utility-class creates filenames from an input file and a template.

   <p>
     The template recognizes the following escape-sequences:
     <ul>
       <li>%P: full path component of the source-file</li>
       <li>%p: last path component of the source-file</li>
       <li>%F: full filename of the source-file (without path)</li>
       <li>%f: filename of the source-file without path and extension</li>
       <li>%e: extension of the source-file</li>
       <li>%D: drive-letter of the source-file (on windows-systems). Not
       available for source-files with UNC-name.
       </li>
     </ul>
   </p>

   @version $Revision: 1.5 $
   @author  $Author: bablokb $
 
   @since 1.1.0
*/

public class FilenamePatternResolver {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The template of this FilenamePatternResolver.
  */

  private String iTemplate;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The escape-character of the template
  */

  private char iEscChar;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Constructor (uses the default escape-char of %).

     @param pTemplate The template for this FilenamePatternResolver
  */

  public FilenamePatternResolver(String pTemplate) {
    this(pTemplate,'%');
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Constructor.

     @param pTemplate The template for this FilenamePatternResolver
     @param pEscChar  The escape-character used in pTemplate
  */

  public FilenamePatternResolver(String pTemplate, char pEscChar) {
    iTemplate = pTemplate;
    iEscChar  = pEscChar;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Generate a filename based on the input-file and the template.

     @param  pFilename  The input-filename
     @return The new filename based on the template as a String
  */

  public String createName(String pFilename) {
    StringBuilder result = new StringBuilder(iTemplate);
    File pFile = new File(pFilename);

    int index = result.indexOf(iEscChar+"D");
    if (index > -1) {
      result.replace(index,index+2,getDrive(pFile));
    }
    index = result.indexOf(iEscChar+"P");
    if (index > -1) {
      result.replace(index,index+2,getFullpath(pFile));
    }
    index = result.indexOf(iEscChar+"p");
    if (index > -1) {
      result.replace(index,index+2,getPath(pFile));
    }
    index = result.indexOf(iEscChar+"F");
    if (index > -1) {
      result.replace(index,index+2,getFullname(pFile));
    }
    index = result.indexOf(iEscChar+"f");
    if (index > -1) {
      result.replace(index,index+2,getName(pFile));
    }
    index = result.indexOf(iEscChar+"e");
    if (index > -1) {
      result.replace(index,index+2,getExt(pFile));
    }
    return result.toString();
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the drive-letter of the given file. On none-windows systems, this
     method always returns the empty string. Note that this method does not
     work for UNC-filenames.

     @param  pFile  The input file
     @return The drive-letter or the empty string
  */

  public String getDrive(File pFile) {
    if (System.getProperty("os.name").startsWith("Windows")) {
      String path = pFile.getAbsolutePath();
      if (path == null) {
	return "";
      } else {
	return path.substring(0,1);
      }
    } else {
      return "";
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the full pathname of the given file

     @param  pFile  The input file
     @return The full pathname
  */

  public String getFullpath(File pFile) {
    String path = pFile.getParent();
    if (path == null) {
      return "";
    }
    return path;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the pathname of the given file

     @param  pFile  The input file
     @return The pathname
  */

  public String getPath(File pFile) {
    String path = pFile.getParent();
    if (path == null) {
      return "";
    }
    int index = path.lastIndexOf(File.separatorChar);
    if (index==path.length()-1) {
      index = path.lastIndexOf(File.separatorChar,index);
    }
    return path.substring(index+1);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the full filename of the given file

     @param  pFile  The input file
     @return The full filename
  */

  public String getFullname(File pFile) {
    String name = pFile.getName();
    if (name == null) {
      return "";
    } else {
      return name;
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the filename of the given file (filename without extension).

     @param  pFile  The input file
     @return The filename
  */

  public String getName(File pFile) {
    String name = pFile.getName();
    if (name == null) {
      return "";
    } else {
      int index = name.lastIndexOf('.');
      if (index > -1) {
	return name.substring(0,index);
      } else {
	return name;
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the extension of the given file

     @param  pFile  The input file
     @return The filename
  */

  public String getExt(File pFile) {
    String name = pFile.getName();
    if (name == null) {
      return "";
    } else {
      int index = name.lastIndexOf('.');
      if (index > -1) {
	return name.substring(index+1);
      } else {
	return "";
      }
    }
  }
}