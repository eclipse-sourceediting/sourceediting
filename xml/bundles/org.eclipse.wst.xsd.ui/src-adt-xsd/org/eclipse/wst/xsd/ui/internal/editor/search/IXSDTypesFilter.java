/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor.search;

public interface IXSDTypesFilter {	
	/**
	 * Give me an Object o, if I know it and it should be filtered out, I will 
	 * return true. Otherwise I'll return false, even if I don't know the object 
	 * @param o
	 * @return
	 */
	public boolean shouldFilterOut(Object o);
	
	public void turnOn();
	public void turnOff();
	public boolean isOn();
}
