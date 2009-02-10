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

import java.io.Writer;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.eclipse.wst.xsl.jaxp.debug.invoker.IProcessorInvoker;

/**
 * An interface to XSL debugger instances.
 * 
 * @author Doug Satchwell
 */
public interface IXSLDebugger extends Runnable
{
	/**
	 * Set the invoker to use.
	 * 
	 * @param invoker the invoker to set
	 */
	void setInvoker(IProcessorInvoker invoker);

	/**
	 * Set the transformer factory to use.
	 * 
	 * @param factory the factory to use
	 */
	void setTransformerFactory(TransformerFactory factory);

	/**
	 * Add a transformer to this.
	 * 
	 * @param transformer
	 */
	void addTransformer(Transformer transformer);

	/**
	 * Set the Writer for this to write events to.
	 * 
	 * @param writer the writer to write events to
	 */
	void setEventWriter(Writer writer);

	/**
	 * Set the ObjectOutputStream for this to write generated events to.
	 * 
	 * @param writer the stream to write generate events to
	 */
	void setGeneratedWriter(Writer writer);

	/**
	 * Set the URL of the transformation source file.
	 * 
	 * @param sourceURL the source URL
	 */
	void setSource(URL sourceURL);

	/**
	 * Set the transformation Result.
	 * 
	 * @param writer the result
	 */
	void setTarget(Writer writer);

	/**
	 * Add a breakpoint to this.
	 * 
	 * @param breakpoint
	 */
	void addBreakpoint(BreakPoint breakpoint);

	/**
	 * Remove a breakpoint from this.
	 * 
	 * @param breakpoint
	 */
	void removeBreakpoint(BreakPoint breakpoint);

	/**
	 * Perform a 'step into' operation.
	 */
	void stepInto();

	/**
	 * Perform a 'step over' operation.
	 */
	void stepOver();

	/**
	 * Perform a 'step return' operation.
	 */
	void stepReturn();

	/**
	 * Perform a 'suspend' operation.
	 */
	void suspend();

	/**
	 * Perform a 'resume' operation.
	 */
	void resume();

	/**
	 * Quit debugging.
	 */
	void quit();

	/**
	 * Generate a string that represents the current call stack of frames and their variables.
	 * <p>
	 * Frames are generated with the following format: <i>file</i>|<i>frameId</i>|<i>lineNumber</i>|<i>name</i>
	 * This is immediately followed with the id's of the frame's variables (each variable id being unique for the whole process).
	 * </p>
	 * <p>
	 * The separator for frames is $$$. Within a frame, the separator for variable id's is |.
	 * </p>
	 * <p>
	 * e.g. file:/tran1.xsl|1|12|xsl:template name="temp1"|1|2|3$$$file:/tran2.xsl|2|34|xsl:template name="temp2"|4|5|6
	 * 
	 * This defines 2 frames with id's 1 and 2, which are occur in files tran1.xsl and tran2.xsl respectively.
	 * Frame 1 is currently at line 12, in a template with name temp1, and it defines 3 variables with id's 1, 2 and 3. 
	 * </p>
	 * 
	 * @return the generated string
	 */
	String stack();

	/**
	 * Get the variable with the given id.
	 * 
	 * @param id the id of the variable
	 * @return the variable
	 */
	Variable getVariable(int id);
}
