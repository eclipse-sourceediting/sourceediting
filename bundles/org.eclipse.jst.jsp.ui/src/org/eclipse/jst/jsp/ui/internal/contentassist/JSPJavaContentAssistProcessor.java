/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.text.java.JavaParameterListValidator;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jst.jsp.core.internal.text.rules.StructuredTextPartitionerForJSP;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.ui.IReleasable;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.contentassist.IResourceDependentProcessor;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.util.SharedXMLEditorPluginImageHelper;

public class JSPJavaContentAssistProcessor implements IContentAssistProcessor, IResourceDependentProcessor, IReleasable {
	protected IResource fResource;
	protected char completionProposalAutoActivationCharacters[] = new char[]{'.'};
	protected char contextInformationAutoActivationCharacters[] = null;
	protected static final String UNKNOWN_CONTEXT = SSEUIPlugin.getResourceString("%Content_Assist_not_availab_UI_"); //$NON-NLS-1$ = "Content Assist not available at the current location "
	protected String fErrorMessage = null;
	private JSPCompletionProcessor fJspCompletionProcessor = null;

	public JSPJavaContentAssistProcessor() {
		super();
	}

	public JSPJavaContentAssistProcessor(IResource file) {
		super();
		fResource = file;
	}

	public static void main(String[] args) {
		JSPJavaContentAssistProcessor instance = new JSPJavaContentAssistProcessor();
		// unit test for 'reverse' function
		instance._unitTest0();
		instance._unitTestNormal1();
		instance._unitTestNormal2();
		instance._unitTestNormal3();
		instance._unitTestNormal4();
		instance._unitTestNormal5();
	}

	private void _printList(Object[] list) {
		for (int i = 0; i < list.length; i++) {
			System.out.print(list[i] + " "); //$NON-NLS-1$
		}
		System.out.println();

	}

	private void _unitTest0() {
		Object[] listOfObjects = new Object[0];
		_printList(listOfObjects);
		reverse(listOfObjects);
		_printList(listOfObjects);
	}

	private void _unitTestNormal1() {
		Object[] listOfObjects = new Object[1];
		listOfObjects[0] = "one"; //$NON-NLS-1$
		_printList(listOfObjects);
		reverse(listOfObjects);
		_printList(listOfObjects);
	}

	private void _unitTestNormal2() {
		Object[] listOfObjects = new Object[2];
		listOfObjects[0] = "one"; //$NON-NLS-1$
		listOfObjects[1] = "two"; //$NON-NLS-1$
		_printList(listOfObjects);
		reverse(listOfObjects);
		_printList(listOfObjects);
	}

	private void _unitTestNormal3() {
		Object[] listOfObjects = new Object[3];
		listOfObjects[0] = "one"; //$NON-NLS-1$
		listOfObjects[1] = "two"; //$NON-NLS-1$
		listOfObjects[2] = "three"; //$NON-NLS-1$
		_printList(listOfObjects);
		reverse(listOfObjects);
		_printList(listOfObjects);
	}

	private void _unitTestNormal4() {
		Object[] listOfObjects = new Object[4];
		listOfObjects[0] = "one"; //$NON-NLS-1$
		listOfObjects[1] = "two"; //$NON-NLS-1$
		listOfObjects[2] = "three"; //$NON-NLS-1$
		listOfObjects[3] = "four"; //$NON-NLS-1$
		_printList(listOfObjects);
		reverse(listOfObjects);
		_printList(listOfObjects);
	}

	private void _unitTestNormal5() {
		Object[] listOfObjects = new Object[5];
		listOfObjects[0] = "one"; //$NON-NLS-1$
		listOfObjects[1] = "two"; //$NON-NLS-1$
		listOfObjects[2] = "three"; //$NON-NLS-1$
		listOfObjects[3] = "four"; //$NON-NLS-1$
		listOfObjects[4] = "five"; //$NON-NLS-1$
		_printList(listOfObjects);
		reverse(listOfObjects);
		_printList(listOfObjects);
	}

