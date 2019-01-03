/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.launching;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS{
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.internal.launching.messages"; //$NON-NLS-1$

	public static String Utils_0; 
    public static String Utils_1;

    public static String XSLLineBreakpoint_0; 
    public static String XSLStackFrame_5;     

	private Messages() {
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
}
