/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.editor;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.contentmodel.CMNode;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author nsd
 */
public class CMImageUtil {

	public static CMNode getDeclaration(Node node) {
		CMNode decl = null;
		ModelQuery mq = null;
		switch (node.getNodeType()) {
			case Node.ATTRIBUTE_NODE : {
				mq = ModelQueryUtil.getModelQuery(((Attr) node).getOwnerDocument());
				decl = mq.getCMAttributeDeclaration((Attr) node);
			}
			case Node.ELEMENT_NODE : {
				mq = ModelQueryUtil.getModelQuery(node.getOwnerDocument());
				decl = mq.getCMElementDeclaration((Element) node);
			}
		}
		return decl;
	}

	public static Image getImage(CMNode cmnode) {
		if (cmnode == null)
			return null;
		Image image = null;
		ImageDescriptor descriptor = getImageDescriptor(cmnode);
		if (descriptor != null) {
			image = descriptor.createImage(false);
		}
		return image;
	}

	public static ImageDescriptor getImageDescriptor(CMNode cmnode) {
		if (cmnode == null)
			return null;
		// cache CM-specified images with the XML UI plugin
		String imageURL = (String) cmnode.getProperty("small-icon");
		ImageDescriptor descriptor = null;
		if (imageURL != null && imageURL.length() > 0) {
			descriptor = XMLUIPlugin.getInstance().getImageRegistry().getDescriptor(imageURL);
			if (descriptor == null) {
				try {
					descriptor = ImageDescriptor.createFromURL(new URL(imageURL));
					XMLUIPlugin.getInstance().getImageRegistry().put(imageURL, descriptor);
				}
				catch (MalformedURLException e) {
					descriptor = null;
				}
			}
		}
		return descriptor;
	}

	/**
	 * 
	 */
	private CMImageUtil() {
		super();
	}
}
