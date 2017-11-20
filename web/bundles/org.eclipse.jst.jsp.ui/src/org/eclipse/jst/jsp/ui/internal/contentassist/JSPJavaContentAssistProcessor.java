/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.internal.util.SharedXMLEditorPluginImageHelper;

/**
 * @plannedfor 1.0
 * @deprecated This class is no longer used locally and will be removed in the future
 */
public class JSPJavaContentAssistProcessor implements IContentAssistProcessor, IReleasable {
	/**
	 * Preference listener to keep track of changes to content assist
	 * preferences
	 */
	private class PreferenceListener implements IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			String property = event.getProperty();
			IPreferenceStore store = getJavaPreferenceStore();

			if (PreferenceConstants.CODEASSIST_AUTOACTIVATION.equals(property)) {
				fAutoActivate = store.getBoolean(PreferenceConstants.CODEASSIST_AUTOACTIVATION);
			}
			else if (PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA.equals(property)) {
				String autoCharacters = store.getString(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA);
				completionProposalAutoActivationCharacters = (autoCharacters != null) ? autoCharacters.toCharArray() : new char[0];
			}
		}
	}

	private boolean fAutoActivate = true;
	protected char completionProposalAutoActivationCharacters[] = new char[]{'.'};
	protected char contextInformationAutoActivationCharacters[] = null;
	protected static final String UNKNOWN_CONTEXT = JSPUIMessages.Content_Assist_not_availab_UI_;
	protected String fErrorMessage = null;
	protected JSPCompletionProcessor fJspCompletionProcessor = null;
	private IPropertyChangeListener fJavaPreferenceListener;

	public JSPJavaContentAssistProcessor() {
		super();
	}

	/**
	 * Return a list of proposed code completions based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text-editor control.
	 * 
	 * @param documentPosition
	 *            a location within the document
	 * @return an array of code-assist items
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentPosition) {

		IndexedRegion treeNode = ContentAssistUtils.getNodeAt(viewer, documentPosition);

		// get results from JSP completion processor
		fJspCompletionProcessor = getJspCompletionProcessor();
		ICompletionProposal[] results = fJspCompletionProcessor.computeCompletionProposals(viewer, documentPosition);
		fErrorMessage = fJspCompletionProcessor.getErrorMessage();
		if (results.length == 0 && (fErrorMessage == null || fErrorMessage.length() == 0)) {
			fErrorMessage = UNKNOWN_CONTEXT;
		}

		IDOMNode xNode = null;
		IStructuredDocumentRegion flat = null;
		if (treeNode instanceof IDOMNode) {
			xNode = (IDOMNode) treeNode;
			flat = xNode.getFirstStructuredDocumentRegion();
			if (flat != null && flat.getType() == DOMJSPRegionContexts.JSP_CONTENT) {
				flat = flat.getPrevious();
			}
		}

		// this is in case it's a <%@, it will be a region container...
		ITextRegion openRegion = null;
		if (flat != null && flat instanceof ITextRegionContainer) {
			ITextRegionList v = ((ITextRegionContainer) flat).getRegions();
			if (v.size() > 0)
				openRegion = v.get(0);
		}

		// ADD CDATA PROPOSAL IF IT'S AN XML-JSP TAG
		if (flat != null && flat.getType() != DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN && flat.getType() != DOMJSPRegionContexts.JSP_DECLARATION_OPEN && flat.getType() != DOMJSPRegionContexts.JSP_EXPRESSION_OPEN && flat.getType() != DOMRegionContext.BLOCK_TEXT && (openRegion != null && openRegion.getType() != DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN) && !inAttributeRegion(flat, documentPosition)) {

			// determine if cursor is before or after selected range
			int adjustedDocPosition = documentPosition;
			int realCaretPosition = viewer.getTextWidget().getCaretOffset();
			int selectionLength = viewer.getSelectedRange().y;
			if (documentPosition > realCaretPosition) {
				adjustedDocPosition -= selectionLength;
			}

			CustomCompletionProposal cdataProposal = createCDATAProposal(adjustedDocPosition, selectionLength);
			ICompletionProposal[] newResults = new ICompletionProposal[results.length + 1];
			System.arraycopy(results, 0, newResults, 0, results.length);
			newResults[results.length] = cdataProposal;
			results = newResults;
		}

		// (pa) ** this is code in progress...
		// add ending %> proposal for non closed JSP tags
		// String tagText = flat.getText();
		// // TODO need a much better compare (using constants?)
		//		if(tagText.equals("<%") || tagText.equals("<%=") || tagText.equals("<%!"));
		// {
		//			ICompletionProposal testah = ContentAssistUtils.computeJSPEndTagProposal(viewer,documentPosition, treeNode, "<%" , SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_GENERIC);
		// if(testah != null)
		// {
		//				ICompletionProposal[] newResults = new ICompletionProposal[results.length + 1];
		// System.arraycopy( results, 0, newResults, 0, results.length);
		// newResults[results.length] = testah;
		// results = newResults;
		// }
		// }

		return results;
	}

	private CustomCompletionProposal createCDATAProposal(int adjustedDocPosition, int selectionLength) {
		return new CustomCompletionProposal("<![CDATA[]]>", //$NON-NLS-1$
					adjustedDocPosition, selectionLength, // should be the selection length
					9, SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_CDATASECTION), 
					"CDATA Section", //$NON-NLS-1$
					null, null, XMLRelevanceConstants.R_CDATA);
	}

	private boolean inAttributeRegion(IStructuredDocumentRegion flat, int documentPosition) {
		ITextRegion attrContainer = flat.getRegionAtCharacterOffset(documentPosition);
		if (attrContainer != null && attrContainer instanceof ITextRegionContainer) {
			if (attrContainer.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of possible completions.
	 * 
	 * @return the auto activation characters for completion proposal or
	 *         <code>null</code> if no auto activation is desired
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		// if no listener has been created, preferenes have not been
		// initialized
		if (fJavaPreferenceListener == null)
			initializePreferences();

		if (fAutoActivate)
			return completionProposalAutoActivationCharacters;
		else
			return null;
	}

	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of context information.
	 * 
	 * @return the auto activation characters for presenting context
	 *         information or <code>null</code> if no auto activation is
	 *         desired
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return contextInformationAutoActivationCharacters;
	}

	/**
	 * Return the reason why computeProposals was not able to find any
	 * completions.
	 * 
	 * @return an error message or null if no error occurred
	 */
	public String getErrorMessage() {
		return fErrorMessage;
	}

	/**
	 * @see ContentAssistAdapter#release()
	 */
	public void release() {
		// remove listener on java preferences if we added one
		if (fJavaPreferenceListener != null) {
			getJavaPreferenceStore().removePropertyChangeListener(fJavaPreferenceListener);
		}

		if (fJspCompletionProcessor != null) {
			fJspCompletionProcessor.release();
			fJspCompletionProcessor = null;
		}
	}

	/**
	 * 
	 */
	protected JSPCompletionProcessor getJspCompletionProcessor() {
		if (fJspCompletionProcessor == null) {
			fJspCompletionProcessor = new JSPCompletionProcessor();
		}
		return fJspCompletionProcessor;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		List results = new ArrayList();
		// need to compute context info here, if it's JSP, call java computer
		IDocument doc = viewer.getDocument();
		IDocumentPartitioner dp = null;
		if (doc instanceof IDocumentExtension3) {
			dp = ((IDocumentExtension3) doc).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
		}
		if (dp != null) {
			//IDocumentPartitioner dp = viewer.getDocument().getDocumentPartitioner();
			String type = dp.getPartition(documentOffset).getType();
			if (type == IJSPPartitions.JSP_DEFAULT || type == IJSPPartitions.JSP_CONTENT_JAVA) {
				// get context info from completion results...
				ICompletionProposal[] proposals = computeCompletionProposals(viewer, documentOffset);
				for (int i = 0; i < proposals.length; i++) {
					IContextInformation ci = proposals[i].getContextInformation();
					if (ci != null)
						results.add(ci);
				}
			}
		}
		return (IContextInformation[]) results.toArray(new IContextInformation[results.size()]);
	}

	/**
	 * Returns a validator used to determine when displayed context
	 * information should be dismissed. May only return <code>null</code> if
	 * the processor is incapable of computing context information.
	 * 
	 * @return a context information validator, or <code>null</code> if the
	 *         processor is incapable of computing context information
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return new JavaParameterListValidator();
	}

	/**
	 * Gets the java preference store. If this is the first time getting it,
	 * add a preference listener to it.
	 * 
	 * @return IPreferenceStore
	 */
	private IPreferenceStore getJavaPreferenceStore() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		if (fJavaPreferenceListener == null) {
			fJavaPreferenceListener = new PreferenceListener();
			store.addPropertyChangeListener(fJavaPreferenceListener);
		}
		return store;
	}

	/**
	 * Initialize preference for content assist
	 */
	private void initializePreferences() {
		IPreferenceStore store = getJavaPreferenceStore();

		fAutoActivate = store.getBoolean(PreferenceConstants.CODEASSIST_AUTOACTIVATION);
		String autoCharacters = store.getString(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA);
		completionProposalAutoActivationCharacters = (autoCharacters != null) ? autoCharacters.toCharArray() : new char[0];
	}
}
