/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodel;



import java.util.Iterator;

public interface CSSMMNode {
	String getType();

	String getName();

	String getDescription();

	String getAttribute(String name);

	Iterator getChildNodes();

	Iterator getDescendants();


	static final String _PREFIX = "CSSMM."; //$NON-NLS-1$
	static final String TYPE_KEYWORD = _PREFIX + "Keyword"; //$NON-NLS-1$
	static final String TYPE_UNIT = _PREFIX + "Unit"; //$NON-NLS-1$
	static final String TYPE_FUNCTION = _PREFIX + "Function"; //$NON-NLS-1$
	static final String TYPE_STRING = _PREFIX + "String"; //$NON-NLS-1$
	static final String TYPE_STYLE_SHEET = _PREFIX + "StyleSheet"; //$NON-NLS-1$
	static final String TYPE_PROPERTY = _PREFIX + "Property"; //$NON-NLS-1$
	static final String TYPE_DESCRIPTOR = _PREFIX + "Descriptor"; //$NON-NLS-1$
	static final String TYPE_CONTAINER = _PREFIX + "Container"; //$NON-NLS-1$
	static final String TYPE_NUMBER = _PREFIX + "Number"; //$NON-NLS-1$
	static final String TYPE_SEPARATOR = _PREFIX + "Separator"; //$NON-NLS-1$
	static final String TYPE_CHARSET_RULE = _PREFIX + "CharsetRule"; //$NON-NLS-1$
	static final String TYPE_IMPORT_RULE = _PREFIX + "ImportRule"; //$NON-NLS-1$
	static final String TYPE_STYLE_RULE = _PREFIX + "StyleRule"; //$NON-NLS-1$
	static final String TYPE_MEDIA_RULE = _PREFIX + "MediaRule"; //$NON-NLS-1$
	static final String TYPE_PAGE_RULE = _PREFIX + "PageRule"; //$NON-NLS-1$
	static final String TYPE_FONT_FACE_RULE = _PREFIX + "FontFaceRule"; //$NON-NLS-1$
	static final String TYPE_CATEGORY = _PREFIX + "Category"; //$NON-NLS-1$
	static final String TYPE_META_MODEL = _PREFIX + "MetaModel"; //$NON-NLS-1$
	static final String TYPE_SELECTOR = _PREFIX + "Selector"; //$NON-NLS-1$	
}
