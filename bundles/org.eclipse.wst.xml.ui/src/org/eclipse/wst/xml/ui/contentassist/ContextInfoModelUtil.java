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

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;


/**
 * @author pavery
 */
public class ContextInfoModelUtil {
	IStructuredDocument fDocument = null;
	private final String SSE_MODEL_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$

	ContextInfoModelUtil(IStructuredDocument doc) {
		fDocument = doc;
	}

	public IStructuredDocument getDocument() {
		return fDocument;
	}

	public IModelManager getModelManager() {
		return ((IModelManagerPlugin) Platform.getPlugin(SSE_MODEL_ID)).getModelManager();
	}

	public ModelQuery getModelQuery() {
		ModelQuery mq = null;

		XMLModel xmlModel = (XMLModel) getModelManager().getExistingModelForRead(getDocument());
		mq = ModelQueryUtil.getModelQuery(xmlModel.getDocument());
		xmlModel.releaseFromRead();

		return mq;
	}

	public XMLNode getXMLNode(int offset) {
		XMLModel xmlModel = (XMLModel) getModelManager().getExistingModelForRead(getDocument());
		XMLNode xmlNode = (XMLNode) xmlModel.getIndexedRegion(offset);
		xmlModel.releaseFromRead();
		return xmlNode;
	}
}
