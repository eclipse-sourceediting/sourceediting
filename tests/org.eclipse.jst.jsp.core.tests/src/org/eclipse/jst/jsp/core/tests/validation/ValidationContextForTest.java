/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.validation;

import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;

public class ValidationContextForTest implements IValidationContext {
	private String[] fURIs = null;

	public void setURI(String uri) {
		String[] uris = null;
		if (uri != null)
			uris = new String[]{uri};

		setURIs(uris);
	}
	
	public void setURIs(String[] uris) {
		fURIs = uris;
	}

	public String[] getURIs() {
		if (fURIs != null)
			return fURIs;
		return new String[0];
	}

	public Object loadModel(String symbolicName) {
		return null;
	}

	public Object loadModel(String symbolicName, Object[] parms) {
		return null;
	}

}
