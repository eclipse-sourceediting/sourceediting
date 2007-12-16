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

import java.io.File;

import org.eclipse.core.runtime.IStatus;

public interface IProcessorInstall
{
	String getId();

	String getLabel();

	void setLabel(String name);

	String getProcessorTypeId();

	IProcessorType getProcessorType();

	void setProcessorTypeId(String id);

	IProcessorJar[] getProcessorJars();

	void setProcessorJars(IProcessorJar[] locations);

	boolean isContributed();

	boolean hasDebugger();

	String getSupports();

	void setSupports(String supports);

	boolean supports(String xsltVersion);

	/**
	 * Validates the given location of a processor installation.
	 * <p>
	 * For example, an implementation might check whether the processor
	 * libraries are present.
	 * </p>
	 * 
	 * @param installLocation
	 *            the root directory of a potential installation for this type
	 *            of processor
	 * @return a status object describing whether the install location is valid
	 */
	IStatus validateInstallLocation(File installLocation);

	IDebugger getDebugger();

	void setDebuggerId(String debuggerId);
}
