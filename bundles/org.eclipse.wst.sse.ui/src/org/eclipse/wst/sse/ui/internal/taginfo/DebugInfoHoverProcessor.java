/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.internal.taginfo;



import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;

/**
 * Provides debug hover help
 * 
 * @author amywu
 * @see org.eclipse.jface.text.ITextHover
 */
public class DebugInfoHoverProcessor implements ITextHover {
	public static final String TRACEFILTER = "debuginfohover"; // $NON-NLS-1$ //$NON-NLS-1$
	private static final String EDITOR_PLUGIN_ID = "org.eclipse.wst.sse.ui"; // $NON-NLS-1$ //$NON-NLS-1$
	protected IPreferenceStore fPreferenceStore = null;

	public DebugInfoHoverProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer viewer, IRegion hoverRegion) {
		if ((hoverRegion == null) || (viewer == null) || (viewer.getDocument() == null))
			return null;

		String displayText = null;
		int offset = hoverRegion.getOffset();

		ITypedRegion region = viewer.getDocument().getDocumentPartitioner().getPartition(offset);
		if (region != null) {
			displayText = region.getType();
		}

		return displayText;
	}

	/**
	 * @deprecated if enabled flag is false, dont call getHoverInfo in the first place if true, use getHoverInfo(ITextViewer, int)
	 */
	public String getHoverInfo(ITextViewer viewer, IRegion hoverRegion, boolean enabled) {
		if (enabled)
			return getHoverInfo(viewer, hoverRegion);
		else
			return null;
	}

	/**
	 * Returns the region to hover the text over based on the offset.
	 * @param textViewer
	 * @param offset
	 * 
	 * @return IRegion region to hover over if offset is
	 * not over invalid whitespace.  otherwise, returns <code>null</code>
	 * 
	 * @see ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if ((textViewer == null) || (textViewer.getDocument() == null))
			return null;

		ITypedRegion region = textViewer.getDocument().getDocumentPartitioner().getPartition(offset);
		return region;
	}

	/**
	 * @deprecated if enabled flag is false, dont call getHoverRegion in the first place if true, use getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset, boolean enabled) {
		if (enabled)
			return getHoverRegion(textViewer, offset);
		else
			return null;
	}

	/**
	 * @deprecated hover processor no longer preference store-dependent
	 */
	protected IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null) {
			fPreferenceStore = ((AbstractUIPlugin) Platform.getPlugin(EDITOR_PLUGIN_ID)).getPreferenceStore();
		}
		return fPreferenceStore;
	}

	/**
	 * @deprecated this method will not longer be used once other deprecated methods are removed
	 */
	private IModelManager getModelManager() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}

	/**
	 * @deprecated hover processor should not be preference store-dependent
	 */
	protected boolean isHoverHelpEnabled(ITextViewer viewer) {
		// returning false because this method is deprecated
		return false;
	}
}
