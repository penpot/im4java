/**************************************************************************
/* This class implements the processing of image-commands.
/*
/* Copyright (c) 2009-2010 by Bernhard Bablok (mail@bablokb.de)
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

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.FileImageOutputStream;


import org.im4java.process.ErrorConsumer;
import org.im4java.process.ProcessStarter;
import org.im4java.process.ProcessTask;
import org.im4java.process.StandardStream;
import org.im4java.script.ScriptGenerator;
import org.im4java.script.BashScriptGenerator;
import org.im4java.script.CmdScriptGenerator;

/**
   This class implements the processing of image operations. It replaces
   placeholders within the argument-stack and passes all arguments to the
   generic run-method of ProcessStarter.

   @version $Revision: 1.32 $
   @author  $Author: bablokb $
 
   @since 0.95
*/

public class ImageCommand extends ProcessStarter implements ErrorConsumer {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The command (plus initial arguments) to execute.
  */

  private LinkedList<String> iCommands;

  //////////////////////////////////////////////////////////////////////////////

  /**
     List of stderr-output.
  */

  private ArrayList<String> iErrorText;

  //////////////////////////////////////////////////////////////////////////////

  /**
     List of temporary files (input).
  */

  private LinkedList<String> iTmpFiles;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The default ScriptGenerator. 
  */

  private static ScriptGenerator iDefaultScriptGenerator;

  //////////////////////////////////////////////////////////////////////////////

  /**
     The ScriptGenerator of this ImageCommand. 
  */

  private ScriptGenerator iScriptGenerator = null;

  //////////////////////////////////////////////////////////////////////////////

  /**
     Static initializer for default script-generator.
  */

