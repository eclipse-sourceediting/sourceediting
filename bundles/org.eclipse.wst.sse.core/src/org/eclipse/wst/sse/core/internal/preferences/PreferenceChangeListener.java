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
package org.eclipse.wst.sse.core.internal.preferences;

/**
 * @deprecated This used to be used by our own PreferenceManager but since
 *             that class is no longer needed, this class should also no
 *             longer be needed. Use the base's IPreferenceChangeListener or
 *             IPropertyChangeListener instead.
 */
public interface PreferenceChangeListener {

	void preferencesChanged();
}
