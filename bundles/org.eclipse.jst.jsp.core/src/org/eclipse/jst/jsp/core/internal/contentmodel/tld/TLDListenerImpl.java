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
/*
 * Created on Sep 9, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDListener;
import org.eclipse.wst.sse.core.utils.StringUtils;


public class TLDListenerImpl implements TLDListener {
	protected String listenerClass;

	public String getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(String className) {
		listenerClass = className;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("\n\t listener class:" + StringUtils.escape(getListenerClass())); //$NON-NLS-1$
		return buffer.toString();
	}
}
