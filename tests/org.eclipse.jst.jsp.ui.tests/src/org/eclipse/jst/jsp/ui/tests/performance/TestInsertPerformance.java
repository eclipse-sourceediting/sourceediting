/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.performance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocument;

/**
 * @author davidw
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestInsertPerformance extends TestCase {
	float total = 0;
	long thisTime = 0;
	int nTrials = 3; //11;

	public TestInsertPerformance(String name) {
		super(name);
	}

	protected void doTestBigTable(String filename) throws IOException {
		for (int i = 0; i < nTrials; i++) {
			doTestBigTable(i, filename);
			// don't include first, since is 5 times longer than usual
			// presumably due to class loading
			if (i > 0) {
				total = total + thisTime;
			}
		}
		System.out.println();
		System.out.println("          Average Time to insert 1000 row table in " + filename + ": " + (total / (nTrials - 1))); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("          (used " + (nTrials - 1) + " trials)"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	protected void doTestBigTable(int i, String filename) throws IOException {
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		// System.out.println();
		InputStream inStream = getClass().getResourceAsStream(filename);
		//            // run garbage collection to get a little more consistent times.
		//            System.gc();
		//            System.gc();
		IStructuredModel model = modelManager.getModelForEdit(filename, inStream, null);
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		InputStream textStream = getClass().getResourceAsStream("bigTable.txt"); //$NON-NLS-1$
		String tableText = readInputStream(new InputStreamReader(textStream));
		long startTime = System.currentTimeMillis();
		// 80 is known by counting characters in template, and trail and error
		structuredDocument.replaceText(this, 80, 0, tableText);
		long endTime = System.currentTimeMillis();
		thisTime = endTime - startTime;
		//System.out.println(i + ".  Time to insert table text: " + thisTime);
		// String resultingText = structuredDocument.getText();
		// System.out.println(resultingText);
		model.releaseFromEdit();
		inStream.close();
		textStream.close();

	}

	/**
	 * This method is quick/easy way to read "plain" 
	 * (ascii) characters from inputstream.
	 */
	private String readInputStream(InputStreamReader inputStreamReader) throws IOException {
		int numRead = 0;
		StringBuffer buffer = new StringBuffer();
		char tBuff[] = new char[4000];
		while ((numRead = inputStreamReader.read(tBuff, 0, tBuff.length)) != -1) {
			buffer.append(tBuff, 0, numRead);
		}
		return buffer.toString();
	}

	public void testHTMLInsert() throws IOException {
		doTestBigTable("plainTemplate.html"); //$NON-NLS-1$
	}

	public void testXMLInsert() throws IOException {
		doTestBigTable("plainTemplate.xml"); //$NON-NLS-1$
	}

	public void testJSPInsert() throws IOException {
		doTestBigTable("plainTemplate.jsp"); //$NON-NLS-1$
	}
}