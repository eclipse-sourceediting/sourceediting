/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.search;

import java.util.ResourceBundle;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.core.internal.provisional.text.IHTMLPartitionTypes;
import org.eclipse.wst.sse.ui.internal.search.BasicFindOccurrencesAction;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


/**
 * Configures a BasicFindOccurrencesAction with HTML partitions and regions
 * 
 * @author pavery
 */
public class HTMLFindOccurrencesAction extends BasicFindOccurrencesAction {

	public HTMLFindOccurrencesAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	/**
	 * @see com.ibm.sse.editor.internal.search.BasicFindOccurrencesAction#getPartitionTypes()
	 */
	public String[] getPartitionTypes() {

		return new String[]{IHTMLPartitionTypes.HTML_DEFAULT, IXMLPartitions.XML_DEFAULT};
	}

	/**
	 * @see com.ibm.sse.editor.internal.search.BasicFindOccurrencesAction#getRegionTypes()
	 */
	public String[] getRegionTypes() {

		return new String[]{DOMRegionContext.XML_TAG_NAME, DOMRegionContext.XML_TAG_ATTRIBUTE_NAME, DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE};
	}

}