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



//import java.util.ArrayList;
//import java.util.Dictionary;
//import java.util.Hashtable;
//
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.jface.preference.IPreferenceStore;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//

/**
 * @deprecated
 * Not used for now, still using the old way of saving colors into a XML file. See XMLColorPage.
 */
public class XMLStylesPreferencePage {// extends AbstractColorPreferencePage {
/*	

 protected IPreferenceStore doGetPreferenceStore() {
 return CommonPreferencesPlugin.getDefault().getPreferenceStore(ContentTypeHandlerForXML.ContentTypeID_XML);
 }
 protected PreferenceManager getColorManager() {
 return XMLColorManager.getXMLColorManager();
 }
 public String getSampleText() {
 return "<?xml version=\"1.0\"?>\n<?customProcessingInstruction\n\tXML processor specific\n\tcontent ?>\n<!DOCTYPE colors\n\tPUBLIC \"//IBM/XML/COLORS/\" \"colors.dtd\">\n<colors>\n\t<!-- begin color definitions -->\n\t<color name=\"plaintext\" foreground=\"#000000\"\n\t\tbackground=\"#D4D0C8\">\n\t<color name=\"bold\" foreground=\"#000000\"\n\t\tbackground=\"#B3ACA0\">\n\t<![CDATA[<123456789>]]>\n\tNormal text content.\n\t<color name=\"inverse\" foreground=\"#F0F0F0\"\n\t\tbackground=\"#D4D0C8\">\n\n</colors>\n";//$NON-NLS-1$
 }
 protected void initCommonContextStyleMap(Dictionary contextStyleMap) {
 
 contextStyleMap.put(XMLJSPRegionContexts.XML_COMMENT_OPEN, XMLPreferenceNames.COMMENT_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_COMMENT_TEXT, XMLPreferenceNames.COMMENT_TEXT);
 contextStyleMap.put(XMLJSPRegionContexts.XML_COMMENT_CLOSE, XMLPreferenceNames.COMMENT_BORDER);
 
 contextStyleMap.put(XMLJSPRegionContexts.XML_TAG_OPEN, XMLPreferenceNames.TAG_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_END_TAG_OPEN, XMLPreferenceNames.TAG_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_TAG_NAME, XMLPreferenceNames.TAG_NAME);
 contextStyleMap.put(XMLJSPRegionContexts.XML_TAG_ATTRIBUTE_NAME, XMLPreferenceNames.TAG_ATTRIBUTE_NAME);
 contextStyleMap.put(XMLJSPRegionContexts.XML_TAG_ATTRIBUTE_VALUE, XMLPreferenceNames.TAG_ATTRIBUTE_VALUE);
 contextStyleMap.put(XMLJSPRegionContexts.XML_TAG_CLOSE, XMLPreferenceNames.TAG_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_EMPTY_TAG_CLOSE, XMLPreferenceNames.TAG_BORDER);
 
 contextStyleMap.put(XMLJSPRegionContexts.XML_DECLARATION_OPEN, XMLPreferenceNames.DECL_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DECLARATION_CLOSE, XMLPreferenceNames.DECL_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_DECLARATION, XMLPreferenceNames.TAG_NAME);
 contextStyleMap.put(XMLJSPRegionContexts.XML_ELEMENT_DECLARATION, XMLPreferenceNames.DECL_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_ELEMENT_DECL_CLOSE, XMLPreferenceNames.DECL_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_DECLARATION_CLOSE, XMLPreferenceNames.DECL_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_ELEMENT_DECL_NAME, XMLPreferenceNames.DOCTYPE_NAME);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_NAME, XMLPreferenceNames.DOCTYPE_NAME);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_EXTERNAL_ID_PUBLIC, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_EXTERNAL_ID_PUBREF, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_PUBREF);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_EXTERNAL_ID_SYSTEM, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID);
 contextStyleMap.put(XMLJSPRegionContexts.XML_DOCTYPE_EXTERNAL_ID_SYSREF, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_SYSREF);
 contextStyleMap.put(XMLJSPRegionContexts.XML_CONTENT, XMLPreferenceNames.XML_CONTENT);
 
 }
 protected void initCommonDescriptions(Dictionary descriptions) {
 
 // create descriptions for hilighting types
 descriptions.put(XMLPreferenceNames.COMMENT_BORDER, ResourceHandler.getString("Comment_Delimiters_UI_")); //$NON-NLS-1$ = "Comment Delimiters"
 descriptions.put(XMLPreferenceNames.COMMENT_TEXT, ResourceHandler.getString("Comment_Content_UI_")); //$NON-NLS-1$ = "Comment Content"
 descriptions.put(XMLPreferenceNames.TAG_BORDER, ResourceHandler.getString("Tag_Delimiters_UI_")); //$NON-NLS-1$ = "Tag Delimiters"
 descriptions.put(XMLPreferenceNames.TAG_NAME, ResourceHandler.getString("Tag_Names_UI_")); //$NON-NLS-1$ = "Tag Names"
 descriptions.put(XMLPreferenceNames.TAG_ATTRIBUTE_NAME, ResourceHandler.getString("Attribute_Names_UI_")); //$NON-NLS-1$ = "Attribute Names"
 descriptions.put(XMLPreferenceNames.TAG_ATTRIBUTE_VALUE, ResourceHandler.getString("Attribute_Values_UI_")); //$NON-NLS-1$ = "Attribute Values"
 descriptions.put(XMLPreferenceNames.DECL_BORDER, ResourceHandler.getString("Declaration_Delimiters_UI_")); //$NON-NLS-1$ = "Declaration Delimiters"
 descriptions.put(XMLPreferenceNames.DOCTYPE_NAME, ResourceHandler.getString("DOCTYPE_Name_UI_")); //$NON-NLS-1$ = "DOCTYPE Name"
 descriptions.put(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID, ResourceHandler.getString("DOCTYPE_SYSTEM/PUBLIC_Keyw_UI_")); //$NON-NLS-1$ = "DOCTYPE SYSTEM/PUBLIC Keyword"
 descriptions.put(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_PUBREF, ResourceHandler.getString("DOCTYPE_Public_Reference_UI_")); //$NON-NLS-1$ = "DOCTYPE Public Reference"
 descriptions.put(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_SYSREF, ResourceHandler.getString("DOCTYPE_System_Reference_UI_")); //$NON-NLS-1$ = "DOCTYPE System Reference"
 descriptions.put(XMLPreferenceNames.XML_CONTENT, ResourceHandler.getString("Content_UI_")); //$NON-NLS-1$ = "Content"
 }
 protected void initCommonStyleList(ArrayList list) {
 
 //list.add(XMLPreferenceNames.CDATA_BORDER);
 //list.add(XMLPreferenceNames.CDATA_TEXT);
 //list.add(XMLPreferenceNames.PI_BORDER);
 //list.add(XMLPreferenceNames.PI_CONTENT);
 
 list.add(XMLPreferenceNames.TAG_BORDER);
 list.add(XMLPreferenceNames.TAG_NAME);
 list.add(XMLPreferenceNames.TAG_ATTRIBUTE_NAME);
 list.add(XMLPreferenceNames.TAG_ATTRIBUTE_VALUE);
 list.add(XMLPreferenceNames.COMMENT_BORDER);
 list.add(XMLPreferenceNames.COMMENT_TEXT);
 list.add(XMLPreferenceNames.DECL_BORDER);
 list.add(XMLPreferenceNames.DOCTYPE_NAME);
 list.add(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID);
 list.add(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_PUBREF);
 list.add(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_SYSREF);
 list.add(XMLPreferenceNames.XML_CONTENT);
 }
 protected void initContextStyleMap(Dictionary contextStyleMap) {
 
 initCommonContextStyleMap(contextStyleMap);
 contextStyleMap.put(XMLJSPRegionContexts.XML_CDATA_OPEN, XMLPreferenceNames.CDATA_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_CDATA_TEXT, XMLPreferenceNames.CDATA_TEXT);
 contextStyleMap.put(XMLJSPRegionContexts.XML_CDATA_CLOSE, XMLPreferenceNames.CDATA_BORDER);
 
 contextStyleMap.put(XMLJSPRegionContexts.XML_PI_OPEN, XMLPreferenceNames.PI_BORDER);
 contextStyleMap.put(XMLJSPRegionContexts.XML_PI_CONTENT, XMLPreferenceNames.PI_CONTENT);
 contextStyleMap.put(XMLJSPRegionContexts.XML_PI_CLOSE, XMLPreferenceNames.PI_BORDER);
 
 }
 protected void initDescriptions(Dictionary descriptions) {
 
 initCommonDescriptions(descriptions);
 descriptions.put(XMLPreferenceNames.CDATA_BORDER, ResourceHandler.getString("CDATA_Delimiters_UI_")); //$NON-NLS-1$ = "CDATA Delimiters"
 descriptions.put(XMLPreferenceNames.CDATA_TEXT, ResourceHandler.getString("CDATA_Content_UI_")); //$NON-NLS-1$ = "CDATA Content"
 descriptions.put(XMLPreferenceNames.PI_BORDER, ResourceHandler.getString("Processing_Instruction_Del_UI_")); //$NON-NLS-1$ = "Processing Instruction Delimiters"
 descriptions.put(XMLPreferenceNames.PI_CONTENT, ResourceHandler.getString("Processing_Instruction_Con_UI__UI_")); //$NON-NLS-1$ = "Processing Instruction Content"
 }
 protected void initStyleList(ArrayList list) {
 initCommonStyleList(list);
 list.add(XMLPreferenceNames.CDATA_BORDER);
 list.add(XMLPreferenceNames.CDATA_TEXT);
 list.add(XMLPreferenceNames.PI_BORDER);
 list.add(XMLPreferenceNames.PI_CONTENT);
 }
 public boolean performOk() {
 // required since the superclass *removes* existing preferences before saving its own
 super.performOk();
 
 getColorManager().save();
 return true;
 }
 protected void setupPicker(StyledTextColorPicker picker) {
 
 //		picker.setParser(new ParserFactory().createParser(IStructuredModel.XML));
 ModelManagerPlugin plugin = (ModelManagerPlugin) Platform.getPlugin(ModelManagerPlugin.ID);
 if(plugin != null) {
 IModelManager mmanager = plugin.getModelManager();
 picker.setParser(mmanager.createStructuredDocumentFor(ContentTypeHandlerForXML.ContentTypeID_XML).getParser());
 }
 else picker.setParser(new XMLSourceParser());
 
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
 protected Control createContents(Composite parent) {
 Control control = super.createContents(parent);

 initializeValues();

 return control;
 }
 protected void performDefaults() {
 Node colors = getColorManager().getRootElement();
 NodeList colorNodes = colors.getChildNodes();
 int colorNodesLength = colorNodes.getLength();
 for (int i = 0; i < colorNodesLength; i++) {
 colors.removeChild(colorNodes.item(0));
 }

 addColor(colors, XMLPreferenceNames.TAG_ATTRIBUTE_NAME, getColorString(127, 0, 127), null);
 addColor(colors, XMLPreferenceNames.TAG_ATTRIBUTE_VALUE, getColorString(42, 0, 255), null);

 addColor(colors, XMLPreferenceNames.COMMENT_BORDER, getColorString(63, 95, 191), null);
 addColor(colors, XMLPreferenceNames.COMMENT_TEXT, getColorString(63, 95, 191), null);

 addColor(colors, XMLPreferenceNames.DECL_BORDER, getColorString(0, 128, 128), null);
 addColor(colors, XMLPreferenceNames.DOCTYPE_NAME, getColorString(0, 0, 128), null);
 addColor(colors, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID, getColorString(128, 128, 128), null);
 addColor(colors, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_PUBREF, getColorString(0, 0, 128), null);
 addColor(colors, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_SYSREF, getColorString(63, 127, 95), null);

 addColor(colors, XMLPreferenceNames.XML_CONTENT, null, null); // specified value is black; leaving as widget default

 addColor(colors, XMLPreferenceNames.TAG_BORDER, getColorString(0, 128, 128), null);
 addColor(colors, XMLPreferenceNames.TAG_NAME, getColorString(63, 127, 127), null);

 addColor(colors, XMLPreferenceNames.PI_BORDER, getColorString(0, 128, 128), null);
 addColor(colors, XMLPreferenceNames.PI_CONTENT, null, null); // specified value is black; leaving as widget default

 addColor(colors, XMLPreferenceNames.CDATA_BORDER, getColorString(0, 128, 128), null);
 addColor(colors, XMLPreferenceNames.CDATA_TEXT, null, null); // specified value is black; leaving as widget default

 getPicker().setColorsNode(colors);
 getPicker().refresh();
 }
 protected void initializeValues() {
 Node colors = getColorManager().getRootElement();
 NodeList colorNodes = colors.getChildNodes();
 int colorNodesLength = colorNodes.getLength();
 for (int i = 0; i < colorNodesLength; i++) {
 colors.removeChild(colorNodes.item(0));
 }

 String preferencesString = getPreferenceStore().getString(XMLPreferenceNames.TAG_ATTRIBUTE_NAME);
 String preferences[] = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.TAG_ATTRIBUTE_NAME, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.TAG_ATTRIBUTE_VALUE);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.TAG_ATTRIBUTE_VALUE, preferences[0], preferences[1]);
 
 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.COMMENT_BORDER);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.COMMENT_BORDER, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.COMMENT_TEXT);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.COMMENT_TEXT, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.DECL_BORDER);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.DECL_BORDER, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.DOCTYPE_NAME);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.DOCTYPE_NAME, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_PUBREF);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_PUBREF, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_SYSREF);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.DOCTYPE_EXTERNAL_ID_SYSREF, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.XML_CONTENT);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.XML_CONTENT, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.TAG_BORDER);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.TAG_BORDER, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.TAG_NAME);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.TAG_NAME, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.PI_BORDER);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.PI_BORDER, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.PI_CONTENT);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.PI_CONTENT, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.CDATA_BORDER);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.CDATA_BORDER, preferences[0], preferences[1]);

 preferencesString = getPreferenceStore().getString(XMLPreferenceNames.CDATA_TEXT);
 preferences = StringUtils.asFixedArray(preferencesString, XMLPreferenceNames.COMMA);
 addColor(colors, XMLPreferenceNames.CDATA_TEXT, preferences[0], preferences[1]);

 getPicker().setColorsNode(colors);
 getPicker().refresh();
 }
 protected void storeValues() {
 NodeList colorNodes = getColorManager().getRootElement().getChildNodes();
 int colorNodesLength = colorNodes.getLength();
 for (int i = 0; i < colorNodesLength; i++) {
 NamedNodeMap attributes = colorNodes.item(i).getAttributes();
 if (attributes != null) {
 int attributesLength = attributes.getLength();
 String name, foreground, background, bold;
 name = foreground = background = bold = null;
 for (int j = 0; j < attributesLength; j++) {
 Node preference = attributes.item(j);
 if (preference.getNodeName().compareTo(XMLPreferenceNames.NAME) == 0)
 name = preference.getNodeValue();
 if (preference.getNodeName().compareTo(XMLPreferenceNames.FOREGROUND) == 0)
 foreground = preference.getNodeValue();
 if (preference.getNodeName().compareTo(XMLPreferenceNames.BACKGROUND) == 0)
 background = preference.getNodeValue();
 if (preference.getNodeName().compareTo(XMLPreferenceNames.BOLD) == 0)
 bold = preference.getNodeValue();
 }
 String preferencesString = new String();
 if (foreground != null)
 preferencesString += foreground;
 preferencesString += XMLPreferenceNames.COMMA;
 if (background != null)
 preferencesString += background;
 preferencesString += XMLPreferenceNames.COMMA;
 if (bold != null)
 preferencesString += bold;
 
 if (name != null)
 getPreferenceStore().setValue(name, preferencesString);
 }
 }
 }
 protected Element addColor(Node colors, String name, String foreground, String background) {
 Element newColor = newColor(colors.getOwnerDocument(), name, foreground, background);
 colors.appendChild(newColor);
 return newColor;
 }
 public static String getColorString(int r, int g, int b) {
 return "#" + getHexString(r, 2) + getHexString(g, 2) + getHexString(b, 2);//$NON-NLS-1$
 }
 public static String getHexString(int value, int minWidth) {
 String hexString = Integer.toHexString(value);
 for (int i = hexString.length(); i < minWidth; i++) {
 hexString = "0" + hexString;//$NON-NLS-1$
 }
 return hexString;
 }
 protected Element newColor(Document doc, String name, String foreground, String background) {
 if (doc == null || name == null || name.length() < 1)
 return null;
 Element newColor = doc.createElement(XMLPreferenceNames.COLOR);
 newColor.setAttribute(XMLPreferenceNames.NAME, name);
 if(foreground != null)
 newColor.setAttribute(XMLPreferenceNames.FOREGROUND, foreground);
 if(background != null)
 newColor.setAttribute(XMLPreferenceNames.BACKGROUND, background);
 return newColor;
 }*/


}
