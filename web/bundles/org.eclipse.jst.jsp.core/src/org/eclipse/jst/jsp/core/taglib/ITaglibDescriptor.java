/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.taglib;

/**
 * A representation of information within a tag library descriptor. Provides
 * much of the high-level information expressed by the descriptor.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.2
 */
public interface ITaglibDescriptor {
	/**
	 * @return the description value of this tag library
	 */
	String getDescription();
	/**
	 * @return the display name value of this tag library
	 */
	String getDisplayName();
	/**
	 * @return the stated required JSP version of this tag library
	 */
	String getJSPVersion();
	/**
	 * @return a URL string to the large icon for this tag library
	 */
	String getLargeIcon();
	/**
	 * @return the stated short name for this tag library
	 */
	String getShortName();
	/**
	 * @return a URL string to the small icon for this tag library
	 */
	String getSmallIcon();
	/**
	 * @return the stated version of this tag library if specified
	 */
	String getTlibVersion();
	/**
	 * @return a URI pointing to this tag library's descriptor, or null
	 */
	String getURI();
}
