/**************************************************************************
/* This class implements various batch-conversion methods.
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

import org.im4java.core.*;
import org.im4java.process.*;

/**
   This class implements various batch-conversion methods.

     <ul>
       <li>SEQUENTIAL: run commands sequentially for all images
           (this mode is mainly for debugging and benchmarking)
       </li>
       <li>PARALLEL: run commands in parallel using a 
       {@link ProcessExecutor}
       </li>
       <li>BATCH: convert images at once with a single command.</li>
     </ul>
     <p>Note that not every mode will work with every operation. The
       batch-mode will typically fail with all operations using ImageMagick's
       image-sequence operators (e.g. -clone).
     </p>

     <p>Sequential and parallel-mode will convert all images and will
       save the indices of failed conversions. You can query these
       indices with the method {@link #getFailedConversions}. The batch mode
       behaves differently: it is a all-or-nothing conversion.
     </p>

   @version $Revision: 1.8 $
   @author  $Author: bablokb $
 
   @since 1.1.0
*/

public class BatchConverter extends ProcessExecutor {

  //////////////////////////////////////////////////////////////////////////////

  /**
     Enumeration-type for the conversion mode:
     <ul>
       <li>SEQUENTIAL: run commands sequentially for all images
           (this mode is mainly for debugging and benchmarking)
       </li>
       <li>PARALLEL: run commands in parallel using a 
       {@link ProcessExecutor}
       </li>
       <li>BATCH: convert images at once with a single command.</li>
     </ul>
  */

  public enum Mode {SEQUENTIAL, PARALLEL, BATCH};

  //////////////////////////////////////////////////////////////////////////////

  /**
     The conversion-mode.
  */

  private Mode iMode = null;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The list of exceptions of failed conversions. For Mode.BATCH there will
     be only one exception and the index is not relevant.
  */

  private List<ConvertException> iExceptions = null;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The constructor.

     @param pMode The conversion mode of this BatchConverter
  */

