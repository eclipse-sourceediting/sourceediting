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
package org.eclipse.wst.css.ui.internal.contentoutline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.document.ICSSStyleRule;
import org.eclipse.wst.css.core.document.ICSSStyleSheet;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;

/**
 * Adapts the CSS DOM node to a JFace viewer.
 */
class CSSNodeAdapter implements INodeAdapter, Runnable {
	class NotifyContext {
		NotifyContext(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
			this.notifier = notifier;
			this.eventType = eventType;
			this.changedFeature = changedFeature;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.pos = pos;
		}

		void fire() {
			internalNotifyChanged(notifier, eventType, changedFeature, oldValue, newValue, pos);
		}

		INodeNotifier notifier;
		int eventType;
		Object changedFeature;
		Object oldValue;
		Object newValue;
		int pos;
	}

	class StyleViewUpdater implements Runnable {
		public void run() {
			if (lastUpdater == this) {
				internalActionPerformed();
				lastUpdater = null;
			}
		}
	}

	protected IAdapterFactory adapterFactory;
	private Vector notifyQueue;
	StyleViewUpdater lastUpdater;
	protected int delayMSecs = 500;
	final static Class ADAPTER_KEY = IJFaceNodeAdapter.class;

	public CSSNodeAdapter(IAdapterFactory adapterFactory) {
		super();
		this.adapterFactory = adapterFactory;
	}

	/**
	 * Insert the method's description here.
	 */
	protected void internalActionPerformed() {
		if (notifyQueue == null) {
			return;
		}
		boolean refresh_all = false;
		boolean refresh_rule = false;
		int pos_all = 0;
		List targets = new ArrayList();
		for (int i = 0; i < notifyQueue.size(); i++) {
			NotifyContext context = (NotifyContext) notifyQueue.get(i);
			if (context.notifier instanceof ICSSStyleSheet) {
				refresh_all = true;
				pos_all = i;
			}
			if (context.notifier instanceof ICSSStyleDeclaration) {
				refresh_rule = true;
				targets.add(context);
				//			pos_rule = i;
			}
			//		((NotifyContext) notifyQueue.get(i)).fire();
		}
		if (refresh_all) {
			((NotifyContext) notifyQueue.get(pos_all)).fire();
		} else if (refresh_rule) {
			Iterator i = targets.iterator();
			while (i.hasNext()) {
				((NotifyContext) i.next()).fire();
			}
			//	else if (refresh_rule) internalRefreshAll();
		} else {
			for (int i = 0; i < notifyQueue.size(); i++) {
				((NotifyContext) notifyQueue.get(i)).fire();
			}
		}
		notifyQueue.clear();
	}

