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
