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
package org.eclipse.wst.html.core.internal.htmlcss;



import java.util.Enumeration;

import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.eclipse.wst.css.core.internal.util.CSSLinkConverter;
import org.eclipse.wst.css.core.internal.util.declaration.CSSPropertyContext;

/**
 */
class CSSQueryContext extends CSSPropertyContext {

	/**
	 */
	public CSSQueryContext() {
		super();
	}

	/**
	 */
	public CSSQueryContext(ICSSStyleDeclaration decl) {
		super(decl);
	}

	/**
	 *
	 */
	public void applyFull(ICSSStyleDeclaration decl) {
		if (decl == null)
			return;
		Enumeration keys = fProperties.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object val = fProperties.get(key);

			if (val instanceof CSSQueryDeclarationData) {
				ICSSStyleDeclItem declItem = ((CSSQueryDeclarationData) val).getDeclItem();
				if (declItem.getLength() <= 0) {
					ICSSStyleDeclItem itemToRemove = decl.getDeclItemNode(key.toString());
					if (itemToRemove != null) {
						decl.removeDeclItemNode(itemToRemove);
					}
				}
				else {
					decl.setDeclItemNode(declItem);
				}
			}
			else {
				String value = (val instanceof ICSSValue) ? ((ICSSValue) val).getCSSValueText() : val.toString();

				if (value == null || value.length() <= 0) {
					ICSSStyleDeclItem itemToRemove = decl.getDeclItemNode(key.toString());
					if (itemToRemove != null) {
						decl.removeDeclItemNode(itemToRemove);
					}
				}
				else {
					decl.setProperty(key.toString(), value, null);
				}
			}
		}
	}

	/**
	 */
	private boolean check(String propName, boolean important, int specificity) {
		Object current = fProperties.get(propName);
		if (current != null && current instanceof CSSQueryValueData) {
			CSSQueryValueData currentValue = (CSSQueryValueData) current;
			if ((!important && currentValue.important) || (currentValue.getSpecificity() > specificity)) {
				return false;
			}
		}
		return true;
	}

	/**
	 */
	public void overrideWithExpand(ICSSStyleDeclaration decl, int specificity) {
		if (decl == null)
			return;

		CSSLinkConverter conv = new CSSLinkConverter(decl.getOwnerDocument().getModel());

		int nProperties = decl.getLength();
		for (int i = 0; i < nProperties; i++) {
			String propName = decl.item(i);
			if (propName != null) {
				String propN = propName.trim().toLowerCase();
				if (propN.length() != 0) {
					PropCMProperty prop = PropCMProperty.getInstanceOf(propN);
					String priority = decl.getPropertyPriority(propName);
					boolean important = priority != null && priority.length() > 0;
					if (prop != null && prop.isShorthand()) {
						// expand shorthand property
						CSSQueryContext context = new CSSQueryContext();
						expandToLeaf(prop, decl.getPropertyValue(propName), context);

						Enumeration properties = context.properties();
						while (properties.hasMoreElements()) {
							propN = properties.nextElement().toString();
							if (check(propN, important, specificity)) {
								fProperties.put(propN, new CSSQueryValueData(conv.toAbsolute(context.get(propN)), important, specificity));
							}
						}
					}
					else {
						if (check(propN, important, specificity)) {
							ICSSStyleDeclItem declItem = (ICSSStyleDeclItem) decl.getDeclItemNode(propName).cloneNode(true);
							int nValues = declItem.getLength();
							for (int j = 0; j < nValues; j++) {
								conv.toAbsolute(declItem.item(j));
							}
							declItem.setPriority(null);
							fProperties.put(propN, new CSSQueryDeclarationData(declItem, important, specificity));
						}
					}
				}
			}
		}
	}
}
