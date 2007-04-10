/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;

/**
 * @author pavery
 */
public class AdapterFactoryRegistryTest extends TestCase {

	private final String CLASSNAME_HTML_FACTORY = "org.eclipse.wst.html.ui.internal.registry.AdapterFactoryProviderForHTML";
	private final String CLASSNAME_DTD_FACTORY = "org.eclipse.wst.dtd.ui.internal.registry.AdapterFactoryProviderForDTD";
	private final String CLASSNAME_JSP_FACTORY = "org.eclipse.jst.jsp.ui.internal.registry.AdapterFactoryProviderForJSP";
	private final String CLASSNAME_CSS_FACTORY = "org.eclipse.wst.css.ui.internal.registry.AdapterFactoryProviderCSS";
	private final String CLASSNAME_XML_FACTORY = "org.eclipse.wst.xml.ui.internal.registry.AdapterFactoryProviderForXML";


	public void testCreate() {
		AdapterFactoryRegistry instance = AdapterFactoryRegistryImpl.getInstance();
		assertNotNull(instance);
	}

	public void testGetAllFactories() {
		AdapterFactoryRegistry instance = AdapterFactoryRegistryImpl.getInstance();
		Iterator it = instance.getAdapterFactories();
		Object provider = null;

		// all providers expected classnames added here
		List known = new ArrayList();
		known.add(CLASSNAME_HTML_FACTORY);
		known.add(CLASSNAME_DTD_FACTORY);
		known.add(CLASSNAME_JSP_FACTORY);
		known.add(CLASSNAME_CSS_FACTORY);
		known.add(CLASSNAME_XML_FACTORY);

		while (it.hasNext()) {
			provider = it.next();
			known.remove(provider.getClass().getName());
			// System.out.println(provider.getClass().getName());
		}

		// should have encountered all expected adapter factories.
		assertEquals("unknown (extra) adapter factories found", 0, known.size());
	}

	public void testGetFactoriesXML() {

		List known = new ArrayList();
		known.add(CLASSNAME_XML_FACTORY);

		getFactoriesForContentType(known, ContentTypeIdForXML.ContentTypeID_XML, CLASSNAME_HTML_FACTORY);
	}

	public void testGetFactoriesHTML() {

		List known = new ArrayList();
		known.add(CLASSNAME_JSP_FACTORY);

		getFactoriesForContentType(known, ContentTypeIdForJSP.ContentTypeID_JSP, CLASSNAME_XML_FACTORY);
	}

	public void testGetFactoriesJSP() {

		List known = new ArrayList();
		known.add(CLASSNAME_HTML_FACTORY);

		getFactoriesForContentType(known, ContentTypeIdForHTML.ContentTypeID_HTML, CLASSNAME_DTD_FACTORY);
	}

	public void testGetFactoriesDTD() {

		List known = new ArrayList();
		known.add(CLASSNAME_DTD_FACTORY);

		getFactoriesForContentType(known, "org.eclipse.wst.dtd.core.dtdsource", CLASSNAME_HTML_FACTORY);
	}

	public void testGetFactoriesCSS() {

		List known = new ArrayList();
		known.add(CLASSNAME_CSS_FACTORY);

		getFactoriesForContentType(known, ContentTypeIdForCSS.ContentTypeID_CSS, CLASSNAME_JSP_FACTORY);
	}

	/**
	 * Compare to a expected list of AdapterFactoryProviders (may be smaller
	 * than the actual list) for a given contentTypeId. At minimum these
	 * expected providers must be found to pass.
	 * 
	 * @param expected
	 * @param contentTypeId
	 */
	private void getFactoriesForContentType(List expected, String contentTypeId, String notExpectedClassname) {

		AdapterFactoryRegistryImpl instance = (AdapterFactoryRegistryImpl) AdapterFactoryRegistryImpl.getInstance();
		Iterator it = instance.getAdapterFactories(contentTypeId);
		Object provider = null;
		while (it.hasNext()) {
			provider = it.next();
			expected.remove(provider.getClass().getName());

			// System.out.println("encountered provider: " +
			// provider.getClass().getName());
			assertTrue("!provider: " + notExpectedClassname + " shouldn't be in the list!", !notExpectedClassname.equals(provider.getClass().getName()));
		}
		// we should have at least found the expected factory (or factories)
		assertTrue("extra providers (expected none): " + expected, expected.isEmpty());
	}
}
