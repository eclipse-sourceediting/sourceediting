/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.breakpoint;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for the debug UI breakpoints.
 * 
 * @author Doug Satchwell
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.internal.debug.ui.breakpoint.messages"; //$NON-NLS-1$
	/**
	 * Error message for null breakpoints.
	 */
	public static String XSLBreakpointProvider_0;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
