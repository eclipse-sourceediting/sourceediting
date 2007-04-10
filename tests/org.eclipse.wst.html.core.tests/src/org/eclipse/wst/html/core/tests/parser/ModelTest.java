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
package org.eclipse.wst.html.core.tests.parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import junit.framework.TestCase;

import org.eclipse.wst.html.core.tests.utils.DateUtil;
import org.eclipse.wst.html.core.tests.utils.FileUtil;
import org.eclipse.wst.html.core.tests.utils.StringCompareUtil;
import org.eclipse.wst.html.core.tests.utils.TestWriter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.document.StructuredDocumentRegionChecker;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;

public abstract class ModelTest extends TestCase {

	public final static String testResultsDirectoryPrefix = "ParserResults";
	private final static String fileExtension = ".txt";
	protected TestWriter fOutputWriter = new TestWriter();
	private int READ_BUFFER_SIZE = 4096;

	private boolean echoToSystemOut = false;
	private boolean printedOnce;
	private boolean printSummaryToSystemOut;

	/**
	 * Constructor for ModelTest.
	 * 
	 * @param name
	 */
	public ModelTest(String name) {
		super(name);
		try {
			printClass();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ModelTest() {
		this("dummy");
		//		try {
		//			printClass();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}

	public static IDOMModel createHTMLModel() {

		//return new XMLModelImpl();

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			model = modelManager.getModelForEdit("test.html", new NullInputStream(), null);

			// always use the same line delimiter for these tests, regardless
			// of
			// plaform or preference settings
			model.getStructuredDocument().setLineDelimiter(TestWriter.commonEOL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//assertTrue("basic XML empty model could not be created", model !=
		// null);
		return (IDOMModel) model;

	}

	public static IDOMModel createXMLModel() {

		//return new XMLModelImpl();

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			//assertTrue("modelManager must not be null", modelManager !=
			// null);

			model = modelManager.getModelForEdit("test.xml", new NullInputStream(), null);

			// always use the same line delimiter for these tests, regardless
			// of
			// plaform or preference settings
			model.getStructuredDocument().setLineDelimiter(TestWriter.commonEOL);


		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//assertTrue("basic XML empty model could not be created", model !=
		// null);
		return (IDOMModel) model;

	}

	/**
	 */
	private void printClass() throws IOException {
		fOutputWriter.writeln(getClass().getName());
	}

	public static void printException(Exception ex) {
		ex.printStackTrace();
	}

	private void printNode(Node node, int indent) {
		try {
			StringBuffer buffer = new StringBuffer(10);
			for (int i = 0; i < indent; i++)
				buffer.append("--");
			buffer.append(StringUtils.escape(node.toString()));
			fOutputWriter.writeln(buffer.toString());
			indent++;
			Node child = node.getFirstChild();
			while (child != null) {
				printNode(child, indent);
				child = child.getNextSibling();
			}
		}
		catch (Exception ex) {
			printException(ex);
		}
	}

	public void printSource(IDOMModel model) {
		try {
			fOutputWriter.writeln("Source :");
			fOutputWriter.writeln(model.getStructuredDocument().getText());
			fOutputWriter.writeln("");
		}
		catch (Exception ex) {
			printException(ex);
		}
	}

	public void printTree(IDOMModel model) {
		try {
			printFlatModel(model.getStructuredDocument());
			new StructuredDocumentRegionChecker(fOutputWriter).checkModel(model);
			fOutputWriter.writeln("Tree :");
			printNode(model.getDocument(), 0);
			fOutputWriter.writeln("");

		}
		catch (Exception ex) {
			printException(ex);
		}

	}

	protected void saveAndCompareTestResults() {
		try {
			String testOutputDirectory = testResultsDirectoryPrefix + DateUtil.now();
			String currentFilename = getClass().getName() + fileExtension;
			File fileout = FileUtil.makeFileFor(testOutputDirectory, currentFilename, testResultsDirectoryPrefix);
			Writer fileWriter = new FileWriter(fileout);
			fileWriter.write(fOutputWriter.toString());
			fileWriter.close();
			compareWithPreviousResults(fOutputWriter, currentFilename);

			if (echoToSystemOut) {
				System.out.println(fOutputWriter.toString());
			}

			fOutputWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method compareWithPreviousResults.
	 * 
	 * @param outputWriter
	 * @param currentFilename
	 */
	private void compareWithPreviousResults(TestWriter outputWriter, String currentFilename) throws IOException {
		boolean success = false;
		File previousResultsDir = FileUtil.getPreviousResultsDirectory(testResultsDirectoryPrefix);
		if (previousResultsDir != null) {
			String currentResults = outputWriter.toString();
			File previousResultsFile = new File(previousResultsDir, currentFilename);
			FileReader previousResultsFileReader = new FileReader(previousResultsFile);
			String previousResults = readString(previousResultsFileReader);
			StringCompareUtil stringCompare = new StringCompareUtil();
			// trying to "force" the same linedelimter didn't work well,
			// I think since the line delimiter is input on some tests,
			// and CVS is likely to change anyway (for ASCII files).
			// so we'll ignore in our comparisons. Note: we wouldn't
			// want to ignore whitespace. There might actually be some
			// tests that are not as accurate if we ignore EOL, but there
			// seems to be no easy way to handle, with out a lot of trouble.
			if (!stringCompare.equalsIgnoreLineSeperator(previousResults, currentResults)) {
				// previousResults.equals(currentResults)) {
				// fail
				success = false;

				System.out.println();
				System.out.println(currentFilename + " failed comparison to previous results");
				System.out.println("Previous: ");
				System.out.println(previousResults);
				System.out.println();
				System.out.println("Current: ");
				System.out.println(currentResults);
				System.out.println();
			}
			else {
				success = true;
				if (printSummaryToSystemOut) {
					System.out.println();
					System.out.println(currentFilename + " compared ok");
					System.out.println();
				}
			}
		}
		else {
			if (!printedOnce) {
				System.out.println();
				System.out.println("No Previous Directory found ... couldn't compare " + currentFilename + " with previous results");
				printedOnce = true;
				System.out.println();
			}
		}
		assertTrue("current is not equal to reference results for " + currentFilename, success);
	}

	/**
	 * Method readString.
	 * 
	 * @param previousResultsFileReader
	 * @return String
	 */
	private String readString(FileReader fileReader) throws IOException {
		return readInputStream(fileReader);
	}

	private String readInputStream(InputStreamReader inputStreamReader) throws IOException {
		int numRead = 0;
		StringBuffer buffer = new StringBuffer();
		char tBuff[] = new char[READ_BUFFER_SIZE];
		while ((numRead = inputStreamReader.read(tBuff, 0, tBuff.length)) != -1) {
			buffer.append(tBuff, 0, numRead);
		}
		// remember -- we didn't open stream ... so we don't close it
		return buffer.toString();
	}

	public abstract void testModel();

	protected void printFlatModel(IStructuredDocument flatModel) {
		fOutputWriter.writeln("");
		fOutputWriter.writeln("StructuredDocument Regions :");
		IStructuredDocumentRegion flatnode = flatModel.getFirstStructuredDocumentRegion();
		while (flatnode != null) {

			fOutputWriter.writeln(flatnode.toString());
			flatnode = flatnode.getNext();

		}
		fOutputWriter.writeln("");

	}
}
