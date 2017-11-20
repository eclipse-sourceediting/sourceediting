/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.document;

import org.eclipse.jst.jsp.core.internal.Assert;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

/**
 * This class adapts document 
 * with the an instance of PageDirectiveAdapter
 */
public class PageDirectiveAdapterFactory extends AbstractAdapterFactory implements INodeAdapterFactory {


	private PageDirectiveAdapter pageDirectiveAdapterInstance = null;

	/**
	 * Constructor for PageDirectiveAdapterFactory.
	 * Note: its important not to be a singleton, since
	 * this factory needs to track its adapter(s) and release
	 * them when they are released.
	 * 
	 * @param adapterKey
	 * @param registerAdapters
	 */
	protected PageDirectiveAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/**
	 * The no argument constructor assumes its a 
	 * Factory for PageDirectiveAdapter
	 */
	public PageDirectiveAdapterFactory() {
		this(PageDirectiveAdapter.class, true);
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		PageDirectiveAdapter result = null;
		if (target instanceof IDOMNode) {
			IDOMNode node = (IDOMNode) target;
			if (node.getNodeType() == Node.DOCUMENT_NODE) {
				result = getAdapterInstance(target);
			}

		}
		return result;
	}

	public void release() {
		if (pageDirectiveAdapterInstance != null) {
			pageDirectiveAdapterInstance.release();
		}
	}

	/**
	 * We assume this is only called for 'document' target
	 */
	protected PageDirectiveAdapter getAdapterInstance(INodeNotifier target) {
		// if our instance already exists with a different
		// target, then, somehow, the document node must 
		// have changed for a model, so we should release 
		// old adapter and create new one for new document 
		// node. This is probably a programming error.
		if (pageDirectiveAdapterInstance != null) {
			if (target != pageDirectiveAdapterInstance.getTarget()) {
				release();
				pageDirectiveAdapterInstance = new PageDirectiveAdapterImpl(target);
			}
			// else return the one we have
		}
		else {
			// if is equal to null, create a new one
			pageDirectiveAdapterInstance = new PageDirectiveAdapterImpl(target);
		}
		Assert.isNotNull(pageDirectiveAdapterInstance, "pageDipageDirectiveAdapterInstance was null"); //$NON-NLS-1$
		return pageDirectiveAdapterInstance;
	}

	public INodeAdapterFactory copy() {

		return new PageDirectiveAdapterFactory(getAdapterKey(), isShouldRegisterAdapter());
	}
}
