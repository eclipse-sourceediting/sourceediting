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
package org.eclipse.jst.jsp.ui.internal.reconcile;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilableModel;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.xml.core.document.IDOMDocument;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Creates a JSPTranslation for use w/ the JavaReconcileStep. Adapts Java
 * error positions to the JSP positions.
 * 
 * @author pavery
 */
public class ReconcileStepForJspTranslation extends StructuredReconcileStep {

	/**
	 * This contains is the translated java document. We create this here,
	 * then set it as the model on the next step, ReconcileStepForJava
	 */
	private JSPTranslationWrapper fModel = null;
	private IReconcileResult[] EMPTY_RECONCILE_RESULT_SET = new IReconcileResult[0];
	private JSPTranslationAdapter fTranslationAdapter = null;
	private JSPTranslationExtension fJSPTranslation = null;
	private static final boolean DEBUG = false;
	
	public ReconcileStepForJspTranslation(IReconcileStep step) {
		super(step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.AbstractReconcileStep#reconcileModel(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {

		if(DEBUG)
            System.out.println("[trace reconciler] > translating JSP in JSP TRANSLATE step"); //$NON-NLS-1$	

		if (isCanceled() || dirtyRegion == null)
			return EMPTY_RECONCILE_RESULT_SET;

		// create java model for java reconcile
		JSPTranslationAdapter adapter = getJSPTranslationAdapter();
		fJSPTranslation = adapter.getJSPTranslation();
		fModel = new JSPTranslationWrapper(fJSPTranslation);

		if(DEBUG)
            System.out.println("[trace reconciler] > JSP TRANSLATE step done"); //$NON-NLS-1$	

		// this step doesn't actually produce results, only creates the java
		// model
		return EMPTY_RECONCILE_RESULT_SET;
	}

	/**
	 * @return
	 */
	private JSPTranslationAdapter getJSPTranslationAdapter() {
		if (fTranslationAdapter == null) {
			IReconcilableModel reconcilableModel = getInputModel();
			IDocument doc = null;

			if (reconcilableModel instanceof DocumentAdapter)
				doc = ((DocumentAdapter) reconcilableModel).getDocument();

			if (doc != null) {
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
					if (model != null) {
						IDOMDocument xmlDoc = ((IDOMModel) model).getDocument();
						fTranslationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
					}
				}
				finally {
					if (model != null)
						model.releaseFromRead();
				}
			}
		}
		return fTranslationAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.internal.ui.text.AbstractStructuredReconcileStep#getModel()
	 */
	public IReconcilableModel getModel() {
		return fModel;
	}

	/*
	 * @see org.eclipse.text.reconcilerpipe.AbstractReconcilePipeParticipant#convertToInputModel(org.eclipse.text.reconcilerpipe.IReconcileResult[])
	 */
	protected IReconcileResult[] convertToInputModel(IReconcileResult[] inputResults) {

		if (inputResults == null)
			return null;

		HashMap java2jspRanges = fJSPTranslation.getJava2JspMap();
		for (int i = 0; i < inputResults.length; i++) {
			if (isCanceled())
				return EMPTY_RECONCILE_RESULT_SET;
			if (!(inputResults[i] instanceof TemporaryAnnotation))
				continue;
			TemporaryAnnotation result = (TemporaryAnnotation) inputResults[i];
			adaptJava2JspPosition(result, java2jspRanges);
		}

		return inputResults;
	}

	/**
	 * @param pos
	 * @param java2jspRanges
	 */
	private void adaptJava2JspPosition(TemporaryAnnotation annotation, HashMap java2jspRanges) {
		Position pos = annotation.getPosition();
		int javaOffset = pos.offset;
		int offsetInRange = 0;
		Position jspPos, javaPos = null;
		boolean found = false;

		// iterate all mapped java ranges
		Iterator it = java2jspRanges.keySet().iterator();
		while (it.hasNext()) {
			javaPos = (Position) it.next();
			if (!javaPos.includes(javaOffset))
				continue;

			offsetInRange = javaOffset - javaPos.offset;
			jspPos = (Position) java2jspRanges.get(javaPos);
			if (jspPos != null) {
				pos.offset = jspPos.offset + offsetInRange;
				found = true;
				additionalPositionAdjustment(annotation, jspPos);
			}
			break;
		}
		// hide unmapped errors
		if (!found) {
			pos.offset = -1;
			pos.length = 0;
		}
	}

	/**
	 * Adjusts java position to JSP position for ranges that don't map
	 * "exactly". eg. <%@include file=""%>, <jsp:useBean/>, <%@import
	 * src=""%>...
	 * 
	 * @param pos
	 * @param jspPos
	 */
	private void additionalPositionAdjustment(TemporaryAnnotation annotation, Position jspPos) {
		Position pos = annotation.getPosition();
		IStructuredDocument sDoc = null;
		IStructuredDocumentRegion sdRegion = null;
		// analyze the sdRegion to see if it's import, expression, include
		ITextRegionList regions = null;
		ITextRegion r = null;
		String tagName = ""; //$NON-NLS-1$

		sDoc = (IStructuredDocument) ((DocumentAdapter) getInputModel()).getDocument();
		sdRegion = sDoc.getRegionAtCharacterOffset(jspPos.offset);
		// analyze the sdRegion to see if it's import, expression, include,
		// useBean...
		regions = sdRegion.getRegions();
		for (int i = 0; i < regions.size(); i++) {
			r = regions.get(i);
			if (r.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME || r.getType() == DOMRegionContext.XML_TAG_NAME) {
				tagName = sdRegion.getText(r).trim();
				if (tagName.equals("include")) { //$NON-NLS-1$
					adjustForInclude(annotation, pos, sdRegion, regions, i);
				}
				else if (tagName.equals("page")) { //$NON-NLS-1$
					adjustForPage(annotation, pos, sdRegion, regions, i);
				}
				else if (tagName.equals("jsp:useBean")) { //$NON-NLS-1$
					//adjustForUseBean(pos, sdRegion, regions, i);
					// do nothing for usebean for now...
					break;
				}
                // this can actually cause the WRONG node to be underlined
                // esp in the case of embedded attr regions
//				else {
//					// catch all for all other cases for now, at least we'll
//					// get
//					// the squiggle in the general area of the problem instead
//					// of some random place
//					pos.offset = sdRegion.getStartOffset(r);
//					pos.length = 1;
//					break;
//				}
			}
		}
	}

	private void adjustForInclude(TemporaryAnnotation annotation, Position pos, IStructuredDocumentRegion sdRegion, ITextRegionList regions, int startingRegionNumber) {
		ITextRegion r;
		String tagName;
		String noQuotes;
		for (int j = startingRegionNumber; j < regions.size(); j++) {
			r = regions.get(j);
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME && !sdRegion.getText(r).trim().equals("file")) //$NON-NLS-1$
				// there's only one attribute allowed for <@include
				break;
			else if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				tagName = sdRegion.getText(r).trim();
				noQuotes = StringUtils.strip(tagName);
				pos.offset = sdRegion.getStartOffset(r) + ((tagName.length() - noQuotes.length()) == 2 ? 1 : 0);
				pos.length = noQuotes.length();
				annotation.setText(annotation.getText() + " (in file: \"" + noQuotes + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			}
		}
	}

