/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * HTML extension for data types.
 */
public interface HTMLCMDataType extends CMDataType {

	/** Boolean; it should be defined in CMDataType. */
	public static final String BOOLEAN = "BOOLEAN"; //$NON-NLS-1$
	public static final String COLOR = "COLOR"; //$NON-NLS-1$
	public static final String EVENT = "EVENT"; //$NON-NLS-1$
	public static final String IDREFS = "IDREFS"; //$NON-NLS-1$
	/** Name; it should be defined in CMDataType. */
	public static final String NAME = "NAME"; //$NON-NLS-1$
	// Following types are just aliases.
	/** %Character; == CDATA */
	public static final String CHARACTER = CMDataType.CDATA;
	/** %Charset; == CDATA */
	public static final String CHARSET = CMDataType.CDATA;
	/** %Charsets; == CDATA */
	public static final String CHARSETS = CMDataType.CDATA;
	/** %ContentType; == CDATA */
	public static final String CONTENT_TYPE = CMDataType.CDATA;
	/** %Coords; == CDATA */
	public static final String COORDS = CMDataType.CDATA;
	/** %Datetime; == CDATA */
	public static final String DATETIME = CMDataType.CDATA;
	/** %FrameTarget; == CDATA */
	public static final String FRAME_TARGET = CMDataType.CDATA;
	/** %LanguageCode; == NAME */
	public static final String LANGUAGE_CODE = NAME;
	/** %Length; == CDATA */
	public static final String LENGTH = CMDataType.CDATA;
	/** %LinkTypes; == CDATA */
	public static final String LINK_TYPES = CMDataType.CDATA;
	/** %LIStyle; == CDATA */
	public static final String LI_STYLE = CMDataType.CDATA;
	/** %MediaDesc; == CDATA */
	public static final String MEDIA_DESC = CMDataType.CDATA;
	/** %MultiLength; == CDATA */
	public static final String MULTI_LENGTH = CMDataType.CDATA;
	/** %OLStyle; == CDATA */
	public static final String OL_STYLE = CMDataType.CDATA;
	/** %Pixles; == CDATA */
	public static final String PIXELS = CMDataType.CDATA;
	/** %Script; == EVENT */
	public static final String SCRIPT = EVENT;
	/** %StyleSheet; == EVENT */
	public static final String STYLE_SHEET = CMDataType.CDATA;
	/** %Text; == CDATA */
	public static final String TEXT = CMDataType.CDATA;
	/** %MediaType; == CDATA */
	public static final String MEDIA_TYPE = CMDataType.CDATA;
	/** %BrowsingContext; == CDATA */
	public static final String BROWSING_CONTEXT = CMDataType.CDATA;
	
}
