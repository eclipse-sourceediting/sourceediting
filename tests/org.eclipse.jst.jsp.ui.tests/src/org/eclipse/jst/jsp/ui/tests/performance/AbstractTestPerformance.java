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
/*
 * Created on Jun 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.jst.jsp.ui.tests.performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.jst.jsp.core.contentmodel.tld.DocumentProvider;
import org.eclipse.jst.jsp.ui.tests.utils.DateUtil;
import org.eclipse.jst.jsp.ui.tests.utils.FileUtil;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.PropagatingAdapter;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author davidw
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AbstractTestPerformance extends TestCase {

	/**
	 * convenience class for grouping time, memory, w/ a filename
	 * (date+filename)
	 */
	class FileData {
		public String fFilename;
		public long fMemory;
		public long fType;

		public FileData(String filename, String time, String memory) {
			if (filename != null && time != null && memory != null) {
				this.fType = Long.parseLong(time.trim());
				this.fMemory = Long.parseLong(memory.trim());
				this.fFilename = filename;
			}
		}

	}

	protected boolean testStructuredModel = true;

	protected boolean collectGarbage = false;

	protected Vector fileDataVector = new Vector();

	protected Runtime fRuntime = Runtime.getRuntime();

	protected int fTrials = 3; //11;//101;

	protected String nl = System.getProperty("line.separator");

	protected double PERCENT_DEVIATION = .05;

	/**
	 *  
	 */
	public AbstractTestPerformance() {
		super();
	}

	//	/**
	//	 * @param name
	//	 */
	//	public AbstractTestPerformance(String name) {
	//		super(name);
	//	}

	protected void collectGarbage() {
		fRuntime.gc();
	}

	/**
	 * compares the test results w/ previous test results performs 2
	 * assertions, to make sure the deviation in time and memory usage to
	 * create the model is within the PERCENT_DEVIATION
	 * 
	 * @param filename
	 * @param time
	 * @param memory
	 */
	protected void compareResults(String filename, long time, long memory) {
		FileData prev = getPreviousResultsForFile(filename);

		//		System.out.println("current > " + filename + "::" + time + "::" +
		// memory);
		//		System.out.println("previous> " + prev.filename + "::" + prev.time
		// + "::" + prev.memory);

		if (prev != null) {

			// another note: currently the test will FAIL even if the memory
			// usage is 5% BETTER
			//				 the results should actually still be logged ant the test
			// probably shouldn't fail
			//				 or maybe it should fail, just to point out that the performance
			// has
			//               dramatically improved, then record the new results and use them
			// as a baseline
			//               for future testing...

			//long diffTime = Math.abs(time - prev.time) / time;
			long diffMem = Math.abs(memory - prev.fMemory) / memory;

			// (pa) the problem with checking time is that different
			// processors run at diffrent speed
			// and also cpu cycles may be running different on different
			// systems (w/ other apps running)
			//

			//            assertTrue(
			//                "** time isn't within " + PERCENT_DEVIATION*100 + "% of last
			// test run >> " + nl +
			//                "file: " + filename + nl +
			//                "old: " + prev.time + nl +
			//                "new: " + time + nl +
			//                "diff: " + diffTime,
			//                (diffTime <= PERCENT_DEVIATION));

			//	assertTrue("** memory usage isn't within " + PERCENT_DEVIATION
			// * 100 + "% of last test run >> " + nl + "file: " + filename +
			// nl + "old: " + prev.memory + nl + "new: " + memory + nl +
			// "diff: " + diffMem, (diffMem <= PERCENT_DEVIATION));
		}
	}

	/**
	 * Method countEmbeddedFactories.
	 * 
	 * @param document
	 */
	protected int countEmbeddedFactories(Document document) {
		int result = 0;
		if (document instanceof XMLDocument) {
			XMLDocument xmlDocument = (XMLDocument) document;
			PageDirectiveAdapter pageDirectiveAdapter = (PageDirectiveAdapter) ((INodeNotifier) xmlDocument).getExistingAdapter(PageDirectiveAdapter.class);
			if (pageDirectiveAdapter != null) {
				EmbeddedTypeHandler embeddedHandler = pageDirectiveAdapter.getEmbeddedType();
				if (embeddedHandler != null) {
					result = embeddedHandler.getAdapterFactories().size();
				}
			}
		}
		return result;
	}

	/**
	 * Method countFactories.
	 * 
	 * @param model
	 * @return int
	 */
	protected int countFactories(IStructuredModel model) {
		int result = 0;
		if (model != null) {
			IFactoryRegistry reg = model.getFactoryRegistry();
			result = reg.getFactories().size();
		}
		return result;
	}

	protected int countNodes(Node node) {
		int count = 1; // one for this node
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			count = count + countNodes(child);
		}
		return count;
	}

	/**
	 * Method countPropatingFactories.
	 * 
	 * @param document
	 * @return int
	 */
	protected int countPropatingFactories(Document document) {
		int result = 0;
		if (document instanceof XMLDocument) {
			PropagatingAdapter pAdapter = (PropagatingAdapter) ((XMLDocument) document).getAdapterFor(PropagatingAdapter.class);
			result = pAdapter.getAdaptOnCreateFactories().size();
		}
		return result;
	}

	protected void doStructuredModelTest(String filename) throws IOException {
		int nodeCount = 0;
		//        int adapterCount = 0;
		int nFactories = 0;
		int nPropagatingFactories = 0;
		int nEmbeddedFactories = 0;
		IStructuredModel model = null;
		Document document = null;
		long thisMem = 0;
		double thisTime = 0;

		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();

		System.out.println();
		double totalTime = 0;
		long totalMem = 0;
		for (int i = 0; i < fTrials; i++) {
			InputStream inStream = getClass().getResourceAsStream(filename);

			if (collectGarbage) {
				collectGarbage();
			}
			// no need to collect memory stats, if not using them
			long startMem = 0;
			if (collectGarbage) {
				startMem = fRuntime.totalMemory() - fRuntime.freeMemory();
			}

			long startTime = System.currentTimeMillis();

			if (testStructuredModel) {
				model = modelManager.getModelForEdit(filename, inStream, null);
				document = ((XMLModel) model).getDocument();
			}
			else {
				DocumentProvider provider = new DocumentProvider();
				provider.setInputStream(inStream);
				document = provider.getDocument();
			}

			long endTime = System.currentTimeMillis();

			long endMem = 0;
			if (collectGarbage) {
				endMem = fRuntime.totalMemory() - fRuntime.freeMemory();
			}

			thisTime = endTime - startTime;

			if (collectGarbage) {
				thisMem = endMem - startMem;
			}
			// don't include first, since is 5 times longer than usual
			// presumably due to class loading
			if (i > 0) {
				totalTime = totalTime + thisTime;
				if (collectGarbage) {
					totalMem = totalMem + thisMem;
				}
			}
			else {
				// just need to do this statistics gathering
				// once, so will put here.
				nodeCount = countNodes(document);
				// adapterCount = countAdapters(document);
				if (testStructuredModel) {
					nFactories = countFactories(model);
					nPropagatingFactories = countPropatingFactories(document);
					nEmbeddedFactories = countEmbeddedFactories(document);
				}
			}
			markModel(model);
			//            System.out.println(i + ". Time to create Model: " + thisTime);
			//            System.out.println(i + ". Memory for Model: " + thisMem);
			if (model != null) {
				model.releaseFromEdit();
			}
			if (inStream != null) {
				inStream.close();
			}
		}

		if (fTrials > 1) {
			// minus one since we "skipped" first one
			double aveTime = (totalTime / (fTrials - 1));
			long aveMem = (totalMem / (fTrials - 1));

			printResults(filename, aveTime, aveMem, fTrials, nodeCount, nFactories, nPropagatingFactories, nEmbeddedFactories);
		}
		else {
			printResults(filename, thisTime, thisMem, fTrials, nodeCount, nFactories, nPropagatingFactories, nEmbeddedFactories);
		}

		//compareResults(filename, aveTime, aveMem);

		// when do we actually want to save results??
		//saveNewResults(filename, Long.toString(Math.round(aveTime)),
		// Long.toString(Math.round(aveMem)));

		//assertTrue("model could not be created!", model != null);

	}

	/**
	 * @param model
	 */
	protected void markModel(IStructuredModel model) {
		// this method is here just to specify to profilers,
		// so snapshots can be taken before model is released

	}

	/**
	 * gets the previous FileData for the file specified (if it exists)
	 * otherwise returns null
	 * 
	 * @param filename
	 * @return results the previous test FileData, or null if it doesn't exist
	 *         in the results file
	 */
	protected FileData getPreviousResultsForFile(String filename) {
		FileData results = null;
		if (fileDataVector != null) {
			FileData temp = null;
			for (int i = fileDataVector.size() - 1; i >= 0; i--) {
				temp = (FileData) fileDataVector.get(i);
				if (temp.fFilename.indexOf(filename) > 0) // the first one we
														  // encounter should
														  // be the latest
				{
					results = temp;
					break;
				}
			}
		}
		return results;
	}

	protected void printResults(String filename, double aveTime, long aveMem, int nTrials, int nodeCount, int nFactories, int nPropagatingFactories, int nEmbeddedFactories) {
		System.out.println();
		System.out.println("          Model for " + filename);
		System.out.println();
		System.out.println("          Average Time: " + aveTime);
		// memory is not meaningful if didn't run garbage collection bewtween
		// trials
		if (collectGarbage) {
			System.out.println();
			System.out.println("          Average Heap Memory: " + aveMem);
		}
		System.out.println();
		System.out.println("          (used " + (nTrials - 1) + " trials)");
		System.out.println("          (N Nodes == " + nodeCount + ")");
		//        System.out.println(" (N Adapters == " + adapterCount + ")");
		System.out.println("          (N Factories == " + nFactories + ")");
		System.out.println("          (N PropagatingFactories == " + nPropagatingFactories + ")");
		System.out.println("          (N EmbeddedFactories == " + nEmbeddedFactories + ")");
	}

	/**
	 * reads one block of data from the buffer (3 lines) creates a FileData
	 * object, then returns it
	 */
	protected FileData readBlock(BufferedReader br) throws IOException {
		String filename = br.readLine();
		String time = br.readLine();
		String memory = br.readLine();

		return new FileData(filename, time, memory);
	}

	/**
	 * fills private field 'fileDataVector' for comparison w/ new results
	 */
	protected void readPreviousResults() throws IOException {
		File rFile = FileUtil.makeFileFor("PerformanceResults", "results.txt", null);
		BufferedReader bReader = new BufferedReader(new FileReader(rFile));

		while (bReader.readLine() != null) // is actually reading in "---"
		{
			fileDataVector.add(readBlock(bReader));
		}
	}

	/**
	 * this just appends the results to the end of the results file...
	 */
	protected void saveNewResults(String filename, String time, String memory) throws FileNotFoundException, IOException {
		// ==> // boolean success = true;

		File fileout = FileUtil.makeFileFor("PerformanceResults", "results.txt", null);
		// in case file doesn't exist yet
		if (!fileout.isFile())
			fileout.createNewFile();
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(fileout.getAbsolutePath(), true));

		bWriter.write("---" + nl);
		bWriter.write(DateUtil.now() + "-" + filename + nl);
		bWriter.write(time + nl);
		bWriter.write(memory + nl);

		bWriter.close();
	}

	protected boolean runOnce;

}