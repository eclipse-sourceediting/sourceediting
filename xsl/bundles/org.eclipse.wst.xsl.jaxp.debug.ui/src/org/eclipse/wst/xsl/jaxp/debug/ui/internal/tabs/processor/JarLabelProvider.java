/*******************************************************************************
 * Copyright (c) 2007, 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;

public class JarLabelProvider extends LabelProvider
{
	@Override
	public String getText(Object element)
	{
		IProcessorJar jar = (IProcessorJar) element;
		IPath path = jar.getPath();
		return path.lastSegment() + " - " + path.removeLastSegments(1).toOSString() + ""; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
