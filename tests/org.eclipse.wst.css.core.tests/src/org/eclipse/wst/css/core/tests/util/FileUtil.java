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
package org.eclipse.wst.css.core.tests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.osgi.framework.Bundle;

/**
 * 
 */
public class FileUtil {
	public static final String commonEOL = "\r\n";

	public static File createFile(String directory, String filename) throws IOException {
		Bundle bundle = Platform.getBundle("org.eclipse.wst.css.core.tests"); //$NON-NLS-1$
		URL url = bundle.getEntry("/"); //$NON-NLS-1$
		URL localURL = FileLocator.toFileURL(url);
		String installPath = localURL.getPath();
		String totalDirectory = installPath + directory;
		String totalPath = totalDirectory + "/" + filename; //$NON-NLS-1$
		URL totalURL = new URL(url, totalPath);
		String finalFile = totalURL.getFile();
		File file = new File(finalFile);
		return file;
	}

	// public static Reader createReader(File file) throws
	// FileNotFoundException {
	// return new FileReader(file);
	// }
	//	
	// public static Reader createReader(String directory, String filename)
	// throws IOException {
	// File file = createFile(directory, filename);
	// return createReader(file);
	// }

	public static String createString(String directory, String filename) throws FileNotFoundException, IOException {
		StringBuffer buf = new StringBuffer();
		Reader fileReader = new FileReader(createFile(directory, filename));
		BufferedReader reader = new BufferedReader(fileReader);
		String line;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
			buf.append(commonEOL);
		}
		return buf.toString();
		// return new DataInputStream(new
		// FileInputStream(createFile(directory, filename))).readUTF();
	}

	static int uniqueNum = 0;

	public static ICSSModel createModel() {
		IStructuredModel model = null;
		try {

			IModelManager modelManager = StructuredModelManager.getModelManager();

			model = modelManager.getModelForEdit("test" + uniqueNum++ + ".css", new NullInputStream(), null); //$NON-NLS-1$

			// always use the same line delimiter for these tests, regardless
			// of plaform or preference settings
			model.getStructuredDocument().setLineDelimiter(commonEOL);


		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return (ICSSModel) model;

	}

	public static void dumpString(String string, String directory, String filename) throws IOException {
		File file = createFile(directory, filename);
		Writer writer = new FileWriter(file);
		writer.write(string);
		writer.close();
	}

}
