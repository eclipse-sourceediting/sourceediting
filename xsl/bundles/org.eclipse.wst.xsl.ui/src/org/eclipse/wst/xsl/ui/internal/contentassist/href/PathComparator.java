/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist.href;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.core.runtime.IPath;

final class PathComparator implements Comparator<IPath>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4401179581193111280L;

	public int compare(IPath o1, IPath o2)
	{
		int countComp = o1.segmentCount() - o2.segmentCount();
		if (countComp != 0)
			return countComp;
		
		for(int i=0;i<o1.segmentCount();i++)
		{
			String seg1 = o1.segment(i);
			String seg2 = o2.segment(i);
			if (!seg1.equals(seg2))
			{
				if (seg1.equals("..")) //$NON-NLS-1$
					return 1;
				if (seg2.equals("..")) //$NON-NLS-1$
					return -1;
			}
		}
		
		return o1.toString().compareTo(o2.toString());
	}
}