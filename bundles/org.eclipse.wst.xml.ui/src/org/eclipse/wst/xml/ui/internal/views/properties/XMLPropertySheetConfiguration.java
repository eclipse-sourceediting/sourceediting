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
package org.eclipse.wst.xml.ui.internal.views.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.sse.ui.internal.provisional.views.properties.StructuredPropertySheetConfiguration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManagerListener;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class XMLPropertySheetConfiguration extends StructuredPropertySheetConfiguration {
	public class CMDocumentManagerListenerImpl implements CMDocumentManagerListener {
		public void cacheCleared(CMDocumentCache cache) {
			// nothing to do
		}

		public void cacheUpdated(CMDocumentCache cache, final String uri, int oldStatus, int newStatus, CMDocument cmDocument) {
			if (newStatus == CMDocumentCache.STATUS_LOADED || newStatus == CMDocumentCache.STATUS_ERROR) {
				refreshPages();
			}
		}

		public void propertyChanged(CMDocumentManager cmDocumentManager, String propertyName) {
			if (cmDocumentManager.getPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD)) {
				refreshPages();
			}
		}

		private void refreshPages() {
			PropertySheetPage[] pages = getPages();
			for (int i = 0; i < pages.length; i++) {
				getPropertiesRefreshJob().addPropertySheetPage(pages[i]);
				getPropertiesRefreshJob().schedule(PropertiesRefreshJob.UPDATE_DELAY);
			}
		}
	}

	private class PropertiesRefreshJob extends UIJob {
		public static final int UPDATE_DELAY = 200;

		private List propertySheetPages = null;

		public PropertiesRefreshJob() {
			super(XMLUIMessages.JFaceNodeAdapter_1);
			setSystem(true);
			setPriority(Job.SHORT);
			propertySheetPages = new ArrayList(1);
		}

		void addPropertySheetPage(PropertySheetPage page) {
			propertySheetPages.add(page);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			List pages = propertySheetPages;
			propertySheetPages = new ArrayList(1);

			for (int i = 0; i < propertySheetPages.size(); i++) {
				PropertySheetPage page = (PropertySheetPage) propertySheetPages.get(i);
				if (page.getControl() != null && !page.getControl().isDisposed()) {
					page.refresh();
				}
			}

			return Status.OK_STATUS;
		}
	}

	private CMDocumentManagerListenerImpl fCMDocumentManagerListener = null;
	private PropertiesRefreshJob fPropertiesRefreshJob = null;
	private Set fPropertySheetPages = null;
	private CMDocumentManager[] fSelectedCMDocumentManagers;

	public XMLPropertySheetConfiguration() {
		super();
		fPropertySheetPages = new HashSet(2);
		fSelectedCMDocumentManagers = new CMDocumentManager[0];
		// register for CMDocumentManager events
		fCMDocumentManagerListener = new CMDocumentManagerListenerImpl();
	}

	protected IPropertySourceProvider createPropertySourceProvider(IPropertySheetPage page) {
		fPropertySheetPages.add(page);
		return super.createPropertySourceProvider(page);
	}

	public PropertySheetPage[] getPages() {
		PropertySheetPage[] pages = (PropertySheetPage[]) fPropertySheetPages.toArray(new PropertySheetPage[fPropertySheetPages.size()]);
		return pages;
	}

	public PropertiesRefreshJob getPropertiesRefreshJob() {
		if (fPropertiesRefreshJob != null) {
			fPropertiesRefreshJob = new PropertiesRefreshJob();
		}
		return fPropertiesRefreshJob;
	}

	public ISelection getSelection(IWorkbenchPart selectingPart, ISelection selection) {
		// On Attr nodes, select the owner Element. On Text nodes, select the
		// parent Element.
		ISelection preferredSelection = selection;
		if (selection instanceof IStructuredSelection) {
			for (int i = 0; i < fSelectedCMDocumentManagers.length; i++) {
				fSelectedCMDocumentManagers[i].removeListener(fCMDocumentManagerListener);
			}
			Set managers = new HashSet(1);

			IStructuredSelection structuredSel = (IStructuredSelection) selection;

			List inputList = new ArrayList(structuredSel.toList());
			for (int i = 0; i < inputList.size(); i++) {
				Object inode = inputList.get(i);
				if (inode instanceof Node) {
					Node node = (Node) inputList.get(i);
					// replace Attribute Node with its owner
					if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
						inputList.set(i, ((Attr) node).getOwnerElement());
						ModelQuery query = ModelQueryUtil.getModelQuery(((Attr) node).getOwnerElement().getOwnerDocument());
						if (query != null) {
							Object o = query.getCMDocumentManager();
							if (o != null) {
								managers.add(o);
							}
						}
					}
					// replace Text Node with its parent
					else if ((node.getNodeType() == Node.TEXT_NODE || (node.getNodeType() == Node.CDATA_SECTION_NODE)) && node.getParentNode() != null) {
						inputList.set(i, node.getParentNode());
						ModelQuery query = ModelQueryUtil.getModelQuery(node.getParentNode().getOwnerDocument());
						if (query != null) {
							Object o = query.getCMDocumentManager();
							if (o != null) {
								managers.add(o);
							}
						}
					}
				}
			}

			fSelectedCMDocumentManagers = (CMDocumentManager[]) managers.toArray(new CMDocumentManager[managers.size()]);
			for (int i = 0; i < fSelectedCMDocumentManagers.length; i++) {
				fSelectedCMDocumentManagers[i].addListener(fCMDocumentManagerListener);
			}

			preferredSelection = new StructuredSelection(inputList);
		}
		return preferredSelection;
	}

	public void unconfigure() {
		super.unconfigure();
		for (int i = 0; i < fSelectedCMDocumentManagers.length; i++) {
			fSelectedCMDocumentManagers[i].removeListener(fCMDocumentManagerListener);
		}
		fPropertySheetPages.clear();
	}
}
