/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.modelhandler;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;

public class ModelHandlerRegistryTest extends TestCase {

	private static final String PROJECT = "ModelHandlerRegistryTest_";
	private static final String FILE = "test.MHRTfoo";

	IProject setUp(String name) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT + name);
		if (!project.isAccessible()) {
			project = createSimpleProject(project.getName());
		}
		return project;
	}

	void tearDown(String name) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT + name);
		project.delete(true, true, null);
	}

	/**
	 * Creates a simple project.
	 * 
	 * @param name
	 *            - the name of the project
	 * @return
	 */
	static IProject createSimpleProject(String name) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(name);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
	}

	public void testGetHandler() throws Exception {
		IProject project = setUp(getName());
		IFile file = project.getFile(FILE);
		IModelHandler handler;
		try {
			handler = ModelHandlerRegistry.getInstance().getHandlerFor(file);
			assertNotNull("Model handler default should not be null.", handler);
			assertEquals("Proper default model handler was not returned.", handler.getId(), "org.eclipse.wst.xml.core.modelhandler");
		} catch (CoreException e) {
			fail("Caught exception: " + e);
		}
		tearDown(getName());
	}

	public void testGetHandlerWithDefault()  throws Exception {
		IProject project = setUp(getName());
		IFile file = project.getFile(FILE);
		IModelHandler handler;
		try {
			handler = ModelHandlerRegistry.getInstance().getHandlerFor(file, true);
			assertNotNull("Model handler default should not be null.", handler);
			assertEquals("Proper default model handler was not returned.", handler.getId(), "org.eclipse.wst.xml.core.modelhandler");
		} catch (CoreException e) {
			fail("Caught exception: " + e);
		}
		tearDown(getName());
	}

	public void testGetHandlerWithoutDefault()  throws Exception {
		IProject project = setUp(getName());
		IFile file = project.getFile(FILE);
		IModelHandler handler;
		try {
			handler = ModelHandlerRegistry.getInstance().getHandlerFor(file, false);
			assertNull("Model handler should not have returned a handler.", handler);
		} catch (CoreException e) {
			fail("Caught exception: " + e);
		}
		tearDown(getName());
	}
}