  public BatchConverter(Mode pMode) {
    iMode = pMode;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Convert the given images with the given {@link Operation}. 

     @param pOp            the conversion-operation
     @param pImages        List of images to convert
     @param pTargetPattern Pattern for the target-files.
                           See {@link FilenamePatternResolver} for details
  */

  public void run(Operation pOp, List<String> pImages, String pTargetPattern)
         throws Exception, IOException, InterruptedException, IM4JavaException {
    // check if anything needs to be done
    if (pImages.isEmpty()) {
      return;
    }
    // reinitialize list of failed conversions
    iExceptions = new LinkedList<ConvertException>();

    try {
      switch (iMode) {
        case SEQUENTIAL: runSeq(pOp,pImages,pTargetPattern); break;
        case PARALLEL:   runPar(pOp,pImages,pTargetPattern); break;
        case BATCH:      runBatch(pOp,pImages,pTargetPattern); break;
      }
      shutdown();
    } catch (Exception e) {
      shutdown();
      throw e;
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Convert the given images sequentially with the given {@link Operation}. 

     @param pOp            the conversion-operation
     @param pImages        List of images to convert
     @param pTargetPattern Pattern for the target-files.
                           See {@link FilenamePatternResolver} for details
  */

  private void runSeq(Operation pOp, List<String> pImages, String pTargetPattern) 
                     throws IOException, InterruptedException, IM4JavaException {
    ConvertCmd cmd = new ConvertCmd();
    FilenamePatternResolver fpr = new FilenamePatternResolver(pTargetPattern);

    int i=0;
    for (String img:pImages) {
      try {
	cmd.run(pOp,img,fpr.createName(img));
      } catch (Exception e) {
	addException(i,e);
      } finally {
	i++;
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Convert the given images in parallel with the given {@link Operation}. 

     @param pOp            the conversion-operation
     @param pImages        List of images to convert
     @param pTargetPattern Pattern for the target-files.
                           See {@link FilenamePatternResolver} for details
  */

  private void runPar(Operation pOp, List<String> pImages, String pTargetPattern)
                     throws IOException, InterruptedException, IM4JavaException {
    FilenamePatternResolver fpr = new FilenamePatternResolver(pTargetPattern);

    int i=0;
    for (String img:pImages) {
      ConvertCmd cmd = new ConvertCmd();
      cmd.setPID(i++);
      ProcessTask pt = cmd.getProcessTask(pOp,img,fpr.createName(img));
      execute(pt);
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     {@inheritDoc}

     In case of error, we will save the PID of the process (in our case,
     the PID is the index of the image causing the problem).
  */

  public void processTerminated(ProcessEvent pEvent) {
    if (pEvent.getReturnCode() != 0) {
      addException(pEvent.getPID(),pEvent.getException());
    }
    super.processTerminated(pEvent);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Convert the given images with the given {@link Operation}. 

     @param pOp            the conversion-operation
     @param pImages        List of images to convert
     @param pTargetPattern Pattern for the target-files.
                           See {@link FilenamePatternResolver} for details
  */

  private void 
    runBatch(Operation pOp, List<String> pImages, String pTargetPattern)
                  throws IOException, InterruptedException, IM4JavaException {
    FilenamePatternResolver fpr = new FilenamePatternResolver(pTargetPattern);

    // create template for ImageMagick
    // we create the files in the directory the target-pattern suggests
    File firstFile = new File(fpr.createName(pImages.get(0)));
    String path=fpr.getFullpath(firstFile);
    String ext=fpr.getExt(firstFile);
    String rand = String.format("%04d",(new Random()).nextInt(10000));
    String template = String.format("%s%cim4java_%s.%s",
                                    path,File.separatorChar,rand,ext);

    // replace first placeholder with n placeholders
    Operation op = new Operation();
    LinkedList<String> args = pOp.getCmdArgs();
    boolean copyDone=false;
    for (String arg:args) {
      if ((!copyDone) && arg.startsWith(Operation.IMG_PLACEHOLDER)) {
	// replicate placeholder for every image
	for (int i=0; i<pImages.size();++i) {
	  op.addRawArgs(arg);
	}
	copyDone=true;
      } else {
	// just copy the arg
	op.addRawArgs(arg);
      }
    }

    // run conversion
    ConvertCmd cmd = new ConvertCmd();
    String[] images = new String[pImages.size()+1];
    images=pImages.toArray(images);
    images[pImages.size()]=template;

    try {
      cmd.run(op,(Object[]) images);
    } catch (CommandException ce) {
      // try to clean up
      String prefix=path+File.separatorChar+"im4java_"+rand+"-",
	suffix="."+ext;
      for (int i=0; i<pImages.size(); ++i) {
 	File src = new File(prefix+i+suffix);
	src.delete();
      }
      addException(0,ce);
      return;
    }

    // rename files
    int i=0;
    String prefix=path+File.separatorChar+"im4java_"+rand+"-",
      suffix="."+ext;
    for (String img:pImages) {
      try {
	File src  = new File(prefix+i+suffix);
	File dest = new File(fpr.createName(img));
	dest.delete();
	if (!src.renameTo(dest)) {
	  addException(0,new Exception("rename failed"));
	}
      } catch (Exception e) {
	addException(0,e);
	return;
      } finally {
	i++;
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add index and execption of failed operations.

     @param pIndex     the index to add to the list
     @param pException the exception to add to the list
  */

  private void addException(int pIndex, Exception pException) {
    synchronized(iExceptions) {
      iExceptions.add(new ConvertException(pIndex,pException));
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the list of exceptions.

     @return the list of exceptions (might be null)
  */

  public List<ConvertException> getFailedConversions() {
    return iExceptions;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return the current run-mode

     @return the run-mode
  */

  public Mode getMode() {
    return iMode;
  }

  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  /**
     This nested exception wraps the original exception of failed conversions. 
     It has a field with the index of the image causing the failure.
  */

  public class ConvertException extends Exception {

    ////////////////////////////////////////////////////////////////////////////
    
    /**
       The index of the image causing the failure.
    */
    
    private int iIndex;

    ////////////////////////////////////////////////////////////////////////////
    
    /**
       The constructor.

       @param pIndex The index causing the failure
       @param pCause The original exception
    */

    public ConvertException(int pIndex, Exception pCause) {
      super(pCause);
      fillInStackTrace();
      iIndex=pIndex;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    /**
       Query the index of the image causing the falure.

       @return The index of the image causing the failure
    */
    
    public int getIndex() {
      return iIndex;
    }
  }
}