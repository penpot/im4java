/**************************************************************************
/* This helper class reads an InputStream and creates a BufferedImage.
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

package org.im4java.core;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;


import org.im4java.process.OutputConsumer;

/**
   This helper class reads an InputStream and creates a BufferedImage.

   @version $Revision: 1.4 $
   @author  $Author: bablokb $
 
   @since 0.95
*/

public class Stream2BufferedImage implements OutputConsumer {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The BufferedImage.
  */

  private BufferedImage iImage;

  //////////////////////////////////////////////////////////////////////////////

  /**
   * The constructor
   */

  public Stream2BufferedImage() {
  }

  //////////////////////////////////////////////////////////////////////////////
    
  /**
     Read the output of the command and create a BufferedImage
     
     @see org.im4java.process.OutputConsumer#consumeOutput(java.io.InputStream)
  */
    
  public void consumeOutput(InputStream pInputStream) throws IOException {
    ImageInputStream iis = ImageIO.createImageInputStream(pInputStream);
    if (iis != null) {
      Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
      if (iter.hasNext()) {
	ImageReader reader = iter.next();
	reader.setInput(iis);
	iImage = reader.read(0);
	iis.close();
	reader.dispose();
      } else {
	throw new IllegalStateException("no ImageReader for given format");
      }
    } else {
      throw new IllegalStateException("failed to create ImageInputStream");
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Return the BufferedImage.
   */

  public BufferedImage getImage() {
    return iImage;
  }
}
