/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types;

import org.eclipse.core.resources.IFile;

/*
 * Loads Avaliable Types/Elements from the given IFile.  Implementors
 * needed to handle certain File types (xsd, wsdl, etc...)
 */
public abstract class LoadAvaliableItems {		
	public java.util.List getAvaliableItems(IFile file) {		                
		return loadFile(file);
	}
	
	protected abstract java.util.List loadFile(IFile iFile);
}