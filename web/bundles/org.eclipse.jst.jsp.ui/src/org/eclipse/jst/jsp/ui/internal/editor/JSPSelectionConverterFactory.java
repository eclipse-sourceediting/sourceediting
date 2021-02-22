/*******************************************************************************
 * Copyright (c) 2008, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.jsp.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.SelectionConverter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.NamedNodeMap;

/**
 * @author nitin
 */
public class JSPSelectionConverterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[]{SelectionConverter.class};

	public static class XMLSelectionConverter extends SelectionConverter {
		public Object[] getElements(IStructuredModel model, int start, int end) {
			Object[] localSelectedStructures = null;
			if (model != null) {
				IDOMNode region = (IDOMNode)model.getIndexedRegion(start);
				if (region != null) {
					if (end <= region.getEndOffset()) {
						// single selection
						localSelectedStructures = new Object[1];
						localSelectedStructures[0] = region;
					} else {
						List<IDOMNode> structures = new ArrayList<>(2);

						IDOMNode node = region;
						while(node != null) {
							structures.add(node);
							IDOMNode next = (IDOMNode)node.getNextSibling();
							if(next == null) {
								next = (IDOMNode)node.getParentNode();
							}
							if(next != null) {
								if(next.getEndOffset() > end) {
									break;
								}
							}
							node = next;
						}
						localSelectedStructures = structures.toArray();
					}
				}
			}
			if (localSelectedStructures == null) {
				localSelectedStructures = new Object[0];
			}

			Object[] objects = localSelectedStructures;
			// narrow single selected Elements into Attrs if possible
			if (objects.length == 1) {
				if (objects[0] instanceof IDOMNode) {
					IDOMNode node = (IDOMNode) objects[0];
					NamedNodeMap attributes = node.getAttributes();
					if (attributes != null) {
						for (int i = 0; i < attributes.getLength(); i++) {
							IDOMAttr attribute = (IDOMAttr) attributes.item(i);
							if (attribute.contains(start) && attribute.contains(end)) {
								objects[0] = attribute;
								break;
							}
						}
					}
				}
			}
			return objects;
		}
		
		IRegion getNameRegion(ITextRegionCollection collection) {
			ITextRegionList regions = collection.getRegions();
			int count = collection.getNumberOfRegions();
			for (int i = 0; i < count; i++) {
				ITextRegion region = regions.get(i);
				if (DOMRegionContext.XML_TAG_NAME.equals(region.getType())) {
					return new Region(collection.getStartOffset(region), region.getTextLength());
				}
				if (DOMJSPRegionContexts.JSP_DIRECTIVE_NAME.equals(region.getType())) {
					return new Region(collection.getStartOffset(region), region.getTextLength());
				}
				if (DOMJSPRegionContexts.JSP_ROOT_TAG_NAME.equals(region.getType())) {
					return new Region(collection.getStartOffset(region), region.getTextLength());
				}
				if (DOMRegionContext.XML_DOCTYPE_NAME.equals(region.getType())) {
					return new Region(collection.getStartOffset(region), region.getTextLength());
				}
				if (DOMRegionContext.XML_ELEMENT_DECL_NAME.equals(region.getType())) {
					return new Region(collection.getStartOffset(region), region.getTextLength());
				}
				if (DOMRegionContext.XML_ATTLIST_DECL_NAME.equals(region.getType())) {
					return new Region(collection.getStartOffset(region), region.getTextLength());
				}
				if (DOMRegionContext.XML_COMMENT_TEXT.equals(region.getType()) || DOMJSPRegionContexts.JSP_COMMENT_TEXT.equals(region.getType())) {
					String commented = collection.getText(region);
					int inset = 0;
					while (inset < Math.min(region.getTextLength(), 240) && Character.isWhitespace(commented.charAt(inset))) {
						inset++;
					}
					return new Region(collection.getStartOffset(region) + inset, 0);
				}
			}
			return null;
		}

		@Override
		public IRegion getSelectionRegion(Object o) {
			if (o instanceof IDOMNode) {
				IDOMNode domNode = ((IDOMNode)o);
				IStructuredDocumentRegion documentRegion = domNode.getFirstStructuredDocumentRegion();
				if (documentRegion == null) {
					return new Region(domNode.getStartOffset(), domNode.getLength());
				}
				IRegion nameRegion = getNameRegion(documentRegion);
				if (nameRegion != null) {
					return nameRegion;
				}
			}
			return super.getSelectionRegion(o);
		}

	}

	private static final Object selectionConverter = new XMLSelectionConverter();

	/**
	 * 
	 */
	public JSPSelectionConverterFactory() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof IDOMModel && SelectionConverter.class.equals(adapterType))
			return (T) selectionConverter;
		return null;
	}

	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
