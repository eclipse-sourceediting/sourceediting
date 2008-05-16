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
