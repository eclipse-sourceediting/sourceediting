/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 * IBM Corporation - bug489230 - Compilation error for CustomExtendedTagValidator
 *******************************************************************************/
package org.eclipse.wst.html.ui.tests.validation;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.html.core.internal.validate.Segment;
import org.eclipse.wst.html.core.validate.extension.CustomValidatorUtil;
import org.eclipse.wst.html.core.validate.extension.IHTMLCustomTagValidator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.junit.Assert;

public class CustomExtendedTagValidator implements IHTMLCustomTagValidator{
	private String currentFileLocation;
	
	public boolean canValidate(IDOMElement target) {
		if (target.getLocalName().startsWith("eclipse")) {
			return true;
		}
		return false;
	}

	public void init(IStructuredDocument doc) {
		currentFileLocation = getResource(doc);
	}

	public ValidationMessage validateTag(IDOMElement target) {
		Assert.assertEquals(currentFileLocation, getResource(target.getStructuredDocument()));
		String tagName = target.getLocalName();
		if (tagName.indexOf("thym") >= 0) {
			Segment segment = CustomValidatorUtil.getTagSegment(target, CustomValidatorUtil.SEG_START_TAG_NAME);
			return new ValidationMessage("Thym is available only with external installation", segment.getOffset(), segment.getLength(), ValidationMessage.ERROR);
		}
		return null;
	}
	
	private String getResource(IDocument document) {
		if (document == null) return null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (sModel != null) {
				return sModel.getBaseLocation();
			}
		}
		finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		
		return null;
	}
}
