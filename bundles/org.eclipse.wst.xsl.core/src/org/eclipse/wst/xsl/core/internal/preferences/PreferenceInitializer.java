/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.preferences;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.xsl.core.ValidationPreferences;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;

/**
 * Preferences initializer for XSL core preferences.
 * 
 * @author Doug Satchwell
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(XSLCorePlugin.getDefault().getBundle().getSymbolicName());
		node.putInt(ValidationPreferences.MAX_ERRORS, 100);
		node.putInt(ValidationPreferences.MISSING_PARAM, IMarker.SEVERITY_WARNING);
		node.putInt(ValidationPreferences.XPATHS, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.CALL_TEMPLATES, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.EMPTY_PARAM, IMarker.SEVERITY_WARNING);
		node.putInt(ValidationPreferences.MISSING_INCLUDE, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.CIRCULAR_REF, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.TEMPLATE_CONFLICT, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.NAME_ATTRIBUTE_MISSING, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.NAME_ATTRIBUTE_EMPTY, IMarker.SEVERITY_ERROR);
		node.putInt(ValidationPreferences.DUPLICATE_PARAMETER, IMarker.SEVERITY_ERROR);
	}
}
