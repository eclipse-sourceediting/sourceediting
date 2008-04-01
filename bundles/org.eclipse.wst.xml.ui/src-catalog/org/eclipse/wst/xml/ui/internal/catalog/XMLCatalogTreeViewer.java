/*******************************************************************************
 * Copyright (c) 2002, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.internal.catalog;

import com.ibm.icu.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;


public class XMLCatalogTreeViewer extends TreeViewer {
	protected static Image xmlCatalogImage = ImageFactory.INSTANCE.getImage("icons/obj16/xmlcatalog_obj.gif"); //$NON-NLS-1$
	protected static Image errorImage = ImageFactory.INSTANCE.getImage("icons/ovr16/error-overlay.gif"); //$NON-NLS-1$
	protected static Image entryImage = ImageFactory.INSTANCE.getImage("icons/obj16/entry_obj.png"); //$NON-NLS-1$
	protected static Image nextCatalogImage = ImageFactory.INSTANCE.getImage("icons/obj16/nextCatalog_obj.gif"); //$NON-NLS-1$

	protected static String ERROR_STATE_KEY = "errorstatekey"; //$NON-NLS-1$

	protected ICatalog fWorkingUserCatalog;
	protected ICatalog fSystemCatalog;

	public static String USER_SPECIFIED_ENTRIES_OBJECT = XMLCatalogMessages.UI_LABEL_USER_SPECIFIED_ENTRIES;
	public static String PLUGIN_SPECIFIED_ENTRIES_OBJECT = XMLCatalogMessages.UI_LABEL_PLUGIN_SPECIFIED_ENTRIES;

	public XMLCatalogTreeViewer(Composite parent, ICatalog workingUserCatalog, ICatalog systemCatalog) {
		super(parent, SWT.BORDER);
		this.fWorkingUserCatalog = workingUserCatalog;
		this.fSystemCatalog = systemCatalog;

		setContentProvider(new CatalogEntryContentProvider());
		setLabelProvider(new CatalogEntryLabelProvider());
	}

	public void setFilterExtensions(String[] extensions) {
		resetFilters();
		addFilter(new XMLCatalogTableViewerFilter(extensions));
	}

	public class CatalogEntryLabelProvider extends LabelProvider {
		protected HashMap imageTable = new HashMap();

		public String getText(Object object) {
			String result = null;
			if (object instanceof ICatalogEntry) {
				ICatalogEntry catalogEntry = (ICatalogEntry) object;
				result = catalogEntry.getKey();
				// result = URIHelper.removePlatformResourceProtocol(result);
			}
			else if (object instanceof INextCatalog) {
				INextCatalog nextCatalog = (INextCatalog) object;
				// result = nextCatalog.getCatalogLocation();
				result = URIUtils.convertURIToLocation(nextCatalog.getCatalogLocation());
				if (nextCatalog.getCatalogLocation().startsWith("file:")) {
					result += " (" + XMLCatalogMessages.UI_LABEL_FILE_SYSTEM_RESOURCE + ")";
				}
				else if (nextCatalog.getCatalogLocation().startsWith("platform:")) {
					result += " (" + XMLCatalogMessages.UI_LABEL_PLATFORM_RESOURCE + ")";
				}
			}
			return result != null ? result : object.toString();
		}

		public Image getImage(Object object) {
			Image result = null;
			Image base = null;
			if (object instanceof String) {
				result = xmlCatalogImage;
			}
			else if (object instanceof ICatalogEntry) {
				ICatalogEntry catalogEntry = (ICatalogEntry) object;
				String uri = catalogEntry.getURI();
				IEditorRegistry er = PlatformUI.getWorkbench().getEditorRegistry();
				ImageDescriptor imageDescriptor = er.getImageDescriptor(uri);
				Image image = (Image) imageTable.get(imageDescriptor);
				if (image == null) {
					image = imageDescriptor.createImage();
					imageTable.put(imageDescriptor, image);
				}
				base = image;

				if (base != null) {
					if (URIHelper.isReadableURI(uri, true)) {
						result = base;
					}
					else {
						result = ImageFactory.INSTANCE.createCompositeImage(base, errorImage, ImageFactory.BOTTOM_LEFT);
					}
				}
			}
			else if (object instanceof INextCatalog) {
				// TODO: add image to the imageTable and add error overlay if
				// next catalog URI is not readable
				result = nextCatalogImage;
			}
			return result;
		}

		public void dispose() {
			super.dispose();
			for (Iterator it = imageTable.values().iterator(); it.hasNext();) {
				((Image) it.next()).dispose();
			}
		}
	}


	public class CatalogEntryContentProvider implements ITreeContentProvider {
		protected Object[] roots;

		public CatalogEntryContentProvider() {
			roots = new Object[2];
			roots[0] = USER_SPECIFIED_ENTRIES_OBJECT;
			roots[1] = PLUGIN_SPECIFIED_ENTRIES_OBJECT;
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
			return result;
		}

		protected Object[] getChildrenHelper(ICatalog catalog) {
			// TODO add entries from the nested catalogs

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


	public void menuAboutToShow(IMenuManager menuManager) {
		Action action = new Action("hello") { //$NON-NLS-1$
			public void run() {
				System.out.println("run!"); //$NON-NLS-1$
			}
		};
		menuManager.add(action);
	}


	class XMLCatalogTableViewerFilter extends ViewerFilter {
		protected String[] extensions;

		public XMLCatalogTableViewerFilter(String[] extensions1) {
			this.extensions = extensions1;
		}

		public boolean isFilterProperty(Object element, Object property) {
			return false;
		}

		public boolean select(Viewer viewer, Object parent, Object element) {
			boolean result = false;
			if (element instanceof ICatalogEntry) {
				ICatalogEntry catalogEntry = (ICatalogEntry) element;
				for (int i = 0; i < extensions.length; i++) {
					if (catalogEntry.getURI().endsWith(extensions[i])) {
						result = true;
						break;
					}
				}
			}
			else if (element.equals(XMLCatalogTreeViewer.PLUGIN_SPECIFIED_ENTRIES_OBJECT) || element.equals(XMLCatalogTreeViewer.USER_SPECIFIED_ENTRIES_OBJECT)) {
				return true;
			}
			return result;
		}
	}


}
