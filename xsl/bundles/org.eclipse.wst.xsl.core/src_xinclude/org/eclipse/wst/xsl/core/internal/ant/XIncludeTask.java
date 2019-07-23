/******************************************************************************
* Copyright (c) 2008, 2019 Lars Vogel and others
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/eplv10.html
*
* Contributors:
* Lars Vogel - Lars.Vogel@gmail.com - initial API and implementation
*******************************************************************************/

package org.eclipse.wst.xsl.core.internal.ant;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.wst.xsl.core.internal.xinclude.XIncluder;

/**
 * This class provides an XInclude ANT task definition.
 * 
 * @author Lars Vogel
 *
 */
public class XIncludeTask extends Task {
	private String inFile;

	private String outFile;

	/**
	 * TODO: Add JavaDoc 
	 * @param inFile
	 */
	public void setIn(String inFile) {
		log("Setting the input file to: " + inFile, Project.MSG_VERBOSE); //$NON-NLS-1$
		this.inFile = inFile;
	}

	/**
	 * TODO: Add JavaDoc
	 * @param outFile
	 */
	public void setOut(String outFile) {
		log("Setting the output file to: " + outFile, Project.MSG_VERBOSE); //$NON-NLS-1$
		this.outFile = outFile;
	}

	@Override
	public void execute() {
		validate();
		log("Executing the XIncludeTask", Project.MSG_VERBOSE); //$NON-NLS-1$
		File file = new File(inFile).getAbsoluteFile();
		if (!file.exists()) {
			throw new BuildException("Specified input file does not exist: " //$NON-NLS-1$
					+ inFile);
		}
		// We will check if the file exists, if not we will try to create the
		// output file and or the output directory
		File fileout = new File(outFile).getAbsoluteFile();
		File dir = new File(fileout.getParent());
		if (!dir.exists()) {
			log("Creating the output directory: " + dir.getAbsolutePath()); //$NON-NLS-1$

			Boolean success = (new File(dir.getAbsolutePath())).mkdirs();
			if (!success) {
				throw new BuildException("Could not create output file: " //$NON-NLS-1$
						+ outFile);
			}
		}
		// check if the included files are modified after the last run
		// Assumption that all the included files are part of the input file
		// directory
		if (checkmodified(file, fileout)) {
			log("Changes detected, creating a new output file", //$NON-NLS-1$
					Project.MSG_INFO);
			XIncluder la = new XIncluder();
			try {
				la.extractXMLFile(inFile, outFile);
			} catch (Exception e) {
				throw new BuildException("An error occurred during processing: " //$NON-NLS-1$
						+ e.getMessage());
			}
		}
	}

	private boolean checkmodified(File in, File out) {
		File dir = new File(in.getParent());

		Collection<File> allFiles = listFiles(dir, true);
		for (File f : allFiles) {
			if (f.lastModified() > out.lastModified()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO: Add JavaDoc
	 * @param directory
	 * @param recurse
	 * @return
	 */
	public Collection<File> listFiles(File directory, boolean recurse) {
		// List of files / directories
		Vector<File> files = new Vector<File>();

		// Get files / directories in the directory
		File[] entries = directory.listFiles();

		// Go over entries
		for (File entry : entries) {

			files.add(entry);

			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, recurse));
			}
		}

		// Return collection of files
		return files;
	}

	private void validate() {
		if (inFile == null) {
			throw new BuildException("Please specify input file"); //$NON-NLS-1$
		}
		if (outFile == null) {
			throw new BuildException("Please specify output file"); //$NON-NLS-1$
		}
	}
}
