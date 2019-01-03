/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
package org.eclipse.wst.xsl.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.wst.xsl.launching.model.XSLStackFrame;

/**
 * Translate a stack frame into a source file name
 */
public class XSLTSourceLookupParticipant extends AbstractSourceLookupParticipant
{
	public String getSourceName(Object object) throws CoreException
	{
		if (object instanceof XSLStackFrame)
		{
			return ((XSLStackFrame) object).getSourceName();
		}
		return null;
	}
}
