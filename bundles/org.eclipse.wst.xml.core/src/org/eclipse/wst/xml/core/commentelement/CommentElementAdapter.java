/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.commentelement;



import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.commentelement.impl.CommentElementConfiguration;
import org.eclipse.wst.xml.core.document.TagAdapter;
import org.eclipse.wst.xml.core.document.XMLElement;


/**
 */
public class CommentElementAdapter implements TagAdapter {
	public CommentElementAdapter(boolean isEndTag, CommentElementHandler handler) {
		fEndTag = isEndTag;
		fHandler = handler;
	}

	/**
	 */
	public String getStartTag(XMLElement element) {
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

	/**
	 */
	public String getEndTag(XMLElement element) {
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

	public IPluginDescriptor getHandlerPluginDescriptor() {
		return fConfiguration.getHandlerPluginDescriptor();
	}

	/**
	 */
	public boolean isEndTag() {
		return fEndTag;
	}

	public boolean isContainer() {
		return (!fHandler.isEmpty());
	}

	/**
	 */
	public boolean isAdapterForType(Object type) {
		return (type == CommentElementAdapter.class || type == TagAdapter.class);
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	private String generateCommentOpen(XMLElement element) {
		return (element.isJSPTag()) ? "<%--" : "<!--"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String generateCommentClose(XMLElement element) {
		return (element.isJSPTag()) ? "--%>" : "-->"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setConfiguration(CommentElementConfiguration configuration) {
		fConfiguration = configuration;
	}

	public String getProperty(String name) {
		return getConfiguration().getProperty(name);
	}

	private CommentElementConfiguration getConfiguration() {
		return fConfiguration;
	}

	private boolean fEndTag;
	private CommentElementHandler fHandler;
	private CommentElementConfiguration fConfiguration;
}
