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
package org.eclipse.wst.html.core.internal.text;

import java.util.Locale;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.css.core.text.ICSSPartitions;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.IRegionComparible;
import org.eclipse.wst.sse.core.internal.text.rules.IStructuredTypedRegion;
import org.eclipse.wst.sse.core.internal.util.ScriptLanguageKeys;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;

/**
 * Document partitioner for HTML. Client-side scripts of type JavaScript are
 * reported as ST_SCRIPT, all others for <script language="foo" type="foo2">
 * as SCRIPT.language:FOO and SCRIPT.type:FOO2.
 */
public class StructuredTextPartitionerForHTML extends StructuredTextPartitionerForXML implements IStructuredTextPartitioner {

	private final static String[] configuredContentTypes = new String[]{IHTMLPartitions.HTML_DEFAULT, IHTMLPartitions.HTML_DECLARATION, IHTMLPartitions.HTML_COMMENT, IHTMLPartitions.SCRIPT, ICSSPartitions.STYLE,IHTMLPartitions.SCRIPT_EVENTHANDLER};

	public static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
	public static final String JAVASCRIPT_APPLICATION = "application/x-javascript"; //$NON-NLS-1$
	
	private static final String[] EVENT_ATTRIBUTE_NAMES = 
		{HTML40Namespace.ATTR_NAME_ONCLICK, 
		HTML40Namespace.ATTR_NAME_ONDBLCLICK,
		HTML40Namespace.ATTR_NAME_ONMOUSEDOWN,
		HTML40Namespace.ATTR_NAME_ONMOUSEUP,
		HTML40Namespace.ATTR_NAME_ONMOUSEOVER,
		HTML40Namespace.ATTR_NAME_ONMOUSEMOVE,
		HTML40Namespace.ATTR_NAME_ONMOUSEOUT,
		HTML40Namespace.ATTR_NAME_ONKEYPRESS,
		HTML40Namespace.ATTR_NAME_ONKEYDOWN,
		HTML40Namespace.ATTR_NAME_ONKEYUP,
		HTML40Namespace.ATTR_NAME_ONHELP,
		HTML40Namespace.ATTR_NAME_ONBLUR,
		HTML40Namespace.ATTR_NAME_ONFOCUS,
		HTML40Namespace.ATTR_NAME_ONLOAD,
		HTML40Namespace.ATTR_NAME_ONUNLOAD,
		HTML40Namespace.ATTR_NAME_ONSUBMIT};
	
	/** array of style attribute names */
	private static final String[] STYLE_ATTRIBUTE_NAMES =  {HTML40Namespace.ATTR_NAME_STYLE};
	
	public StructuredTextPartitionerForHTML() {
		super();
	}

	public IStructuredTypedRegion createPartition(int offset, int length, String type) {
		if (type == IHTMLPartitions.SCRIPT) {
			IStructuredDocumentRegion node = fStructuredDocument.getRegionAtCharacterOffset(offset);
			if (node != null) {
				String stype = getScriptingPartitionType(node);
				return super.createPartition(offset, length, stype);
			}
		}
		return super.createPartition(offset, length, type);
	}

	protected void setInternalPartition(int offset, int length, String type) {
		String localType = type;
		if (type == IHTMLPartitions.SCRIPT) {
			IStructuredDocumentRegion node = fStructuredDocument.getRegionAtCharacterOffset(offset);
			if (node != null) {
				localType = getScriptingPartitionType(node);
			}
		}
		super.setInternalPartition(offset, length, localType);
	}

