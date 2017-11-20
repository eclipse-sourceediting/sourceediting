/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSDTContetAssistInvocationContext extends JavaContentAssistInvocationContext {
	public static JSDTContetAssistInvocationContext getInstance(ITextViewer viewer, int offset, JSDTProposalCollector theCollector) {
		JSDTContetAssistInvocationContext.getJSPTranslation(viewer);
		return new JSDTContetAssistInvocationContext(viewer, offset, theCollector);
	}
	
	private static IJsTranslation getJSPTranslation(ITextViewer viewer) {
		IJsTranslation fTranslation = null;
		
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			IDOMDocument xmlDoc = xmlModel.getDocument();
			JsTranslationAdapter fTranslationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			if (fTranslationAdapter != null) {
				fTranslation = fTranslationAdapter.getJsTranslation(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return fTranslation;
	}
	ITextViewer viewer;
	
	private JSDTContetAssistInvocationContext(ITextViewer viewer, int offset, JSDTProposalCollector theCollector) {
		super(viewer, offset, null);
		this.viewer = viewer;
		// this.offset=getJSPTranslation().getJavaOffset(offset);
		// CompletionProposalCollector theCollector = getProposalCollector();
		super.setCollector(theCollector);
	}
	
// public IDocument getDocument() {
// return
// ((JSPTranslationExtension)getJSPTranslation(viewer)).getJavaDocument();
//      
// }
// protected CompletionProposalCollector getProposalCollector() {
//       
// return ((CompletionProposalCollector) ( new JSPProposalCollector(
// getJSPTranslation()) ));
// }
	
	public IDocument getDocument() {
		return viewer.getDocument();
	}
// public IJavaScriptUnit getCompilationUnit() {
// return getJSPTranslation(viewer).getCompilationUnit();
// }
}
