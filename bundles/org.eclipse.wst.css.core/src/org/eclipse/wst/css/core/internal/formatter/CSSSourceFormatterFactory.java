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
package org.eclipse.wst.css.core.internal.formatter;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;


/**
 * 
 */
public class CSSSourceFormatterFactory {
	/**
	 * 
	 */
	private CSSSourceFormatterFactory() {
		super();
	}

	/**
	 * 
	 */
	public CSSSourceGenerator getSourceFormatter(INodeNotifier target) {
		ICSSNode node = (ICSSNode) target;
		short type = node.getNodeType();
		switch (type) {
			case ICSSNode.CHARSETRULE_NODE :
				return CharsetRuleFormatter.getInstance();
			case ICSSNode.FONTFACERULE_NODE :
				return FontFaceRuleFormatter.getInstance();
			case ICSSNode.IMPORTRULE_NODE :
				return ImportRuleFormatter.getInstance();
			case ICSSNode.MEDIALIST_NODE :
				return MediaListFormatter.getInstance();
			case ICSSNode.MEDIARULE_NODE :
				return MediaRuleFormatter.getInstance();
			case ICSSNode.PRIMITIVEVALUE_NODE :
				ICSSPrimitiveValue value = (ICSSPrimitiveValue) node;
				if (value.getPrimitiveType() == org.w3c.dom.css.CSSPrimitiveValue.CSS_COUNTER)
					return CounterFormatter.getInstance();
				else if (value.getPrimitiveType() == org.w3c.dom.css.CSSPrimitiveValue.CSS_RECT)
					return RectFormatter.getInstance();
				else if (value.getPrimitiveType() == org.w3c.dom.css.CSSPrimitiveValue.CSS_RGBCOLOR)
					return RGBFormatter.getInstance();
				else
					return PrimitiveValueFormatter.getInstance();
			case ICSSNode.PAGERULE_NODE :
				return PageRuleFormatter.getInstance();
			case ICSSNode.STYLEDECLARATION_NODE :
				return StyleDeclarationFormatter.getInstance();
			case ICSSNode.STYLEDECLITEM_NODE :
				return StyleDeclItemFormatter.getInstance();
			case ICSSNode.STYLERULE_NODE :
				return StyleRuleFormatter.getInstance();
			case ICSSNode.STYLESHEET_NODE :
				return StyleSheetFormatter.getInstance();
			case ICSSNode.ATTR_NODE :
				return AttrFormatter.getInstance();
			default :
				return UnknownRuleFormatter.getInstance();
		}
	}

	public synchronized static CSSSourceFormatterFactory getInstance() {
		if (fInstance == null) {
			fInstance = new CSSSourceFormatterFactory();
		}
		return fInstance;
	}

	private static CSSSourceFormatterFactory fInstance;
}
