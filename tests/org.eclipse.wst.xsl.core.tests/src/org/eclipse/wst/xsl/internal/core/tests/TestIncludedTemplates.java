/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.core.tests;

import java.util.HashSet;
import java.util.Set;


public class TestIncludedTemplates extends AbstractValidationTest
{
	public void test1() throws Exception
	{
		Set<Integer> errors = new HashSet<Integer>();
		errors.add(27);
		
		Set<Integer> warnings = new HashSet<Integer>();
		
		validate(getFile("style1.xsl"),errors,warnings);
	}
}