	private void adjustForPage(TemporaryAnnotation annotation, Position pos, IStructuredDocumentRegion sdRegion, ITextRegionList regions, int startingRegionNumber) {
		ITextRegion r;
		String value;
		int size = regions.size();

		for (int j = startingRegionNumber; j < size; j++) {
			r = regions.get(j);
			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME && sdRegion.getText(r).trim().equals("import")) { //$NON-NLS-1$
				if (size > j + 2) {
					r = regions.get(j + 2);
					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						value = sdRegion.getText(r);
						pos.offset = sdRegion.getStartOffset(r);
						pos.length = value.trim().length();
						annotation.setText(annotation.getText() + " (in file: \"" + StringUtils.stripQuotes(value) + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
						break;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.xml.reconcile.StructuredReconcileStep#release()
	 */
	public void release() {
		if (fTranslationAdapter != null) {
			if(DEBUG) {
				System.out.println("ReconcileStepForJSPTranslation ["+this+"] releasing JSPTranslationAdapter " + fTranslationAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
			fTranslationAdapter.release();
		}
		super.release();
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcileStep#setInputModel(org.eclipse.jface.text.reconciler.IReconcilableModel)
	 */
	public void setInputModel(IReconcilableModel inputModel) {
		// force to rebuild translation
		fTranslationAdapter = null;
		if (DEBUG) {
			System.out.println("======================================"); //$NON-NLS-1$
			System.out.println("setting input model" + inputModel); //$NON-NLS-1$
			System.out.println("======================================"); //$NON-NLS-1$
		}
		super.setInputModel(inputModel);
		
		reinitTranslationAdapter(inputModel);
	}

	/**
	 * @param inputModel
	 */
	private void reinitTranslationAdapter(IReconcilableModel inputModel) {

		IDocument doc = null;
		if (inputModel instanceof DocumentAdapter)
			doc = ((DocumentAdapter) inputModel).getDocument();

		if (doc != null) {
			IStructuredModel model = null;
			try {
				model = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
				if(getJSPTranslationAdapter() != null)
					getJSPTranslationAdapter().setXMLModel((IDOMModel) model);
			}
			finally {
				if (model != null)
					model.releaseFromRead();
			}
		}
	}
}