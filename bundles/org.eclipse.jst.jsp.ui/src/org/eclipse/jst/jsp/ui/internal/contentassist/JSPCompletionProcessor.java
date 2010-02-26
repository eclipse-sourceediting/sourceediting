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

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.framework.Bundle;


/**
 * @plannedfor 1.0
 * @deprecated This class is no longer used locally and will be removed in the future
 */
public class JSPCompletionProcessor implements IContentAssistProcessor, IReleasable {
	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private static final String JDT_CORE_PLUGIN_ID = "org.eclipse.jdt.core"; //$NON-NLS-1$

	protected int fJspSourcePosition, fJavaPosition;
	protected String fErrorMessage = null;
	protected StructuredTextViewer fViewer = null;
	private JSPTranslationAdapter fTranslationAdapter = null;
	// translation adapter may be stale, check the model id
	private String fModelId = null;

	/**
	 * Returns a list of completion proposals based on the specified location
	 * within the document that corresponds to the current cursor position
	 * within the text viewer.
	 * 
	 * @param viewer
	 *            the viewer whose document is used to compute the proposals
	 * @param documentPosition
	 *            an offset within the document for which completions should
	 *            be computed
	 * @return an array of completion proposals or <code>null</code> if no
	 *         proposals are possible
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int pos) {
		return computeCompletionProposals(viewer, pos, 0);
	}
	
	/**
	 * The same as the normal <code>computeCompeltaionProposals</code> except the calculated
	 * java position is offset by the given extra offset.
	 * 
	 * @param viewer
	 *            the viewer whose document is used to compute the proposals
	 * @param documentPosition
	 *            an offset within the document for which completions should
	 *            be computed
	 * @param javaPositionExtraOffset
	 * 				the extra offset for the java position
	 * @return an array of completion proposals or <code>null</code> if no
	 *         proposals are possible
	 */
	protected ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int pos, int javaPositionExtraOffset) {
		initialize(pos);

		JSPProposalCollector collector = null;
		
		IDOMModel xmlModel = null;
		try {
			if (viewer instanceof StructuredTextViewer)
				fViewer = (StructuredTextViewer) viewer;

			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fViewer.getDocument());

			IDOMDocument xmlDoc = xmlModel.getDocument();
			if (fTranslationAdapter == null || xmlModel.getId() != fModelId) {
				fTranslationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				fModelId = xmlModel.getId();
			}
			if (fTranslationAdapter != null) {

				JSPTranslation translation = fTranslationAdapter.getJSPTranslation();
				fJavaPosition = translation.getJavaOffset(getDocumentPosition())+javaPositionExtraOffset;

				if (DEBUG)
					System.out.println(debug(translation));

				try {

					ICompilationUnit cu = translation.getCompilationUnit();

					// can't get java proposals w/out a compilation unit
					// or without a valid position
					if (cu == null || -1 == fJavaPosition)
						return new ICompletionProposal[0];
					
					collector = getProposalCollector(cu, translation);
					synchronized (cu) {
						cu.codeComplete(fJavaPosition, collector, (WorkingCopyOwner) null);
					}
				}
				catch (CoreException coreEx) {
					// a possible Java Model Exception due to not being a Web
					// (Java) Project
					coreEx.printStackTrace();
				}
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
			// throw out exceptions on code assist.
		}
		finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		ICompletionProposal[] results = new ICompletionProposal[0];
		if(collector != null) {
			results = collector.getJSPCompletionProposals();
			if (results == null || results.length < 1)
				fErrorMessage = JSPUIMessages.Java_Content_Assist_is_not_UI_;
		}
		return results;
	}
	
	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		return new JSPProposalCollector(cu, translation);
	}
	
	/**
	 * For debugging translation mapping only.
	 * 
	 * @param translation
	 */
	private String debug(JSPTranslation translation) {
		StringBuffer debugString = new StringBuffer();
		HashMap jsp2java = translation.getJsp2JavaMap();
		String javaText = translation.getJavaText();
		String jspText = fViewer.getDocument().get();
		debugString.append("[jsp2JavaMap in JSPCompletionProcessor]\r\n"); //$NON-NLS-1$
		debugString.append("jsp cursor position >> " + fViewer.getTextWidget().getCaretOffset() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		Iterator it = jsp2java.keySet().iterator();
		while (it.hasNext()) {
			try {
				Position jspPos = (Position) it.next();
				Position javaPos = (Position) jsp2java.get(jspPos);
				debugString.append("jsp > " + jspPos.offset + ":" + jspPos.length + ":" + jspText.substring(jspPos.offset, jspPos.offset + jspPos.length) + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				debugString.append("java > " + javaPos.offset + ":" + javaPos.length + ":" + javaText.substring(javaPos.offset, javaPos.offset + javaPos.length) + ":\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				debugString.append("-------------------------------------------------\n"); //$NON-NLS-1$
			}
			catch (Exception e) {
				// eat exceptions, it's only for debug
			}
		}
		return debugString.toString();
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
	 * @return an array of context information objects or <code>null</code>
	 *         if no context could be found
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
	 * @return the auto activation characters for presenting context
	 *         information or <code>null</code> if no auto activation is
	 *         desired
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/**
	 * Returns a validator used to determine when displayed context
	 * information should be dismissed. May only return <code>null</code> if
	 * the processor is incapable of computing context information.
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

	/**
	 * Initialize the code assist processor.
	 */
	protected void initialize(int pos) {
		initializeJavaPlugins();

		fJspSourcePosition = pos;
		fErrorMessage = null;
	}

	/**
	 * Initialize the Java Plugins that the JSP processor requires.
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=143765
	 * We should not call "start", because that will cause that 
	 * state to be remembered, and re-started automatically during 
	 * the next boot up sequence. 
	 * 
	 * ISSUE: we may be able to get rid of this all together, in future, 
	 * since 99% we probably have already used some JDT class by the time 
	 * we need JDT to be active ... but ... this is the safest fix for 
	 * this point in 1.5 stream. Next release, let's just remove this, 
	 * re-discover what ever bug this was fixing (if any) and if there is 
	 * one, then we'll either put back in, as is, or come up with a 
	 * more appropriate fix. 
	 * 
	 */
	protected void initializeJavaPlugins() {
		try {
			Bundle bundle = Platform.getBundle(JDT_CORE_PLUGIN_ID);
			bundle.loadClass("dummyClassNameThatShouldNeverExist");
		}
		catch (ClassNotFoundException e) {
			// this is the expected result, we just want to 
			// nudge the bundle to be sure its activated. 
		}
	}

	public void release() {
		fTranslationAdapter = null;
	}
}
