/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.views.contentoutline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.ui.actions.AbstractNodeActionManager;
import org.w3c.dom.Node;


public class XMLNodeActionManager extends AbstractNodeActionManager {
	/**
	 * @deprecated
	 * @param model
	 * @param modelQuery
	 * @param viewer
	 */
	public XMLNodeActionManager(IStructuredModel model, ModelQuery modelQuery, Viewer viewer) {
		super(model, modelQuery, viewer);
	}

	public XMLNodeActionManager(IStructuredModel model, Viewer viewer) {
		this(model, ModelQueryUtil.getModelQuery(model), viewer);
	}

	/**
	 * @deprecated - ModelQueryUtil now supports getModelQuery(IStructuredModel)
	 * @param model
	 * @return
	 */
	public static ModelQuery getModelQuery(IStructuredModel model) {
		return ModelQueryUtil.getModelQuery(model);
	}

	public void reformat(Node newElement, boolean deep) {
		try {
			// tell the model that we are about to make a big model change
			model.aboutToChangeModel();

			// format selected node
			IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
			formatProcessor.formatNode(newElement);
		}
		finally {
			// tell the model that we are done with the big model change
			model.changedModel();
		}
	}

	public void setModel(IStructuredModel newModel) {
		model = newModel;
		setModelQuery(ModelQueryUtil.getModelQuery(newModel));
	}

	protected void setModelQuery(ModelQuery newModelQuery) {
		modelQuery = newModelQuery;
	}
}
