package org.eclipse.wst.xsl.internal.model.tests;

import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;

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
	}
}
