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
package org.eclipse.jst.jsp.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CMContentWrapperImpl extends CMNodeWrapperImpl implements CMContent {

	private CMContent fCMContent = null;

	/**
	 * CMContentWrapper constructor comment.
	 * @param prefix java.lang.String
	 * @param node org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	public CMContentWrapperImpl(String prefix, org.eclipse.wst.xml.core.internal.contentmodel.CMContent node) {
		super(prefix, node);
		fCMContent = node;
	}

	/**
	 * getMaxOccur method
	 * @return int
	 *
	 * If -1, it's UNBOUNDED.
	 */
	public int getMaxOccur() {
		return fCMContent.getMaxOccur();
	}

	/**
	 * getMinOccur method
	 * @return int
	 *
	 * If 0, it's OPTIONAL.
	 * If 1, it's REQUIRED.
	 */
	public int getMinOccur() {
		return fCMContent.getMinOccur();
	}

	public CMNode getOriginNode() {
		return fCMContent;
	}
}
