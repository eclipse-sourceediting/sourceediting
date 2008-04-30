/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.catalog.tests.internal;

import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;


public class CatalogResolverTest extends AbstractCatalogTest {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public CatalogResolverTest(String name) {
		super(name);
	}

	public final void testResolveResolver() throws Exception {
	
	
		// from plugin.xml file
		String pluginBase = resolvePluginLocation(TestPlugin.getDefault().getBundle().getSymbolicName()).toString();

		String resolvedActual = defaultCatalog.resolvePublic("InvoiceId_test", null);
		String resolvedURI = makeAbsolute(pluginBase, "data/Invoice/Invoice.dtd");
		assertEquals(resolvedURI, resolvedActual);
		resolvedActual = defaultCatalog.resolveSystem("http://personal/personal.dtd");
		resolvedURI = makeAbsolute(pluginBase, "data/Personal/personal.dtd");
		assertEquals(resolvedURI, resolvedActual);
		resolvedActual = defaultCatalog.resolveURI("http://apache.org/xml/xcatalog/example");
		resolvedURI = makeAbsolute(pluginBase, "data/example/example.xsd");
		assertEquals(resolvedURI, resolvedActual);
		resolvedActual = defaultCatalog.resolveURI("http://www.w3.org/2001/XMLSchema");
		resolvedURI = resolvePath("", "platform:/plugin/org.eclipse.xsd/cache/www.w3.org/2001/XMLSchema.xsd");
		resolvedURI = makeAbsolute(pluginBase, resolvedURI);
		assertEquals(resolvedURI, resolvedActual);
		
		// from catalog1.xml
		resolvedActual = defaultCatalog.resolvePublic("InvoiceId_test", null);
		resolvedURI = makeAbsolute(pluginBase, "data/Invoice/Invoice.dtd");
		assertEquals(resolvedURI, resolvedActual);

		resolvedActual = defaultCatalog.resolveSystem("Invoice.dtd");
		resolvedURI = makeAbsolute(pluginBase, "data/Invoice/Invoice.dtd");
		assertEquals(URIHelper.ensureFileURIProtocolFormat(resolvedURI), resolvedActual);

		resolvedActual = defaultCatalog.resolveURI("http://www.test.com/Invoice.dtd");
		resolvedURI = makeAbsolute(pluginBase, "data/Invoice/Invoice.dtd");
		assertEquals(URIHelper.ensureFileURIProtocolFormat(resolvedURI), resolvedActual);

		
		// from catalog2.xml
		resolvedActual = defaultCatalog.resolvePublic("http://www.eclipse.org/webtools/Catalogue_001", null);
		resolvedURI = makeAbsolute(pluginBase, "data/PublicationCatalogue/Catalogue.xsd");
		assertEquals(URIHelper.ensureFileURIProtocolFormat(resolvedURI), resolvedActual);
		
		resolvedActual = defaultCatalog.resolvePublic("http://www.eclipse.org/webtools/Catalogue_002", null);
		resolvedURI = makeAbsolute(pluginBase, "data/PublicationCatalogue/Catalogue.xsd");
		assertEquals(URIHelper.ensureFileURIProtocolFormat(resolvedURI), resolvedActual);

		resolvedActual = defaultCatalog.resolveSystem("Catalogue.xsd");
		resolvedURI = makeAbsolute(pluginBase, "data/PublicationCatalogue/Catalogue.xsd");
		assertEquals(URIHelper.ensureFileURIProtocolFormat(resolvedURI), resolvedActual);

	}



}
