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
package org.eclipse.wst.html.core.internal.text.rules;

import java.util.Locale;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.css.core.internal.text.rules.StructuredTextPartitionerForCSS;
import org.eclipse.wst.html.core.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.text.StructuredTypedRegion;
import org.eclipse.wst.sse.core.util.ScriptLanguageKeys;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;

/**
 * Document partitioner for HTML. Client-side scripts of type JavaScript are
 * reported as ST_SCRIPT, all others for <script language="foo" type="foo2">
 * as SCRIPT.language:FOO and SCRIPT.type:FOO2.
 */
public class StructuredTextPartitionerForHTML extends StructuredTextPartitionerForXML implements IStructuredTextPartitioner {

	public final static String ST_DEFAULT_HTML = "org.eclipse.wst.html.DEFAULT_HTML"; //$NON-NLS-1$
	public final static String ST_HTML_DECLARATION = "org.eclipse.wst.html.HTML_DECLARATION"; //$NON-NLS-1$
	public final static String ST_HTML_COMMENT = "org.eclipse.wst.htmlHTML_COMMENT"; //$NON-NLS-1$
	public final static String ST_SCRIPT = "org.eclipse.wst.html.SCRIPT"; //$NON-NLS-1$
	private final static String[] configuredContentTypes = new String[]{ST_DEFAULT_HTML, ST_HTML_DECLARATION, ST_HTML_COMMENT, ST_SCRIPT, StructuredTextPartitionerForCSS.ST_STYLE};

	public static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
	public static final String JAVASCRIPT_APPLICATION = "application/x-javascript"; //$NON-NLS-1$


	/**
	 * Constructor for JSPDocumentPartioner.
	 */
	public StructuredTextPartitionerForHTML() {
		super();
	}

	public StructuredTypedRegion createPartition(int offset, int length, String type) {
		if (type == ST_SCRIPT) {
			IStructuredDocumentRegion node = structuredDocument.getRegionAtCharacterOffset(offset);
			if (node != null) {
				String stype = getScriptingPartitionType(node);
				return super.createPartition(offset, length, stype);
			}
		}
		return super.createPartition(offset, length, type);
	}

	/**
	 * @see com.ibm.sed.structuredDocument.partition.StructuredTextPartitioner#createPartition(int,
	 *      int, java.lang.String)
	 */
	protected void setInternalPartition(int offset, int length, String type) {
		String localType = type;
		if (type == ST_SCRIPT) {
			IStructuredDocumentRegion node = structuredDocument.getRegionAtCharacterOffset(offset);
			if (node != null) {
				localType = getScriptingPartitionType(node);
			}
		}
		super.setInternalPartition(offset, length, localType);
	}

