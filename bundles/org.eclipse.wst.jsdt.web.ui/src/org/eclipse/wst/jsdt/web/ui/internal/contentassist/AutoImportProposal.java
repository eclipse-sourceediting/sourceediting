package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AutoImportProposal extends JSPCompletionProposal {

	// the import string, no quotes or colons
	String fImportDeclaration;

	public AutoImportProposal(String importDeclaration,
			String replacementString, int replacementOffset,
			int replacementLength, int cursorPosition, Image image,
			String displayString, IContextInformation contextInformation,
			String additionalProposalInfo, int relevance,
			boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, relevance,
				updateReplacementLengthOnValidate);
		setImportDeclaration(importDeclaration);
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {
		super.apply(viewer, trigger, stateMask, offset);
		addImportDeclaration(viewer);
	}

	/**
	 * adds the import declaration to the document in the viewer in the
	 * appropriate position
	 * 
	 * @param viewer
	 */
	private void addImportDeclaration(ITextViewer viewer) {

		IDocument doc = viewer.getDocument();

		// calculate once and pass along
		boolean isXml = isXmlFormat(doc);

		int insertPosition = getInsertPosition(doc, isXml);
		String insertText = createImportDeclaration(doc, isXml);
		InsertEdit insert = new InsertEdit(insertPosition, insertText);
		try {
			insert.apply(doc);
		} catch (MalformedTreeException e) {
			Logger.logException(e);
		} catch (BadLocationException e) {
			Logger.logException(e);
		}

		// make sure the cursor position after is correct
		setCursorPosition(getCursorPosition() + insertText.length());
	}

	/**
	 * 
	 * @param doc
	 * @param isXml
	 * @return position after <jsp:root> if xml, otherwise right before the
	 *         document element
	 */
	private int getInsertPosition(IDocument doc, boolean isXml) {
		int pos = 0;
		IStructuredModel sModel = StructuredModelManager.getModelManager()
				.getExistingModelForRead(doc);
		try {
			if (sModel != null) {
				if (sModel instanceof IDOMModel) {
					IDOMDocument documentNode = ((IDOMModel) sModel)
							.getDocument();
					Node docElement = documentNode.getDocumentElement();
					if (docElement != null && docElement instanceof IDOMElement) {
						IStructuredDocumentRegion sdRegion = ((IDOMElement) docElement)
								.getFirstStructuredDocumentRegion();
						if (isXml) {
							// insert right after document element
							pos = sdRegion.getEndOffset();
						} else {
							// insert before document element
							pos = sdRegion.getStartOffset();
						}
					}
				}
			}
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return pos;
	}

	/**
	 * 
	 * @param doc
	 * @return true if this document is xml-jsp syntax, otherwise false
	 */
	private boolean isXmlFormat(IDocument doc) {
		boolean isXml = false;
		IStructuredModel sModel = StructuredModelManager.getModelManager()
				.getExistingModelForRead(doc);
		try {
			if (sModel != null) {
				if (!isXml) {
					if (sModel instanceof IDOMModel) {
						IDOMDocument documentNode = ((IDOMModel) sModel)
								.getDocument();
						Element docElement = documentNode.getDocumentElement();
						isXml = docElement != null
								&& ((docElement.getNodeName()
										.equals("jsp:root")) || ((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null && ((IDOMNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$
					}
				}
			}
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return isXml;
	}

	/**
	 * 
	 * @param doc
	 * @param isXml
	 * @return appropriate import declaration string depending if document is
	 *         xml or not
	 */
	private String createImportDeclaration(IDocument doc, boolean isXml) {
		String delim = (doc instanceof IStructuredDocument) ? ((IStructuredDocument) doc)
				.getLineDelimiter()
				: TextUtilities.getDefaultLineDelimiter(doc);
		if (isXml) {
			return delim
					+ "<jsp:directive.page import=\"" + getImportDeclaration() + "\"/>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "<%@page import=\"" + getImportDeclaration() + "\"%>" + delim; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getImportDeclaration() {
		return fImportDeclaration;
	}

	public void setImportDeclaration(String importDeclaration) {
		fImportDeclaration = importDeclaration;
	}
}
