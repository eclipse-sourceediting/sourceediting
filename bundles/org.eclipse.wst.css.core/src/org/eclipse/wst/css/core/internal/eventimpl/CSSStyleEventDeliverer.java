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



import java.util.Vector;

import org.eclipse.wst.css.core.internal.event.ICSSStyleListener;
import org.eclipse.wst.css.core.internal.event.ICSSStyleNotifier;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;


/**
 * 
 */
public class CSSStyleEventDeliverer {

	private Vector fVisited = new Vector();
	private ICSSModel fSrcModel;
	private ICSSSelector[] fRemoved;
	private ICSSSelector[] fAdded;
	private String fMedia;

	/**
	 * 
	 */
	public CSSStyleEventDeliverer() {
		super();
	}

	/**
	 * 
	 */
	public void fire(ICSSModel srcModel, ICSSSelector[] removed, ICSSSelector[] added, String media) {
		if (srcModel == null || srcModel.getStyleListeners() == null)
			return;

		this.fSrcModel = srcModel;
		this.fRemoved = removed;
		this.fAdded = added;
		this.fMedia = media;

		// prohibit delivering to srcModel
		fVisited.clear();
		fVisited.add(srcModel);
		Object[] listeners = srcModel.getStyleListeners().toArray();
		for (int i = 0; i < listeners.length; i++) {
			visit((ICSSStyleListener) listeners[i]);
		}
	}

	/**
	 * 
	 */
	public void fireTo(ICSSStyleListener listener, ICSSModel srcModel, ICSSSelector[] removed, ICSSSelector[] added, String media) {
		this.fSrcModel = srcModel;
		this.fRemoved = removed;
		this.fAdded = added;
		this.fMedia = media;

		// prohibit delivering to srcModel
		fVisited.clear();
		// visited.add(srcModel); : because looping problem
		visit(listener);
	}

	/**
	 * 
	 */
	public void fireUpdate(ICSSModel srcModel) {
		if (srcModel == null || srcModel.getStyleListeners() == null)
			return;

		this.fSrcModel = srcModel;

		// prohibit delivering to srcModel
		fVisited.clear();
		fVisited.add(srcModel);
		Object[] listeners = srcModel.getStyleListeners().toArray();
		for (int i = 0; i < listeners.length; i++) {
			visitUpdate((ICSSStyleListener) listeners[i]);
		}
	}

	/**
	 * 
	 */
	public void fireUpdateTo(ICSSStyleListener listener, ICSSModel srcModel) {
		if (srcModel == null || srcModel.getStyleListeners() == null)
			return;

		this.fSrcModel = srcModel;

		// prohibit delivering to srcModel
		fVisited.clear();
		// visited.add(srcModel);
		visitUpdate(listener);
	}

	/**
	 * 
	 */
	protected void visit(ICSSStyleListener listener) {
		if (listener == null || fVisited.contains(listener))
			return;

		// fire event to listener
		listener.styleChanged(fSrcModel, fRemoved, fAdded, fMedia);
		fVisited.add(listener);

		// traverse notifier
		if (listener instanceof ICSSStyleNotifier) {
			ICSSStyleNotifier notifier = (ICSSStyleNotifier) listener;
			if (notifier.getStyleListeners() != null) {
				Object[] listeners = notifier.getStyleListeners().toArray();
				for (int i = 0; i < listeners.length; i++) {
					visit((ICSSStyleListener) listeners[i]);
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void visitUpdate(ICSSStyleListener listener) {
		if (listener == null || fVisited.contains(listener))
			return;

		// fire event to listener
		listener.styleUpdate(fSrcModel);
		fVisited.add(listener);

		// traverse notifier
		if (listener instanceof ICSSStyleNotifier) {
			ICSSStyleNotifier notifier = (ICSSStyleNotifier) listener;
			if (notifier.getStyleListeners() != null) {
				Object[] listeners = notifier.getStyleListeners().toArray();
				for (int i = 0; i < listeners.length; i++) {
					visitUpdate((ICSSStyleListener) listeners[i]);
				}
			}
		}
	}
}
