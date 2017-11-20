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
