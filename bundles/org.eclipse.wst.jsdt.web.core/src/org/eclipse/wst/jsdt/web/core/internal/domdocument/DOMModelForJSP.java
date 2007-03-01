/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.core.internal.domdocument;

import org.eclipse.wst.html.core.internal.document.DOMStyleModelImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelParser;
import org.eclipse.wst.xml.core.internal.document.XMLModelUpdater;
import org.w3c.dom.Document;

public class DOMModelForJSP extends DOMStyleModelImpl {

	/**
	 * 
	 */
	public DOMModelForJSP() {
		super();
		// remember, the document is created in super constructor,
		// via internalCreateDocument
	}

	/**
	 * createDocument method
	 * 
	 * @return org.w3c.dom.Document
	 */
	@Override
	protected Document internalCreateDocument() {
		DOMDocumentForJSP document = new DOMDocumentForJSP();
		document.setModel(this);
		return document;
	}

	@Override
	protected XMLModelParser createModelParser() {
		return new NestedDOMModelParser(this);
	}

	@Override
	protected XMLModelUpdater createModelUpdater() {
		return new NestDOMModelUpdater(this);
	}
}
