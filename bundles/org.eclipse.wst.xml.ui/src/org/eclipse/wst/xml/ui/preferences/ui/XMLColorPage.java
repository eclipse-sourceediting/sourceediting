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
package org.eclipse.wst.xml.ui.preferences.ui;



import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractColorPage;
import org.eclipse.wst.sse.ui.preferences.ui.StyledTextColorPicker;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;
import org.eclipse.wst.xml.ui.style.IStyleConstantsXML;



public class XMLColorPage extends AbstractColorPage {

	protected Control createContents(Composite parent) {
		Composite pageComponent = createComposite(parent, 1);
		((GridData) pageComponent.getLayoutData()).horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;

		super.createContents(pageComponent);
		WorkbenchHelp.setHelp(pageComponent, IHelpContextIds.XML_PREFWEBX_STYLES_HELPID);
		return pageComponent;
	}

	protected PreferenceManager getColorManager() {
		return XMLColorManager.getXMLColorManager();
	}

	public String getSampleText() {
		return ResourceHandler.getString("Sample_XML_doc"); //$NON-NLS-1$ = "<?xml version=\"1.0\"?>\n<?customProcessingInstruction\n\tXML processor specific\n\tcontent ?>\n<!DOCTYPE colors\n\tPUBLIC \"//IBM/XML/COLORS/\" \"colors.dtd\">\n<colors>\n\t<!-- begin color definitions -->\n\t<color name=\"plaintext\" foreground=\"#000000\"\n\t\tbackground=\"#D4D0C8\"/>\n\t<color name=\"bold\" foreground=\"#000000\"\n\t\tbackground=\"#B3ACA0\">\n\t<![CDATA[<123456789>]]>\n\tNormal text content.\n\t<color name=\"inverse\" foreground=\"#F0F0F0\"\n\t\tbackground=\"#D4D0C8\"/>\n\n</colors>\n";
	}

	protected void initCommonContextStyleMap(Dictionary contextStyleMap) {

		contextStyleMap.put(XMLRegionContext.XML_COMMENT_OPEN, IStyleConstantsXML.COMMENT_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_COMMENT_TEXT, IStyleConstantsXML.COMMENT_TEXT);
		contextStyleMap.put(XMLRegionContext.XML_COMMENT_CLOSE, IStyleConstantsXML.COMMENT_BORDER);

		contextStyleMap.put(XMLRegionContext.XML_TAG_OPEN, IStyleConstantsXML.TAG_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_END_TAG_OPEN, IStyleConstantsXML.TAG_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_TAG_NAME, IStyleConstantsXML.TAG_NAME);
		contextStyleMap.put(XMLRegionContext.XML_TAG_ATTRIBUTE_NAME, IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		contextStyleMap.put(XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE, IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		contextStyleMap.put(XMLRegionContext.XML_TAG_CLOSE, IStyleConstantsXML.TAG_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_EMPTY_TAG_CLOSE, IStyleConstantsXML.TAG_BORDER);

		contextStyleMap.put(XMLRegionContext.XML_DECLARATION_OPEN, IStyleConstantsXML.DECL_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_DECLARATION_CLOSE, IStyleConstantsXML.DECL_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_ELEMENT_DECLARATION, IStyleConstantsXML.DECL_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_ELEMENT_DECL_CLOSE, IStyleConstantsXML.DECL_BORDER);

		contextStyleMap.put(XMLRegionContext.XML_CONTENT, IStyleConstantsXML.XML_CONTENT);
	}

	protected void initDocTypeContextStyleMap(Dictionary contextStyleMap) {

		contextStyleMap.put(XMLRegionContext.XML_ELEMENT_DECL_NAME, IStyleConstantsXML.DOCTYPE_NAME);
		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_DECLARATION, IStyleConstantsXML.TAG_NAME);
		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_DECLARATION_CLOSE, IStyleConstantsXML.DECL_BORDER);

		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_NAME, IStyleConstantsXML.DOCTYPE_NAME);
		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBLIC, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSTEM, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		contextStyleMap.put(XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF, IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
	}

	protected void initCommonDescriptions(Dictionary descriptions) {

		// create descriptions for hilighting types
		descriptions.put(IStyleConstantsXML.COMMENT_BORDER, ResourceHandler.getString("Comment_Delimiters_UI_")); //$NON-NLS-1$ = "Comment Delimiters"
		descriptions.put(IStyleConstantsXML.COMMENT_TEXT, ResourceHandler.getString("Comment_Content_UI_")); //$NON-NLS-1$ = "Comment Content"
		descriptions.put(IStyleConstantsXML.TAG_BORDER, ResourceHandler.getString("Tag_Delimiters_UI_")); //$NON-NLS-1$ = "Tag Delimiters"
		descriptions.put(IStyleConstantsXML.TAG_NAME, ResourceHandler.getString("Tag_Names_UI_")); //$NON-NLS-1$ = "Tag Names"
		descriptions.put(IStyleConstantsXML.TAG_ATTRIBUTE_NAME, ResourceHandler.getString("Attribute_Names_UI_")); //$NON-NLS-1$ = "Attribute Names"
		descriptions.put(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE, ResourceHandler.getString("Attribute_Values_UI_")); //$NON-NLS-1$ = "Attribute Values"
		descriptions.put(IStyleConstantsXML.DECL_BORDER, ResourceHandler.getString("Declaration_Delimiters_UI_")); //$NON-NLS-1$ = "Declaration Delimiters"
		descriptions.put(IStyleConstantsXML.XML_CONTENT, ResourceHandler.getString("Content_UI_")); //$NON-NLS-1$ = "Content"
	}

