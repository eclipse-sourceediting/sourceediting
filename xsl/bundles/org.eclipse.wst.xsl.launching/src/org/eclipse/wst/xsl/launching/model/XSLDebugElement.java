/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.ITerminate;

public abstract class XSLDebugElement extends DebugElement implements IDisconnect
{
	public XSLDebugElement(IDebugTarget target)
	{
		super(target);
	}

	public String getModelIdentifier()
	{
		return IXSLConstants.ID_XSL_DEBUG_MODEL;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == ITerminate.class) {
			return getDebugTarget();
		}
		return super.getAdapter(adapter);
	}
	
	public boolean canDisconnect()
	{
		return getDebugTarget().canDisconnect();
	}
	
	public void disconnect() throws DebugException
	{
		getDebugTarget().disconnect();
	}
	
	public boolean isDisconnected()
	{
		return getDebugTarget().isDisconnected();
	}
}