	/**
	 * Called by the object being adapter (the notifier) when something has
	 * changed.
	 */
	public void internalNotifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		Iterator iterator = ((IJFaceNodeAdapterFactory) adapterFactory).getListeners().iterator();
		while (iterator.hasNext()) {
			Object listener = iterator.next();
			if (listener instanceof StructuredViewer) {
				notifyChangedForStructuredViewer((StructuredViewer) listener, notifier, eventType, changedFeature, oldValue, newValue, pos);
			} else if (listener instanceof PropertySheetPage) {
				notifyChangedForPropertySheetPage((PropertySheetPage) listener, notifier, eventType, changedFeature, oldValue, newValue, pos);
			}
		}
	}

	private void notifyChangedForPropertySheetPage(PropertySheetPage page, INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (page.getControl() == null || page.getControl().isDisposed()) {
			return;
		}
		if (eventType == INodeNotifier.CHANGE || eventType == INodeNotifier.ADD || eventType == INodeNotifier.REMOVE) {
			page.refresh();
		}
	}

	private void notifyChangedForStructuredViewer(StructuredViewer viewer, INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (viewer.getControl() == null || viewer.getControl().isDisposed()) {
			return;
		}
		if (eventType == INodeNotifier.CHANGE) {
			if (notifier instanceof ICSSStyleSheet) {
				ICSSNode temp = (changedFeature != null) ? (ICSSNode) changedFeature : (ICSSNode) newValue;
				if (temp instanceof ICSSStyleRule) {
					viewer.refresh();
				} else {
					for (;;) {
						if (temp instanceof ICSSStyleRule) {
							break;
						}
						temp = temp.getParentNode();
						if (temp == null) {
							break;
						}
					}
					if (temp == null || temp instanceof ICSSStyleSheet) {
						viewer.refresh();
					} else {
						viewer.refresh(temp);
					}
				}
			} else {
				ICSSNode temp = (ICSSNode) notifier;
				if (temp != null) {
					temp = temp.getParentNode();
				}
				if (temp == null || temp instanceof ICSSStyleSheet) {
					viewer.refresh();
				} else {
					viewer.refresh(temp);
				}
			}
		}
		if (eventType == INodeNotifier.ADD) {
			if (notifier instanceof ICSSStyleSheet) {
				ICSSNode temp = (changedFeature != null) ? (ICSSNode) changedFeature : (ICSSNode) newValue;
				if (temp instanceof ICSSStyleRule) {
					viewer.refresh();
				} else {
					for (;;) {
						if (temp instanceof ICSSStyleRule) {
							break;
						}
						temp = temp.getParentNode();
						if (temp == null) {
							break;
						}
					}
				}
				if (temp == null || (temp instanceof ICSSStyleSheet)) {
					viewer.refresh();
				} else {
					viewer.refresh(temp);
				}
			} else {
				if (newValue != null && (newValue instanceof ICSSStyleDeclItem)) {
					viewer.refresh(((ICSSNode) newValue).getParentNode());
				} else {
					ICSSNode temp = (ICSSNode) notifier;
					if (temp != null) {
						temp = temp.getParentNode();
					}
					if (temp == null || (temp instanceof ICSSStyleSheet)) {
						viewer.refresh();
					} else {
						viewer.refresh(temp);
					}
				}
			}
		} else if (eventType == INodeNotifier.REMOVE) {
			if (notifier instanceof ICSSStyleSheet) {
				ICSSNode temp = (changedFeature != null) ? (ICSSNode) changedFeature : (ICSSNode) newValue;
				if (temp instanceof ICSSStyleRule) {
					viewer.refresh();
				} else {
					for (;;) {
						if (temp instanceof ICSSStyleRule) {
							break;
						}
						temp = temp.getParentNode();
						if (temp == null) {
							break;
						}
					}
					if (temp == null || (temp instanceof ICSSStyleSheet)) {
						viewer.refresh();
					} else {
						viewer.refresh(temp);
					}
				}
			} else {
				//							viewer.refresh(notifier);
				ICSSNode temp = (ICSSNode) notifier;
				if (temp != null) {
					temp = temp.getParentNode();
				}
				if (temp == null || (temp instanceof ICSSStyleSheet)) {
					viewer.refresh();
				} else {
					viewer.refresh(temp);
				}
			}
		}
		//				}
	}

	/**
	 *  
	 */
	public void internalRefreshAll() {
		Collection listeners = ((JFaceNodeAdapterFactoryCSS) adapterFactory).getListeners();
		Iterator iterator = listeners.iterator();
		while (iterator.hasNext()) {
			Object listener = iterator.next();
			if (listener instanceof StructuredViewer) {
				StructuredViewer viewer = (StructuredViewer) listener;
				if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
					viewer.refresh();
				}
			} else if (listener instanceof PropertySheetPage) {
				PropertySheetPage page = (PropertySheetPage) listener;
				if (page.getControl() != null && !page.getControl().isDisposed()) {
					page.refresh();
				}
			}
		}
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type.equals(ADAPTER_KEY);
	}

	/**
	 * Called by the object being adapter (the notifier) when something has
	 * changed.
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (notifyQueue == null)
			notifyQueue = new Vector();
		notifyQueue.add(new NotifyContext(notifier, eventType, changedFeature, oldValue, newValue, pos));
		// TODO-future: there's probably a better way than relying on async
		// exec
		if (Thread.currentThread() == getDisplay().getThread())
			getDisplay().timerExec(delayMSecs, this);
		else
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getDisplay() != null) {
						getDisplay().timerExec(delayMSecs, this);
					}
				}
			});
	}

	Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * this method is intended only for timerExec()
	 */
	public void run() {
		lastUpdater = new StyleViewUpdater();
		getDisplay().asyncExec(lastUpdater);
	}
}