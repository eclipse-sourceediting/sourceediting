/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API
 *******************************************************************************/

package org.eclipse.wst.xml.xpath.ui.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.wst.xml.xpath.core.XPathCorePlugin;
import org.eclipse.wst.xml.xpath.core.XPathProcessorPreferences;
import org.eclipse.wst.xml.xpath.core.util.XPathCoreHelper;
import org.eclipse.wst.xml.xpath.ui.internal.XPathUIPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class XPathProcessorHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.matchesRadioState(event)) {
			return null;
		}
		
		String xpathState = event.getParameter(RadioState.PARAMETER_ID);

		toggleState(xpathState);
		
		HandlerUtil.updateRadioState(event.getCommand(), xpathState);
		return null;
	}

	protected void toggleState(String xpathState) {
		Preferences osgiPrefs = XPathCoreHelper.getPreferences();
		if (xpathState.equals("xpath10")) {
			osgiPrefs.putBoolean(XPathProcessorPreferences.XPATH_1_0_PROCESSOR, true);
			osgiPrefs.putBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, false);
		} else if (xpathState.equals("xpath2")) {
			osgiPrefs.putBoolean(XPathProcessorPreferences.XPATH_1_0_PROCESSOR, false);
			osgiPrefs.putBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, true);
		}
		
		try {
			osgiPrefs.flush();
		} catch (BackingStoreException ex) {
			XPathUIPlugin.log(ex);
		}
	}

}
