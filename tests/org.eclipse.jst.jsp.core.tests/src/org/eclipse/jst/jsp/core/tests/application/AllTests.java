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
package org.eclipse.jst.jsp.core.tests.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.Platform;


/**
 * This class is to run all the sed tests.
 */
public class AllTests extends TestCase {
	private final static String INTERACTIVE = "-interactive";
	private final static String VARY_ENCODING = "-varyEncoding";
	private final static String PERFORMANCE = "-performance";
	private final static String MEMORY = "-memory";
	protected boolean interactive = false;
	public static boolean varyEncoding = false;
	public static boolean doPerformanceTest = false;
	public static boolean doMemory = false;

	/**
	 * Method parseArgs.
	 * 
	 * @param args
	 */
	private void parseArgs(Object args) {
		// typicially args is an array of strings,
		// not sure when it wouldn't be.
		if (args instanceof String[]) {
			String[] strArgs = (String[]) args;
			for (int i = 0; i < strArgs.length; i++) {
				String arg = strArgs[i];
				//System.out.println("arg: " + arg);
				if (INTERACTIVE.equalsIgnoreCase(arg)) {
					interactive = true;
				}
				// note: this "very_encoding" branch of testing
				// is not ready for prime time (i.e. should not be
				// used), but I'm leaving in so as to not lose
				// work in test project
				if (VARY_ENCODING.equalsIgnoreCase(arg)) {
					varyEncoding = true;
				}
				else if (PERFORMANCE.equalsIgnoreCase(arg)) {
					doPerformanceTest = true;
				}
				else if (MEMORY.equalsIgnoreCase(arg)) {
					doMemory = true;
				}

			}
		}
	}

	/**
	 * Method pause.
	 */
	private void pause(String message) throws IOException {
		System.out.println(message);
		System.in.read();
	}

	public static void main(String[] args) {
		new AllTests().runMain(args);
	}

	protected String getOutputName() {
		return "sedtests.out";
	}

	public Object runMain(Object args) {
		Object result = null;
		File outFile = null;
		try {
			PrintStream output = null;
			PrintStream saveDefaultOutput = null;
			TestRunner testRunner = null;

			parseArgs(args);

			if (!interactive) {
				System.out.println();
				System.out.println("Tests running ... output will be saved to file");
				saveDefaultOutput = System.out;
				outFile = new File(getOutputName());
				OutputStream outstream = new FileOutputStream(outFile);
				output = new PrintStream(outstream);
				System.setOut(output);
				testRunner = new TestRunner(output);
			}
			else {
				testRunner = new TestRunner(System.out);
			}

			printVMInfo();

			Test suite = suite();

			TestResult testResult = testRunner.doRun(suite, false);
			if (output != null) {
				output.close();
			}
			if (interactive) {
				if (!Platform.inDevelopmentMode()) {
					pause("press any key to continue");
				}
			}
			else {
				System.setOut(saveDefaultOutput);
				// even if not interactive, print a summary to console
				printHeader(testResult);
				System.out.println("Full output in \n     " + outFile.getAbsolutePath());
			}
		}
		catch (Exception e) {
			result = e;
		}

		return result;
	}

	protected void printVMInfo() {
		String vminfo = System.getProperty("java.vm.info");
		String vmname = System.getProperty("java.vm.name");
		System.out.println();
		System.out.println("Sed tests running in " + vmname);
		System.out.println("   " + vminfo);
		System.out.println();
	}

	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTest(SSEModelJSPTestSuite.suite());
		return testSuite;
	}

	/**
	 * Prints the header of the report
	 */
	protected void printHeader(TestResult result) {
		if (result.wasSuccessful()) {
			System.out.println();
			System.out.print("OK");
			System.out.println(" (" + result.runCount() + " tests)");

		}
		else {
			System.out.println();
			System.out.println("FAILURES!!!");
			System.out.println("Tests run: " + result.runCount() + ",  Failures: " + result.failureCount() + ",  Errors: " + result.errorCount());
		}
	}
}