/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.eventimpl;



import org.eclipse.wst.css.core.internal.event.ICSSStyleListener;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;


public class CSSEmbededStyleNotifyAdapter extends CSSStyleNotifyAdapter {

	/**
	 * CSSEmbeddedStyleNotifyAdapter constructor comment.
	 * 
	 * @param model
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSModel
	 */
	public CSSEmbededStyleNotifyAdapter(ICSSModel model) {
		super(model);
	}

	/**
	 * 
	 */
	public void styleChanged(ICSSModel srcModel, ICSSSelector[] removed, ICSSSelector[] added, String media) {
		IDOMNode node = (IDOMNode) model.getOwnerDOMNode();
		if (node != null)
			return;
		INodeAdapter adapter = node.getAdapterFor(IStyleSheetAdapter.class);
		if (adapter instanceof ICSSStyleListener) {
			((ICSSStyleListener) adapter).styleChanged(srcModel, removed, added, media);
		}
	}

	/**
	 * 
	 */
	public void styleUpdate(ICSSModel srcModel) {
		IDOMNode node = (IDOMNode) model.getOwnerDOMNode();
		if (node != null)
			return;
		INodeAdapter adapter = node.getAdapterFor(IStyleSheetAdapter.class);
		if (adapter instanceof ICSSStyleListener) {
			((ICSSStyleListener) adapter).styleUpdate(srcModel);
		}
	}
}
