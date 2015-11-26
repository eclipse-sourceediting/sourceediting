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
package org.eclipse.wst.sse.core.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class TestUnsupportedContentType extends TestCase {
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
			if (!project.exists())
				project.create(description, new NullProgressMonitor());
			if (!project.isAccessible())
				project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
	}


	/**
	 * Verify that attempting to load a model for an unsupported content type
	 * simply returns null without Exceptions or assertion failures.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	public void testGetForReadWithUnsupported() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getName());
		if (!project.isAccessible()) {
			project = createSimpleProject(getName());
		}

		IFile file = project.getFile("testReadFile.js");
		file.create(new ByteArrayInputStream("var n = 0;".getBytes()), true, null);
		assertTrue("test file not created", file.isAccessible());
		try {
			IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(file);
			assertNull("model returned even though expected to silently get null", model);
			if (model != null)
				model.releaseFromRead();
		}
		catch (Exception e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			fail(out.toString());
		}
		project.delete(true, null);
	}

	/**
	 * Verify that attempting to load a model for an unsupported content type
	 * simply returns null without Exceptions or assertion failures.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	public void testGetForEditWithUnsupported() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getName());
		if (!project.isAccessible()) {
			project = createSimpleProject(getName());
		}

		IFile file = project.getFile("testEditFile.js");
		file.create(new ByteArrayInputStream("var n = 0;".getBytes()), true, null);
		assertTrue("test file not created", file.isAccessible());
		try {
			IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit(file);
			assertNull("model returned even though expected to silently get null", model);
			if (model != null)
				model.releaseFromRead();
		}
		catch (Exception e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			fail(out.toString());
		}
		project.delete(true, null);
	}
}
