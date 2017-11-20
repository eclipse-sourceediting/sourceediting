/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.commentelement;



import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.commentelement.impl.CommentElementConfiguration;
import org.eclipse.wst.xml.core.internal.document.TagAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;


/**
 */
public class CommentElementAdapter implements TagAdapter {
	private CommentElementConfiguration fConfiguration;

	private boolean fEndTag;
	private CommentElementHandler fHandler;

	public CommentElementAdapter(boolean isEndTag, CommentElementHandler handler) {
		fEndTag = isEndTag;
		fHandler = handler;
	}

	private String generateCommentClose(IDOMElement element) {
		return (element.isJSPTag()) ? "--%>" : "-->"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String generateCommentOpen(IDOMElement element) {
		return (element.isJSPTag()) ? "<%--" : "<!--"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private CommentElementConfiguration getConfiguration() {
		return fConfiguration;
	}

	public String getEndTag(IDOMElement element) {
		String content = fHandler.generateEndTagContent(element);
		if (content == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();

		buffer.append(generateCommentOpen(element));
		buffer.append(content);
		buffer.append(generateCommentClose(element));

		return buffer.toString();
	}

	public String getHandlerID() {
		return getConfiguration().getHandlerID();
	}


	public String getProperty(String name) {
		return getConfiguration().getProperty(name);
	}

	public String getStartTag(IDOMElement element) {
		String content = fHandler.generateStartTagContent(element);
		if (content == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();

		buffer.append(generateCommentOpen(element));
		buffer.append(content);
		buffer.append(generateCommentClose(element));

		return buffer.toString();
	}

	public boolean isAdapterForType(Object type) {
		return (type == CommentElementAdapter.class || type == TagAdapter.class);
	}

	public boolean isContainer() {
		return (!fHandler.isEmpty());
	}

	public boolean isEndTag() {
		return fEndTag;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	public void setConfiguration(CommentElementConfiguration configuration) {
		fConfiguration = configuration;
	}
}
