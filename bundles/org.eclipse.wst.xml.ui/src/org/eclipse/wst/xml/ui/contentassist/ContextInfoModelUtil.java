/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.contentassist;

import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;


/**
 * @author pavery
 */
public class ContextInfoModelUtil {
	IStructuredDocument fDocument = null;

	ContextInfoModelUtil(IStructuredDocument doc) {
		fDocument = doc;
	}

	public IStructuredDocument getDocument() {
		return fDocument;
	}

	public ModelQuery getModelQuery() {
		ModelQuery mq = null;

		IStructuredModel xmlModel = null;
		try {
			xmlModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
			mq = ModelQueryUtil.getModelQuery(xmlModel);
		}
		finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return mq;
	}

	public XMLNode getXMLNode(int offset) {
		IStructuredModel xmlModel = null;
		XMLNode xmlNode = null;
		try {
			xmlModel = StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
			xmlNode = (XMLNode) xmlModel.getIndexedRegion(offset);
		}
		finally {
			xmlModel.releaseFromRead();
		}
		return xmlNode;
	}
}
