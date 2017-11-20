/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory
 *                                           modified in order to process JSON Objects.                    
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.contentoutline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.ui.internal.ColorTypesHelper;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.style.IStyleConstantsJSON;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;

/**
 * An adapter factory to create JFaceNodeAdapters. Use this adapter factory with
 * a JFaceAdapterContentProvider to display JSON nodes in a tree.
 */
public class JFaceNodeAdapterFactory extends AbstractAdapterFactory implements
		IJFaceNodeAdapterFactory {

	private class PropertyChangeListener implements IPropertyChangeListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org
		 * .eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			// have to do it this way so others can override the method
			handlePropertyChange(event);
		}

	}

	private PropertyChangeListener fPreferenceListener = new PropertyChangeListener();

	// public class CMDocumentManagerListenerImpl implements
	// CMDocumentManagerListener {
	// private static final int UPDATE_DELAY = 200;
	//
	// public void cacheCleared(CMDocumentCache cache) {
	// // nothing to do
	// }
	//
	// public void cacheUpdated(CMDocumentCache cache, final String uri, int
	// oldStatus, int newStatus, CMDocument cmDocument) {
	// if ((newStatus == CMDocumentCache.STATUS_LOADED) || (newStatus ==
	// CMDocumentCache.STATUS_ERROR)) {
	// refreshViewers();
	// }
	// }
	//
	// public void propertyChanged(CMDocumentManager cmDocumentManager, String
	// propertyName) {
	// if
	// (cmDocumentManager.getPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD))
	// {
	// refreshViewers();
	// }
	// }
	//
	// private void refreshViewers() {
	// Object[] listeners = getListeners().toArray();
	// for (int i = 0; i < listeners.length; i++) {
	// if (listeners[i] instanceof StructuredViewer) {
	// final StructuredViewer viewer = (StructuredViewer) listeners[i];
	// Job refresh = new UIJob(XMLUIMessages.refreshoutline_0) {
	// public IStatus runInUIThread(IProgressMonitor monitor) {
	// Control refreshControl = viewer.getControl();
	// if ((refreshControl != null) && !refreshControl.isDisposed()) {
	// viewer.refresh(true);
	// }
	// return Status.OK_STATUS;
	// }
	// };
	// refresh.setSystem(true);
	// refresh.setPriority(Job.SHORT);
	// refresh.schedule(UPDATE_DELAY);
	// }
	// else if (listeners[i] instanceof Viewer) {
	// final Viewer viewer = (Viewer) listeners[i];
	// Job refresh = new UIJob(XMLUIMessages.refreshoutline_0) {
	// public IStatus runInUIThread(IProgressMonitor monitor) {
	// Control refreshControl = viewer.getControl();
	// if ((refreshControl != null) && !refreshControl.isDisposed()) {
	// viewer.refresh();
	// }
	// return Status.OK_STATUS;
	// }
	// };
	// refresh.setSystem(true);
	// refresh.setPriority(Job.SHORT);
	// refresh.schedule(UPDATE_DELAY);
	// }
	// }
	// }
	// }
	//
	// private CMDocumentManager cmDocumentManager;
	// private CMDocumentManagerListenerImpl fCMDocumentManagerListener = null;
	/**
	 * This keeps track of all the listeners.
	 */
	private Set fListeners = new HashSet();

	protected INodeAdapter singletonAdapter;

	private Map<String, Styler> stylers;

	public JFaceNodeAdapterFactory() {
		this(IJFaceNodeAdapter.class, true);
	}

	public JFaceNodeAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public synchronized void addListener(Object listener) {
		fListeners.add(listener);
	}

	public INodeAdapterFactory copy() {
		return new JFaceNodeAdapterFactory(getAdapterKey(),
				isShouldRegisterAdapter());
	}

	/**
	 * Create a new JFace adapter for the JSON node passed in
	 */
	@Override
	protected INodeAdapter createAdapter(INodeNotifier node) {
		if (singletonAdapter == null) {
			// create the JFaceNodeAdapter
			singletonAdapter = new JFaceNodeAdapter(this);
			initAdapter(singletonAdapter, node);
		}
		return singletonAdapter;
	}

	/**
	 * returns "copy" so no one can modify our list. It is a shallow copy.
	 */
	public synchronized Collection getListeners() {
		return new ArrayList(fListeners);
	}

	protected void initAdapter(INodeAdapter adapter, INodeNotifier node) {
		// Assert.isTrue(cmDocumentManager == null);
		// Assert.isTrue(fCMDocumentManagerListener == null);
		//
		// // register for CMDocumentManager events
		// ModelQueryAdapter mqadapter = (ModelQueryAdapter)
		// node.getAdapterFor(ModelQueryAdapter.class);
		// if (mqadapter != null) {
		// ModelQuery mquery = mqadapter.getModelQuery();
		// if ((mquery != null) && (mquery.getCMDocumentManager() != null)) {
		// cmDocumentManager = mquery.getCMDocumentManager();
		// fCMDocumentManagerListener = new CMDocumentManagerListenerImpl();
		// cmDocumentManager.addListener(fCMDocumentManagerListener);
		// }
		// }
	}

	public void release() {
		// deregister from CMDocumentManager events
		// if ((cmDocumentManager != null) && (fCMDocumentManagerListener !=
		// null)) {
		// cmDocumentManager.removeListener(fCMDocumentManagerListener);
		// }
		unRegisterPreferenceManager();
		fListeners.clear();
		if (singletonAdapter != null
				&& singletonAdapter instanceof JFaceNodeAdapter) {
			RefreshStructureJob refreshJob = ((JFaceNodeAdapter) singletonAdapter).fRefreshJob;
			if (refreshJob != null) {
				refreshJob.cancel();
			}
		}
	}

	public synchronized void removeListener(Object listener) {
		fListeners.remove(listener);
	}

	private void handlePropertyChange(PropertyChangeEvent event) {
		ColorTypesHelper.getNewStyle(event);
	}

	private void registerPreferenceManager() {
		IPreferenceStore pref = getColorPreferences();
		if (pref != null) {
			pref.addPropertyChangeListener(fPreferenceListener);
		}
	}

	private void unRegisterPreferenceManager() {
		IPreferenceStore pref = getColorPreferences();
		if (pref != null) {
			pref.removePropertyChangeListener(fPreferenceListener);
		}
	}

	private void addStyler(String regionType) {
		String colorKey = ColorTypesHelper.getColor(regionType);
		if (getColorPreferences() != null) {
			String prefString = getColorPreferences().getString(colorKey);
			String[] stylePrefs = ColorHelper
					.unpackStylePreferences(prefString);
			if (stylePrefs != null) {
				RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
				RGB background = ColorHelper.toRGB(stylePrefs[1]);

				Styler styler = new RGBStyler(foreground, background);
				getStylers().put(regionType, styler);
			}
		}
	}

	protected Map<String, Styler> getStylers() {
		if (stylers == null) {
			stylers = new HashMap<String, Styler>();
		}
		return stylers;
	}

	public Styler getStyler(String type) {
		if (stylers == null) {
			addStyler(JSONRegionContexts.JSON_VALUE_BOOLEAN);
			addStyler(JSONRegionContexts.JSON_VALUE_NULL);
			addStyler(JSONRegionContexts.JSON_VALUE_NUMBER);
			addStyler(JSONRegionContexts.JSON_VALUE_STRING);
		}
		return stylers.get(type);
	}

	protected IPreferenceStore getColorPreferences() {
		return JSONUIPlugin.getDefault().getPreferenceStore();
	}

	private static class RGBStyler extends StyledString.Styler {
		private final RGB fForegroundColorName;
		private final RGB fBackgroundColorName;

		public RGBStyler(RGB foregroundColorName, RGB backgroundColorName) {
			this.fForegroundColorName = foregroundColorName;
			this.fBackgroundColorName = backgroundColorName;
		}

		public void applyStyles(TextStyle textStyle) {
			if (this.fForegroundColorName != null) {
				textStyle.foreground = EditorUtility
						.getColor(fForegroundColorName);
			}
			if (this.fBackgroundColorName != null)
				textStyle.background = EditorUtility
						.getColor(fBackgroundColorName);
		}
	}

}
