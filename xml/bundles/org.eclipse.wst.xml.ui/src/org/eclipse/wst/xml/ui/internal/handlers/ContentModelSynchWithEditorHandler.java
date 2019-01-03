/*******************************************************************************
 * Copyright (c) 2010 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ContentModelSynchWithEditorHandler extends
		AbstractHandler  {

	public Object execute(ExecutionEvent event) throws ExecutionException {
	     Command command = event.getCommand();
	     HandlerUtil.toggleCommandState(command);
	     return null;
	}
}
