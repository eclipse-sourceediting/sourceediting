/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Orangevolt (www.orangevolt.com) - XSLT support
 *     Jesper Steen Moller - refactored Orangevolt XSLT support into WST
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * 
 * @author dcarver
 * @deprecated
 */
@Deprecated
public class DOMNodeLabelProvider extends LabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof Document) {
			return element.toString();
		} else if (element instanceof Element) {
			StringBuffer sb = new StringBuffer(((Element) element).getTagName());

			NamedNodeMap attrs = ((Element) element).getAttributes();
			if (attrs.getLength() > 0) {
				sb.append("("); //$NON-NLS-1$
				for (int i = 0; i < attrs.getLength(); i++) {
					Attr attr = (Attr) attrs.item(i);
					sb.append('@').append(attr.getName())
							.append("=\"").append(attr.getValue()).append('\"'); //$NON-NLS-1$
					if (i < attrs.getLength() - 1) {
						sb.append(' ');
					}
				}
				sb.append(")"); //$NON-NLS-1$
			}
			return sb.toString();
		} else if (element instanceof Comment) {
			return element.toString();
		} else if (element instanceof Attr) {
			Attr attr = (Attr) element;

			return "@" + attr.getName() + "=\"" + attr.getValue() + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (element instanceof CDATASection) {
			return element.toString();
		} else if (element instanceof Entity) {
			return element.toString();
		} else if (element instanceof ProcessingInstruction) {
			return element.toString();
		} else if (element instanceof DocumentType) {
			return element.toString();
		} else if (element instanceof Text) {
			return ((Text) element).getData();
		} else {
			return element.toString();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof Document) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_TAG_GENERIC);
		} else if (element instanceof Element) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_ELEMENT);
		} else if (element instanceof Comment) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_COMMENT);
		} else if (element instanceof CDATASection) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_CDATASECTION);
		} else if (element instanceof Entity) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_ENTITY);
		} else if (element instanceof ProcessingInstruction) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_PROCESSINGINSTRUCTION);
		} else if (element instanceof DocumentType) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_DOCTYPE);
		} else if (element instanceof Attr) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
		} else if (element instanceof Text) {
			return XMLEditorPluginImageHelper.getInstance().getImage(
					XMLEditorPluginImages.IMG_OBJ_TXTEXT);
		} else {
			return super.getImage(element);
		}
	}
}
