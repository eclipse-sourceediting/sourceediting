/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.style.IStyleConstantsXML;
import org.eclipse.wst.xml.ui.style.LineStyleProviderForXML;

public class LineStyleProviderForHTML extends LineStyleProviderForXML implements LineStyleProvider {

	public LineStyleProviderForHTML() {
		super();
	}

	/**
	 * a method to centralize all the "format rules" for regions 
	 * specifically associated for how to "open" the region.
	 */
	// NOTE: this method was just copied down form LineStyleProviderForXML
	public TextAttribute getAttributeFor(ITextRegion region) {
		// not sure why this is coming through null, but just to catch it
		if (region == null) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.XML_CONTENT);
		}
		if (region.getType() == XMLRegionContext.BLOCK_TEXT) {
			return (TextAttribute)getTextAttributes().get(IStyleConstantsXML.XML_CONTENT);
		}
		// first try "standard" tag attributes from super class
		TextAttribute result = super.getAttributeFor(region);
		if (result == null) {
			// the HTML adapter is used for *embedded* JSP code,
			// so the HTML adapter can decide whether to treat
			// as HTML, or as JSP code. By *embedded* when mean when
			// it is postioned as something other than content (e.g. an
			// attribute value)
			//			String type = region.getType();
			//			if ((type == XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN) //                || (type == XMLJSPRegionContexts.JSP_DECLARATION_OPEN)
			//				|| (type == XMLJSPRegionContexts.JSP_EXPRESSION_OPEN) //                || (type == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN)
			//				//                || (type == XMLJSPRegionContexts.JSP_DIRECTIVE_CLOSE)
			//				|| (type == XMLJSPRegionContexts.JSP_CLOSE)) {
			//				result = fStructuredTextColors.SCRIPT_AREA_BORDER;
			//			} else
			//				// Nitin: do you recall why these were put here? from their name, sounds like
			//				// they should be in "XML one"?
			//				if ((type == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE_DQUOTE) || (type == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE_SQUOTE)) {
			//					result = fStructuredTextColors.RTF_TAG_ATTRIBUTE_VALUE;
			//				}

		}
		return result;
	}

	protected void loadColors() {
		super.loadColors();

		addTextAttribute(IStyleConstantsHTML.SCRIPT_AREA_BORDER);
	}
	
	
	protected void handlePropertyChange(PropertyChangeEvent event) {
		if (event != null) {
			String prefKey = event.getProperty();
			// check if preference changed is a style preference
			if (IStyleConstantsHTML.SCRIPT_AREA_BORDER.equals(prefKey)) {
				addTextAttribute(IStyleConstantsHTML.SCRIPT_AREA_BORDER);

				// this is what AbstractLineStyleProvider.propertyChange() does
				getHighlighter().refreshDisplay();
			} else {
				super.handlePropertyChange(event);
			}
		} else {
			super.handlePropertyChange(event);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider#getColorPreferences()
	 */
	protected IPreferenceStore getColorPreferences() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}
}