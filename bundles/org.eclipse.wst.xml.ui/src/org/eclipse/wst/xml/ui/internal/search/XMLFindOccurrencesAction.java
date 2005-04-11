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
package org.eclipse.wst.xml.ui.internal.search;

import java.util.ResourceBundle;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.search.BasicFindOccurrencesAction;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.text.IXMLPartitions;



/**
 * <p>
 * Configures a BasicFindOccurrencesAction with XML partitions and regions
 * </p>
 * 
 * <p>
 * Uses default <code>getSearchQuery()</code>.
 * </p>
 * 
 * @author pavery
 */
public class XMLFindOccurrencesAction extends BasicFindOccurrencesAction {

	public XMLFindOccurrencesAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	public String[] getPartitionTypes() {

		return new String[]{IXMLPartitions.XML_DEFAULT};
	}

	public String[] getRegionTypes() {

		return new String[]{DOMRegionContext.XML_TAG_NAME, DOMRegionContext.XML_TAG_ATTRIBUTE_NAME, DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE};
	}

}
