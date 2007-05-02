package org.eclipse.wst.html.internal.validation;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;


public class HTMLPartValidator extends HTMLValidator {

	private IDocument fDocument;
	
	public void connect(IDocument document) {
		fDocument = document;
		System.out.println("Unimplemented method:HTMLPartValidator.connect");
	}

	public void disconnect(IDocument document) {
		fDocument = null;
		System.out.println("Unimplemented method:HTMLPartValidator.disconnect");
		
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		// TODO Auto-generated method stub
		System.out.println("Unimplemented method:HTMLPartValidator.validate");
		
	}

}
