/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.document.XMLModelNotifierImpl
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.util.Debug;

public class JSONModelNotifierImpl implements JSONModelNotifier {

	private static class NotifyEvent {
		Object changedFeature;
		boolean discarded;
		Object newValue;
		// note: don't initialize instance variables, since
		// that causes double assignments, and lots of these are created.
		INodeNotifier notifier;
		Object oldValue;
		int pos;
		String reason;
		int type;
		int index;

		NotifyEvent(INodeNotifier notifier, int type, Object changedFeature,
				Object oldValue, Object newValue, int pos) {
			this.notifier = notifier;
			this.type = type;
			this.changedFeature = changedFeature;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.pos = pos;
			this.reason = ""; //$NON-NLS-1$
		}
	}

	private final static String ADDED_THEN_REMOVED = "Discard: Added then removed rule"; //$NON-NLS-1$
	private final static boolean fOptimizeDeferred = true;
	private final static boolean fOptimizeDeferredAccordingToParentAdded = true;
	private final static boolean fOptimizeDeferredAccordingToParentRemoved = true;
	private final static String PARENT_IS_ADDED = "Disarded: Parent has just been added"; //$NON-NLS-1$
	/* start: for debugging only */
	private final static String PARENT_IS_REMOVED_TOO = "Discard: Parent was removed too"; //$NON-NLS-1$
	private final static String PARENT_IS_REPARENTED = "Not Discard: Parent was removed so this implies reparenting"; //$NON-NLS-1$
	private IJSONNode changedRoot = null;

	private boolean changing = false;
	private boolean doingNewModel = false;
	private List fEvents = null;
	private boolean flushing = false;

	/**
	 */
	public JSONModelNotifierImpl() {
		super();
	}

	/**
	 * attrReplaced method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 * @param newAttr
	 *            org.w3c.dom.IJSONNode
	 * @param oldAttr
	 *            org.w3c.dom.IJSONNode
	 */
	public void pairReplaced(IJSONObject element, IJSONPair newAttr,
			IJSONPair oldAttr) {
		if (element == null)
			return;
		IJSONNode attr = null;
		IJSONValue oldValue = null;
		IJSONValue newValue = null;
		if (oldAttr != null) {
			attr = oldAttr;
			oldValue = oldAttr.getValue();
		}
		if (newAttr != null) {
			attr = newAttr;
			newValue = newAttr.getValue();
		}
		IJSONNode notifier = (IJSONNode) element;
		int offset = notifier.getStartOffset();
		notify(notifier, INodeNotifier.CHANGE, attr, oldValue, newValue, offset);
		propertyChanged(notifier);
	}

	/**
	 */
	public void beginChanging() {
		this.changing = true;
	}

	/**
	 */
	public void beginChanging(boolean newModel) {
		beginChanging();
		this.doingNewModel = newModel;
	}

	public void cancelPending() {
		// we don't want to change the size of this array, since
		// the array may be being processed, in the deferred notification
		// loop, but we can signal that all
		// should be discarded, so any remaining ones will be ignored.
		if (this.fEvents != null) {
			int size = fEvents.size();
			for (int i = 0; i < size; i++) {
				NotifyEvent event = (NotifyEvent) fEvents.get(i);
				event.discarded = true;
			}
		}
		// this cancel is presumably being called as a function of
		// "reinitiailization" so we can ignore changes to the
		// old root, and changes to the new one will be triggered during
		// reinitialization.
		changedRoot = null;
	}

