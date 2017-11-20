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

package org.eclipse.wst.xml.xpath2.api;

/**
 * A match found by the XPath2 pattern matcher
 * 
 * * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 *
 */

public interface Match {
	/**
	 * @return The number of matching patterns on the input.
	 */
	int getMatchingCount();
	
	/**
	 * Returns the XPath2 pattern which best matched the input (considering mode and priority)
	 * 
	 * @return Pattern which was the best match.
	 */
	XPath2Pattern getBestMatch();
}
