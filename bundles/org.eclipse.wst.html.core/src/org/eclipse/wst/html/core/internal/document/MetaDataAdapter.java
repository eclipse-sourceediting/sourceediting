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



import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.document.TagAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 */
public class MetaDataAdapter implements TagAdapter, MetaData {

	private IDOMElement element = null;
	private String type = null;
	private String data = null;
	private String endData = null;

	/**
	 */
	public MetaDataAdapter(String type) {
		super();

		if (type != null) {
			if (type.equals(ANNOTATION)) {
				this.type = ANNOTATION;
			}
			else if (type.equals(AUTHOR_TIME_VISUAL)) {
				this.type = AUTHOR_TIME_VISUAL;
			}
			else {
				this.type = type;
			}
		}
	}

	/**
	 */
	private String getData(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return null;
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return null;

		String data = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (isCommentText(regionType)) {
				data = flatNode.getText(region);
				break;
			}
		}
		if (data == null)
			return null;
		int length = data.length();
		int offset = 0;
		for (; offset < length; offset++) {
			char c = data.charAt(offset);
			if (c == '\r' || c == '\n') {
				offset++;
				break;
			}
		}
		for (; offset < length; offset++) {
			char c = data.charAt(offset);
			if (c != '\r' && c != '\n') {
				break;
			}
		}
		return data.substring(offset);
	}

	private boolean isCommentText(String regionType) {
		boolean result = false;
		result = isDOMComment(regionType) || isNestedContentComment(regionType);
		return result;
	}

	/**
	 * ISSUE: this is a bit of hidden JSP knowledge that was implemented this
	 * way for expedency. Should be evolved in future to depend on
	 * "nestedContext".
	 */

	private boolean isNestedContentComment(String regionType) {
		final String JSP_COMMENT_TEXT = "JSP_COMMENT_TEXT"; //$NON-NLS-1$
		return regionType.equals(JSP_COMMENT_TEXT);
	}

	private boolean isDOMComment(String regionType) {
		return regionType == DOMRegionContext.XML_COMMENT_TEXT;
	}

	public String getData() {
		if (this.element == null)
			return null;
		IStructuredDocumentRegion flatNode = this.element.getStartStructuredDocumentRegion();
		if (flatNode == null)
			return null;
		if (this.data != null)
			return this.data;
		return getData(flatNode);
	}

	/**
	 */
	private String getDelimiter(IDOMModel model) {
		String delim = null;
		if (model != null) {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			if (structuredDocument != null)
				delim = structuredDocument.getLineDelimiter();
		}
		if (delim == null)
			delim = "\r\n";//$NON-NLS-1$
		return delim;
	}

	/**
	 */
	public String getEndData() {
		if (this.element == null)
			return null;
		IStructuredDocumentRegion flatNode = this.element.getEndStructuredDocumentRegion();
		if (flatNode == null)
			return null;
		if (this.endData != null)
			return this.endData;
		return getData(flatNode);
	}

	/**
	 */
	public String getEndTag(IDOMElement element) {
		StringBuffer buffer = new StringBuffer();
		if (element.isJSPTag())
			buffer.append("<%--");//$NON-NLS-1$
		else
			buffer.append("<!--");//$NON-NLS-1$
		buffer.append(METADATA);
		buffer.append(' ');
		buffer.append(TYPE);
		buffer.append("=\"");//$NON-NLS-1$
		buffer.append(this.type);
		buffer.append("\" ");//$NON-NLS-1$
		buffer.append(MetaData.ENDSPAN);
		String data = getEndData();
		if (data != null && data.length() > 0) {
			String delim = getDelimiter(element.getModel());
			buffer.append(delim);
			buffer.append(data);
			buffer.append(delim);
		}
		if (element.isJSPTag())
			buffer.append("--%>");//$NON-NLS-1$
		else
			buffer.append("-->");//$NON-NLS-1$

		return buffer.toString();
	}

	/**
	 */
	public String getStartTag(IDOMElement element) {
		StringBuffer buffer = new StringBuffer();
		if (element.isJSPTag())
			buffer.append("<%--");//$NON-NLS-1$
		else
			buffer.append("<!--");//$NON-NLS-1$
		buffer.append(METADATA);
		buffer.append(' ');
		buffer.append(TYPE);
		buffer.append("=\"");//$NON-NLS-1$
		buffer.append(this.type);
		buffer.append("\" ");//$NON-NLS-1$
		buffer.append(MetaData.STARTSPAN);
		String data = getData();
		if (data != null && data.length() > 0) {
			String delim = getDelimiter(element.getModel());
			buffer.append(delim);
			buffer.append(data);
			buffer.append(delim);
		}
		if (element.isJSPTag())
			buffer.append("--%>");//$NON-NLS-1$
		else
			buffer.append("-->");//$NON-NLS-1$

		return buffer.toString();
	}

	/**
	 */
	public String getType() {
		return this.type;
	}

	/**
	 */
	public boolean isAdapterForType(Object type) {
		return (type == TagAdapter.class || type == MetaDataAdapter.class);
	}

	/**
	 */
	public boolean isEndTag() {
		if (this.element == null)
			return false;
		if (this.element.hasStartTag())
			return false;
		if (this.element.hasEndTag())
			return true;
		if (this.data != null)
			return false;
		return (this.endData != null);
	}

	/**
	 */
	public boolean isRuntimeContainer() {
		return (this.type == ANNOTATION || this.type == AUTHOR_TIME_VISUAL);
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	/**
	 */
	public void setData(String data) {
		this.data = data;

		if (this.element != null)
			this.element.notifyStartTagChanged();
	}

	/**
	 */
	public void setEndData(String data) {
		this.endData = data;

		if (this.element != null)
			this.element.notifyEndTagChanged();
	}

	/**
	 */
	public void setElement(IDOMElement element) {
		this.element = element;

		if (this.element != null) {
			this.element.setCommentTag(true);
			if (this.type != MetaData.ANNOTATION) {
				this.element.setJSPTag(true);
			}
		}
	}

	/**
	 */
	public void setRuntimeSource(String source) {
		if (source == null)
			return;
		if (this.element == null)
			return;
		if (isRuntimeContainer())
			return;

		IDOMModel model = this.element.getModel();
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;
		int offset = this.element.getStartEndOffset();
		int end = this.element.getEndStartOffset();
		int length = end - offset;
		structuredDocument.replaceText(model, offset, length, source);
	}
}
