/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.model;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.sse.core.internal.util.URIResolverExtension;

public class ModelTest extends TestCase {

	public void testNewModelURIResolver() throws IOException {
		IStructuredModel model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor("org.eclipse.core.runtime.xml");
		model.setResolver(new DummyResolver());
		IStructuredModel newModel = StructuredModelManager.getModelManager().createNewInstance(model);
		assertNotSame("Model URI Resolvers should be different.", model.getResolver(), newModel.getResolver());
	}

	private class DummyResolver implements URIResolver, URIResolverExtension {

		private DummyResolver() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolverExtension#newInstance()
		 */
		public URIResolver newInstance() {
			return new DummyResolver();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getFileBaseLocation()
		 */
		public String getFileBaseLocation() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getLocationByURI(java.lang.String)
		 */
		public String getLocationByURI(String uri) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getLocationByURI(java.lang.String, boolean)
		 */
		public String getLocationByURI(String uri, boolean resolveCrossProjectLinks) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getLocationByURI(java.lang.String, java.lang.String)
		 */
		public String getLocationByURI(String uri, String baseReference) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getLocationByURI(java.lang.String, java.lang.String, boolean)
		 */
		public String getLocationByURI(String uri, String baseReference, boolean resolveCrossProjectLinks) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getProject()
		 */
		public IProject getProject() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getRootLocation()
		 */
		public IContainer getRootLocation() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#getURIStream(java.lang.String)
		 */
		public InputStream getURIStream(String uri) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#setFileBaseLocation(java.lang.String)
		 */
		public void setFileBaseLocation(String newLocation) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.wst.sse.core.internal.util.URIResolver#setProject(org.eclipse.core.resources.IProject)
		 */
		public void setProject(IProject newProject) {
		}
		
	}

	public void testOKtoReleaseUnmanagedRead() {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor("org.eclipse.core.runtime.xml");
			model.getStructuredDocument().set(getName());
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	public void testOKtoReleaseUnmanagedEdit() {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor("org.eclipse.core.runtime.xml");
			model.getStructuredDocument().set(getName());
		}
		finally {
			if (model != null)
				model.releaseFromEdit();
		}
}
	
}