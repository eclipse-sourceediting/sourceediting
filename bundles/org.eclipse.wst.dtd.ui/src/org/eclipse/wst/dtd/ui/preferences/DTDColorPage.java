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
package org.eclipse.wst.dtd.ui.preferences;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.dtd.ui.style.IStyleConstantsDTD;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractColorPage;
import org.eclipse.wst.sse.ui.preferences.ui.StyledTextColorPicker;

public class DTDColorPage extends AbstractColorPage {

	protected Control createContents(Composite parent) {
		Composite pageComponent = createComposite(parent, 1);
		((GridData) pageComponent.getLayoutData()).horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;

		super.createContents(pageComponent);
		WorkbenchHelp.setHelp(pageComponent, IHelpContextIds.DTD_PREFWEBX_STYLES_HELPID);

		return pageComponent;
	}

	/**
	 * Set up all the style preference keys in the overlay store
	 */
	protected OverlayKey[] createOverlayStoreKeys() {
		ArrayList overlayKeys = new ArrayList();

		ArrayList styleList = new ArrayList();
		initStyleList(styleList);
		Iterator i = styleList.iterator();
		while (i.hasNext()) {
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, (String) i.next()));
		}

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return DTDUIPlugin.getDefault().getPreferenceStore();
	}

	public String getSampleText() {
		return DTDUIPlugin.getResourceString("DTDColorPage.0"); //$NON-NLS-1$
	}

	protected void initContextStyleMap(Dictionary contextStyleMap) {
		contextStyleMap.put(DTDRegionTypes.CONTENT_EMPTY, IStyleConstantsDTD.DTD_DATA);
		contextStyleMap.put(DTDRegionTypes.CONTENT_ANY, IStyleConstantsDTD.DTD_DATA);
		contextStyleMap.put(DTDRegionTypes.CONTENT_PCDATA, IStyleConstantsDTD.DTD_DATA);
		contextStyleMap.put(DTDRegionTypes.NDATA_VALUE, IStyleConstantsDTD.DTD_DATA);
		contextStyleMap.put(DTDRegionTypes.NAME, IStyleConstantsDTD.DTD_DATA);
		contextStyleMap.put(DTDRegionTypes.ENTITY_PARM, IStyleConstantsDTD.DTD_DATA);

		contextStyleMap.put(DTDRegionTypes.ELEMENT_TAG, IStyleConstantsDTD.DTD_TAGNAME);
		contextStyleMap.put(DTDRegionTypes.ENTITY_TAG, IStyleConstantsDTD.DTD_TAGNAME);
		contextStyleMap.put(DTDRegionTypes.ATTLIST_TAG, IStyleConstantsDTD.DTD_TAGNAME);
		contextStyleMap.put(DTDRegionTypes.NOTATION_TAG, IStyleConstantsDTD.DTD_TAGNAME);

		contextStyleMap.put(DTDRegionTypes.CONNECTOR, IStyleConstantsDTD.DTD_SYMBOL);
		contextStyleMap.put(DTDRegionTypes.OCCUR_TYPE, IStyleConstantsDTD.DTD_SYMBOL);

		contextStyleMap.put(DTDRegionTypes.START_TAG, IStyleConstantsDTD.DTD_TAG);
		contextStyleMap.put(DTDRegionTypes.END_TAG, IStyleConstantsDTD.DTD_TAG);
		contextStyleMap.put(DTDRegionTypes.EXCLAMATION, IStyleConstantsDTD.DTD_TAG);

		contextStyleMap.put(DTDRegionTypes.COMMENT_START, IStyleConstantsDTD.DTD_COMMENT);
		contextStyleMap.put(DTDRegionTypes.COMMENT_CONTENT, IStyleConstantsDTD.DTD_COMMENT);
		contextStyleMap.put(DTDRegionTypes.COMMENT_END, IStyleConstantsDTD.DTD_COMMENT);

		contextStyleMap.put(DTDRegionTypes.SINGLEQUOTED_LITERAL, IStyleConstantsDTD.DTD_STRING);
		contextStyleMap.put(DTDRegionTypes.DOUBLEQUOTED_LITERAL, IStyleConstantsDTD.DTD_STRING);

		contextStyleMap.put(DTDRegionTypes.SYSTEM_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.PUBLIC_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.NDATA_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.CDATA_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.ID_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.IDREF_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.IDREFS_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.ENTITY_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.ENTITIES_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.NMTOKEN_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.NMTOKENS_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.NOTATION_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.REQUIRED_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.IMPLIED_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
		contextStyleMap.put(DTDRegionTypes.FIXED_KEYWORD, IStyleConstantsDTD.DTD_KEYWORD);
	}

	protected void initDescriptions(Dictionary descriptions) {
		descriptions.put(IStyleConstantsDTD.DTD_COMMENT, DTDUIPlugin.getResourceString("DTDColorPage.1")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_DATA, DTDUIPlugin.getResourceString("DTDColorPage.2")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_DEFAULT, DTDUIPlugin.getResourceString("DTDColorPage.3")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_KEYWORD, DTDUIPlugin.getResourceString("DTDColorPage.4")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_STRING, DTDUIPlugin.getResourceString("DTDColorPage.5")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_SYMBOL, DTDUIPlugin.getResourceString("DTDColorPage.6")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_TAG, DTDUIPlugin.getResourceString("DTDColorPage.7")); //$NON-NLS-1$
		descriptions.put(IStyleConstantsDTD.DTD_TAGNAME, DTDUIPlugin.getResourceString("DTDColorPage.8")); //$NON-NLS-1$
	}

	protected void initStyleList(ArrayList list) {
		list.add(IStyleConstantsDTD.DTD_COMMENT);
		list.add(IStyleConstantsDTD.DTD_DATA);
		list.add(IStyleConstantsDTD.DTD_DEFAULT);
		list.add(IStyleConstantsDTD.DTD_KEYWORD);
		list.add(IStyleConstantsDTD.DTD_STRING);
		list.add(IStyleConstantsDTD.DTD_SYMBOL);
		list.add(IStyleConstantsDTD.DTD_TAG);
		list.add(IStyleConstantsDTD.DTD_TAGNAME);
	}

	protected void setupPicker(StyledTextColorPicker picker) {
		IModelManager mmanager = StructuredModelManager.getModelManager();
		picker.setParser(mmanager.createStructuredDocumentFor(IContentTypeIdentifier.ContentTypeID_DTD).getParser());

		Dictionary descriptions = new Hashtable();
		initDescriptions(descriptions);

		Dictionary contextStyleMap = new Hashtable();
		initContextStyleMap(contextStyleMap);

		ArrayList styleList = new ArrayList();
		initStyleList(styleList);

		picker.setContextStyleMap(contextStyleMap);
		picker.setDescriptions(descriptions);
		picker.setStyleList(styleList);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.preferences.ui.AbstractColorPage#savePreferences()
	 */
	protected void savePreferences() {
		DTDUIPlugin.getDefault().savePluginPreferences();
	}
}
