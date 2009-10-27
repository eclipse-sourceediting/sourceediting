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
import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSDTHtmlCompletionProcessor {
	
	public JSDTHtmlCompletionProcessor() {}
	
	
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		/* add </script if necisary */
		ArrayList allProposals = new ArrayList();
		getJSPTranslation(viewer);
		
		return (ICompletionProposal[])allProposals.toArray(new ICompletionProposal[allProposals.size()]);
	}
	
	public ICompletionProposal getEndScriptProposal(ITextViewer viewer, int offset) {
		/* add </script if necisary */
	
		IJsTranslation tran = getJSPTranslation(viewer);
		if(tran==null) return null;
		
		int missingAtOffset = tran.getMissingTagStart();
		
		if(offset>=missingAtOffset&& missingAtOffset>-1) {
			
			String allText = viewer.getDocument().get();
			String text = "</script>"; //$NON-NLS-1$
			
			int startInTag = -1;
			
			for(int i=0;i<text.length() && allText.length()>offset-1;i++) {
				if(allText.charAt(offset-1)==text.charAt(i)) {
					startInTag = i;
					break;
				}
			}
			
			if(startInTag==-1 ) {
				String displayText = Messages.getString("JSDTHtmlCompletionProcessor.1"); //$NON-NLS-1$
				return new CustomCompletionProposal("\n" + text + "\n" ,offset,0,offset,null,displayText,null,Messages.getString("JSDTHtmlCompletionProcessor.4"),100); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			String text1 = allText.substring(offset - startInTag - 1, offset).toLowerCase();
			String text2 = text.substring(0, startInTag+1).toLowerCase();
			if(startInTag>-1  && text2.compareTo(text1)==0 ) {
				String displayText = Messages.getString("JSDTHtmlCompletionProcessor.5"); //$NON-NLS-1$
				return new CustomCompletionProposal(text  ,offset-startInTag-1,0,text.length(),null,displayText,null,Messages.getString("JSDTHtmlCompletionProcessor.6"),100); //$NON-NLS-1$
			}
			
		}
		
		return null;
	}
	private IJsTranslation getJSPTranslation(ITextViewer viewer) {
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			IDOMDocument xmlDoc = xmlModel.getDocument();
			
			JsTranslationAdapter fTranslationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			
			if (fTranslationAdapter != null) {
				return fTranslationAdapter.getJsTranslation(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return null;
	}
	
}
