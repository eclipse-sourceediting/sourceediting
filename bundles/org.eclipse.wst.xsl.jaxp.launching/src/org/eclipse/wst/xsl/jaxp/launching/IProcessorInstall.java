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
package org.eclipse.wst.xsl.jaxp.launching;

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
	 * Get the the processor type associated with this install
	 * @return the processor type id
	 */
	IProcessorType getProcessorType();

	/**
	 * Get the jar files for this processor
	 * @return a set of jar files
	 */
	IProcessorJar[] getProcessorJars();

	/**
	 * Get whether this install has been contributed via the <code>org.eclipse.wst.xsl.launching.processor</code> extension point.
	 * @return <code>true</code> if contributed via the extension point, otherwise <code>false</code>.
	 */
	boolean isContributed();

	/**
	 * Get the list of supported XSLT versions
	 * @return <code>true</code> if this install has a debugger, otherwise <code>false</code>.
	 */
	// TODO deprecate and use boolean supports instaed
	String getSupports();

	/**
	 * Get whether this install supports the specified xslt version.
	 * @return <code>true</code> if this install supports the version, otherwise <code>false</code>.
	 */
	boolean supports(String xsltVersion);

	/**
	 * Get the debugger associated with this install
	 * @return the debugger, or null if no debugger has been set
	 */
	IDebugger getDebugger();
}
