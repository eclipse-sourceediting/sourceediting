/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.dtd.core.modelhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.encoding.EncodingRule;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.ModelDumper;

public final class DTDModelDumper implements ModelDumper {

	/* (non-Javadoc)
	 */
	public void dump(IStructuredModel model, OutputStream outputStream, EncodingRule encodingRule, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException {
		// TODO Auto-generated method stub
		System.out.println("Implement DTDModelDumper.dump()"); //$NON-NLS-1$

	}
}
