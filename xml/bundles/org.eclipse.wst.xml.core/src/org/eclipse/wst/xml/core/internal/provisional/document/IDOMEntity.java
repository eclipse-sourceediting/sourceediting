/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.provisional.document;

import org.w3c.dom.Entity;

public interface IDOMEntity extends Entity {

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation of DOM 3.
	 */
	public String getInputEncoding();

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation of DOM 3.
	 */
	public String getXmlEncoding();

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation of DOM 3.
	 */
	public String getXmlVersion();
}
