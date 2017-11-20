/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
