/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.internal.text.hover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.Logger;
import org.eclipse.wst.json.ui.internal.editor.JSONEditorPluginImageHelper;
import org.osgi.framework.Bundle;

/**
 * Provides a set of convenience methods for creating HTML info for JSON content
 * assist and hover.
 *
 */
public class HTMLJSONPrinter {

	/**
	 * Style sheets content.
	 */
	private static String fgStyleSheet;

	public static String getAdditionalProposalInfo(IJSONPair pair) {
		StringBuffer buffer = new StringBuffer();
		ImageDescriptor descriptor = null;
		IJSONValue value = pair.getValue();
		if (value != null) {
			descriptor = JSONEditorPluginImageHelper.getInstance()
					.getImageDescriptor(value.getNodeType());
		}

		startPage(buffer, getTitleKey(value), descriptor);
		startDefinitionList(buffer);
		addDefinitionListItem(buffer, "Key", pair.getName());
		addDefinitionListItem(buffer, "Type", getValueType(pair.getValue()));
		endDefinitionList(buffer);
		endPage(buffer);
		return buffer.toString();
	}

	private static String getTitleKey(IJSONValue value) {
		return new StringBuilder("<b>JSON ").append(getValueType(value))
				.append("</b>").toString();
	}

	private static String getValueType(IJSONValue value) {
		if (value == null) {
			return "?";
		}
		switch (value.getNodeType()) {
		case IJSONNode.ARRAY_NODE:
			return "Array";
		case IJSONNode.DOCUMENT_NODE:
			return "Document";
		case IJSONNode.OBJECT_NODE:
			return "Object";
		case IJSONNode.VALUE_BOOLEAN_NODE:
			return "Boolean";
		case IJSONNode.VALUE_NULL_NODE:
			return "Null";
		case IJSONNode.VALUE_NUMBER_NODE:
			return "Number";
		case IJSONNode.VALUE_STRING_NODE:
			return "String";
		}
		return "?";
	}

	public static String getAdditionalProposalInfo(IJSONValue value) {
		StringBuffer buffer = new StringBuffer();
		ImageDescriptor descriptor = JSONEditorPluginImageHelper.getInstance()
				.getImageDescriptor(value.getNodeType());
		startPage(buffer, "<b>JSON Value</b>", descriptor);
		startDefinitionList(buffer);
		addDefinitionListItem(buffer, "Value", value.getSimpleValue());
		addDefinitionListItem(buffer, "Type", getValueType(value));
		endDefinitionList(buffer);
		endPage(buffer);
		return buffer.toString();
	}

	private static String getTitle(IJSONValue value) {
		StringBuilder title = new StringBuilder("<b>");
		title.append(getValueType(value));
		title.append("</b>");
		return title.toString();
	}

	public static void startDefinitionList(StringBuffer buffer) {
		buffer.append("<dl>"); //$NON-NLS-1$
	}

	public static void endDefinitionList(StringBuffer buffer) {
		buffer.append("</dl>"); //$NON-NLS-1$
	}

	public static void addDefinitionListItem(StringBuffer buffer, String name,
			String value) {
		if (value != null) {
			buffer.append("<dt><b>");
			buffer.append(name);
			buffer.append(":</b></dt>");
			buffer.append("<dd>");
			buffer.append(value);
			buffer.append("</dd>");
		}
	}

	/**
	 * Returns the Javadoc hover style sheet with the current JSON font from the
	 * preferences.
	 * 
	 * @return the updated style sheet
	 */
	private static String getStyleSheet() {
		if (fgStyleSheet == null) {
			fgStyleSheet = loadStyleSheet("/JSONHoverStyleSheet.css"); //$NON-NLS-1$
		}
		String css = fgStyleSheet;
		if (css != null) {
			FontData fontData = JFaceResources.getFontRegistry().getFontData(
					JFaceResources.DIALOG_FONT)[0];
			css = HTMLPrinter.convertTopLevelFont(css, fontData);
		}

		return css;
	}

