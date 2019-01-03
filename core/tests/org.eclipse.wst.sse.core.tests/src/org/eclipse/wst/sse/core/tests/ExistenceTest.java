/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.tests;


import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.SSECorePlugin;


public class ExistenceTest extends TestCase {

	public void testPluginExists(){
		assertNotNull(SSECorePlugin.getDefault());
	}
}
