/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.validation.internal.operations.ReferencialFileValidator;

public class XSLReferencialFileValidator implements ReferencialFileValidator {

	public XSLReferencialFileValidator() {
		System.out.println("XSLReferencialFileValidator ctor");
	}
	
	public List<IFile> getReferencedFile(List inputFiles) {
		return new ArrayList<IFile>();
	}

}
