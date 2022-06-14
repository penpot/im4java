/**************************************************************************
/* This class implements a test of the ChannelMixer.
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

import org.im4java.core.*;
import org.im4java.utils.*;

/**
   This class implements a test of the ChannelMixer.

   @version $Revision: 1.6 $
   @author  $Author: bablokb $
 
   @since 1.0.0
 */

public class TestCase4 extends AbstractTestCase {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the description of the test.
  */

  public String getDescription() {
    return "mixer";
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Main method. Just calls AbstractTestCase.runTest(), which catches and
     prints exceptions.
  */

  public static void main(String[] args) {
    TestCase4 tc = new TestCase4();
    tc.runTest(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Run the test.
  */

  public void run() throws Exception {
    System.err.println(" 4. Testing channel-mixer ...");
    IMOperation mix = new IMOperation();

    // add image to operation and save in memory-register
    mix.addImage(iImageDir+"tulip1.jpg");
    mix.write("mpr:orig");

    // convert to BW (special settings)
    mix.openOperation();
    mix.clone(0);
    mix.addOperation(new  ChannelMixer(0,0.12,0.78));
    mix.closeOperation();

    // convert to BW (emulate Ilford PANF film)
    mix.openOperation();
    mix.addImage("mpr:orig");
    mix.addOperation( ChannelMixer.ILFORD_PANF);
    mix.closeOperation();

    // append all images (same as p_append())
    mix.appendHorizontally();

    mix.addImage(iTmpImage);
    ConvertCmd convert = new ConvertCmd();
    convert.createScript(iImageDir+"mixer.sh",mix);
    convert.run(mix);
    DisplayCmd.show(iTmpImage);
  }
}
