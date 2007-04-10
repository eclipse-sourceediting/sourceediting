/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;

import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;

public interface TLDFunction {

	String getClassName();
	
	String getDescription();

	String getDisplayName();

	String getExample();

	List getExtensions();

	String getIcon();

	String getName();

	CMDocument getOwnerDocument();

	String getSignature();
}
