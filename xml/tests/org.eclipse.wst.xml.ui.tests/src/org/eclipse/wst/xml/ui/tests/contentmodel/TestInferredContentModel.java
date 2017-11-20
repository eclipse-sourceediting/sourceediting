/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.tests.contentmodel;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.editor.EditorModelUtil;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author nitin
 *
 */
public class TestInferredContentModel extends TestCase {

	/**
	 * 
	 */
	public TestInferredContentModel() {
	}

	public void testInferredAttrTypeDefault_bug318108() {
		boolean useInferred = XMLUIPlugin.getDefault().getPreferenceStore().getBoolean(XMLUIPreferenceNames.USE_INFERRED_GRAMMAR);
		XMLUIPlugin.getDefault().getPreferenceStore().setValue(XMLUIPreferenceNames.USE_INFERRED_GRAMMAR, true);
		
		
		IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		model.getStructuredDocument().set("<root>\n<a r=\"1\"/>\n<a>\n</root>\n");
		
		EditorModelUtil.addFactoriesTo(model);
		// need to wait for delayed DOMObserver to build inferred content model
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		NodeList elementsByTagName = model.getDocument().getElementsByTagName("a");
		try {
			for (int i = 0; i < elementsByTagName.getLength(); i++) {
				Element a = (Element) elementsByTagName.item(i);
				a.getAttribute("r");
			}
		}
		catch (Exception e) {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=318108
			fail(e.toString());
		}
		finally {
			XMLUIPlugin.getDefault().getPreferenceStore().setValue(XMLUIPreferenceNames.USE_INFERRED_GRAMMAR, useInferred);
		}
	}
}
