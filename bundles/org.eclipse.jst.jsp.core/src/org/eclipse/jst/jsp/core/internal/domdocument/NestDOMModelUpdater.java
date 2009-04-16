/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.domdocument;

import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelUpdater;


public class NestDOMModelUpdater extends XMLModelUpdater {

	/**
	 * @param model
	 */
	public NestDOMModelUpdater(DOMModelImpl model) {
		super(model);
	}

	protected boolean isNestedTagClose(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_CLOSE || regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE;
		return result;
	}

}
