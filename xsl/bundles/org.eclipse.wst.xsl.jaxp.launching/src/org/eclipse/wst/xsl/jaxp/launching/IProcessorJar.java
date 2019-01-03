/*******************************************************************************
 * Copyright (c) 2007, 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching;

import java.net.URL;

import org.eclipse.core.runtime.IPath;

/**
 * A jar file for an XSLT processor
 * 
 * @see IProcessorInstall
 * @author Doug Satchwell
 */
public interface IProcessorJar
{
	/**
	 * The path may be relative or absolute; in the workspace or external.
	 * @return the path to this jar
	 */
	IPath getPath();

	/**
	 * The URL will always be absolute and can be opened.
	 * @return a URL to the jar
	 */
	URL asURL();
}
