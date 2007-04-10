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
package org.eclipse.wst.html.core.internal.document;



import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.document.TagAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 */
public class UnknownTagAdapter implements TagAdapter {

	private String startTag = null;
	private String endTag = null;

	/**
	 */
	public UnknownTagAdapter() {
		super();
	}

	/**
	 */
	public String getEndTag(IDOMElement element) {
		String tag = null;
		if (this.endTag != null) {
			tag = this.endTag;
			this.endTag = null;
		}
		return tag;
	}

	/**
	 */
	public String getStartTag(IDOMElement element) {
		String tag = null;
		if (this.startTag != null) {
			tag = this.startTag;
			this.startTag = null;
		}
		return tag;
	}

	/**
	 */
	public boolean isAdapterForType(Object type) {
		return (type == TagAdapter.class);
	}

	/**
	 */
	public boolean isEndTag() {
		return false;
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	/**
	 */
	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}

	/**
	 */
	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}
}
