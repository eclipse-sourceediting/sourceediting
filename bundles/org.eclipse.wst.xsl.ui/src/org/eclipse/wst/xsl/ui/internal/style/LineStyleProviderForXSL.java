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
 *     Benjamin Muskalla, b.muskalla@gmx.net - [158660] character entities should have their own syntax highlighting preference     
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;

public class LineStyleProviderForXSL extends LineStyleProviderForXML implements LineStyleProvider {
	public LineStyleProviderForXSL() {
		super();
	}


	protected IPreferenceStore getColorPreferences() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}


}
