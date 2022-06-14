/**************************************************************************
/* Create a thumbnail from a given image.
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

package org.im4java.examples;

import java.util.*;

import org.im4java.core.*;
import org.im4java.script.*;

/**
   Create a thumbnail from a given Image

   @version $Revision: 1.5 $
   @author  $Author: bablokb $
 */

public class Thumbnail {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method.
  */

  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("usage: org.im4java.examples.Thumbnail infile outfile");
      System.exit(1);
    }

    IMOperation op = new IMOperation();
    op.addImage();
    op.thumbnail(100,100,'^');
    op.gravity("center");
    op.extent(100,100);
    op.addImage();

    ConvertCmd convert = new ConvertCmd();
    // convert.setSearchPath("/usr/bin");

    try {
      convert.createScript("thumbnail.sh",op);
      convert.run(op,(Object[]) args);
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