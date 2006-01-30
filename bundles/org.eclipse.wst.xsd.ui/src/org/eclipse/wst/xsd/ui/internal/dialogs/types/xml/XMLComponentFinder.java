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
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xml;

import org.eclipse.core.resources.IFile;

/**
 * this thing parsers xml artifacts and picks out the specified components attributes
 * 
 */
public class XMLComponentFinder {
    public static final int ENCLOSING_PROJECT_SCOPE = 0;
    public static final int ENTIRE_WORKSPACE_SCOPE = 1;

    protected IFile currentIFile;

	public XMLComponentFinder() {
	}
    
    /*
     * Takes in the IFile we are currently editing.
     * The currentIFile must be set before the getEnclosingProjectFiles()
     * method will return correctly.
     */
    public void setFile(IFile file) {
        currentIFile = file;
    }
}
