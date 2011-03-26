/*******************************************************************************
 * Copyright (c) 2011 Jesper Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.api.typesystem;

/**
 * @since 2.0
 */
public interface ItemType {
	
	public int getOccurrence();
	
	public final static int OCCURRENCE_OPTIONAL = 0;
	public final static int OCCURRENCE_ONE = 1;
	public final static int OCCURRENCE_NONE_OR_MANY = 2;
	public final static int OCCURRENCE_ONE_OR_MANY = 3;

	public final static int ALWAYS_ONE_MASK = 0x01;
	public final static int MAYBE_MANY_MASK = 0x02;
}
