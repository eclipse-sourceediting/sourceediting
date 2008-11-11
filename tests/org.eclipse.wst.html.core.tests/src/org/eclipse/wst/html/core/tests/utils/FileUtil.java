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
package org.eclipse.wst.html.core.tests.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.encoding.util.NullInputStream;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.framework.Bundle;

/**
 * @author davidw
 *
 */
public class FileUtil {
	private static final String PLUGIN_ID = "org.eclipse.wst.html.core.tests";
	public static final String commonEOL = "\r\n";
	public static String fLineSeparator = System.getProperty("line.separator");
	public static String fPathSeparator = System.getProperty("path.separator");
	public static String fFileSeparator = System.getProperty("file.separator");

	static File previousResultsDirectory = null;
	static private String logSubDirectoryPath;

	private static boolean notTriedYet = true;
	static int uniqueNum = 0;

	public static class DirFilenameFilter implements FilenameFilter {
		String startOfAcceptableNames;

		public DirFilenameFilter(String startOfDirName) {
			startOfAcceptableNames = startOfDirName;
		}

		/**
		 * @see java.io.FileFilter#accept(File)
		 */
		public boolean accept(File pathname, String filename) {
			// we'll just assume if it starts with right key, it is a directory ... for now
			return filename.startsWith(startOfAcceptableNames);
		}

	}

	public static File getPreviousResultsDirectory(String directoryRootName) throws IOException {
		if (previousResultsDirectory == null && notTriedYet) {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			URL url = bundle.getEntry("/"); //$NON-NLS-1$

			URL finalurl = FileLocator.toFileURL(url);
			String finalFile = finalurl.getPath();

			File pluginHomeDir = new File(finalFile);
			FilenameFilter dirFilter = new DirFilenameFilter(directoryRootName);
			File[] allDirs = pluginHomeDir.listFiles(dirFilter);

			// assume first in list is newest, then start looking with the 
			// second item ... just to avoid "null" case checking
			if (allDirs.length > 0) {
				File currentNewest = allDirs[0];
				for (int i = 1; i < allDirs.length; i++) {
					File current = allDirs[i];
					// NOTE: we go by modified date, not by the actual time stamp in name.
					// This should work for all normal cases, but may not if someone does some
					// "funny" copying, or updating from repository.
					if (current.lastModified() > currentNewest.lastModified()) {
						currentNewest = current;
					}
				}
				previousResultsDirectory = currentNewest;
			}
			notTriedYet = false;
		}
		return previousResultsDirectory;
	}

	public static File makeFileFor(String directory, String filename, String testResultsDirectoryPrefix) throws IOException {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		URL url = bundle.getEntry("/");  //$NON-NLS-1$
		URL localURL = FileLocator.toFileURL(url);
		String installPath = localURL.getPath();
		String totalDirectory = installPath + directory;
		String totalPath = totalDirectory + "/" + filename;
		URL totalURL = new URL(url, totalPath);
		//URL finalurl = Platform.asLocalURL(totalURL);
		String finalFile = totalURL.getFile();
		File file = new File(finalFile);
		String finalPath = file.getParent();
		File dir = new File(finalPath);
		if (!dir.exists()) {
			// a little safety net, be sure the previous newest directory
			// is initialized, just in case no one else has done so
			if (testResultsDirectoryPrefix != null && testResultsDirectoryPrefix.length() > 0) {
				FileUtil.getPreviousResultsDirectory(testResultsDirectoryPrefix);
			}
			// now its "safe" to make the new one
			dir.mkdirs();
		}
		return file;
	}

	/**
	 * Just a general utility method
	 * @param filename
	 * @return String
	 */
	public static String getExtension(String filename) {
		String extension = null;
		int dotPostion = filename.lastIndexOf('.');
		if (dotPostion > -1) {
			extension = filename.substring(dotPostion + 1);
		}
		else {
			extension = new String();
		}
		return extension;
	}

	/**
	 * General purpose utility method to ensure the log
	 * directory exists, and returns the name. 
	 */
	public static String getLogDirectory() {
		if (logSubDirectoryPath == null) {
			String mainDirectory = "/logs";
			File dir = new File(mainDirectory);
			ensureExists(dir);
			String subDirectory = TimestampUtil.timestamp();
			logSubDirectoryPath = mainDirectory + "/" + subDirectory;
			File subdir = new File(logSubDirectoryPath);
			ensureExists(subdir);
		}
		return logSubDirectoryPath;
	}

	public static void ensureExists(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static String getPerformanceOutputLogName() {
		String result = null;
		String directoryName = FileUtil.getLogDirectory();
		String extension = ".out";
		String baseName = "sedTests";
		long now = System.currentTimeMillis();
		String nowStr = String.valueOf(now);
		result = directoryName + FileUtil.fFileSeparator + baseName + nowStr + extension;
		return result;
	}
	public static IDOMModel createHTMLModel() {
		IStructuredModel model = null;
		try {

			IModelManager modelManager = StructuredModelManager.getModelManager();

			model = modelManager.getModelForEdit("test" + uniqueNum++ + ".html", new NullInputStream(), null); //$NON-NLS-1$

			// always use the same line delimiter for these tests, regardless
			// of plaform or preference settings
			model.getStructuredDocument().setLineDelimiter(commonEOL);


		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return  (IDOMModel) model;

	}

}
