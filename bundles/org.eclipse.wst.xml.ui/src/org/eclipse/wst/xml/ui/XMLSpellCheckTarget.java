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
package org.eclipse.wst.xml.ui;

import org.eclipse.wst.sse.ui.SpellCheckTargetImpl;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

/**
 * @deprecated - to be removed in M4
 */

public class XMLSpellCheckTarget extends SpellCheckTargetImpl {

	public XMLSpellCheckTarget() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.SpellCheckTargetImpl#isValidType(java.lang.String)
	 */
	protected boolean isValidType(String type) {
		boolean valid = false;
		if (XMLRegionContext.XML_COMMENT_TEXT.equals(type) || XMLRegionContext.XML_CONTENT.equals(type)) {
			valid = true;
		}
		return valid || super.isValidType(type);
	}

}
