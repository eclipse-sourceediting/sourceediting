/*******************************************************************************
 * Copyright (c) 2001, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.dialogs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IDelegateCatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xml.ui.internal.catalog.XMLCatalogMessages;

import com.ibm.icu.text.Collator;

public class XMLCatalogEntryContentProvider implements ITreeContentProvider {

	protected Object[] roots;
	private ICatalog fWorkingUserCatalog;
	private ICatalog fSystemCatalog;
	static final String USER_SPECIFIED_ENTRIES_OBJECT = XMLCatalogMessages.UI_LABEL_USER_SPECIFIED_ENTRIES;
	static final String PLUGIN_SPECIFIED_ENTRIES_OBJECT = XMLCatalogMessages.UI_LABEL_PLUGIN_SPECIFIED_ENTRIES;

	public XMLCatalogEntryContentProvider() {
		roots = new Object[2];

		roots[0] = USER_SPECIFIED_ENTRIES_OBJECT;
		roots[1] = PLUGIN_SPECIFIED_ENTRIES_OBJECT;

		ICatalog defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
		for (int i = 0; i < nextCatalogs.length; i++) {
			INextCatalog catalog = nextCatalogs[i];
			ICatalog referencedCatalog = catalog.getReferencedCatalog();
			if (referencedCatalog != null) {
				if (XMLCorePlugin.SYSTEM_CATALOG_ID.equals(referencedCatalog.getId())) {
					fSystemCatalog = referencedCatalog;
				}
				else if (XMLCorePlugin.USER_CATALOG_ID.equals(referencedCatalog.getId())) {
					fWorkingUserCatalog = referencedCatalog;
				}
			}
		}
	}

	public boolean isRoot(Object object) {
		return (object instanceof String) || (object instanceof INextCatalog);
	}

	public Object[] getElements(Object element) {
		return roots;
	}

	public Object[] getChildren(Object parentElement) {
		Object[] result = new Object[0];
		if (parentElement == roots[0]) {
			result = getChildrenHelper(fWorkingUserCatalog);
		}
		else if (parentElement == roots[1]) {
			result = getChildrenHelper(fSystemCatalog);
		}
		else if (parentElement instanceof INextCatalog) {
			ICatalog nextCatalog = ((INextCatalog) parentElement).getReferencedCatalog();
			result = getChildrenHelper(nextCatalog);
		}
		else if (parentElement instanceof IDelegateCatalog) {
			ICatalog nextCatalog = ((IDelegateCatalog) parentElement).getReferencedCatalog();
			result = getChildrenHelper(nextCatalog);
		}
		return result;
	}

	protected Object[] getChildrenHelper(ICatalog catalog) {

		ICatalogEntry[] entries = catalog.getCatalogEntries();
		if (entries.length > 0) {
			Comparator comparator = new Comparator() {
				public int compare(Object o1, Object o2) {
					int result = 0;
					if ((o1 instanceof ICatalogEntry) && (o2 instanceof ICatalogEntry)) {
						ICatalogEntry entry1 = (ICatalogEntry) o1;
						ICatalogEntry entry2 = (ICatalogEntry) o2;
						result = Collator.getInstance().compare(entry1.getKey(), entry2.getKey());
					}
					return result;
				}
			};
			Arrays.sort(entries, comparator);
		}
		Vector result = new Vector();
		result.addAll(Arrays.asList(entries));
		result.addAll(Arrays.asList(catalog.getRewriteEntries()));
		result.addAll(Arrays.asList(catalog.getSuffixEntries()));
		result.addAll(Arrays.asList(catalog.getDelegateCatalogs()));
		INextCatalog[] nextCatalogs = catalog.getNextCatalogs();
		List nextCatalogsList = Arrays.asList(nextCatalogs);
		result.addAll(nextCatalogsList);

		return result.toArray(new ICatalogElement[result.size()]);
	}

	public Object getParent(Object element) {
		return (element instanceof String) ? null : USER_SPECIFIED_ENTRIES_OBJECT;
	}

	public boolean hasChildren(Object element) {
		return isRoot(element) ? getChildren(element).length > 0 : false;
	}

	public void dispose() {
		// nothing to dispose
	}

	public void inputChanged(Viewer viewer, Object old, Object newobj) {
		// ISSUE: seems we should do something here
	}

	public boolean isDeleted(Object object) {
		return false;
	}

}