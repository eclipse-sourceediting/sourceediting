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

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.contentassist.IResourceDependentProcessor;
import org.eclipse.wst.xml.core.document.IDOMDocument;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.document.IDOMNode;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author pavery
 */
public class JSPCompletionProcessor implements IContentAssistProcessor, IReleasable, IResourceDependentProcessor {
	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private static final String JDT_CORE_PLUGIN_ID = "org.eclipse.jdt.core"; //$NON-NLS-1$

	protected int fJspSourcePosition, fJavaPosition;
	protected IResource fResource;
	protected JSPCompletionRequestor fCollector;
	protected String fErrorMessage = null;
	protected StructuredTextViewer fViewer = null;
	private JSPTranslationAdapter fTranslationAdapter = null;

	public JSPCompletionProcessor(IResource resource) {
		fResource = resource;
	}

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
		initialize(pos);
		IDOMModel xmlModel = null;
		try {
			if (viewer instanceof StructuredTextViewer)
				fViewer = (StructuredTextViewer) viewer;

			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fViewer.getDocument());

			IDOMDocument xmlDoc = xmlModel.getDocument();
			if (fTranslationAdapter == null)
				fTranslationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			if (fTranslationAdapter != null) {

				JSPTranslation translation = fTranslationAdapter.getJSPTranslation();
				fJavaPosition = translation.getJavaOffset(getDocumentPosition());

				fCollector.setCodeAssistOffset(fJavaPosition);
				fCollector.setJavaToJSPOffset(fJspSourcePosition - fJavaPosition);
				fCollector.setCursorInExpression(cursorInExpression());


				if (DEBUG)
					System.out.println(debug(translation));

				try {

					ICompilationUnit cu = translation.getCompilationUnit();
					fCollector.setCompilationUnit(cu);

					// can't get java proposals w/out a compilation unit
					if (cu == null)
						return new ICompletionProposal[0];

					synchronized (cu) {
						cu.codeComplete(fJavaPosition, fCollector, null);
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
		ICompletionProposal[] results = fCollector.getResults();
		if (results == null || results.length < 1)
			fErrorMessage = SSEUIPlugin.getResourceString("%Java_Content_Assist_is_not_UI_"); //$NON-NLS-1$ = "Java Content Assist is not available for the current cursor location"

		return results;
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
	 * @return whether the source cursor is in an expression
	 */
	private boolean cursorInExpression() {
		boolean inExpression = false;
		IStructuredDocumentRegion sdRegion = null;
		IDOMModel xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(fViewer.getDocument());
		IDOMNode xmlNode = null;
		xmlModel.releaseFromRead();
		xmlNode = (IDOMNode) xmlModel.getIndexedRegion(fJspSourcePosition);
		if (xmlNode != null) {
			IDOMNode parent = (IDOMNode) xmlNode.getParentNode();
			if (parent != null) {
				sdRegion = parent.getFirstStructuredDocumentRegion();
				inExpression = sdRegion != null && (sdRegion.getType() == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN || sdRegion.getType() == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN);
			}
		}
		return inExpression;
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
	 * Return the Java project for the to create completions within
	 */
	protected IJavaProject getJavaProject() {
		if (fResource == null)
			return null;
		IProject proj = (fResource.getType() == IResource.PROJECT) ? (IProject) fResource : fResource.getProject();
		IJavaProject javaProj = JavaCore.create(proj);
		return javaProj;
	}

	/**
	 * Initialize the code assist processor.
	 */
	protected void initialize(int pos) {
		initializeJavaPlugins();

		// fCollector = new JSPResultCollector();
		fCollector = new JSPCompletionRequestor();
		fJspSourcePosition = pos;
		fErrorMessage = null;
	}

	/**
	 * Initialize the Java Plugins that the JSP processor requires.
	 */
	protected void initializeJavaPlugins() {
		try {
			Bundle bundle = Platform.getBundle(JDT_CORE_PLUGIN_ID);
			bundle.start();
		}
		catch (BundleException exc) {
			Logger.logException("Could not initialize the JDT Plugins", exc);//$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.IReleasable#release()
	 */
	public void release() {
		fTranslationAdapter = null;
	}

	/*
	 * @see com.ibm.sse.editor.contentassist.IResourceDependent#initialize(org.eclipse.core.resources.IResource)
	 */
	public void initialize(IResource resource) {
		fResource = resource;
	}
}