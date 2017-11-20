/*******************************************************************************
 * Copyright (c) 2008, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - copied over from XSL UI Tests for XML UI Tests
 *     IBM Corporation - make use of (copy of JSP UI Tests') ProjectUtil to skip
 *         file-system calls
 *******************************************************************************/

package org.eclipse.wst.xml.ui.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

/* 
 * Made abstract, so won't be automatically picked up as test (since intended to be subclassed).
 */
public abstract class AbstractXMLUITest extends TestCase
{
	protected static IProject fTestProject;
	private static boolean fTestProjectInitialized;
	private static final String PROJECT_FILES = "testresources";
	private static final String TEST_PROJECT_NAME = "AbstractXMLUITest_testresources";

	protected void setUp() throws Exception
	{
		super.setUp();
		if (!fTestProjectInitialized)
		{
			ProjectUtil.createProject(TEST_PROJECT_NAME, null, null);
			ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, TEST_PROJECT_NAME);

			fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			fTestProjectInitialized = true;
		}
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}
}
