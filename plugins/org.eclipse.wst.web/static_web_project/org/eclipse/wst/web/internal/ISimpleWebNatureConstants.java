/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public interface ISimpleWebNatureConstants {
	java.lang.String STATIC_CONTEXT_ROOT = "/"; //$NON-NLS-1$
	String STATIC_NATURE_ID = "org.eclipse.wst.web" + ".StaticWebNature"; //$NON-NLS-1$ //$NON-NLS-2$
	int STATIC_WEB_PROJECT = 0;
	
	String CSS_DIRECTORY = "theme"; //$NON-NLS-1$
	String INFO_DIRECTORY = "WEB-INF"; //$NON-NLS-1$
	String LIBRARY_DIRECTORY = "lib"; //$NON-NLS-1$
	String WEB_MODULE_DIRECTORY_V4 = "webApplication";//$NON-NLS-1$
	String WEBSETTINGS_FILE_NAME = ".j2ee"; //$NON-NLS-1$
	String WEB_MODULE_DIRECTORY_ = "Web Content";//$NON-NLS-1$
	IPath WEB_MODULE_PATH_V4 = new Path(WEB_MODULE_DIRECTORY_V4);
	IPath WEB_MODULE_PATH_ = new Path(WEB_MODULE_DIRECTORY_);
}
