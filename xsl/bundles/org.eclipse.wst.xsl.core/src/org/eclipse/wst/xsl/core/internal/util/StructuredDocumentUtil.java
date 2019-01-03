/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.util;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * General Purpose utility classes to convert from StructuredDocument to DOM.
 * 
 * @author dcarver
 *
 */
public class StructuredDocumentUtil {
	
	/**
	 * Given a StructuredDocumentRegion and a TextRegion, return a
	 * IDOMNode for that particular position in the StructuredDocument
	 * 
	 * @param documentRegion
	 * @param textRegion
	 * @return IDOMNode
	 */
	public static IDOMNode getNode(IStructuredDocumentRegion documentRegion, ITextRegion textRegion) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(documentRegion.getParentDocument());
		IDOMDocument documentNode = ((IDOMModel) sModel).getDocument();

		return (IDOMNode)documentNode.getModel().getIndexedRegion(documentRegion.getStartOffset(textRegion));
	}

}
