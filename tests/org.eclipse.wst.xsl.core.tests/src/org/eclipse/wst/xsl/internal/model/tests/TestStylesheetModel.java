package org.eclipse.wst.xsl.internal.model.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.core.model.XSLElement;

public class TestStylesheetModel extends AbstractModelTest {

	public TestStylesheetModel() {
		
	}
	
	public void testStyleSheetModel() {
		
		StylesheetModel model = XSLCore.getInstance().getStylesheet(getFile("globalVariablesTest.xsl"));
		assertNotNull("Failed to load stylesheet 'globalVariablesTest.xsl'.", model);
		model = XSLCore.getInstance().getStylesheet(getFile("style1.xsl"));
		assertNotNull("Failed to load stylesheet 'style1.xsl'.", model);
		model = XSLCore.getInstance().getStylesheet(getFile("XSLT20Test.xsl"));
		assertNotNull("Failed to load stylesheet 'XSLT20Test.xsl'.", model);
		model = XSLCore.getInstance().getStylesheet(getFile("circularref.xsl"));
		assertNotNull("Failed to load stylesheet 'circularref.xsl'.", model);
		model = XSLCore.getInstance().getStylesheet(getFile("modeTest.xsl"));
		assertNotNull("Failed to load stylesheet 'modeTest.xsl'.", model);
		
	}
	
	public void testFindAvailableTemplateModes() {
		ArrayList<String> modes = new ArrayList();
		StylesheetModel model = XSLCore.getInstance().getStylesheet(getFile("modeTest.xsl"));
		List<Template> templates = model.getTemplates();
		assertTrue("No templates returned.", templates.size() > 0);
		
		for (Template template : templates) {
			XSLAttribute attribute = template.getAttribute("mode");
			if (attribute != null) {
				if (modes.indexOf(attribute.getValue()) == -1 ) {
					modes.add(attribute.getValue());
				}
			}
		}
		assertEquals("Wrong number of mode templates returned.", 3, modes.size());
	}
}
