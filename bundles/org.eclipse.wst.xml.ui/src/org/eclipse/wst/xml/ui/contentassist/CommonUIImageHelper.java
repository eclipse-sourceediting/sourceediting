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
package org.eclipse.wst.xml.ui.contentassist;



import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;


/**
 * @deprecated use internal XMLEditorPluginImageHelper or external
 *             SharedXMLEditorPluginImageHelper instead
 */
public class CommonUIImageHelper extends SourceEditorImageHelper {

	public Image createImage(String resource) {
		return XMLEditorPluginImageHelper.getInstance().getImage(resource);
	}
}
