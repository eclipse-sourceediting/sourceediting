/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;

public class ModelHandlerRegistryTest extends TestCase {

	private int running = 0;
	private static final String PROJECT = "momdelHandlerRegistry";
	private static final String FILE = "/test.foo";
	protected void setUp() throws Exception {
		super.setUp();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
		synchronized (PROJECT) {
			if (!project.isAccessible()) {
				project = createSimpleProject(PROJECT);
			}
		}
		running++;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		running--;
		if (running == 0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
			synchronized (PROJECT) {
				project.delete(true, true, null);
			}
		}
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

	public void testGetHandle() {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(PROJECT + FILE));
		IModelHandler handler;
		try {
			handler = ModelHandlerRegistry.getInstance().getHandlerFor(file);
			assertNotNull("Model handler default should not be null.", handler);
			assertEquals("Proper default model handler was not returned.", handler.getId(), "org.eclipse.wst.xml.core.modelhandler");
		} catch (CoreException e) {
			fail("Caught exception: " + e);
		}
	}

	public void testGetHandleWithDefault() {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(PROJECT + FILE));
		IModelHandler handler;
		try {
			handler = ModelHandlerRegistry.getInstance().getHandlerFor(file, true);
			assertNotNull("Model handler default should not be null.", handler);
			assertEquals("Proper default model handler was not returned.", handler.getId(), "org.eclipse.wst.xml.core.modelhandler");
		} catch (CoreException e) {
			fail("Caught exception: " + e);
		}
	}

	public void testGetHandleWithoutDefault() {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(PROJECT + FILE));
		IModelHandler handler;
		try {
			handler = ModelHandlerRegistry.getInstance().getHandlerFor(file, false);
			assertNull("Model handler should not have returned a handler.", handler);
		} catch (CoreException e) {
			fail("Caught exception: " + e);
		}
	}
}