	/**
	 * Loads and returns the style sheet associated with either JSON hover or
	 * the view.
	 * 
	 * @param styleSheetName
	 *            the style sheet name of either the Javadoc hover or the view
	 * @return the style sheet, or <code>null</code> if unable to load
	 */
	private static String loadStyleSheet(String styleSheetName) {
		Bundle bundle = Platform.getBundle(JSONUIPlugin.PLUGIN_ID);
		URL styleSheetURL = bundle.getEntry(styleSheetName);
		if (styleSheetURL == null)
			return null;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					styleSheetURL.openStream()));
			StringBuffer buffer = new StringBuffer(1500);
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line);
				buffer.append('\n');
				line = reader.readLine();
			}

			FontData fontData = JFaceResources.getFontRegistry().getFontData(
					JFaceResources.DIALOG_FONT)[0];
			return HTMLPrinter.convertTopLevelFont(buffer.toString(), fontData);
		} catch (IOException ex) {
			Logger.logException("Error while loading style sheets", ex);
			return ""; //$NON-NLS-1$
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public static void startPage(StringBuffer buf, String title,
			ImageDescriptor descriptor) {
		int imageWidth = 16;
		int imageHeight = 16;
		int labelLeft = 20;
		int labelTop = 2;

		// buf.append("<p>");
		buf.append("<div style='word-wrap: break-word; position: relative; "); //$NON-NLS-1$

		String imageSrcPath = getImageURL(descriptor);
		if (imageSrcPath != null) {
			buf.append("margin-left: ").append(labelLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("padding-top: ").append(labelTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
		}

		buf.append("'>"); //$NON-NLS-1$
		if (imageSrcPath != null) {

			String uri = ""; // HoverLocationListener.TERN_DEFINITION_PROTOCOL;
			buf.append("<a href=\"");
			buf.append(uri);
			buf.append("\" >");

			StringBuffer imageStyle = new StringBuffer(
					"border:none; position: absolute; "); //$NON-NLS-1$
			imageStyle.append("width: ").append(imageWidth).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("height: ").append(imageHeight).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("left: ").append(-labelLeft - 1).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$

			// hack for broken transparent PNG support in IE 6, see
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=223900 :
			buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); //$NON-NLS-1$
			String tooltip = "alt='" + "" + "' "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<span ").append(tooltip).append("style=\"").append(imageStyle). //$NON-NLS-1$ //$NON-NLS-2$
					append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='").append(imageSrcPath).append("')\"></span>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("<![endif]><![endif]-->\n"); //$NON-NLS-1$

			buf.append("<!--[if !IE]>-->\n"); //$NON-NLS-1$
			buf.append("<img ").append(tooltip).append("style='").append(imageStyle).append("' src='").append(imageSrcPath).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			buf.append("<!--<![endif]-->\n"); //$NON-NLS-1$
			buf.append("<!--[if gte IE 7]>\n"); //$NON-NLS-1$
			buf.append("<img ").append(tooltip).append("style='").append(imageStyle).append("' src='").append(imageSrcPath).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			buf.append("<![endif]-->\n"); //$NON-NLS-1$
			// if (element != null) {

			buf.append("</a>"); //$NON-NLS-1$

			// }
		}
		buf.append(title);

		buf.append("</div>"); //$NON-NLS-1$
		buf.append("<hr />");
	}

	private static String getImageURL(ImageDescriptor descriptor) {
		if (descriptor == null) {
			return null;
		}
		String imageName = null;
		URL imageUrl = JSONEditorPluginImageHelper.getInstance().getImageURL(
				descriptor);
		if (imageUrl != null) {
			imageName = imageUrl.toExternalForm();
		}
		return imageName;
	}

	public static void endPage(StringBuffer buffer) {
		HTMLPrinter
				.insertPageProlog(buffer, 0, HTMLJSONPrinter.getStyleSheet());
		HTMLPrinter.addPageEpilog(buffer);
	}
}
