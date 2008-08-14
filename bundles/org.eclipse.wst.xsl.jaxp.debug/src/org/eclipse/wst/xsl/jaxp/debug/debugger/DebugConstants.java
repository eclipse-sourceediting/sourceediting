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
package org.eclipse.wst.xsl.jaxp.debug.debugger;

/**
 * Constants used by the debugger.
 * 
 * <p>
 * Note that class is loaded both on the Eclipse classpath and the 
 * launched transformation process, such that they can use exactly the same constants
 * for communication.
 * </p>
 * 
 * @author Doug Satchwell
 */
public class DebugConstants
{
	/**
	 * The constant used to notify the debugger to start.
	 */
	public static final String REQUEST_START = "start"; //$NON-NLS-1$
	/**
	 * The constant used to signify addition of a breakpoint.
	 */
	public static final String REQUEST_ADD_BREAKPOINT = "add"; //$NON-NLS-1$
	/**
	 * The constant used to signify removal of a breakpoint.
	 */
	public static final String REQUEST_REMOVE_BREAKPOINT = "remove"; //$NON-NLS-1$
	/**
	 * The constant used to signify step into.
	 */
	public static final String REQUEST_STEP_INTO = "step into"; //$NON-NLS-1$
	/**
	 * The constant used to signify step over.
	 */
	public static final String REQUEST_STEP_OVER = "step over"; //$NON-NLS-1$
	/**
	 * The constant used to signify suspension.
	 */
	public static final String REQUEST_SUSPEND = "suspend"; //$NON-NLS-1$
	/**
	 * The constant used to signify resume.
	 */
	public static final String REQUEST_RESUME = "resume"; //$NON-NLS-1$
	/**
	 * The constant used to request the stack.
	 */
	public static final String REQUEST_STACK = "stack"; //$NON-NLS-1$
	/**
	 * The constant used to request a variable.
	 */
	public static final String REQUEST_VARIABLE = "var"; //$NON-NLS-1$
	/**
	 * The constant used to request a variable value.
	 */
	public static final String REQUEST_VALUE = "value"; //$NON-NLS-1$
	/**
	 * The constant used to signify step return.
	 */
	public static final String REQUEST_STEP_RETURN = "step return"; //$NON-NLS-1$
}