	/**
	 * childReplaced method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	@Override
	public void childReplaced(IJSONNode parentNode, IJSONNode newChild,
			IJSONNode oldChild) {
		if (parentNode == null)
			return;
		IJSONNode notifier = (IJSONNode) parentNode;
		int type = INodeNotifier.CHANGE;
		if (newChild == null)
			type = INodeNotifier.REMOVE;
		else if (oldChild == null)
			type = INodeNotifier.ADD;
		int offset = notifier.getStartOffset();
		notify(notifier, type, oldChild, oldChild, newChild, offset);
		structureChanged(notifier);
	}

	// public void editableChanged(Node node) {
	// if (node == null)
	// return;
	// IJSONNode notifier = (IJSONNode) node;
	// int offset = notifier.getStartOffset();
	// notify(notifier, INodeNotifier.CHANGE, null, null, null, offset);
	// propertyChanged(notifier);
	// }

	/**
	 */
	public void endChanging() {
		this.doingNewModel = false;
		if (!this.changing)
			return; // avoid nesting calls
		notifyDeferred();
		if (this.changedRoot != null) {
			notifyStructureChanged(this.changedRoot);
			if (Debug.debugNotifyDeferred) {
				String p = this.changedRoot.getNodeName();
				System.out.println("Deferred STRUCTURE_CHANGED: " + p); //$NON-NLS-1$
			}
			this.changedRoot = null;
		}
		this.changing = false;
	}

	/**
	 */
	// public void endTagChanged(Element element) {
	// if (element == null)
	// return;
	// IJSONNode notifier = (IJSONNode) element;
	// int offset = notifier.getStartOffset();
	// notify(notifier, INodeNotifier.CHANGE, null, null, null, offset);
	// propertyChanged(element);
	// }

	/**
	 */
	public boolean hasChanged() {
		return (this.fEvents != null);
	}

	/**
	 */
	public boolean isChanging() {
		return this.changing;
	}

