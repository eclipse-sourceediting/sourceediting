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
package org.eclipse.wst.dtd.ui.style.dtd;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.ui.preferences.ui.DTDColorManager;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class LineStyleProviderForDTD extends AbstractLineStyleProvider implements LineStyleProvider {
	public LineStyleProviderForDTD() {
		super();
		loadColors();
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		/**
		 * a method to centralize all the "format rules" for regions 
		 * specifically associated for how to "open" the region.
		 */
		// not sure why this is coming through null, but just to catch it
		if (region == null) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_DEFAULT);
		}
		String type = region.getType();
		if (type == DTDRegionTypes.CONTENT_EMPTY || type == DTDRegionTypes.CONTENT_ANY || type == DTDRegionTypes.CONTENT_PCDATA) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_DATA);
		}
		else if (type == DTDRegionTypes.ELEMENT_TAG || type == DTDRegionTypes.ENTITY_TAG || type == DTDRegionTypes.ATTLIST_TAG || type == DTDRegionTypes.NOTATION_TAG) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_TAGNAME);
		}
		else if (type == DTDRegionTypes.CONNECTOR || type == DTDRegionTypes.OCCUR_TYPE) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_SYMBOL);
		}
		else if (type == DTDRegionTypes.NDATA_VALUE) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_DATA);
		}
		else if (type == DTDRegionTypes.START_TAG || type == DTDRegionTypes.END_TAG || type == DTDRegionTypes.EXCLAMATION) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_TAG);
		}
		else if (type == DTDRegionTypes.COMMENT_START || type == DTDRegionTypes.COMMENT_CONTENT || type == DTDRegionTypes.COMMENT_END) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_COMMENT);
		}
		else if (type == DTDRegionTypes.SINGLEQUOTED_LITERAL || type == DTDRegionTypes.DOUBLEQUOTED_LITERAL) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_STRING);
		}
		else if (type == DTDRegionTypes.SYSTEM_KEYWORD || type == DTDRegionTypes.PUBLIC_KEYWORD || type == DTDRegionTypes.NDATA_KEYWORD || type == DTDRegionTypes.CDATA_KEYWORD || type == DTDRegionTypes.ID_KEYWORD || type == DTDRegionTypes.IDREF_KEYWORD || type == DTDRegionTypes.IDREFS_KEYWORD || type == DTDRegionTypes.ENTITY_KEYWORD || type == DTDRegionTypes.ENTITIES_KEYWORD || type == DTDRegionTypes.NMTOKEN_KEYWORD || type == DTDRegionTypes.NMTOKENS_KEYWORD || type == DTDRegionTypes.NOTATION_KEYWORD || type == DTDRegionTypes.REQUIRED_KEYWORD || type == DTDRegionTypes.IMPLIED_KEYWORD || type == DTDRegionTypes.FIXED_KEYWORD) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_KEYWORD);
		}
		else if (type == DTDRegionTypes.NAME || type == DTDRegionTypes.ENTITY_PARM) {
			//			if (region instanceof DTDRegion) {
			//				DTDRegion dtdRegion = (DTDRegion) region;
			//				IStructuredDocumentRegion flatNode = dtdRegion.getParent();
			//				String regionText = flatNode.getText(dtdRegion);
			//				if (regionText.equals("ANY") || regionText.equals("EMPTY")) {
			//					return new TextAttribute(DTDColors.DTD_KEYWORD);
			//				}
			//			}
			return (TextAttribute)getTextAttributes().get(IStyleConstantsDTD.DTD_DATA);
		}

		// default, return null to signal "not handled"
		// in which case, other factories should be tried
		return null;
	}

	/* (non-Javadoc)
	 */
	protected PreferenceManager getColorManager() {
		return DTDColorManager.getDTDColorManager();
	}
	
	protected void loadColors() {
		PreferenceManager mgr = getColorManager();
		Node colors = mgr.getRootElement();
		if (colors == null)
			return;
		clearColors();
		//----------------------------------------------------------------------
		// (pa) 20021217
		// cmvc defect 235554
		// performance enhancement: using child.getNextSibling() rather than
		// nodeList(item) for O(n) vs. O(n*n)
		//----------------------------------------------------------------------
		for (Node colorNode = colors.getFirstChild(); colorNode != null; colorNode = colorNode.getNextSibling()) {
			//		NodeList colorList = colors.getChildNodes();
			//		for (int i = 0; i < colorList.getLength(); i++) {
			//			Node colorNode = colorList.item(i);
			if (colorNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element color = (Element) colorNode;
			String colorName = color.getAttribute(ColorHelper.NAME);
			if (colorName == null || colorName.length() < 1)
				continue;
			RGB foreground = ColorHelper.toRGB(color.getAttribute(ColorHelper.FOREGROUND));
			RGB background = ColorHelper.toRGB(color.getAttribute(ColorHelper.BACKGROUND));
			boolean bold = Boolean.valueOf(color.getAttribute(ColorHelper.BOLD)).booleanValue();

			if (colorName.equals(IStyleConstantsDTD.DTD_DEFAULT)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_DEFAULT, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_TAG)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_TAG, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_TAGNAME)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_TAGNAME, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_COMMENT)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_COMMENT, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_KEYWORD)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_KEYWORD, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_STRING)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_STRING, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_DATA)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_DATA, createTextAttribute(foreground, background, bold));
			}
			else if (colorName.equals(IStyleConstantsDTD.DTD_SYMBOL)) {
				getTextAttributes().put(IStyleConstantsDTD.DTD_SYMBOL, createTextAttribute(foreground, background, bold));
			}
		}
	}
	protected void clearColors() {
		getTextAttributes().clear();
	}
	/**
	 * preferencesChanged method comment.
	 */
	public void preferencesChanged() {
		loadColors();
		super.preferencesChanged();
	}
}
