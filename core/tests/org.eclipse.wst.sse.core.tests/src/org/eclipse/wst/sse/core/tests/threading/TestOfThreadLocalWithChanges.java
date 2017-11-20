/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.threading;

import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;


/**
 * This class started off as copy of TestOfThreadLocalImprovements, and I just
 * added another thread with modifies the document.
 */
public class TestOfThreadLocalWithChanges extends TestCase {
	// number of times a pair of tests (with and without) thread local are
	// repeated (for more accurate averages).
	int nTrials = 3;
	IStructuredDocument fDocument = null;
	private static final boolean DEBUG_TEST_DETAIL = false;
	// tests from 1 to MAX_TREADS-1 threads
	private int MAX_THREADS = 14;
	private long SLEEP_TIME = 2;
	private int N_CHANGES = 10000;

	public TestOfThreadLocalWithChanges() throws IOException {
		super();
	}

	private final String getContent() {
		return "<test>" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)"
					+ "<extra> <junk> to make a <large/> file (over <1000> chars)" + "<extra> <junk> to make a <large/> file (over <1000> chars)" + "</test>";
	}

	private IStructuredDocument getDocument(String content) throws IOException {
		if (fDocument == null) {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			fDocument = modelManager.createStructuredDocumentFor("testPerf.xml", content, null);
		}
		return fDocument;
	}

	void linkRunner(final Boolean direction, IStructuredDocument document) {
		IStructuredDocument structuredDocument = document;
		IStructuredDocumentRegion previousDocumentRegion = null;
		long startTime = System.currentTimeMillis();
		int start = 0;
		int length = structuredDocument.getLength();
//		int nRegions = 0;
		// I made length "dynamic" after adding modification thread
		for (int i = start; i < structuredDocument.getLength(); i++) {
			int index = i;
			if (!direction.booleanValue()) {
				int currentLength = structuredDocument.getLength();
				index = (currentLength - 1) - i;
			}
			IStructuredDocumentRegion currentDocumentRegion = structuredDocument.getRegionAtCharacterOffset(index);
			if (currentDocumentRegion != previousDocumentRegion) {
//				nRegions++;
				previousDocumentRegion = currentDocumentRegion;
			}
			Thread.yield();
			try {
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		if (DEBUG_TEST_DETAIL) {
			System.out.println("Thread: " + Thread.currentThread() + "   from " + length + " characters, found " + /*nRegions*/ "" + " regions in " + (endTime - startTime) + " msecs.");
		}
	}

	private long dotestDocumentWalkingWithThreadLocal(int nThreads) throws IOException {
		BasicStructuredDocument.setUSE_LOCAL_THREAD(true);
		fDocument = getDocument(getContent());
		long startTimeOverall = System.currentTimeMillis();
		doDocumentWalking(nThreads);
		long endTimeOverall = System.currentTimeMillis();
		long finalOverall = endTimeOverall - startTimeOverall;
		if (DEBUG_TEST_DETAIL) {
			System.out.println("Overall time using Local_Thread: " + finalOverall);
		}
		return finalOverall;
	}

	private void doDocumentWalking(int nThreads) throws IOException {
		Thread[] threads = new Thread[nThreads];
		boolean toggle = true;
		for (int i = 0; i < threads.length; i++) {
			final Boolean direction = new Boolean(toggle);
			// toggle controls whether moves from start to end, or end to start
			// of document.
			toggle = !toggle;
			ThreadGroup threadGroup = new ThreadGroup("unit tests");
			Thread thread = new Thread(threadGroup, new Runnable() {
				public void run() {
					linkRunner(direction, fDocument);
				}
			}, ("sse unit test thread " + i));
			// I used MIN_PRIORITY since that makes it easier to "see" in
			// debugger list of threads
			thread.setPriority(Thread.MIN_PRIORITY);
			threads[i] = thread;
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		// now that "readers" have started, we'll start a modification thread
		// too
		Thread modifyingThread = new Thread(new Runnable() {
			public void run() {
				modifiyDocument(fDocument);
			}
		}, ("sse unit test modification thread "));
		// I used MAX_PRIORITY to be sure lots of modifications take place
		modifyingThread.setPriority(Thread.MIN_PRIORITY);
		modifyingThread.start();
		try {
			for (int i = 0; i < threads.length; i++) {
				threads[i].join();
			}
			modifyingThread.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void modifiyDocument(IStructuredDocument document) {
		Random random = new Random();
		for (int i = 0; i < N_CHANGES + 1; i++) {
			int randomOffset = random.nextInt(document.getLength());
			// 100 just seems like a good maximum length to replace
			// (could play with in future).
			int randomLength = random.nextInt(100);
			// if we "accidently" get more than the length, just skipit
			if (randomOffset + randomLength < document.getLength()) {
				document.replaceText(this, randomOffset, randomLength, "testingtext");
			}
		}
	}

	public void testNThreads() throws IOException {
		for (int i = 1; i < MAX_THREADS; i++) {
			int nThreads = i;
			if (DEBUG_TEST_DETAIL) {
				System.out.println(" >>>>---- N Threads: " + i);
			}
			float wCumm = 0;
			for (int j = 0; j < nTrials; j++) {
				wCumm = wCumm + dotestDocumentWalkingWithThreadLocal(nThreads);
			}
		}
		// if gets to here (with no exceptions) all is ok.
		assertTrue(0 == 0);
	}
}