	protected void initDocTypeDescriptions(Dictionary descriptions) {

		// create descriptions for hilighting types for DOCTYPE related items
		descriptions.put(IStyleConstantsXML.DOCTYPE_NAME, ResourceHandler.getString("DOCTYPE_Name_UI_")); //$NON-NLS-1$ = "DOCTYPE Name"
		descriptions.put(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID, ResourceHandler.getString("DOCTYPE_SYSTEM/PUBLIC_Keyw_UI_")); //$NON-NLS-1$ = "DOCTYPE SYSTEM/PUBLIC Keyword"
		descriptions.put(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF, ResourceHandler.getString("DOCTYPE_Public_Reference_UI_")); //$NON-NLS-1$ = "DOCTYPE Public Reference"
		descriptions.put(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF, ResourceHandler.getString("DOCTYPE_System_Reference_UI_")); //$NON-NLS-1$ = "DOCTYPE System Reference"
	}

	protected void initCommonStyleList(ArrayList list) {

		//list.add(IStyleConstantsXML.CDATA_BORDER);
		//list.add(IStyleConstantsXML.CDATA_TEXT);
		//list.add(IStyleConstantsXML.PI_BORDER);
		//list.add(IStyleConstantsXML.PI_CONTENT);

		list.add(IStyleConstantsXML.TAG_BORDER);
		list.add(IStyleConstantsXML.TAG_NAME);
		list.add(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		list.add(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		list.add(IStyleConstantsXML.COMMENT_BORDER);
		list.add(IStyleConstantsXML.COMMENT_TEXT);
		list.add(IStyleConstantsXML.DECL_BORDER);
		list.add(IStyleConstantsXML.XML_CONTENT);
	}

	protected void initDocTypeStyleList(ArrayList list) {

		list.add(IStyleConstantsXML.DOCTYPE_NAME);
		list.add(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		list.add(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		list.add(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
	}

	protected void initContextStyleMap(Dictionary contextStyleMap) {

		initCommonContextStyleMap(contextStyleMap);
		initDocTypeContextStyleMap(contextStyleMap);
		contextStyleMap.put(XMLRegionContext.XML_CDATA_OPEN, IStyleConstantsXML.CDATA_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_CDATA_TEXT, IStyleConstantsXML.CDATA_TEXT);
		contextStyleMap.put(XMLRegionContext.XML_CDATA_CLOSE, IStyleConstantsXML.CDATA_BORDER);

		contextStyleMap.put(XMLRegionContext.XML_PI_OPEN, IStyleConstantsXML.PI_BORDER);
		contextStyleMap.put(XMLRegionContext.XML_PI_CONTENT, IStyleConstantsXML.PI_CONTENT);
		contextStyleMap.put(XMLRegionContext.XML_PI_CLOSE, IStyleConstantsXML.PI_BORDER);

	}

	protected void initDescriptions(Dictionary descriptions) {

		initCommonDescriptions(descriptions);
		initDocTypeDescriptions(descriptions);
		descriptions.put(IStyleConstantsXML.CDATA_BORDER, ResourceHandler.getString("CDATA_Delimiters_UI_")); //$NON-NLS-1$ = "CDATA Delimiters"
		descriptions.put(IStyleConstantsXML.CDATA_TEXT, ResourceHandler.getString("CDATA_Content_UI_")); //$NON-NLS-1$ = "CDATA Content"
		descriptions.put(IStyleConstantsXML.PI_BORDER, ResourceHandler.getString("Processing_Instruction_Del_UI_")); //$NON-NLS-1$ = "Processing Instruction Delimiters"
		descriptions.put(IStyleConstantsXML.PI_CONTENT, ResourceHandler.getString("Processing_Instruction_Con_UI__UI_")); //$NON-NLS-1$ = "Processing Instruction Content"
	}

	protected void initStyleList(ArrayList list) {
		initCommonStyleList(list);
		initDocTypeStyleList(list);
		list.add(IStyleConstantsXML.CDATA_BORDER);
		list.add(IStyleConstantsXML.CDATA_TEXT);
		list.add(IStyleConstantsXML.PI_BORDER);
		list.add(IStyleConstantsXML.PI_CONTENT);
	}

	public boolean performOk() {
		// required since the superclass *removes* existing preferences before saving its own
		super.performOk();

		getColorManager().save();
		return true;
	}

	protected void setupPicker(StyledTextColorPicker picker) {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		if (plugin != null) {
			IModelManager mmanager = plugin.getModelManager();
			picker.setParser(mmanager.createStructuredDocumentFor(IContentTypeIdentifier.ContentTypeID_SSEXML).getParser());
		}
		else
			picker.setParser(new XMLSourceParser());

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
}
