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
package org.eclipse.wst.html.ui.tests.application;



import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.wst.html.ui.tests.Logger;


/**
 * Minimal app to run as Eclipse "application"
 */
public class SSEHTMLTests implements IPlatformRunnable {

	/**
	 * @see org.eclipse.core.boot.IPlatformRunnable#run(Object)
	 */
	public Object run(Object args) throws Exception {
		Object result = new AllTests().runMain(args);
		if (result != null) {
			if (result instanceof Throwable) {
				Logger.log(Logger.ERROR, "tests didn't return 'ok'", (Throwable) result);
			}
			else {
				Logger.log(Logger.ERROR, "tests didn't return 'ok'");
			}
			return result;
		}
		else
			return org.eclipse.core.runtime.IPlatformRunnable.EXIT_OK;
	}
}