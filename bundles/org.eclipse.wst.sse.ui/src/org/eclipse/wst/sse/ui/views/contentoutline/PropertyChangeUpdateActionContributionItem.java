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
package org.eclipse.wst.sse.ui.views.contentoutline;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * A listerner 
 */
public class PropertyChangeUpdateActionContributionItem extends ActionContributionItem {

	private class PreferenceUpdateListener implements IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(fProperty)) {
				((IUpdate) getAction()).update();
			}
		}
	}

	private IPropertyChangeListener fListener = null;

	protected String fProperty = null;
	private IPreferenceStore fStore;

	public PropertyChangeUpdateActionContributionItem(PropertyChangeUpdateAction action) {
		super(action);
		fProperty = action.getPreferenceKey();
		fStore = action.getPreferenceStore();
		fListener = new PreferenceUpdateListener();
		connect();
	}

	public void connect() {
		fStore.addPropertyChangeListener(fListener);
	}

	public void disconnect() {
		if (fStore != null)
			fStore.removePropertyChangeListener(fListener);
	}

	public void dispose() {
		super.dispose();
		disconnect();
		fProperty = null;
		fStore = null;
	}

	public String toString() {
		if (getAction().getId() != null)
			return super.toString();
		else
			return getClass().getName() + "(text=" + getAction().getText() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
