/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal.validation;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.internal.validation.MarkupValidator;

/**
 * @author dcarver
 *
 */
public class XSLMarkupValidator extends MarkupValidator implements IValidator, ISourceValidator {


	/**
	 *  (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void cleanup(IReporter reporter) {
		// TODO Auto-generated method stub
		super.cleanup(reporter);

	}

	/**
	 * 
	 * @param helper 
	 * @param reporter 
	 * @throws ValidationException 
	 * 
	 */
	@Override
	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		// TODO Auto-generated method stub
		super.validate(helper, reporter);
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.validation.MarkupValidator#connect(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void connect(IDocument document) {
        super.connect(document);
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.validation.MarkupValidator#disconnect(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void disconnect(IDocument document) {
		super.disconnect(document);
	}

	/**
	 *  (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.validation.MarkupValidator#validate(org.eclipse.jface.text.IRegion, org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {
		super.validate(dirtyRegion, helper, reporter);
	}
	
	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.validation.MarkupValidator#validate(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void validate(IStructuredDocumentRegion structuredDocumentRegion,
			IReporter reporter) {
		// TODO Auto-generated method stub
		super.validate(structuredDocumentRegion, reporter);
	}
	
}
