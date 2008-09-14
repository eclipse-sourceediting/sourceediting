package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.Collection;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An extension of the XML ContentAssistRequest class.  This provides
 * a basis for the XSL content assistance.  Classes may subclass this
 * class and implement specific functionality.
 * 
 * @author dcarver
 *
 */
public abstract class AbstractXSLContentAssistRequest extends
		ContentAssistRequest {
	
	protected ITextViewer textViewer = null;

	/**
	 * @param node
	 * @param parent
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @deprecated  
	 */
	public AbstractXSLContentAssistRequest(Node node, Node parent,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter) {
		super(node, parent, documentRegion, completionRegion, begin, length, filter);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Handles Content Assistance requests for Select Attributes.  This is called an instantiated
	 * through the use of the computeProposals method from the XSLContentAssistProcessor.  It will
	 * calculate the available proposals that are available for the XSL select attribute.
	 * 
	 * @param node
	 * @param parent
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	
	public AbstractXSLContentAssistRequest(Node node, Node parent,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, parent, documentRegion, completionRegion, begin, length, filter);
		this.textViewer = textViewer;
	}

	/**
	 * Checks to make sure that the NodeList has data
	 * @param nodes A NodeList object
	 * @return True if has data, false if empty
	 */
	protected boolean hasNodes(NodeList nodes) {
		return nodes != null && nodes.getLength() > 0;
	}

	/**
	 * Get the cursor position within the Text Viewer
	 * @return An int value containing the cursor position
	 */
	protected int getCursorPosition() {
		return textViewer.getTextWidget().getCaretOffset();
	}
	
	protected Collection<NamespaceInfo> getNamespaces(IDOMElement element) {
		 NamespaceTable table = new NamespaceTable(element.getOwnerDocument());
		 table.visitElement(element);
		 
		 Collection<NamespaceInfo> namespaceInfoList =  table.getNamespaceInfoCollection();
		 
		 return namespaceInfoList;
	}

	/**
	 * Retrieves the base location for the IDOMDocument for this class. This is
	 * used to populate a new Path class for retrieving an IFile instance.
	 * @return
	 */
	protected String getLocation() {
		IDOMDocument document = (IDOMDocument) node.getOwnerDocument();
		return document.getModel().getBaseLocation();		
	}
	
		
}
