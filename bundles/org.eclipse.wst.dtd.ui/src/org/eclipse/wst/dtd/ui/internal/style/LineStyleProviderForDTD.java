/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.internal.style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

public class LineStyleProviderForDTD extends AbstractLineStyleProvider implements LineStyleProvider {
	public LineStyleProviderForDTD() {
		super();
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		/**
		 * a method to centralize all the "format rules" for regions
		 * specifically associated for how to "open" the region.
		 */
		// not sure why this is coming through null, but just to catch it
		if (region == null) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_DEFAULT);
		}
		String type = region.getType();
		if (type == DTDRegionTypes.CONTENT_EMPTY || type == DTDRegionTypes.CONTENT_ANY || type == DTDRegionTypes.CONTENT_PCDATA) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_DATA);
		}
		else if (type == DTDRegionTypes.ELEMENT_TAG || type == DTDRegionTypes.ENTITY_TAG || type == DTDRegionTypes.ATTLIST_TAG || type == DTDRegionTypes.NOTATION_TAG) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_TAGNAME);
		}
		else if (type == DTDRegionTypes.CONNECTOR || type == DTDRegionTypes.OCCUR_TYPE) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_SYMBOL);
		}
		else if (type == DTDRegionTypes.NDATA_VALUE) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_DATA);
		}
		else if (type == DTDRegionTypes.START_TAG || type == DTDRegionTypes.END_TAG || type == DTDRegionTypes.EXCLAMATION) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_TAG);
		}
		else if (type == DTDRegionTypes.COMMENT_START || type == DTDRegionTypes.COMMENT_CONTENT || type == DTDRegionTypes.COMMENT_END) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_COMMENT);
		}
		else if (type == DTDRegionTypes.SINGLEQUOTED_LITERAL || type == DTDRegionTypes.DOUBLEQUOTED_LITERAL) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_STRING);
		}
		else if (type == DTDRegionTypes.SYSTEM_KEYWORD || type == DTDRegionTypes.PUBLIC_KEYWORD || type == DTDRegionTypes.NDATA_KEYWORD || type == DTDRegionTypes.CDATA_KEYWORD || type == DTDRegionTypes.ID_KEYWORD || type == DTDRegionTypes.IDREF_KEYWORD || type == DTDRegionTypes.IDREFS_KEYWORD || type == DTDRegionTypes.ENTITY_KEYWORD || type == DTDRegionTypes.ENTITIES_KEYWORD || type == DTDRegionTypes.NMTOKEN_KEYWORD || type == DTDRegionTypes.NMTOKENS_KEYWORD || type == DTDRegionTypes.NOTATION_KEYWORD || type == DTDRegionTypes.REQUIRED_KEYWORD || type == DTDRegionTypes.IMPLIED_KEYWORD || type == DTDRegionTypes.FIXED_KEYWORD) {
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_KEYWORD);
		}
		else if (type == DTDRegionTypes.NAME || type == DTDRegionTypes.ENTITY_PARM) {
			// if (region instanceof DTDRegion) {
			// DTDRegion dtdRegion = (DTDRegion) region;
			// IStructuredDocumentRegion flatNode = dtdRegion.getParent();
			// String regionText = flatNode.getText(dtdRegion);
			// if (regionText.equals("ANY") || regionText.equals("EMPTY")) {
			// return new TextAttribute(DTDColors.DTD_KEYWORD);
			// }
			// }
			return (TextAttribute) getTextAttributes().get(IStyleConstantsDTD.DTD_DATA);
		}

		// default, return null to signal "not handled"
		// in which case, other factories should be tried
		return null;
	}

	protected IPreferenceStore getColorPreferences() {
		return DTDUIPlugin.getDefault().getPreferenceStore();
	}

	protected void handlePropertyChange(PropertyChangeEvent event) {
		String styleKey = null;

		if (event != null) {
			String prefKey = event.getProperty();
			// check if preference changed is a style preference
			if (IStyleConstantsDTD.DTD_DEFAULT.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_DEFAULT;
			}
			else if (IStyleConstantsDTD.DTD_TAG.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_TAG;
			}
			else if (IStyleConstantsDTD.DTD_TAGNAME.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_TAGNAME;
			}
			else if (IStyleConstantsDTD.DTD_COMMENT.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_COMMENT;
			}
			else if (IStyleConstantsDTD.DTD_KEYWORD.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_KEYWORD;
			}
			else if (IStyleConstantsDTD.DTD_STRING.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_STRING;
			}
			else if (IStyleConstantsDTD.DTD_DATA.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_DATA;
			}
			else if (IStyleConstantsDTD.DTD_SYMBOL.equals(prefKey)) {
				styleKey = IStyleConstantsDTD.DTD_SYMBOL;
			}
		}

		if (styleKey != null) {
			// overwrite style preference with new value
			addTextAttribute(styleKey);
			super.handlePropertyChange(event);
		}
	}

	protected void loadColors() {
		addTextAttribute(IStyleConstantsDTD.DTD_DEFAULT);
		addTextAttribute(IStyleConstantsDTD.DTD_TAG);
		addTextAttribute(IStyleConstantsDTD.DTD_TAGNAME);
		addTextAttribute(IStyleConstantsDTD.DTD_COMMENT);
		addTextAttribute(IStyleConstantsDTD.DTD_KEYWORD);
		addTextAttribute(IStyleConstantsDTD.DTD_STRING);
		addTextAttribute(IStyleConstantsDTD.DTD_DATA);
		addTextAttribute(IStyleConstantsDTD.DTD_SYMBOL);
	}
}
