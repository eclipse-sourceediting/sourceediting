/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
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
package org.eclipse.wst.xsl.internal.core.tests;


import org.eclipse.wst.xsl.core.XSLCore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestXSLCore {

	@Test
	public void testisXSLNamespaceNullFalse() {
		assertFalse(XSLCore.isXSLNamespace(null));
	}
		
}