	private String getScriptingPartitionType(IStructuredDocumentRegion coreNode) {
		String language = null;
		String type = null;
		String result = IHTMLPartitions.SCRIPT;
		IStructuredDocumentRegion node = coreNode;
		ITextRegion attrNameRegion = null;
		while (node != null && isValidScriptingRegionType(node.getType())) {
			node = node.getPrevious();
		}

		ITextRegionList regions = node.getRegions();
		if (regions.size() > 4 && regions.get(1).getType() == DOMRegionContext.XML_TAG_NAME) {
			ITextRegion potentialLanguageRegion = regions.get(1);
			String potentialLanguageString = node.getText(potentialLanguageRegion);
			if (potentialLanguageString.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT)) {
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					String regionType = region.getType();
					if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
						attrNameRegion = region;
					else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						String attrName = node.getText(attrNameRegion);
						if (attrName.equalsIgnoreCase(HTML40Namespace.ATTR_NAME_LANGUAGE))
							language = StringUtils.strip(node.getText(region));
						else if (attrName.equalsIgnoreCase(HTML40Namespace.ATTR_NAME_TYPE)) {
							type = StringUtils.strip(node.getText(region));
							/*
							 * Avoid partition names built with MIME subtypes,
							 * e.g. type="text/javascript;e4x=1"
							 */
							if (type != null) {
								int index = type.indexOf(';');
								if (index > 1)
									type = type.substring(0, index);
							}
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
		return type == DOMRegionContext.BLOCK_TEXT || type == DOMRegionContext.XML_CDATA_OPEN || type == DOMRegionContext.XML_CDATA_TEXT || type == DOMRegionContext.XML_CDATA_CLOSE;
	}

	protected void initLegalContentTypes() {
		fSupportedTypes = getConfiguredContentTypes();
	}

	private String lookupScriptType(String type) {
		for (int i = 0; i < ScriptLanguageKeys.JAVASCRIPT_MIME_TYPE_KEYS.length; i++)
			if (ScriptLanguageKeys.JAVASCRIPT_MIME_TYPE_KEYS[i].equalsIgnoreCase(type))
				return IHTMLPartitions.SCRIPT;
		return IHTMLPartitions.SCRIPT + ".type." + type.toUpperCase(Locale.ENGLISH); //$NON-NLS-1$
	}

	private String lookupScriptLanguage(String language) {
		for (int i = 0; i < ScriptLanguageKeys.JAVASCRIPT_LANGUAGE_KEYS.length; i++)
			if (ScriptLanguageKeys.JAVASCRIPT_LANGUAGE_KEYS[i].equalsIgnoreCase(language))
				return IHTMLPartitions.SCRIPT;
		return IHTMLPartitions.SCRIPT + ".language." + language.toUpperCase(Locale.ENGLISH); //$NON-NLS-1$
	}

	public String getPartitionType(ITextRegion region, int offset) {
		String result = null;
		if (region.getType() == DOMRegionContext.XML_COMMENT_TEXT || region.getType() == DOMRegionContext.XML_COMMENT_OPEN || region.getType() == DOMRegionContext.XML_COMMENT_CLOSE)
			result = IHTMLPartitions.HTML_COMMENT;
		else if (region.getType() == DOMRegionContext.XML_DOCTYPE_DECLARATION || region.getType() == DOMRegionContext.XML_DECLARATION_OPEN)
			result = IHTMLPartitions.HTML_DECLARATION;
		else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE && isScriptAttributeValue(region, offset))
			result = IHTMLPartitions.SCRIPT_EVENTHANDLER;
		else if (isStyleAttributeValue(region, offset))
			result = ICSSPartitions.STYLE;
		else
			result = super.getPartitionType(region, offset);
		return result;
	}

	public String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode) {
		
		ITextRegion previousStartTagNameRegion = null;
		ITextRegion nextEndTagNameRegion = null;
		
		ITextRegion[] regions = previousNode.getRegions().toArray();
		for (int i = 0; i < regions.length; i++) {
			if(regions[i].getType() == DOMRegionContext.XML_TAG_NAME) {
				previousStartTagNameRegion = regions[i];
				break;
			}
		}
		regions = nextNode.getRegions().toArray();
		for (int i = 0; i < regions.length; i++) {
			if(regions[i].getType() == DOMRegionContext.XML_TAG_NAME) {
				nextEndTagNameRegion = regions[i];
				break;
			}
		}
		
		if(previousStartTagNameRegion == null || nextEndTagNameRegion == null)
			return IHTMLPartitions.HTML_DEFAULT;
		
		String name1 = previousNode.getText(previousStartTagNameRegion);
		String name2 = nextNode.getText(nextEndTagNameRegion);
		if (name1.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT) && name2.equalsIgnoreCase(HTML40Namespace.ElementName.SCRIPT))
			//			return ST_SCRIPT;
			return getScriptingPartitionType(fStructuredDocument.getRegionAtCharacterOffset(previousNode.getStartOffset(previousStartTagNameRegion)));
		else if (name1.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE) && name2.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE))
			return ICSSPartitions.STYLE;
		return super.getPartitionTypeBetween(previousNode, nextNode);
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
			result = IHTMLPartitions.SCRIPT;
		else if (tagname.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE) || isStyleAttributeValue(region,offset))
			result = ICSSPartitions.STYLE;
		else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE && isScriptAttributeValue(region, offset))
			result = IHTMLPartitions.SCRIPT_EVENTHANDLER;
		else
			result = super.getPartitionType(region, offset);

		return result;
	}

	public String getDefaultPartitionType() {
		return IHTMLPartitions.HTML_DEFAULT;
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
	
	private boolean isScriptAttributeValue(ITextRegion region, int offset) {
		if (region.getType() != DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
			return false;

		return isAttributeNameForValueInArray(EVENT_ATTRIBUTE_NAMES, region, offset);
	}
	
	/**
	 * @param region {@link ITextRegion} containing <code>offset</code>
	 * @param offset offset in the given <code>region</code> to check if it is in
	 * the attribute value region of a style attribute
	 * @return <code>true</code> if the given offset in the given region is
	 * in the value region of a style attribute, <code>false</code> otherwise
	 */
	private boolean isStyleAttributeValue(ITextRegion region, int offset) {
		boolean isStyleAttributeValue = false;
		if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			isStyleAttributeValue = isAttributeNameForValueInArray(STYLE_ATTRIBUTE_NAMES, region, offset);
		}

		return isStyleAttributeValue;
	}
	
	/**
	 * <p>determines if the attribute name associated with the given attribute region
	 * is in the given array of attribute names</p>
	 * 
	 * @param attributeNames determine if the attribute name associated with the given offset
	 * is in this array of attribute names
	 * @param attrValueRegion {@link ITextRegion} of the attribute region containing the given offset
	 * @param offset offset in an attribute region to determine if it is in the list of given attribute names
	 * @return <code>true</code> if the attribute name associated with the given offset is in the given
	 * list of attribute names, <code>false</code> otherwise
	 */
	private boolean isAttributeNameForValueInArray(String[] attributeNames, ITextRegion attrValueRegion, int offset) {
		IStructuredDocumentRegion node = fStructuredDocument.getRegionAtCharacterOffset(offset);
		ITextRegionList regionList = node.getRegions();
		int currentIndex = regionList.indexOf(attrValueRegion);
		
		/*
		 * 4 is the minimum index allowing for the tag's open, name, attribute
		 * name and equals character to appear first
		 */
		if (currentIndex < 4)
			return false;
		ITextRegion tagAttrNameRegion = regionList.get(currentIndex - 2);
		
		boolean foundAttributeName = false;
		if (fStructuredDocument instanceof IRegionComparible) {
			int start = node.getStartOffset(tagAttrNameRegion);
			for (int i = 0; !foundAttributeName && i < attributeNames.length; i++) {
				foundAttributeName = ((IRegionComparible) fStructuredDocument).regionMatchesIgnoreCase(start, tagAttrNameRegion.getTextLength(), attributeNames[i]);
			}
		}
		else {
			String tagAttrName = node.getText(tagAttrNameRegion);
			foundAttributeName = StringUtils.contains(attributeNames, tagAttrName, false);
		}
		return foundAttributeName;
	}
}
