/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - refactored to it's own class instead of an inline override.
 ********************************************************************************/

package org.eclipse.wst.xsl.core.internal.validation.eclipse;

import java.util.HashMap;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;

public class XSLValidationReport implements ValidationReport {
    private String uri;
	
	public XSLValidationReport(String uri) {
		this.uri = uri;
	}
	
	public String getFileURI()
	{
		return uri;
	}

	public HashMap<?, ?> getNestedMessages()
	{
		return new HashMap<Object, Object>();
	}

	public ValidationMessage[] getValidationMessages()
	{
		return new ValidationMessage[0];
	}

	public boolean isValid()
	{
		return true;
	}	
}