	private String getScriptingPartitionType(IStructuredDocumentRegion coreNode) {
		String language = null;
		String type = null;
		String result = ST_SCRIPT;
		IStructuredDocumentRegion node = coreNode;
		ITextRegion attrNameRegion = null;
		while (node != null && isValidScriptingRegionType(node.getType())) {
			node = node.getPrevious();
		}

		ITextRegionList regions = node.getRegions();
		if (regions.size() > 4 && regions.get(1).getType() == XMLRegionContext.XML_TAG_NAME) {
			ITextRegion potentialLanguageRegion = regions.get(1);
			String potentialLanguageString = node.getText(potentialLanguageRegion);
			if (potentialLanguageString.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT)) {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					String regionType = region.getType();
					if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME)
						attrNameRegion = region;
					else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						String attrName = node.getText(attrNameRegion);
						if (attrName.equalsIgnoreCase(HTML40Namespace.ATTR_NAME_LANGUAGE))
							language = StringUtils.strip(node.getText(region));
						else if (attrName.equalsIgnoreCase(HTML40Namespace.ATTR_NAME_TYPE)) {
							type = StringUtils.strip(node.getText(region));
							break;
						}
						attrNameRegion = null;
					}
				}
			}
		}
		if (type != null)
			result = lookupScriptType(type);
		else if (language != null)
			result = lookupScriptLanguage(language);
		return result;
	}

	private boolean isValidScriptingRegionType(String type) {
		return type == XMLRegionContext.BLOCK_TEXT || type == XMLRegionContext.XML_CDATA_OPEN || type == XMLRegionContext.XML_CDATA_TEXT || type == XMLRegionContext.XML_CDATA_CLOSE;
	}

	protected void initLegalContentTypes() {
		fSupportedTypes = getConfiguredContentTypes();
	}

	private String lookupScriptType(String type) {
		for (int i = 0; i < ScriptLanguageKeys.JAVASCRIPT_MIME_TYPE_KEYS.length; i++)
			if (ScriptLanguageKeys.JAVASCRIPT_MIME_TYPE_KEYS[i].equalsIgnoreCase(type))
				return ST_SCRIPT;
		return "SCRIPT.type:" + type.toUpperCase(Locale.ENGLISH); //$NON-NLS-1$
	}

	private String lookupScriptLanguage(String language) {
		for (int i = 0; i < ScriptLanguageKeys.JAVASCRIPT_LANGUAGE_KEYS.length; i++)
			if (ScriptLanguageKeys.JAVASCRIPT_LANGUAGE_KEYS[i].equalsIgnoreCase(language))
				return ST_SCRIPT;
		return "SCRIPT.language:" + language.toUpperCase(Locale.ENGLISH); //$NON-NLS-1$
	}

	/**
	 * @see com.ibm.sed.model.StructuredTextPartitioner#getPartitionType(com.ibm.sed.structuredDocument.ITextRegion)
	 */
	public String getPartitionType(ITextRegion region, int offset) {
		String result = null;
		if (region.getType() == XMLRegionContext.XML_COMMENT_TEXT || region.getType() == XMLRegionContext.XML_COMMENT_OPEN)
			result = ST_HTML_COMMENT;
		else if (region.getType() == XMLRegionContext.XML_DOCTYPE_DECLARATION || region.getType() == XMLRegionContext.XML_DECLARATION_OPEN)
			result = ST_HTML_DECLARATION;
		else
			result = super.getPartitionType(region, offset);
		return result;
	}

	/**
	 * @see com.ibm.sed.structuredDocument.partition.StructuredTextPartitioner#getPartitionTypeBetween(com.ibm.sed.structuredDocument.ITextRegion,
	 *      com.ibm.sed.structuredDocument.ITextRegion)
	 */
	public String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, ITextRegion previousStartTagNameRegion, IStructuredDocumentRegion nextNode, ITextRegion nextEndTagNameRegion) {
		String name1 = previousNode.getText(previousStartTagNameRegion);
		String name2 = nextNode.getText(nextEndTagNameRegion);
		if (name1.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT) && name2.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT))
			//			return ST_SCRIPT;
			return getScriptingPartitionType(structuredDocument.getRegionAtCharacterOffset(previousNode.getStartOffset(previousStartTagNameRegion)));
		else if (name1.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE) && name2.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE))
			return StructuredTextPartitionerForCSS.ST_STYLE;
		return super.getPartitionTypeBetween(previousNode, previousStartTagNameRegion, nextNode, nextEndTagNameRegion);
	}


	protected String getPartitionType(ForeignRegion region, int offset) {
		String tagname = region.getSurroundingTag();
		String result = null;
		// tagname should not be null,
		// but see https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4911
		if (tagname == null) {
			result = getUnknown();
		}
		else if (tagname.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT))
			result = ST_SCRIPT;
		else if (tagname.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE))
			result = StructuredTextPartitionerForCSS.ST_STYLE;
		else
			result = super.getPartitionType(region, offset);

		return result;
	}

	public String getDefault() {
		return ST_DEFAULT_HTML;
	}

	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitionerForHTML();
	}

	/**
	 * @return
	 */
	public static String[] getConfiguredContentTypes() {
		return configuredContentTypes;
	}

}