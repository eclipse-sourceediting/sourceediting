package org.eclipse.wst.html.ui.internal.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Detects hyperlinks in tags. Includes detection in DOCTYPE and attribute
 * values. Resolves references to schemas, dtds, href, file, src, etc using the
 * Web Project Resolver.
 * 
 */
public class URIHyperlinkDetector extends XMLHyperlinkDetector {
	// copies of this class exist in:
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.jst.jsp.ui.internal.hyperlink

	private final String SCHEMA_LOCATION = "schemaLocation"; //$NON-NLS-1$
	private final String XMLNS = "xmlns"; //$NON-NLS-1$
	private final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$

	protected String getURIString(Node node, IDocument document) {
		if (isXMLHandled(node))
			return super.getURIString(node, document);
		
		String resolvedURI = null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			// handle attribute node
			Attr attrNode = (Attr) node;
			String attrValue = attrNode.getValue();
			attrValue = StringUtils.strip(attrValue);

			if (attrValue != null && attrValue.length() > 0) {
				// currently using model's URI resolver until a better resolver is
				// extended to common extensible URI resolver
				// future_TODO: should use the new common extensible URI resolver
				// when
				// clients start implementing it
				IStructuredModel sModel = null;
				try {
					sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
					if (sModel != null) {
						URIResolver resolver = sModel.getResolver();
						resolvedURI = resolver != null ? resolver.getLocationByURI(attrValue, true) : attrValue;
					}
				}
				catch (Exception e) {
					Logger.logException(e);
				}
				finally {
					if (sModel != null)
						sModel.releaseFromRead();
				}
			}
		}
		return resolvedURI;
	}
	
	/**
	 * Check if this is an xml-related node (schema location, doctype).
	 * @param node
	 * @return
	 */
	private boolean isXMLHandled(Node node) {
		short nodeType = node.getNodeType();
		if (nodeType == Node.DOCUMENT_TYPE_NODE) {
			return true;
		} else if (nodeType == Node.ATTRIBUTE_NODE) {
			Attr attrNode = (Attr) node;
			String attrName = attrNode.getName();

			// handle schemaLocation attribute
			String prefix = DOMNamespaceHelper.getPrefix(attrName);
			String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attrName);
			if ((XMLNS.equals(prefix)) || (XMLNS.equals(unprefixedName))) {
				return true;
			} else if ((XSI_NAMESPACE_URI.equals(DOMNamespaceHelper.getNamespaceURI(attrNode))) && (SCHEMA_LOCATION.equals(unprefixedName))) {
				return true;
			}
		}
		return false;
	}
}
