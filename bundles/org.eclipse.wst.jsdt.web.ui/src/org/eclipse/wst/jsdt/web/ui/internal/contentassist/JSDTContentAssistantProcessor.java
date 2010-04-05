/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;
import org.osgi.framework.Bundle;

/**
 * Provisional API: This class/interface is part of an interim API that is
 * still under development and expected to change significantly before
 * reaching stability. It is being made available at this early stage to
 * solicit feedback from pioneering adopters on the understanding that any
 * code that uses this API will almost certainly be broken (repeatedly) as the
 * API evolves.
 * 
 * This class is not intended to be subclassed.
 */
public class JSDTContentAssistantProcessor extends AbstractContentAssistProcessor {
	private static final String JSDT_CORE_PLUGIN_ID = "org.eclipse.wst.jsdt.core"; //$NON-NLS-1$
//	static {
//		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslation"); //$NON-NLS-1$
//		//DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
//	}
	protected String fErrorMessage = null;
	protected int fJspSourcePosition, fJavaPosition;
	private JSDTProposalCollector fProposalCollector;
	private JsTranslationAdapter fTranslationAdapter = null;
	protected ITextViewer fViewer = null;
	
	public JSDTContentAssistantProcessor() {
		super();
	}
	
	/**
	 * Returns a list of completion proposals based on the specified location
	 * within the document that corresponds to the current cursor position
	 * within the text viewer.
	 * 
	 * @param viewer
	 *            the viewer whose document is used to compute the proposals
	 * @param documentPosition
	 *            an offset within the document for which completions should be
	 *            computed
	 * @return an array of completion proposals or <code>null</code> if no
	 *         proposals are possible
	 */
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int pos) {
		initialize(pos);
		JSDTProposalCollector collector = null;
		IDOMModel xmlModel = null;
		try {
			fViewer = viewer;
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fViewer.getDocument());
			IDOMDocument xmlDoc = xmlModel.getDocument();
			if (fTranslationAdapter == null) {
				fTranslationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			}
			if (fTranslationAdapter != null) {
				IJsTranslation translation = fTranslationAdapter.getJsTranslation(true);
				fJavaPosition = getDocumentPosition();
				try {
					IJavaScriptUnit cu = translation.getCompilationUnit();
					// can't get java proposals w/out a compilation unit
					// or without a valid position
					if (cu == null || -1 == fJavaPosition) {
						return new ICompletionProposal[0];
					}
					collector = getProposalCollector();
					synchronized (cu) {
						cu.codeComplete(fJavaPosition, collector, null);
					}
				} catch (CoreException coreEx) {
					// a possible Java Model Exception due to not being a Web
					// (Java) Project
					coreEx.printStackTrace();
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			// throw out exceptions on code assist.
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		ICompletionProposal[] results = new ICompletionProposal[0];
		if (collector != null) {
			results = collector.getJSPCompletionProposals();
			if (results == null || results.length < 1) {
				fErrorMessage = JsUIMessages.Java_Content_Assist_is_not_UI_;
			}
		}
		return results;
	}
	
	/**
	 * Returns information about possible contexts based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text viewer.
	 * 
	 * @param viewer
	 *            the viewer whose document is used to compute the possible
	 *            contexts
	 * @param documentPosition
	 *            an offset within the document for which context information
	 *            should be computed
	 * @return an array of context information objects or <code>null</code> if
	 *         no context could be found
	 */
	
	public org.eclipse.jface.text.contentassist.IContextInformation[] computeContextInformation(org.eclipse.jface.text.ITextViewer viewer, int documentOffset) {
		return null;
	}
	
	/**
	 * Returns a string of characters which when pressed should automatically
	 * display content-assist proposals.
	 * 
	 * @return string of characters
	 */
	public java.lang.String getAutoProposalInvocationCharacters() {
		return null;
	}
	
	/**
	 * Returns a string of characters which when pressed should automatically
	 * display a content-assist tip.
	 * 
	 * @return string of characters
	 */
	public java.lang.String getAutoTipInvocationCharacters() {
		return null;
	}
	
	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of possible completions.
	 * 
	 * @return the auto activation characters for completion proposal or
	 *         <code>null</code> if no auto activation is desired
	 */
	
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}
	
	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of context information.
	 * 
	 * @return the auto activation characters for presenting context information
	 *         or <code>null</code> if no auto activation is desired
	 */
	
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}
	
	/**
	 * Returns a validator used to determine when displayed context information
	 * should be dismissed. May only return <code>null</code> if the processor
	 * is incapable of computing context information.
	 * 
	 * @return a context information validator, or <code>null</code> if the
	 *         processor is incapable of computing context information
	 */
	
	public org.eclipse.jface.text.contentassist.IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	protected int getDocumentPosition() {
		return fJspSourcePosition;
	}
	
	
	public String getErrorMessage() {
		// TODO: get appropriate error message
		// if (fCollector.getErrorMessage() != null &&
		// fCollector.getErrorMessage().length() > 0)
		// return fCollector.getErrorMessage();
		return fErrorMessage;
	}
	
	protected JSDTProposalCollector getProposalCollector() {
		return fProposalCollector;
		// return new JSPProposalCollector(translation);
	}
	
	/**
	 * Initialize the code assist processor.
	 */
	protected void initialize(int pos) {
		initializeJavaPlugins();
		fJspSourcePosition = pos;
		fErrorMessage = null;
	}
	
	/**
	 * Initialize the Java Plugins that the JSP processor requires. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=143765 We should not call
	 * "start", because that will cause that state to be remembered, and
	 * re-started automatically during the next boot up sequence.
	 * 
	 * ISSUE: we may be able to get rid of this all together, in future, since
	 * 99% we probably have already used some JDT class by the time we need JDT
	 * to be active ... but ... this is the safest fix for this point in 1.5
	 * stream. Next release, let's just remove this, re-discover what ever bug
	 * this was fixing (if any) and if there is one, then we'll either put back
	 * in, as is, or come up with a more appropriate fix.
	 * 
	 */
	protected void initializeJavaPlugins() {
		try {
			Bundle bundle = Platform.getBundle(JSDTContentAssistantProcessor.JSDT_CORE_PLUGIN_ID);
			bundle.loadClass("dummyClassNameThatShouldNeverExist"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			// this is the expected result, we just want to
			// nudge the bundle to be sure its activated.
		}
	}
	
	
	public void release() {
		fTranslationAdapter = null;
	}
	
	public void setProposalCollector(JSDTProposalCollector translation) {
		this.fProposalCollector = translation;
	}
}
