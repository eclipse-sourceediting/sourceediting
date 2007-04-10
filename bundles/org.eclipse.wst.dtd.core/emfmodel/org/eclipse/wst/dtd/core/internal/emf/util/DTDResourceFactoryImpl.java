/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;


public class DTDResourceFactoryImpl extends ResourceFactoryImpl {
	public DTDResourceFactoryImpl() {
		super();
	}

	public Resource createResource(String filename) {
		return new DTDResourceImpl(filename);
	}

	public Resource createResource(URI uri) {
		return new DTDResourceImpl(uri);
	}

	public Resource createResource(ResourceSet resources, URI uri) {
		Resource resource = new DTDResourceImpl(uri);
		resources.getResources().add(resource);
		return resource;
	}

	public Resource createResource(ResourceSet resources, String uri) {
		Resource resource = new DTDResourceImpl(uri);
		resources.getResources().add(resource);
		return resource;
	}

	public Resource load(String uri) throws Exception {
		Resource resource = createResource(uri);
		resource.load(new HashMap());
		return resource;
	}

	public Resource load(URI uri) throws Exception {
		Resource resource = createResource(uri);
		resource.load(new HashMap());
		return resource;
	}

	public Resource load(ResourceSet resources, String uri) throws Exception {
		return load(resources, uri, new HashMap());
	}

	public Resource load(ResourceSet resources, URI uri) throws Exception {
		return load(resources, uri, new HashMap());
	}

	public Resource load(ResourceSet resources, String uri, Map options) throws Exception {
		Resource resource = createResource(resources, uri);
		resource.load(options);
		return resource;
	}

	public Resource load(ResourceSet resources, URI uri, Map options) throws Exception {
		Resource resource = createResource(resources, uri);
		resource.load(options);
		return resource;
	}
} // DTDResourceFactoryImpl
