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
package org.eclipse.wst.sse.core.tests.threading;

import java.util.Random;

/**
 * See http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#2 for
 * informative article.
 */
public class ThreadLocalInstanceExample {

	// Create thread local class
	// Initial value is a random number from 0-999
	private class MyThreadLocal extends ThreadLocal {
		private Random innerRandom = new Random();

		protected Object initialValue() {
			return new Integer(innerRandom.nextInt(1000));
		}
	}

	// Create class variable
	static volatile int counter = 0;

	// Define/create thread local variable
	ThreadLocal threadLocal = new MyThreadLocal();

	// For random number generation
	// static Random random = new Random();

	// Displays thread local variable, counter,
	// and thread name
	void displayValues() {
		System.out.println(threadLocal.get() + "\t" + counter + "\t" + Thread.currentThread().getName());
	}

	public static void main(String args[]) {
		new ThreadLocalInstanceExample().startRun();
	}

	private void startRun() {

		// Each thread increments counter
		// Displays variable info
		// And sleeps for the random amount of time
		// Before displaying info again
		Runnable runner = new Runnable() {
			public void run() {
				synchronized (this) {
					counter++;
				}
				displayValues();
				try {
					Thread.sleep(((Integer) threadLocal.get()).intValue());
					displayValues();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		// Another instance of class created
		// and values displayed
		displayValues();

		// Here's where the other threads
		// are actually created
		for (int i = 0; i < 5; i++) {
			Thread t = new Thread(runner);
			t.start();
		}
	}

	/**
	 *  
	 */
	public ThreadLocalInstanceExample() {
		super();
	}
}