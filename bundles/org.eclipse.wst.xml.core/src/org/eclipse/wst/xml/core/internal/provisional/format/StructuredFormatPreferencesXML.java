/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.wst.sse.core.internal.format.StructuredFormatPreferences;

public class StructuredFormatPreferencesXML extends StructuredFormatPreferences {
	private boolean fSplitMultiAttrs;
	private boolean fPreservePCDATAContent;

	public boolean getSplitMultiAttrs() {
		return fSplitMultiAttrs;
	}

	public void setSplitMultiAttrs(boolean splitMultiAttrs) {
		fSplitMultiAttrs = splitMultiAttrs;
	}

	public boolean isPreservePCDATAContent() {
		return fPreservePCDATAContent;
	}

	public void setPreservePCDATAContent(boolean preservePCDATAContent) {
		fPreservePCDATAContent = preservePCDATAContent;
	}
}
