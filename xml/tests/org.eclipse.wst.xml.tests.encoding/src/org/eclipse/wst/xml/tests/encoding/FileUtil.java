/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.tests.encoding;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

/**
 * @author davidw
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
class FileUtil {
	private static String currentWorkspace = "dev0725";
	private static boolean printedOnce = false;
	public static String fLineSeparator = System.getProperty("line.separator");
	public static String fPathSeparator = System.getProperty("path.separator");
	public static String fFileSeparator = System.getProperty("file.separator");

	public static File makeFileFor(String directory, String filename, String testResultsDirectoryPrefix) throws IOException {
		String installPath = "/builds/Workspaces/" + currentWorkspace + "/org.eclipse.wst.xml.tests.encoding/";
		URL url = new URL("file://" + installPath);
		String totalDirectory = installPath + directory;
		String totalPath = totalDirectory + "/" + filename;
		URL totalURL = new URL(url, totalPath);
		URL finalurl = FileLocator.toFileURL(totalURL);
		String finalFile = finalurl.getFile();
		File file = new File(finalFile);
		String finalPath = file.getParent();
		File dir = new File(finalPath);
		if (!printedOnce) {
			System.out.println("Output written to " + dir.getAbsolutePath());
			printedOnce = true;
		}

		if (!dir.exists()) {
			dir.mkdirs();
		}
		return file;
	}

}
