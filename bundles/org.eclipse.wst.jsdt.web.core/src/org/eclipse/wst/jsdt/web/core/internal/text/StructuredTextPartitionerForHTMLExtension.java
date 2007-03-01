package org.eclipse.wst.jsdt.web.core.internal.text;

import org.eclipse.wst.jsdt.web.core.internal.java.JsDataTypes;
import org.eclipse.wst.jsdt.web.core.internal.java.NodeHelper;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class StructuredTextPartitionerForHTMLExtension extends
		StructuredTextPartitionerForHTML {

	@Override
	public String getPartitionType(ITextRegion region, int offset) {
		String result = null;
		String attrName = null;
		char charAtOffset = 0;

		try {
			charAtOffset = fStructuredDocument.getChar(offset);
		} catch (Exception e) {
		}

		if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			attrName = getAttrName(region);
		}
		if (region.getType() == DOMRegionContext.XML_COMMENT_TEXT
				|| region.getType() == DOMRegionContext.XML_COMMENT_OPEN) {
			result = IHTMLPartitions.HTML_COMMENT;
		} else if (region.getType() == DOMRegionContext.XML_DOCTYPE_DECLARATION
				|| region.getType() == DOMRegionContext.XML_DECLARATION_OPEN) {
			result = IHTMLPartitions.HTML_DECLARATION;
		} else if (null != attrName
				&& (NodeHelper.isInArray(JsDataTypes.EVENTS, attrName))
				&& charAtOffset != '\'' && charAtOffset != '"') {
			/* check for script elements in attributes */
			result = IHTMLPartitions.SCRIPT;
		} else {
			result = super.getPartitionType(region, offset);
		}
		return result;
	}

	@Override
	public String getPartitionType(ForeignRegion region, int offset) {
		String result = null;
		String attrName = null;

		char charAtOffset = 0;

		try {
			charAtOffset = fStructuredDocument.getChar(offset);
		} catch (Exception e) {
		}

		if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			attrName = getAttrName(region);
		}

		if (null != attrName
				&& (NodeHelper.isInArray(JsDataTypes.EVENTS, attrName))
				&& charAtOffset != '\'' && charAtOffset != '"') {
			/* check for script elements in attributes */
			result = IHTMLPartitions.SCRIPT;
		} else {
			result = super.getPartitionType(region, offset);
		}

		return result;
	}

	private String getAttrName(ITextRegion attrValueRegion) {
		if (attrValueRegion.getType() != DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			return null;
		}

		IStructuredDocumentRegion node = fStructuredDocument
				.getRegionAtCharacterOffset(attrValueRegion.getStart());
		ITextRegionList regionList = node.getRegions();
		int currentIndex = regionList.indexOf(attrValueRegion);

		if ((currentIndex - 2) < 0) {
			return null;
		}
		ITextRegion tagAttrNameRegion = regionList.get(currentIndex - 2);

		String tagAttrName = node.getText().substring(
				tagAttrNameRegion.getStart(), tagAttrNameRegion.getTextEnd())
				.trim();
		return tagAttrName;
	}

}
