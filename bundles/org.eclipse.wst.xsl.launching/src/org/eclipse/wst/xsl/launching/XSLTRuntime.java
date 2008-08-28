/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;

public class XSLTRuntime
{
	private static byte[] NEXT_ID_LOCK = new byte[0];
	private static byte[] REGISTRY_LOCK = new byte[0];

	private static int lastStandinID;
	
	private static void savePreferences()
	{
		LaunchingPlugin.getDefault().savePluginPreferences();
	}

	private static Preferences getPreferences()
	{
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}

}
