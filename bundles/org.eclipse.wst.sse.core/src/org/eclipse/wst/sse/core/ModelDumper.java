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
package org.eclipse.wst.sse.core;



import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.encoding.EncodingRule;

/**
 * Classes that implement this interface are responsible
 * for saving the model to the file system (or wherever
 * the dumpter wants).
 * 
 */
public interface ModelDumper {

	void dump(IStructuredModel model, OutputStream outputStream, EncodingRule encodingRule, IFile iFile) throws UnsupportedEncodingException, IOException, CoreException;
}
