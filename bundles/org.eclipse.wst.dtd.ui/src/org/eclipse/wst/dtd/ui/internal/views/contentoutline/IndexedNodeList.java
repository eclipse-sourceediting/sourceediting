/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.internal.views.contentoutline;

import org.eclipse.wst.dtd.core.internal.NodeList;


/* package */
class IndexedNodeList {
	public NodeList fTarget;

	public IndexedNodeList(NodeList target) {
		fTarget = target;
	}

	public boolean contains(Object child) {
		return fTarget.getNodes().contains(child);
	}

	/**
	 * @return
	 */
	public NodeList getTarget() {
		return fTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + ":" + fTarget.toString(); //$NON-NLS-1$
	}
}
