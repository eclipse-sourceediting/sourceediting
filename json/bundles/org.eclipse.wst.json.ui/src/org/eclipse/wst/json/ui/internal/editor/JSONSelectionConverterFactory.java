/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.editor.DOMSelectionConverterFactory
 *                                           modified in order to process JSON Objects.           
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.ui.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.SelectionConverter;

/**
 * <p>
 * Factory to adapt from {@link IJSONModel} to {@link SelectionConverter}
 * </p>
 */
public class JSONSelectionConverterFactory implements IAdapterFactory {

	/** the list of classes this factory can adapt to */
	private static final Class[] ADAPTER_LIST = new Class[] { SelectionConverter.class };

	/** the adapted class */
	private static final Object selectionConverter = new JSONSelectionConverter();

	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public JSONSelectionConverterFactory() {
	}

	/**
	 * <p>
	 * Adapts {@link IJSONModel} to {@link SelectionConverter}
	 * </p>
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 *      java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object adapter = null;
		if (adaptableObject instanceof IJSONModel
				&& SelectionConverter.class.equals(adapterType)) {
			adapter = selectionConverter;
		}
		return adapter;
	}

	/**
	 * <p>
	 * Adapts to {@link SelectionConverter}
	 * </p>
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

	/**
	 * <p>
	 * {@link SelectionConverter} specific to JSON
	 * </p>
	 */
	private static class JSONSelectionConverter extends SelectionConverter {
		/**
		 * <p>
		 * Default constructor
		 * </p>
		 */
		public JSONSelectionConverter() {
		}

		/**
		 * @see org.eclipse.wst.sse.ui.SelectionConverter#getElements(org.eclipse.wst.sse.core.internal.provisional.IStructuredModel,
		 *      int, int)
		 */
		public Object[] getElements(IStructuredModel model, int start, int end) {
			Object[] localSelectedStructures = null;
			if (model != null) {
				IndexedRegion region = model.getIndexedRegion(start);

				if (region instanceof IJSONObject || region instanceof IJSONDocument) {
					try {
						String selection = model.getStructuredDocument().get(
								start, end);
						int whitespaceLength = beginingWhitespaceLength(selection);
						region = model.getIndexedRegion(start
								+ whitespaceLength + 1);
					} catch (BadLocationException e) {
						// should never be able to happen
						Logger.logException(
								"Bad location on selection, this should never happen",
								e);
					}
				}

				if (region != null) {
					// if single selection spans selected region
					// else multiple selection
					if (end <= region.getEndOffset()) {
						// single selection
						localSelectedStructures = new Object[1];
						localSelectedStructures[0] = region;
					} else {
						int maxLength = model.getStructuredDocument()
								.getLength();
						Set structures = new HashSet();
						while (region != null && region.getEndOffset() <= end
								&& region.getEndOffset() <= maxLength
								&& !structures.contains(region)) {
							structures.add(region);

							// use the JSON model to find the next sibling
							boolean foundNextSibling = false;
							if (region instanceof IJSONNode) {
								IJSONNode node = ((IJSONNode) region)
										.getNextSibling();
								if (node instanceof IndexedRegion) {
									region = (IndexedRegion) node;
									foundNextSibling = true;
								}
							}

							if (!foundNextSibling) {
								region = model.getIndexedRegion(region
										.getEndOffset() + 1);
							}
						}
						localSelectedStructures = structures.toArray();
					}
				}
			}
			if (localSelectedStructures == null) {
				localSelectedStructures = new Object[0];
			}
			return localSelectedStructures;
		}

	}

	/**
	 * @param s
	 *            find the length of the whitespace preceding this
	 *            {@link String}
	 * @return the length of the whitespace preceding the given {@link String}
	 */
	private static int beginingWhitespaceLength(String s) {
		int length = 0;

		for (int i = 0; i < s.length() && length == 0; ++i) {
			if (!Character.isWhitespace(s.charAt(i))) {
				length = i;
			}
		}

		return length;
	}
}
