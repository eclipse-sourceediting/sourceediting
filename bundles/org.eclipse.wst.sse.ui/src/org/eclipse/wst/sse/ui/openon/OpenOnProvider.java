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
package org.eclipse.wst.sse.ui.openon;



import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.extensions.openon.IOpenOn;
import org.eclipse.wst.sse.ui.internal.openon.OpenOnBuilder;
import org.eclipse.wst.sse.ui.internal.openon.OpenOnDefinition;


/**
 * Determines the appropriate IOpenOn to call based on current partition.
 */
public class OpenOnProvider {
	private static OpenOnProvider fInstance;

	/**
	 * returns singleton instance of OpenOnProvider
	 * 
	 * @return OpenOnProvider
	 */
	public synchronized static OpenOnProvider getInstance() {
		if (fInstance == null) {
			fInstance = new OpenOnProvider();
		}
		return fInstance;
	}

	/**
	 * @deprecated use getOpenOn(IDocument, int) instead TODO remove in C5
	 */
	public IOpenOn getOpenOn(ITextViewer viewer, int offset) {
		// determine the current partition
		if (viewer != null) {
			IDocument document = viewer.getDocument();
			return getOpenOn(document, offset);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the appropriate IOpenOn for the current partition
	 * 
	 * @return
	 */
	public IOpenOn getOpenOn(IDocument document, int offset) {
		IOpenOn openOn = null;

		// determine the current partition
		if (document != null) {
			String contentType = getContentType(document);
			String partitionType = getPartitionType(document, offset);

			// query OpenOnBuilder and get the list of open ons for the
			// current partition
			OpenOnDefinition[] defs = OpenOnBuilder.getInstance().getOpenOnDefinitions(contentType, partitionType);

			// if more than 1 openon is returned, need to further check
			// which open on is the appropriate one to return
			// for now just returning the first one
			if (defs != null && defs.length > 0) {
				openOn = defs[0].createOpenOn();
			}
		}

		return openOn;
	}


	/**
	 * Returns the content type of document
	 * 
	 * @param document -
	 *            assumes document is not null
	 * @return String content type of given document
	 */
	protected String getContentType(IDocument document) {
		String type = null;

		IModelManager mgr = ((IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID)).getModelManager();
		IStructuredModel model = null;
		try {
			model = mgr.getExistingModelForRead(document);
			if (model != null) {
				type = model.getContentTypeIdentifier();
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return type;
	}

	/**
	 * Returns the partition type located at offset in the document
	 * 
	 * @param document -
	 *            assumes document is not null
	 * @param offset
	 * @return String partition type
	 */
	protected String getPartitionType(IDocument document, int offset) {
		String type = null;
		ITypedRegion region = document.getDocumentPartitioner().getPartition(offset);
		if (region != null) {
			type = region.getType();
		}

		return type;
	}
}
