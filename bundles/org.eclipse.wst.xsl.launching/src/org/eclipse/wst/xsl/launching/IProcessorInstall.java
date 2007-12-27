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
package org.eclipse.wst.xsl.launching;

/**
 * An instance of an XSLT processor (e.g. Xalan 2.7.0).
 * 
 * @see IProcessorInstall
 * @author Doug Satchwell
 */
public interface IProcessorInstall
{
	/**
	 * Get the unqiue id of this install
	 * @return the unique id
	 */
	String getId();

	/**
	 * Get the name of this install
	 * @return the name of this install
	 */
	String getName();

	/**
	 * Set the name of this install
	 */
	// TODO need to make IProcessorInstall immutable
	void setName(String name);

	/**
	 * Get the id of the processor type associated with this install
	 * @return the processor type id
	 */
	// TODO only need to return the processor type or the id - not both
	String getProcessorTypeId();

	/**
	 * Get the the processor type associated with this install
	 * @return the processor type id
	 */
	// TODO only need to return the processor type or the id - not both
	IProcessorType getProcessorType();
	
	/**
	 * Set the processor type
	 */
	// TODO need to make IProcessorInstall immutable
	void setProcessorTypeId(String id);

	/**
	 * Get the jar files for this processor
	 * @return a set of jar files
	 */
	IProcessorJar[] getProcessorJars();

	/**
	 * Set the list of jar files
	 */
	// TODO need to make IProcessorInstall immutable
	void setProcessorJars(IProcessorJar[] locations);

	/**
	 * Get whether this install has been contributed via the <code>org.eclipse.wst.xsl.launching.processor</code> extension point.
	 * @return <code>true</code> if contributed via the extension point, otherwise <code>false</code>.
	 */
	boolean isContributed();

	/**
	 * Get whether this install has an associated debugger
	 * @return <code>true</code> this install has a debugger, otherwise <code>false</code>.
	 */
	// TODO installs don't have debuggers - processor types do
	boolean hasDebugger();

	/**
	 * Get the list of supported XSLT versions
	 * @return <code>true</code> if this install has a debugger, otherwise <code>false</code>.
	 */
	// TODO deprecate and use boolean supports instaed
	String getSupports();

	/**
	 * Set the supported XSLT versions
	 */
	// TODO need to make IProcessorInstall immutable
	void setSupports(String supports);

	/**
	 * Get whether this install supports the specified xslt version.
	 * @return <code>true</code> if this install supports the version, otherwise <code>false</code>.
	 */
	boolean supports(String xsltVersion);

	/**
	 * Get the debugger associated with this install
	 * @return a debugger for this install
	 */
	// TODO installs don't have debuggers - processor types do
	IDebugger getDebugger();

	/**
	 * Set the debugger id
	 */
	// TODO need to make IProcessorInstall immutable
	void setDebuggerId(String debuggerId);
}
