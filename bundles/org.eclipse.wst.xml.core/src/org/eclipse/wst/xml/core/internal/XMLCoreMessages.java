/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.xml.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by XML Core
 * 
 * @since 1.0
 */
public class XMLCoreMessages {
	private static final String BUNDLE_NAME = "org.eclipse.wst.xml.core.internal.XMLCorePluginResources";//$NON-NLS-1$
	
	public static String Invalid_character_lt_fo_ERROR_;
	public static String Invalid_character_gt_fo_ERROR_;
	public static String Invalid_character_amp_fo_ERROR_;
	public static String Invalid_character__f_EXC_;
	public static String loading;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, XMLCoreMessages.class);
	}
	
	private XMLCoreMessages() {
		// cannot create new instance
	}
}
