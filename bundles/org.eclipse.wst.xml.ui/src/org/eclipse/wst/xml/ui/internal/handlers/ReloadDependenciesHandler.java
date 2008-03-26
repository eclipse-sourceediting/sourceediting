/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation, bug 212330
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.CMDocumentLoader;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.InferredGrammarBuildingCMDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.w3c.dom.Document;

public class ReloadDependenciesHandler extends AbstractHandler implements
		IHandler {
	protected IStructuredModel model;
	
	/**
	 * 
	 */
	public ReloadDependenciesHandler() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart fEditor = HandlerUtil.getActiveEditor(event);

		if (fEditor instanceof XMLMultiPageEditorPart) {
			StructuredTextEditor textEditor = (StructuredTextEditor) fEditor.getAdapter(ITextEditor.class);
			model = (textEditor != null) ? textEditor.getModel() : null;
		}
		
		if (model != null) {
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(model);
			Document document = ((IDOMModel) model).getDocument();
			if ((modelQuery != null) && (modelQuery.getCMDocumentManager() != null)) {
				modelQuery.getCMDocumentManager().getCMDocumentCache().clear();
				// TODO... need to figure out how to access the
				// DOMObserver via ModelQuery
				// ...why?
				CMDocumentLoader loader = new InferredGrammarBuildingCMDocumentLoader(document, modelQuery);
				loader.loadCMDocuments();
			}
		}
		return null;
	}
}
