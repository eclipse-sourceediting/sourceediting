package org.eclipse.wst.xsl.jaxp.debug.ui.internal.preferences;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS{
	private static final String BUNDLE_NAME = "org.eclipse.wst.xsl.jaxp.debug.ui.internal.preferences.messages"; //$NON-NLS-1$

	public static String AddProcessorDialog_1;
	public static String AddProcessorDialog_7;
	public static String AddProcessorDialog_Edit_Title;
	public static String AddProcessorDialog_Add_Title;
	public static String AddProcessorDialog_jars;
	public static String AddProcessorDialog_attributes;
	public static String AddProcessorDialog_processorName;
	public static String AddProcessorDialog_processorType;
	public static String AddProcessorDialog_enterName;
	public static String AddProcessorDialog_duplicateName;

	public static String ProcessorLibraryBlock_6;
	public static String ProcessorLibraryBlock_AddButton;
	public static String ProcessorLibraryBlock_RemoveButton;
	public static String ProcessorLibraryBlock_AddWorkspaceButton;
	public static String ProcessorLibraryBlock_FileDialog_Title;
	public static String ProcessorLibraryBlock_WorkspaceFileDialog_Title;
	public static String ProcessorLibraryBlock_WorkspaceFileDialog_Message;

	public static String InstalledProcessorsBlock_0;
	public static String InstalledProcessorsBlock_1;
	public static String InstalledProcessorsBlock_2;
	public static String InstalledProcessorsBlock_3;
	public static String InstalledProcessorsBlock_4;
	public static String InstalledProcessorsBlock_5;
	public static String InstalledProcessorsBlock_6;
	public static String InstalledProcessorsBlock_7;
	public static String InstalledProcessorsBlock_8;
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
