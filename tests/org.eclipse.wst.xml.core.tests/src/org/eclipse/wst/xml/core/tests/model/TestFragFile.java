/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;



public class TestFragFile extends TestCase {

	public void testFrag() throws UnsupportedEncodingException, IOException {
		IStructuredModel model = null;

		IModelManager modelManager = StructuredModelManager.getModelManager();

		String testString1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<!--This is an internally generated file - manual changes to it will be ignored and overwritten.-->\r\n" + "<Fragment parent=\"/ejb-jar/enterprise-beans&lt;description,display-name,small-icon,large-icon\" destination=\"ejb-jar.xml\" type=\"xml\" xmlid=\"id,xmi:id\" tagSet=\"ejb\">\r\n" + "<session id=\"ejbs.TestBean\">\r\n" + "<ejb-name>Test</ejb-name>\r\n" + "<home>ejbs.TestHome</home>\r\n" + "<remote>ejbs.Test</remote>\r\n" + "<ejb-class>ejbs.TestBean</ejb-class>\r\n" + "<session-type>Stateless</session-type>\r\n" + "<transaction-type>Container</transaction-type>\r\n" + "</session>\r\n" + "</Fragment>";


		InputStream inputStream = new ByteArrayInputStream(testString1.getBytes("utf8"));

		model = modelManager.getModelForRead("test.frag", inputStream, null); //$NON-NLS-1$

		System.out.println(model);
		System.out.println(model.getStructuredDocument().get());
	}

	public void testFragX() throws UnsupportedEncodingException, IOException {
		IStructuredModel model = null;

		IModelManager modelManager = StructuredModelManager.getModelManager();

		String testString = "<!--This is an internally generated file - manual changes to it will be ignored and overwritten.-->\r\n" + "<Fragment parent=\"/ejb-jar/enterprise-beans&lt;description,display-name,small-icon,large-icon\" destination=\"ejb-jar.xml\" type=\"xml\" xmlid=\"id,xmi:id\" tagSet=\"ejb\">\r\n" + "<session id=\"ejbs.TestBean\">\r\n" + "<ejb-name>Test</ejb-name>\r\n" + "<home>ejbs.TestHome</home>\r\n" + "<remote>ejbs.Test</remote>\r\n" + "<ejb-class>ejbs.TestBean</ejb-class>\r\n" + "<session-type>Stateless</session-type>\r\n" + "<transaction-type>Container</transaction-type>\r\n" + "</session>\r\n" + "</Fragment>";


		InputStream inputStream = new StringBufferInputStream(testString);

		model = modelManager.getModelForRead("test.fragx", inputStream, null); //$NON-NLS-1$

		System.out.println(model);
		System.out.println(model.getStructuredDocument().get());
	}

}
