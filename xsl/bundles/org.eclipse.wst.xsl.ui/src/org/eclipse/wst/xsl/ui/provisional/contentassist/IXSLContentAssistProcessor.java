/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.provisional.contentassist;

import java.util.ArrayList;

/**
 * @since 1.1
 */
public interface IXSLContentAssistProcessor {

	/**
	 * ArrayList of Strings for the namespaces this processor activates against.
	 * @return
	 */
	public ArrayList<String> getNamespaces();
	
	public String getMinimumVersion();
	
	public String getMaximumVersion();
	
}
