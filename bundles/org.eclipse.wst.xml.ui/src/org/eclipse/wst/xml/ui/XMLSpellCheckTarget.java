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
package org.eclipse.wst.xml.ui;

import org.eclipse.wst.sse.ui.SpellCheckTargetImpl;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

public class XMLSpellCheckTarget extends SpellCheckTargetImpl {

	/**
	 * @param editor
	 */
	public XMLSpellCheckTarget() {
		super();
	}

	/* (non-Javadoc)
	 */
	protected boolean isValidType(String type) {
		boolean valid = false;
		if (XMLRegionContext.XML_COMMENT_TEXT.equals(type) || XMLRegionContext.XML_CONTENT.equals(type)) {
			valid = true;
		}
		return valid || super.isValidType(type);
	}

}
