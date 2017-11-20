/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API based off wst.xml.ui.CustomTemplateProposal
 *     Doug Satchwell - refactored to internal package
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @since 1.0
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xml.xpath.ui.internal.messages"; //$NON-NLS-1$
	public static String XPathComputer_0;
	public static String XPathComputer_5;
	public static String XPathView_0;
	public static String XPathView_1;
	public static String XPathView_2;
	public static String XPathView_8;
	public static String XPathViewActions_0;
	public static String XPathViewActions_1;
	public static String XPathPrefencePage_0;

	public static String XPathNavigator_XPath_Navigator;
	public static String XPathNavigator_Select_source_first;
	public static String XPathNavigator_XPath_Eval_Failed;
	public static String XPathNavigator_XML_Source_Document;
	public static String XPathNavigator_Context;
	public static String XPathNavigator_Selection;
	public static String XPathNavigator_Document;
	public static String XPathNavigator_Expression;
	public static String XPathNavigator_DOM_Tree;
	public static String XPathNavigator_Text;
	public static String XPathNavigator_Run_XPath_Query;
	public static String XPathNavigator_Run_on_selected;
	public static String XPathNavigator_Refresh_Source_Docs;
	public static String XPathNavigator_Refresh_Source_Docs_Tip;
	public static String XPathNavigator_Show_In_Source;
	public static String XPathNavigator_Show_In_Source_Tip;
	public static String XPathNavigator_Node_could_not_be_selected;
	public static String XPathNavigator_Nothing_selected;
	public static String XPathNavigator_XPath_Show_In_Source_Failed;
	public static String XPathNavigator_Namespaces;
	public static String XPathNavigator_Namespaces_Tip;
	public static String XPathNavigator_Namespace_Prefixes;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