  static {
    if (System.getProperty("os.name").startsWith("Windows")) {
      iDefaultScriptGenerator = new CmdScriptGenerator();
    } else {
      iDefaultScriptGenerator = new BashScriptGenerator();
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Constructor.
   */

  public ImageCommand() {
    super();
    iCommands = new LinkedList<String>();
    iTmpFiles = new LinkedList<String>();
    setOutputConsumer(StandardStream.STDOUT);
    setErrorConsumer(this);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Constructor setting the commands.
   */

  public ImageCommand(String... pCommands) {
    this();
    setCommand(pCommands);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Set the command.
   */

  public void setCommand(String... pCommands) {
    for (String cmd:pCommands) {
      iCommands.add(cmd);
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Get the command.
   */

  public LinkedList<String> getCommand() {
    return iCommands;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Get the error-text associated with this command (might be null).

     @return The error-text associated with this command.
   */

  public ArrayList<String> getErrorText() {
    return iErrorText;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Execute the command (replace given placeholders).

     @param  pOperation The Operation to execute
     @param  images     Zero or more images (replace placeholders in pOperation)
     @throws IM4JavaException 
  */

  private LinkedList<String> prepareArguments(Operation pOperation, 
                                                               Object... images) 
                                            throws IOException, IM4JavaException {
    LinkedList<String> args = new LinkedList<String>(pOperation.getCmdArgs());
    args.addAll(0,iCommands);
    resolveImages(args,images);
    resolveDynamicOperations(pOperation,args,images);
    return args;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Execute the command (replace given placeholders).

     @param  pOperation The Operation to execute
     @param  images     Zero or more images (replace placeholders in pOperation)
     @throws IOException, InterruptedException, IM4JavaException 
  */

  public void run(Operation pOperation, Object... images) 
    throws IOException, InterruptedException, IM4JavaException {

    // prepare list of arguments
    LinkedList<String> args = prepareArguments(pOperation,images);

    try {
      run(args);
    } catch (Exception e) {
      removeTmpFiles();
      CommandException ce = new CommandException(e);
      ce.fillInStackTrace();
      throw ce;
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Return a ProcessTask for future execution (replace given placeholders).

     @param  pOperation The Operation to execute
     @param  images     Zero or more images (replace placeholders in pOperation)
     @throws IOException, IM4JavaException 
  */

  public ProcessTask getProcessTask(Operation pOperation, Object... images) 
                                          throws IOException, IM4JavaException {
    LinkedList<String> args = prepareArguments(pOperation,images);
    return getProcessTask(args);
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Post-processing after the process has terminated. Implements
     the method of the base class.

     @param pReturnCode  the return-code of the process
  */
    
  protected void finished(int pReturnCode) throws Exception {
    if (pReturnCode > 0) {
      CommandException ce;
      if (iErrorText.size() > 0) {
	ce = new CommandException(iErrorText.get(0));
      } else {
	ce = new CommandException("return code: " + pReturnCode);
      }
      ce.setErrorText(iErrorText);
      ce.setReturnCode(pReturnCode);
      throw ce;
    } else {
      removeTmpFiles();
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Resolve images passed as arguments.
  */

  private void resolveImages(LinkedList<String> pArgs,Object... pImages) 
                                                            throws IOException {
    ListIterator<String> argIterator = pArgs.listIterator();
    int i = 0;
    boolean havePlaceholder = false;
    String currentArg = null;
    for (Object obj:pImages) {
      // find the next placeholder
      havePlaceholder = false;
      while (argIterator.hasNext()) {
        currentArg = argIterator.next();
	if (currentArg.startsWith(Operation.IMG_PLACEHOLDER)) {
	  havePlaceholder = true;
	  break;
	}
      }
      if (!havePlaceholder) {
	// we have more argument images than placeholders!
	throw new IllegalArgumentException("more argument images than " +
                                                              "placeholders!");
      }
      if (obj instanceof String) {
        if (currentArg.length() == Operation.IMG_PLACEHOLDER.length()) {
          // a pure image-placeholder
	  argIterator.set((String) obj);
        } else {
          // an image-placeholder with read-modifier
          String modifier = 
                      currentArg.substring(Operation.IMG_PLACEHOLDER.length());
	  argIterator.set(((String) obj)+modifier);
        }
      } else if (obj instanceof BufferedImage) {
	String tmpFile = convert2TmpFile((BufferedImage) obj);
	argIterator.set(tmpFile);
	iTmpFiles.add(tmpFile);
      } else {
	throw new IllegalArgumentException(obj.getClass().getName() +
					   " is an unsupported image-type");
      }
      i++;
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Resolve DynamicOperations.
     @throws IM4JavaException 
  */

  private void resolveDynamicOperations(Operation pOp, LinkedList<String> pArgs,
                                     Object... pImages) throws IM4JavaException {
    ListIterator<String> argIterator = pArgs.listIterator();
    ListIterator<DynamicOperation> dynOps = 
      pOp.getDynamicOperations().listIterator();

    // iterate over all DynamicOperations
    while (dynOps.hasNext()) {
      DynamicOperation dynOp = dynOps.next();
      Operation op = dynOp.resolveOperation(pImages);

      // find the next placeholder
      while (argIterator.hasNext()) {
	if (argIterator.next().equals(Operation.DOP_PLACEHOLDER)) {
	  break;
	}
      }

      if (op == null) {
	// no operation
	argIterator.remove();		  
      } else {
	List<String> args = dynOp.resolveOperation(pImages).getCmdArgs();
	if (args == null) {
	  // empty operation, remove placeholder
	  argIterator.remove();
	} else {
	  // remove placeholder and add replacement
	  argIterator.remove();
	  for (String arg:args) {
	    argIterator.add(arg);
	  }
	}
      }
    }  // while (dynOps.hasNext())
  }
  
  //////////////////////////////////////////////////////////////////////////////
    
  /**
     This method just saves the stderr-output into an internal field.
     
     @see org.im4java.process.ErrorConsumer#consumeError(java.io.InputStream)
  */
    
  public void consumeError(InputStream pInputStream) throws IOException {
    InputStreamReader esr = new InputStreamReader(pInputStream);
    BufferedReader reader = new BufferedReader(esr);
    String line;
    iErrorText= new ArrayList<String>();
    while ((line=reader.readLine()) != null) {
      iErrorText.add(line);
    }
    reader.close();
    esr.close();
  }
  
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Create a temporary file.
   */

  private String getTmpFile() throws IOException {
    File tmpFile = File.createTempFile("im4java-",".png");
    tmpFile.deleteOnExit();
    return tmpFile.getAbsolutePath();
  }
  
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Write a BufferedImage to a temporary file.
   */

  private String convert2TmpFile(BufferedImage pBufferedImage)
                                                             throws IOException {
    String tmpFile = getTmpFile();
    ImageTypeSpecifier spec = 
      ImageTypeSpecifier.createFromRenderedImage(pBufferedImage);
    Iterator<ImageWriter> iter;
    iter = ImageIO.getImageWriters(spec,"TIFF");
    if (!iter.hasNext()) {
      iter = ImageIO.getImageWriters(spec,"PNG");
    }
    if (iter.hasNext()) {
      ImageWriter writer = (ImageWriter) iter.next();
      FileImageOutputStream fios = new FileImageOutputStream(new File(tmpFile));
      writer.setOutput(fios);
      writer.write(pBufferedImage);
      fios.close();
      writer.dispose();
    }
    return tmpFile;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Remove all temporary files.
   */

  private void removeTmpFiles() {
    for (String file:iTmpFiles) {
      try {
	(new File(file)).delete();
      } catch (Exception e) {
	// ignore, since if we can't delete the file, we can't do anything about it
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Set the default ScriptGenerator.
   */

  public static void setDefaultScriptGenerator(ScriptGenerator pGen) {
    iDefaultScriptGenerator =  pGen;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Set the ScriptGenerator.
   */

  public void setScriptGenerator(ScriptGenerator pGen) {
    iScriptGenerator =  pGen;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Generate a script.
   */

  public void createScript(PrintWriter pWriter, Operation pOp, Properties pProps) {
    ScriptGenerator sg = iScriptGenerator;
    if (sg == null) {
      sg = iDefaultScriptGenerator;
    }
    
    // add command as a property
    StringBuilder builder = new StringBuilder();
    for (String token:getCommand()) {
      builder.append(token).append(' ');
    }
    pProps.setProperty("im4java.cmd",builder.toString());

    // add search-paths as properties
    String globalPath=getGlobalSearchPath();
    if (globalPath==null) {
      globalPath="";
    }
    String localPath=getSearchPath();
    if (localPath==null) {
      localPath="";
    }
    pProps.setProperty("im4java.globalSearchPath",globalPath);
    pProps.setProperty("im4java.localSearchPath",localPath);

    sg.init(pWriter,pOp,pProps);
    sg.createScript();
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Generate a script. The method will automatically append the
   * extension ".cmd" on a windows-plattform.
   */

  public void createScript(String pFilename, Operation pOp, Properties pProps) 
                                                   throws FileNotFoundException {
    if (System.getProperty("os.name").startsWith("Windows")) {
      pFilename=pFilename+".cmd";
    }
    PrintWriter pw = new PrintWriter(pFilename);
    createScript(pw,pOp,pProps);
    pw.close();
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
   * Generate a script. Convenience-method without a Properties-object.
   */

  public void createScript(String pFilename, Operation pOp)
                                                  throws FileNotFoundException {
    createScript(pFilename,pOp,new Properties());
  }
}
