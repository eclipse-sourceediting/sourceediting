/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;

import java.util.List;


/**
 * Defines an optional validator that can be used to validate the conformance of a JSP page to using this tag library
 * @see JSP 1.2
 */
public interface TLDValidator {
	/**
	 * @return List - a List of TLDInitParams
	 */
	List getInitParams();

	String getValidatorClass();
}
