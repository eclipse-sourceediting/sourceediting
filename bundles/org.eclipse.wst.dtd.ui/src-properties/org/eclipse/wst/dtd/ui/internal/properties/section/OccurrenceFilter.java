/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.properties.section;

import org.eclipse.jface.viewers.IFilter;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.CMRepeatableNode;

public class OccurrenceFilter implements IFilter {
	public boolean select(Object toTest) {
		if (toTest instanceof CMBasicNode && ((CMBasicNode) toTest).isEmptyAnyOrPCData())
			return false;
		else if (toTest instanceof CMRepeatableNode)
			return true;
		return false;
	}
}
