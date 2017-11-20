/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class DTDPropertiesMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages"; //$NON-NLS-1$

	// org.eclipse.wst.dtd.ui.internal.properties.section.OccurrenceSection.java
	public static String _UI_ONCE;
	public static String _UI_ONE_OR_MORE;
	public static String _UI_OPTIONAL;
	public static String _UI_ZERO_OR_MORE;
	public static String _UI_LABEL_OCCURRENCE;
	// org.eclipse.wst.dtd.ui.internal.properties.section.AttribueDefaultSection.java
	public static String _UI_DEFAULT;
	public static String _UI_LABEL_USAGE;
	public static String _UI_LABEL_DEFAULT_VALUE;

	// org.eclipse.wst.dtd.ui.internal.properties.section.ContentModelGroupSection.java
	public static String _UI_SEQUENCE;
	public static String _UI_CHOICE;
	public static String _UI_LABEL_MODEL_GROUP;

	// org.eclipse.wst.dtd.ui.internal.properties.section.ContentModelNameSection.java
	public static String _UI_LABEL_CONTENT_MODEL;

	// org.eclipse.wst.dtd.ui.internal.properties.section.ContentModelTypeSection.java
	public static String _UI_LABEL_CONTENT_TYPE;

	// org.eclipse.wst.dtd.ui.internal.properties.section.NameSection.java
	public static String _UI_LABEL_NAME;

	// org.eclipse.wst.dtd.ui.internal.properties.section.TypeSection.java
	public static String _UI_LABEL_TYPE;

	// org.eclipse.wst.dtd.ui.internal.properties.section.EntityTypeSection.java
	public static String _UI_LABEL_ENTITY_TYPE;
	public static String _UI_LABEL_EXTERNAL_ENTITY;
	public static String _UI_LABEL_PARAMETER_ENTITY;
	public static String _UI_LABEL_GENERAL_ENTITY;

	// org.eclipse.wst.dtd.ui.internal.properties.section.EntityValueSection.java
	public static String _UI_LABEL_ENTITY_VALUE;
	public static String _UI_LABEL_PUBLIC_ID;
	public static String _UI_LABEL_SYSTEM_ID;

	// org.eclipse.wst.dtd.ui.internal.properties.section.DTDSectionLabelProvider
	public static String _UI_PROPERTIES_VIEW_TITLE_ELEMENT_REF;

	// org.eclipse.wst.dtd.ui.internal.properties.section.NewEntitySection
	public static String _UI_FILEDIALOG_SELECT_DTD;
	public static String _UI_FILEDIALOG_SELECT_DTD_DESC;

	// New property tabs
	public static String DTDSectionLabelProvider_0;
	public static String DTDSectionLabelProvider_1;
	public static String DTDSectionLabelProvider_10;
	public static String DTDSectionLabelProvider_11;
	public static String DTDSectionLabelProvider_6;
	public static String DTDSectionLabelProvider_7;
	public static String DTDSectionLabelProvider_8;
	public static String DTDSectionLabelProvider_9;

	private static ResourceBundle fResourceBundle;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, DTDPropertiesMessages.class);
	}

	public static ResourceBundle getResourceBundle() {
		try {
			if (fResourceBundle == null)
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		}
		catch (MissingResourceException x) {
			fResourceBundle = null;
		}
		return fResourceBundle;
	}

	private DTDPropertiesMessages() {
		super();
	}

}
