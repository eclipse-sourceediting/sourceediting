/*******************************************************************************
 * Copyright (c) 2009, 2017 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver - initial API based off wst.xml.ui.CustomTemplateProposal
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.hander.tests;

import org.eclipse.wst.xml.xpath.ui.internal.handler.XPathProcessorHandler;

/**
 * This is a stub that exposes the protected toggle state method from the
 * supper class for testing purposes.
 * 
 * @author dcarver
 *
 */
public class StubXPathProcessorHandler extends XPathProcessorHandler {

	@Override
	public void toggleState(String xpathState) {
		super.toggleState(xpathState);
	}
	
}
