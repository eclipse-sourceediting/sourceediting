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
package org.eclipse.wst.html.ui.preferences.ui;



import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.html.ui.internal.nls.ResourceHandler;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.StyledTextColorPicker;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.ui.preferences.XMLColorPage;
import org.eclipse.wst.xml.ui.style.IStyleConstantsXML;

public class HTMLColorPage extends XMLColorPage {

	/**
	 * Set up all the style preference keys in the overlay store
	 */
	protected OverlayKey[] createOverlayStoreKeys() {
		ArrayList overlayKeys = new ArrayList();
		
		ArrayList styleList = new ArrayList();
		initStyleList(styleList);
		Iterator i = styleList.iterator();
		while (i.hasNext()) {
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, PreferenceKeyGenerator.generateKey((String)i.next(), IContentTypeIdentifier.ContentTypeID_HTML)));	
		}

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	public String getSampleText() {
		return ResourceHandler.getString("Sample_HTML_doc"); //$NON-NLS-1$ = "<!DOCTYPE HTML\n\tPUBLIC \"-//W3C/DTD/ HTML 4.01 Transitional//EN\"\n\t\"http://www.w3.org/TR/html4/loose.dtd\">\n<HTML>\n\t<HEAD>\n\t\t<META content=\"text/html\">\n\t\t<TITLE>HTML Highlighting Preferences</TITLE>\n\t</HEAD>\n<BODY>\n\t<!--\n\t\twe need a flaming logo!\n\t-->\n\t<%\t%>\n</BODY>\n</HTML>"

	}

	protected void initContextStyleMap(Dictionary contextStyleMap) {

		initCommonContextStyleMap(contextStyleMap);
		initDocTypeContextStyleMap(contextStyleMap);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN, HTMLColorManager.SCRIPT_AREA_BORDER);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_CONTENT, HTMLColorManager.SCRIPT_AREA);
		//	contextStyleMap.put(XMLJSPRegionContexts.BLOCK_TEXT, HTMLColorManager.SCRIPT_AREA);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_DECLARATION_OPEN, HTMLColorManager.SCRIPT_AREA_BORDER);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_EXPRESSION_OPEN, HTMLColorManager.SCRIPT_AREA_BORDER);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN, HTMLColorManager.SCRIPT_AREA_BORDER);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_DIRECTIVE_CLOSE, HTMLColorManager.SCRIPT_AREA_BORDER);
		//	contextStyleMap.put(XMLJSPRegionContexts.JSP_CLOSE, HTMLColorManager.SCRIPT_AREA_BORDER);
		contextStyleMap.put(XMLJSPRegionContexts.JSP_DIRECTIVE_NAME, IStyleConstantsXML.TAG_NAME);
		contextStyleMap.put(XMLJSPRegionContexts.JSP_COMMENT_OPEN, IStyleConstantsXML.COMMENT_BORDER);
		contextStyleMap.put(XMLJSPRegionContexts.JSP_COMMENT_TEXT, IStyleConstantsXML.COMMENT_TEXT);
		contextStyleMap.put(XMLJSPRegionContexts.JSP_COMMENT_CLOSE, IStyleConstantsXML.COMMENT_BORDER);
	}

	protected void initDescriptions(Dictionary descriptions) {

		initCommonDescriptions(descriptions);
		initDocTypeDescriptions(descriptions);
	}

	protected void initStyleList(ArrayList list) {
		initCommonStyleList(list);
		initDocTypeStyleList(list);
		//	list.add(HTMLColorManager.SCRIPT_AREA_BORDER);

	}

	protected void setupPicker(StyledTextColorPicker picker) {
		IModelManager mmanager = StructuredModelManager.getInstance().getModelManager();
		picker.setParser(mmanager.createStructuredDocumentFor(IContentTypeIdentifier.ContentTypeID_HTML).getParser());

		// create descriptions for hilighting types
		Dictionary descriptions = new Hashtable();
		initDescriptions(descriptions);

		// map region types to hilighting types
		Dictionary contextStyleMap = new Hashtable();
		initContextStyleMap(contextStyleMap);

		ArrayList styleList = new ArrayList();
		initStyleList(styleList);

		picker.setContextStyleMap(contextStyleMap);
		picker.setDescriptions(descriptions);
		picker.setStyleList(styleList);

		picker.setGeneratorKey(IContentTypeIdentifier.ContentTypeID_HTML);
		//	updatePickerFont(picker);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		WorkbenchHelp.setHelp(c, IHelpContextIds.HTML_PREFWEBX_STYLES_HELPID);
		return c;
	}
}