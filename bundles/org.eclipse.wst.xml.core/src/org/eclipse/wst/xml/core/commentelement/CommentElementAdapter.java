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
package org.eclipse.wst.xml.core.commentelement;



import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.commentelement.impl.CommentElementConfiguration;
import org.eclipse.wst.xml.core.document.TagAdapter;
import org.eclipse.wst.xml.core.document.XMLElement;


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

	private String generateCommentClose(XMLElement element) {
		return (element.isJSPTag()) ? "--%>" : "-->"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String generateCommentOpen(XMLElement element) {
		return (element.isJSPTag()) ? "<%--" : "<!--"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private CommentElementConfiguration getConfiguration() {
		return fConfiguration;
	}

	/**
	 * @see com.ibm.sed.model.xml.TagAdapter#getEndTag(XMLElement)
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

	/**
	 * @deprecated this should not be needed by anyone
	 */
	public IPluginDescriptor getHandlerPluginDescriptor() {
		return fConfiguration.getHandlerPluginDescriptor();
	}

	public String getProperty(String name) {
		return getConfiguration().getProperty(name);
	}

	/**
	 * @see com.ibm.sed.model.xml.TagAdapter#getStartTag(XMLElement)
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
	 * @see com.ibm.sed.model.INodeAdapter#isAdapterForType(Object)
	 */
	public boolean isAdapterForType(Object type) {
		return (type == CommentElementAdapter.class || type == TagAdapter.class);
	}

	public boolean isContainer() {
		return (!fHandler.isEmpty());
	}

	/**
	 * @see com.ibm.sed.model.xml.TagAdapter#isEndTag()
	 */
	public boolean isEndTag() {
		return fEndTag;
	}

	/**
	 * @see com.ibm.sed.model.INodeAdapter#notifyChanged(INodeNotifier, int,
	 *      Object, Object, Object, int)
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	public void setConfiguration(CommentElementConfiguration configuration) {
		fConfiguration = configuration;
	}
}
