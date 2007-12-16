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
