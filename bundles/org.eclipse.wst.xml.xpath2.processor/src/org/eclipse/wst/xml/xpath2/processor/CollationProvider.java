/*******************************************************************************
 * Copyright (c) 2009 Jesper Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import java.util.Comparator;

/**
 * Service provider interface for looking up collations from within the dynamic context.
 * @since 1.1
 * 
 * @deprecated Use org.eclipse.wst.xml.xpath2.api.CollationProvider instead
 */
public interface CollationProvider {
	/**
	 * Gets the named collator. W3C does not define collation names (yet?) so we are constrained to using an
	 * implementation-defined naming scheme.
	 * 
	 * @param name A URI designating the collation to use
	 * @return The collation to use, or null if no such collation exists by this provider
	 */
	Comparator get_collation(String name);
}
