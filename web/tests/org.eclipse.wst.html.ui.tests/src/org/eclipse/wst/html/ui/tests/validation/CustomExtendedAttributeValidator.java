/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.tests.validation;

import org.eclipse.wst.html.core.internal.validate.Segment;
import org.eclipse.wst.html.core.validate.extension.CustomValidatorUtil;
import org.eclipse.wst.html.core.validate.extension.IHTMLCustomAttributeValidator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class CustomExtendedAttributeValidator implements IHTMLCustomAttributeValidator {

	public void init(IStructuredDocument doc) {
		// do nothing		
	}

	public boolean canValidate(IDOMElement target, String attrName) {
		if (target.getLocalName().startsWith("eclipse")) {
			return true;
		}
		return false;
	}


	public ValidationMessage validateAttribute(IDOMElement target, String attrName) {
		if ("plugins".equals(attrName)) {
			try {
				String attrValue = target.getAttribute(attrName);
				Integer.parseInt(attrValue);
			} catch (NumberFormatException e) {
				Segment segment = CustomValidatorUtil.getAttributeSegment((IDOMNode)target.getAttributeNode(attrName), CustomValidatorUtil.ATTR_REGION_NAME);
				return new ValidationMessage("Attribute should be integer", segment.getOffset(), segment.getLength(), ValidationMessage.ERROR);				
			}
		}
		return null;
	}
}
