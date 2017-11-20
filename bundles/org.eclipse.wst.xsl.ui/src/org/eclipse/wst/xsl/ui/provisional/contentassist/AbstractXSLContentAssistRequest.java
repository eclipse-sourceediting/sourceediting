package org.eclipse.wst.xsl.ui.provisional.contentassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentassist.ProposalComparator;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An extension of the XML ContentAssistRequest class.  This provides
 * a basis for the XSL content assistance.  Classes may subclass this
 * class and implement specific functionality.
 * 
 * @author dcarver
 * @since 1.1
 */
public abstract class AbstractXSLContentAssistRequest implements IContentAssistProposalRequest {
	protected IStructuredDocumentRegion documentRegion = null;
	protected ArrayList<ICompletionProposal> macros = new ArrayList<ICompletionProposal>();
	protected String matchString;
	protected Node node = null;
	protected ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
	protected ITextRegion region = null;
	protected int replacementBeginPosition;
	protected int replacementLength;
	protected ITextViewer textViewer = null;


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
	
	public AbstractXSLContentAssistRequest(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		setNode(node);
		setDocumentRegion(documentRegion);
		setRegion(completionRegion);
		setMatchString(filter);
		setReplacementBeginPosition(begin);
		setReplacementLength(length);
		this.textViewer = textViewer;
	}

	/**
	 * Returns a list of proposals.  Implementations are to provide the appropriate
	 * implementation for the proposals they would like to return.   Use of the getAllCompletionProposals
	 * should be used to return the actual proposals from this method.
	 * @return
	 */
	public abstract ArrayList<ICompletionProposal> getCompletionProposals();
	
	protected ArrayList<ICompletionProposal> getAllCompletionProposals() {
		ArrayList<ICompletionProposal> allProposals = new ArrayList<ICompletionProposal>();
		if ((getProposals().size() > 0) || (getMacros().size() > 0)) {
			allProposals.addAll(getProposals());
			allProposals.addAll(getMacros());
			allProposals = sortProposals(allProposals);
		}
		return allProposals;
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
	
	/**
	 * @param newProposal
	 */
	protected void addMacro(ICompletionProposal newProposal) {
		macros.add(newProposal);
	}

	protected void addProposal(ICompletionProposal newProposal) {
		proposals.add(newProposal);
	}

	protected IStructuredDocumentRegion getDocumentRegion() {
		return documentRegion;
	}

	protected List<ICompletionProposal> getMacros() {
		return macros;
	}

	protected java.lang.String getMatchString() {
		return matchString;
	}

	protected org.w3c.dom.Node getNode() {
		return node;
	}

	protected org.w3c.dom.Node getParent() {
		return node.getParentNode();
	}

	protected List<ICompletionProposal> getProposals() {
		return proposals;
	}

	protected ITextRegion getRegion() {
		return region;
	}

	protected int getReplacementBeginPosition() {
		return replacementBeginPosition;
	}

	protected int getReplacementLength() {
		return replacementLength;
	}

	protected int getStartOffset() {
		if ((getDocumentRegion() != null) && (getRegion() != null)) {
			return ((ITextRegionCollection) getDocumentRegion()).getStartOffset(getRegion());
		}
		return -1;
	}

	protected String getText() {
		if ((getDocumentRegion() != null) && (getRegion() != null)) {
			return ((ITextRegionCollection) getDocumentRegion()).getText(getRegion());
		}
		return ""; //$NON-NLS-1$
	}

	protected int getTextEndOffset() {
		if ((getDocumentRegion() != null) && (getRegion() != null)) {
			return ((ITextRegionCollection) getDocumentRegion()).getTextEndOffset(getRegion());
		}
		return -1;
	}

	protected void setDocumentRegion(IStructuredDocumentRegion region) {
		documentRegion = region;
	}

	protected void setMatchString(java.lang.String newMatchString) {
		matchString = newMatchString;
	}

	
	protected void setNode(org.w3c.dom.Node newNode) {
		node = newNode;
	}


	protected void setRegion(ITextRegion newRegion) {
		region = newRegion;
	}

	protected void setReplacementBeginPosition(int newReplacementBeginPosition) {
		replacementBeginPosition = newReplacementBeginPosition;
	}


	protected void setReplacementLength(int newReplacementLength) {
		replacementLength = newReplacementLength;
	}

	protected ArrayList<ICompletionProposal> sortProposals(ArrayList<ICompletionProposal> proposalsIn) {
		Collections.sort(proposalsIn, new ProposalComparator());
		return proposalsIn;

	}

	/**
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String toString() {
		return "Node: " + getNode() //$NON-NLS-1$
					+ "\nParent: " + getParent() //$NON-NLS-1$
					+ "\nStructuredDocumentRegion: " + StringUtils.escape(getDocumentRegion().toString()) //$NON-NLS-1$
					+ "\nRegion: " + getRegion() //$NON-NLS-1$
					+ "\nMatch string: '" + StringUtils.escape(getMatchString()) + "'" //$NON-NLS-2$//$NON-NLS-1$
					+ "\nOffsets: [" + getReplacementBeginPosition() + "-" + (getReplacementBeginPosition() + getReplacementLength()) + "]\n"; //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}

	protected StylesheetModel getStylesheetModel() {
		IFile editorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getLocation()));
		StylesheetModel model = XSLCore.getInstance().getStylesheet(editorFile);
		return model;
	}
	
		
}