	/**
	 * Return a list of proposed code completions based on the
	 * specified location within the document that corresponds
	 * to the current cursor position within the text-editor control.
	 *
	 * @param documentPosition a location within the document
	 * @return an array of code-assist items 
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentPosition) {

		IndexedRegion treeNode = ContentAssistUtils.getNodeAt((StructuredTextViewer) viewer, documentPosition);

		// get results from JSP completion processor	
		fJspCompletionProcessor = getJspCompletionProcessor();
		ICompletionProposal[] results = fJspCompletionProcessor.computeCompletionProposals(viewer, documentPosition);
		fErrorMessage = fJspCompletionProcessor.getErrorMessage();
		if (results.length == 0 && (fErrorMessage == null || fErrorMessage.length() == 0)) {
			fErrorMessage = UNKNOWN_CONTEXT;
		}

		XMLNode xNode = null;
		IStructuredDocumentRegion flat = null;
		if (treeNode instanceof XMLNode) {
			xNode = (XMLNode) treeNode;
			flat = xNode.getFirstStructuredDocumentRegion();
			if (flat != null && flat.getType() == XMLJSPRegionContexts.JSP_CONTENT) {
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
		if (flat != null && flat.getType() != XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN && flat.getType() != XMLJSPRegionContexts.JSP_DECLARATION_OPEN && flat.getType() != XMLJSPRegionContexts.JSP_EXPRESSION_OPEN && flat.getType() != XMLRegionContext.BLOCK_TEXT && (openRegion != null && openRegion.getType() != XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN) && !inAttributeRegion(flat, documentPosition)) {

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
		//		String tagText = flat.getText();
		//		// TODO need a much better compare (using constants?)
		//		if(tagText.equals("<%") || tagText.equals("<%=") || tagText.equals("<%!"));
		//		{
		//			ICompletionProposal testah = ContentAssistUtils.computeJSPEndTagProposal(viewer,documentPosition, treeNode, "<%" , SharedXMLEditorPluginImageHelper.IMG_OBJ_TAG_GENERIC);
		//			if(testah != null)
		//			{
		//				ICompletionProposal[] newResults = new ICompletionProposal[results.length + 1];
		//				System.arraycopy( results, 0, newResults, 0, results.length);
		//				newResults[results.length] = testah;
		//				results = newResults;
		//			}
		//		}

		reverse(results); // so variables show up first	
		return results;
	}

	// reverses an array of objects 
	public void reverse(Object[] array) {
		Object temp = null;
		//DMW: 8/16/2002 this was <=, but was thrown out of bounds occasionally
		for (int i = 0; i < (array.length / 2); i++) {
			temp = array[i];
			array[i] = array[(array.length - 1) - i];
			array[(array.length - 1) - i] = temp;
		}
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
			if (attrContainer.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of possible completions.
	 *
	 * @return the auto activation characters for completion proposal or <code>null</code>
	 *		if no auto activation is desired
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return completionProposalAutoActivationCharacters;
	}

	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of context information.
	 *
	 * @return the auto activation characters for presenting context information
	 *		or <code>null</code> if no auto activation is desired
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return contextInformationAutoActivationCharacters;
	}

	/**
	 * Return the reason why computeProposals was not able to find any completions.
	 *
	 * @return an error message
	 *   or null if no error occurred
	 */
	public String getErrorMessage() {
		return fErrorMessage;
	}

	/**
	 * @see ContentAssistAdapter#release()
	 */
	public void release() {
		fJspCompletionProcessor.release();
		fJspCompletionProcessor = null;
		fResource = null;
	}

	/**
	 * @see ContentAssistAdapter#initialize(IResource)
	 */
	public void initialize(IResource resource) {
		fResource = resource;
		getJspCompletionProcessor().initialize(resource);
	}

	/**
	 * 
	 */
	private JSPCompletionProcessor getJspCompletionProcessor() {
		if (fJspCompletionProcessor == null) {
			fJspCompletionProcessor = new JSPCompletionProcessor(fResource);
			fJspCompletionProcessor.initialize(fResource);
		}
		return fJspCompletionProcessor;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		List results = new ArrayList();
		// need to compute context info here, if it's JSP, call java computer
		IDocumentPartitioner dp = viewer.getDocument().getDocumentPartitioner();
		String type = dp.getPartition(documentOffset).getType();
		if (type == StructuredTextPartitionerForJSP.ST_DEFAULT_JSP || type == StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA) {
			// get context info from completion results...
			ICompletionProposal[] proposals = computeCompletionProposals(viewer, documentOffset);
			for (int i = 0; i < proposals.length; i++) {
				IContextInformation ci = proposals[i].getContextInformation();
				if (ci != null)
					results.add(ci);
			}
		}
		return (IContextInformation[]) results.toArray(new IContextInformation[results.size()]);
	}

	/**
	 * Returns a validator used to determine when displayed context information
	 * should be dismissed. May only return <code>null</code> if the processor is
	 * incapable of computing context information.
	 *
	 * @return a context information validator, or <code>null</code> if the processor
	 * 			is incapable of computing context information
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return new JavaParameterListValidator();
	}
}