/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.model;

import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * This is an early version of a class that may change over the next few
 * milestones.
 */


public class ModelLifecycleEvent {


	// this list is for "public" consumption
	public static final int MODEL_SAVED = 0x0001;
	public static final int MODEL_RELEASED = 0x00002;
	public static final int MODEL_DOCUMENT_CHANGED = 0x0003;
	public static final int MODEL_DIRTY_STATE = 0x0004;
	public static final int MODEL_REVERT= 0x0005;

	// TODO: finish support for these
	// following not implemented yet
	public static final int MODEL_REINITIALIZED = 0x0006;
	//public static final int ADAPTERS_NOTIFIED = 0x0007;
	//public static final int MODEL_RESOURCE_MOVED = 0x0008;
	//public static final int MODEL_RESOURCE_DELETED = 0x0009;

	// This list (upper two bytes) is for only internal mechanisms and
	// subclasses
	// For simplicity they are "masked out" when client calls getType()
	protected static final int PRE_EVENT = 0x0100;
	private final static int MASK = 0x00FF;
	protected static final int POST_EVENT = 0x0200;


	private IStructuredModel fModel;
	private int fType;

	public ModelLifecycleEvent() {
		super();
	}

	public ModelLifecycleEvent(int type) {
		this();
		fType = type;
	}

	public ModelLifecycleEvent(IStructuredModel structuredModel, int type) {
		this(type);
		fModel = structuredModel;
	}

	private String debugString(int type) {
		String result = null;
		switch (type & MASK) {
			case MODEL_SAVED :
				result = "MODEL_SAVED"; //$NON-NLS-1$
				break;
			case MODEL_RELEASED :
				result = "MODEL_RELEASED"; //$NON-NLS-1$
				break;
			case MODEL_DOCUMENT_CHANGED :
				result = "MODEL_DOCUMENT_CHANGED"; //$NON-NLS-1$
				break;
			case MODEL_DIRTY_STATE :
				result = "MODEL_DIRTY_STATE"; //$NON-NLS-1$
				break;
			/*
			 * case MODEL_REINITIALIZED : result = "MODEL_REINITIALIZED";
			 * break; case MODEL_RELOADED : result = "MODEL_RELOADED"; break;
			 * case ADAPTERS_NOTIFIED : result = "ADAPTERS_NOTIFIED"; break;
			 * case MODEL_RESOURCE_MOVED : result = "MODEL_RESOURCE_MOVED";
			 * break; case MODEL_RESOURCE_DELETED : result =
			 * "MODEL_RESOURCE_DELETED"; break;
			 */
			default :
				throw new IllegalStateException("ModelLifecycleEvent did not have valid type"); //$NON-NLS-1$
		}
		return result;
	}

	protected int getInternalType() {

		return fType;
	}

	public IStructuredModel getModel() {

		return fModel;
	}

	public int getType() {

		// for now, we'll mask type to "public" ones this easy
		// way ... but I know there must be a better way
		return fType & MASK;
	}

	public String toString() {
		String result = null;
		result = "ModelLifecycleEvent: " + debugString(fType); //$NON-NLS-1$
		return result;
	}

}