	/**
	 */
	private void notify(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (notifier == null)
			return;
		if (this.changing && !this.flushing) {
			// defer notification
			if (this.fEvents == null)
				this.fEvents = new ArrayList();
			// we do not defer anything if we are doing a new Model,
			// except for the document event, since all others are
			// trivial and not needed at that initial point.
			// But even for that one document event, in the new model case,
			// it is still important to defer it.
			if ((!doingNewModel)
					|| (((IJSONNode) notifier).getNodeType() == IJSONNode.DOCUMENT_NODE)) {
				this.fEvents.add(new NotifyEvent(notifier, eventType,
						changedFeature, oldValue, newValue, pos));
			}
			return;
		}
		try {
			// Its important to "keep going" if exception occurs, since this
			// notification
			// comes in between "about to change" and "changed" events. We do
			// log, however,
			// since would indicate a program error.
			notifier.notify(eventType, changedFeature, oldValue, newValue, pos);
		} catch (Exception e) {
			Logger.logException(
					"A structured model client threw following exception during adapter notification (" + INodeNotifier.EVENT_TYPE_STRINGS[eventType] + " )", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 */
	private void notifyDeferred() {
		if (this.fEvents == null)
			return;
		if (this.flushing)
			return;
		this.flushing = true; // force notification
		int count = this.fEvents.size();

		if (!doingNewModel && fOptimizeDeferred) {
			Map notifyEvents = new HashMap();
			for (int i = 0; i < count; i++) {
				NotifyEvent event = (NotifyEvent) this.fEvents.get(i);
				if (event == null)
					continue; // error
				event.index = i;
				if (event.type == INodeNotifier.REMOVE) {
					addToMap(event.oldValue, event, notifyEvents);
				}
				if (event.type == INodeNotifier.ADD) {
					addToMap(event.newValue, event, notifyEvents);
				}
			}
			Iterator it = notifyEvents.values().iterator();
			while (it.hasNext()) {
				NotifyEvent[] es = (NotifyEvent[]) it.next();
				for (int i = 0; i < es.length - 1; i++) {
					NotifyEvent event = es[i];
					if (es[i].discarded)
						continue;
					NotifyEvent next = es[i + 1];
					if (es[i].type == INodeNotifier.ADD
							&& next.type == INodeNotifier.REMOVE) {
						// Added then removed later, discard both
						event.discarded = true;
						next.discarded = true;
						if (Debug.debugNotifyDeferred) {
							event.reason = event.reason + ADDED_THEN_REMOVED
									+ "(see " + next.index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
							next.reason = next.reason + ADDED_THEN_REMOVED
									+ "(see " + event.index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
			for (int i = 0; i < count; i++) {
				NotifyEvent event = (NotifyEvent) this.fEvents.get(i);
				if (event == null)
					continue; // error
				if (event.discarded)
					continue;
				if (event.notifier != null
						&& fOptimizeDeferredAccordingToParentAdded) {
					if (event.type == INodeNotifier.ADD) {
						NotifyEvent[] es = (NotifyEvent[]) notifyEvents
								.get(event.notifier);
						if (es != null)
							for (int p = 0; p < es.length
									&& es[p].index < event.index; p++) {
								NotifyEvent prev = es[p];
								if (prev.type == INodeNotifier.REMOVE
										&& prev.oldValue == event.notifier) {
									// parent is reparented, do not discard
									if (Debug.debugNotifyDeferred) {
										event.reason = event.reason
												+ PARENT_IS_REPARENTED
												+ "(see " + prev.index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
									}
									break;
								} else if (prev.type == INodeNotifier.ADD
										&& prev.newValue == event.notifier) {
									// parent has been added, discard this
									event.discarded = true;
									if (Debug.debugNotifyDeferred) {
										event.reason = event.reason
												+ PARENT_IS_ADDED
												+ "(see " + prev.index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
									}
									break;
								}
							}
					}
				}
				if (event.discarded)
					continue;
				if (event.notifier != null
						&& fOptimizeDeferredAccordingToParentRemoved) {
					if (event.type == INodeNotifier.REMOVE) {
						NotifyEvent[] es = (NotifyEvent[]) notifyEvents
								.get(event.notifier);
						if (es != null)
							for (int n = 0; n < es.length; n++) {
								NotifyEvent next = es[n];
								if (next.index > event.index
										&& next.type == INodeNotifier.REMOVE) {
									if (next.oldValue == event.notifier) {
										// parent will be removed, discard this
										event.discarded = true;
										if (Debug.debugNotifyDeferred) {
											event.reason = event.reason
													+ PARENT_IS_REMOVED_TOO
													+ "(see " + next.index + ")"; //$NON-NLS-1$ //$NON-NLS-2$
										}
										break;
									}
								}
							}
					}
				}
				if (event.discarded)
					continue;
			}
		}
		for (int i = 0; i < count; i++) {
			NotifyEvent event = (NotifyEvent) this.fEvents.get(i);
			if (event == null)
				continue; // error
			if (event.discarded)
				continue;
			notify(event.notifier, event.type, event.changedFeature,
					event.oldValue, event.newValue, event.pos);
		}
		if (Debug.debugNotifyDeferred) {
			for (int l = 0; l < count; l++) {
				NotifyEvent event = (NotifyEvent) this.fEvents.get(l);
				Object o = null;
				String t = null;
				if (event.type == INodeNotifier.ADD) {
					o = event.newValue;
					t = " + "; //$NON-NLS-1$
				} else if (event.type == INodeNotifier.REMOVE) {
					o = event.oldValue;
					t = " - "; //$NON-NLS-1$
				}
				if (o instanceof IJSONObject) {
					String p = ((IJSONNode) event.notifier).getNodeName();
					String c = ((IJSONNode) o).getNodeName();
					String d = (event.discarded ? "! " : "  "); //$NON-NLS-1$ //$NON-NLS-2$
					System.out.println(d + p + t + c);
				}
			}
		}
		this.flushing = false;
		this.fEvents = null;
	}

	void addToMap(Object o, NotifyEvent event, Map map) {
		if (o == null)
			return;
		Object x = map.get(o);
		if (x == null) {
			map.put(o, new NotifyEvent[] { event });
		} else {
			NotifyEvent[] es = (NotifyEvent[]) x;
			NotifyEvent[] es2 = new NotifyEvent[es.length + 1];
			System.arraycopy(es, 0, es2, 0, es.length);
			es2[es.length] = event;
			map.put(o, es2);
		}
	}

	/**
	 */
	private void notifyStructureChanged(IJSONNode root) {
		if (root == null)
			return;
		INodeNotifier notifier = (INodeNotifier) root;
		try {
			// Its important to "keep going" if exception occurs, since this
			// notification
			// comes in between "about to change" and "changed" events. We do
			// log, however,
			// since would indicate a program error.
			notifier.notify(INodeNotifier.STRUCTURE_CHANGED, null, null, null,
					-1);
		} catch (Exception e) {
			Logger.logException(
					"A structured model client threw following exception during adapter notification (" + INodeNotifier.EVENT_TYPE_STRINGS[INodeNotifier.STRUCTURE_CHANGED] + " )", e); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * @param node
	 */
	private void setCommonRootIfNeeded(IJSONNode node) {
		// defer notification
		if (this.changedRoot == null) {
			this.changedRoot = node;
		} else {
			// tiny optimization: if previous commonAncestor (changedRoot) is
			// already 'document',
			// or if already equal to this 'node',
			// then no need to re-calculate
			if (changedRoot.getNodeType() != IJSONNode.DOCUMENT_NODE
					&& changedRoot != node) {
				// IJSONNode common = ((JSONNodeImpl)
				// this.changedRoot).getCommonAncestor(node);
				// if (common != null)
				// this.changedRoot = common;
				// else
				// this.changedRoot = node;
			}
		}
	}

	/**
	 */
	// public void startTagChanged(Element element) {
	// if (element == null)
	// return;
	// IJSONNode notifier = (IJSONNode) element;
	// int offset = notifier.getStartOffset();
	// notify(notifier, INodeNotifier.CHANGE, null, null, null, offset);
	// propertyChanged(element);
	// }

	@Override
	public void structureChanged(IJSONNode node) {
		if (node == null)
			return;
		if (isChanging()) {
			setCommonRootIfNeeded(node);
			if (Debug.debugNotifyDeferred) {
				String p = this.changedRoot.getNodeName();
				System.out.println("requested STRUCTURE_CHANGED: " + p); //$NON-NLS-1$
			}
			return;
		}
		if (Debug.debugNotifyDeferred) {
			String p = node.getNodeName();
			System.out.println("STRUCTURE_CHANGED: " + p); //$NON-NLS-1$
		}
		notifyStructureChanged(node);
	}

	/**
	 * valueChanged method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	// public void valueChanged(Node node) {
	// if (node == null)
	// return;
	// IJSONNode notifier = null;
	// if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
	// IJSONNode attr = (IJSONNode) node;
	// notifier = (IJSONNode) attr.getOwnerElement();
	// // TODO_dmw: experimental: changed 06/29/2004 to send "structuure
	// // changed" even for attribute value changes
	// // there are pros and cons to considering attribute value
	// // "structure changed". Will (re)consider
	// // setCommonRootIfNeeded(notifier);
	// if (notifier == null)
	// return;
	// String value = attr.getValue();
	// int offset = notifier.getStartOffset();
	// notify(notifier, INodeNotifier.CHANGE, attr, null, value, offset);
	// } else {
	// // note: we do not send structured changed event for content
	// // changed
	// notifier = (IJSONNode) node;
	// String value = node.getNodeValue();
	// int offset = notifier.getStartOffset();
	// notify(notifier, INodeNotifier.CHANGE, null, null, value, offset);
	// if (node.getNodeType() != Node.ELEMENT_NODE) {
	// IJSONNode parent = (IJSONNode) node.getParentNode();
	// if (parent != null) {
	// notify(parent, INodeNotifier.CONTENT_CHANGED, node, null, value, offset);
	// }
	// }
	// }
	// propertyChanged(notifier);
	// }

	@Override
	public void endTagChanged(IJSONObject element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChanged(IJSONNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startTagChanged(IJSONObject element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueChanged(IJSONNode node) {
		// TODO Auto-generated method stub

	}
}
