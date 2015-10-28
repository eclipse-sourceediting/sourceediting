/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class XSDPreferenceInitializer extends AbstractPreferenceInitializer {
	
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(XSDEditorPlugin.getDefault().getBundle().getSymbolicName());
		// formatting preferences
	    node.putBoolean(XSDEditorPlugin.CONST_SHOW_INHERITED_CONTENT, false);
	    node.put(XSDEditorPlugin.CONST_XSD_DEFAULT_PREFIX_TEXT, "xsd"); //$NON-NLS-1$
	    node.putBoolean(XSDEditorPlugin.CONST_XSD_LANGUAGE_QUALIFY, false);
	    node.put(XSDEditorPlugin.DEFAULT_PAGE, XSDEditorPlugin.DESIGN_PAGE);
	    node.put(XSDEditorPlugin.CONST_DEFAULT_TARGET_NAMESPACE, XSDEditorPlugin.DEFAULT_TARGET_NAMESPACE);
	    node.putBoolean(XSDEditorPlugin.CONST_SHOW_EXTERNALS, false);
	    node.putBoolean(XSDEditorPlugin.CONST_XSD_IMPORT_CLEANUP, false);
	    node.putBoolean(XSDEditorPlugin.CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG, true);
	    
	    //Even the last item in the list must contain a trailing List separator
	    node.put(XSDEditorPlugin.CONST_PREFERED_BUILT_IN_TYPES,     		
	    		"boolean"+ XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"date" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"dateTime" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"double" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"float" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"hexBinary" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"int" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"string" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"time" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR); //$NON-NLS-1$
	}

}
