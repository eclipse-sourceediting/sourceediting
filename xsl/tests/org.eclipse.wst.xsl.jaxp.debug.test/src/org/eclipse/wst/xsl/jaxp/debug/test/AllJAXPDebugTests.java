/*******************************************************************************
 * Copyright (c) 2010, 2018 IBM Corporation and others.
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
package org.eclipse.wst.xsl.jaxp.debug.test;

import org.eclipse.wst.xsl.jaxp.debug.invoker.test.PipelineDefintionTest;
import org.eclipse.wst.xsl.jaxp.debug.invoker.test.TestJAXPProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.debug.invoker.test.TransformDefinitonTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TransformDefinitonTest.class, PipelineDefintionTest.class, TestJAXPProcessorInvoker.class})
public class AllJAXPDebugTests {


}
