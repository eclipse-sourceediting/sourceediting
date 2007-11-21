package org.eclipse.wst.xml.xpath.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by XPath UI
 */
public class XPathUIMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xml.xpath.ui.XPathViewPlugin";//$NON-NLS-1$
	private static ResourceBundle fResourceBundle;


	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, XPathUIMessages.class);
	}

	private XPathUIMessages() {
		// cannot create new instance
	}

	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null) {
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
			}
		}
		catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}

	public static String XPathNavigator_Context;
	public static String XPathNavigator_Document;
	public static String XPathNavigator_DOM_Tree;
	public static String XPathNavigator_Expression;
	public static String XPathNavigator_Node_could_not_be_selected;
	public static String XPathNavigator_Nothing_selected;
	public static String XPathNavigator_Refresh_Source_Docs;
	public static String XPathNavigator_Refresh_Source_Docs_Tip;
	public static String XPathNavigator_Run_on_selected;
	public static String XPathNavigator_Run_XPath_Query;
	public static String XPathNavigator_Select_source_first;
	public static String XPathNavigator_Selection;
	public static String XPathNavigator_Show_In_Source;
	public static String XPathNavigator_Show_In_Source_Tip;
	public static String XPathNavigator_Text;
	public static String XPathNavigator_XML_Source_Document;
	public static String XPathNavigator_XPath_Eval_Failed;
	public static String XPathNavigator_XPath_Show_In_Source_Failed;
	public static String XPathNavigator_XPath_Navigator;
	public static String XPathNavigator_Namespaces;
	public static String XPathNavigator_Namespaces_Tip;
	public static String XPathNavigator_Namespace_Prefixes;
	
}
