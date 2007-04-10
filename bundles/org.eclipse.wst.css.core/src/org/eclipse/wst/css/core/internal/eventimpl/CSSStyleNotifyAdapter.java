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



import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.event.ICSSStyleListener;
import org.eclipse.wst.css.core.internal.event.ICSSStyleNotifier;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.internal.util.ImportedCollector;
import org.eclipse.wst.css.core.internal.util.SelectorsCollector;


/**
 * 
 */
public class CSSStyleNotifyAdapter implements ICSSStyleNotifier {

	protected java.util.Vector listeners;
	protected final org.eclipse.wst.css.core.internal.provisional.document.ICSSModel model;
	protected boolean recording = false;

	/**
	 * 
	 */
	public CSSStyleNotifyAdapter(ICSSModel model) {
		super();
		this.model = model;
	}

	/**
	 * 
	 */
	public void addStyleListener(ICSSStyleListener listener) {
		if (listener == null)
			return;

		if (listeners == null)
			listeners = new Vector();
		if (!listeners.contains(listener)) {

			// send new selectors event to listener
			ImportedCollector trav = new ImportedCollector();
			trav.apply(model.getDocument());
			Iterator it = trav.getExternals().iterator();
			while (it.hasNext()) {
				ICSSStyleSheet sheet = (ICSSStyleSheet) it.next();
				// collect selectors
				SelectorsCollector selTrav = new SelectorsCollector();
				selTrav.apply(sheet);
				int nSel = selTrav.getSelectors().size();
				ICSSSelector[] added = new ICSSSelector[nSel];
				for (int i = 0; i < nSel; i++)
					added[i] = (ICSSSelector) selTrav.getSelectors().get(i);

				// fire event
				CSSStyleEventDeliverer deliverer = new CSSStyleEventDeliverer();
				deliverer.fireTo(listener, sheet.getModel(), null, added, null/*
																				 * media
																				 * will
																				 * be
																				 * implemented
																				 * in
																				 * the
																				 * future
																				 */);
			}

			// add listener
			listeners.add(listener);
		}
	}

	/**
	 * 
	 */
	public void beginRecording() {
		recording = true;
	}

	/**
	 * 
	 */
	public void endRecording() {
		recording = false;
	}

	/**
	 * 
	 */
	public void fire(ICSSSelector[] removed, ICSSSelector[] added, String media) {
		// send selector changed event
		CSSStyleEventDeliverer deliv = new CSSStyleEventDeliverer();
		deliv.fire(model, removed, added, null);

	}

	/**
	 * 
	 */
	public java.util.List getStyleListeners() {
		return listeners;
	}

	/**
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * 
	 */
	public void removeStyleListener(ICSSStyleListener listener) {
		if (listener == null)
			return;

		if (listeners.contains(listener)) {
			listeners.remove(listener);

			// send old selectors event to listener
			ImportedCollector trav = new ImportedCollector();
			trav.apply(model.getDocument());
			Iterator it = trav.getExternals().iterator();
			while (it.hasNext()) {
				ICSSStyleSheet sheet = (ICSSStyleSheet) it.next();
				// collect selectors
				SelectorsCollector selTrav = new SelectorsCollector();
				selTrav.apply(sheet);
				int nSel = selTrav.getSelectors().size();
				ICSSSelector[] removed = new ICSSSelector[nSel];
				for (int i = 0; i < nSel; i++)
					removed[i] = (ICSSSelector) selTrav.getSelectors().get(i);

				// fire event
				CSSStyleEventDeliverer deliverer = new CSSStyleEventDeliverer();
				deliverer.fireTo(listener, sheet.getModel(), removed, null, null/*
																				 * media
																				 * will
																				 * be
																				 * implemented
																				 * in
																				 * the
																				 * future
																				 */);
			}
		}
	}

	/**
	 * 
	 */
	public void styleChanged(org.eclipse.wst.css.core.internal.provisional.document.ICSSModel srcModel, org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector[] removed, org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector[] added, java.lang.String media) {
	}

	/**
	 * 
	 */
	public void styleUpdate(org.eclipse.wst.css.core.internal.provisional.document.ICSSModel srcModel) {
	}
}
