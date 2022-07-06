/*******************************************************************************
 * Copyright (c) 2001, 2022 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.sse.core.internal.tasks.TaskTagPreferenceKeys;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences taskTagDefaults = DefaultScope.INSTANCE.getNode(TaskTagPreferenceKeys.TASK_TAG_NODE);
		taskTagDefaults.putBoolean(TaskTagPreferenceKeys.TASK_TAG_ENABLE, false);
		taskTagDefaults.put(TaskTagPreferenceKeys.TASK_TAG_TAGS, "BUG,NOTE,FIXME,HACK,TODO,XXX"); //$NON-NLS-1$
		// 2,1,0 = high,medium,low
		taskTagDefaults.put(TaskTagPreferenceKeys.TASK_TAG_PRIORITIES, "1,0,2,1,1,1"); //$NON-NLS-1$
		taskTagDefaults.put(TaskTagPreferenceKeys.TASK_TAG_CONTENTTYPES_IGNORED, ""); //$NON-NLS-1$
	}
}
