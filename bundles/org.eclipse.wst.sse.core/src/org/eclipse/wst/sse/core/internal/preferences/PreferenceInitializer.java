/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.sse.core.internal.tasks.TaskTagPreferenceKeys;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IEclipsePreferences taskTagDefaults = new DefaultScope().getNode(TaskTagPreferenceKeys.TASK_TAG_NODE);
		taskTagDefaults.putBoolean(TaskTagPreferenceKeys.TASK_TAG_ENABLE, false);
		taskTagDefaults.put(TaskTagPreferenceKeys.TASK_TAG_TAGS, "TODO,FIXME,XXX"); //$NON-NLS-1$
		taskTagDefaults.put(TaskTagPreferenceKeys.TASK_TAG_PRIORITIES, "1,2,1"); //$NON-NLS-1$
		taskTagDefaults.put(TaskTagPreferenceKeys.TASK_TAG_CONTENTTYPES_IGNORED, ""); //$NON-NLS-1$
	}
}
