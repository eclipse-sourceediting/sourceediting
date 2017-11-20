/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Sep 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.html.core.internal.document;

import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.w3c.dom.Document;

/**
 * @author davidw
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DOMStyleModelImpl extends DOMModelImpl {
	public DOMStyleModelImpl() {
		// remember, the document is created in super constructor, 
		// via internalCreateDocument
		super();
	}

	public void releaseFromEdit() {
		releaseStyleSheets();
		super.releaseFromEdit();
	}

	/**
	 */
	public void releaseFromRead() {
		releaseStyleSheets();
		super.releaseFromRead();
	}

	private void releaseStyleSheets() {
		if (!isShared()) {
			Document doc = getDocument();
			if (doc instanceof DocumentStyleImpl) {
				((DocumentStyleImpl) doc).releaseStyleSheets();
			}
		}
	}

	/**
	 * createDocument method
	 * @return org.w3c.dom.Document
	 */
	protected Document internalCreateDocument() {
		DocumentStyleImpl document = new DocumentStyleImpl();
		document.setModel(this);
		return document;
	}


}
