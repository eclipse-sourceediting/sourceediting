package org.eclipse.wst.xsl.internal.model.tests;

import java.util.List;
import java.util.Map;

import org.eclipse.wst.xsl.core.internal.StylesheetBuilder;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLAttribute;

public class TestStylesheet extends AbstractModelTest {

	public TestStylesheet() {
		// TODO Auto-generated constructor stub
	}
	
	public void testLoadModel() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet model = builder.getStylesheet(getFile("style1.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", model);
	}
	
	public void testgetLocalTemplatesTemplates() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet model = builder.getStylesheet(getFile("style1.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", model);
		
		List<Template> templatesList = model.getTemplates();
		assertEquals("Wrong number of templates returned.", 2, templatesList.size());
	}
	
	public void testGetIncludes() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("style1.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		
		List<Include> includeList = stylesheet.getIncludes();
		assertEquals("Wrong number of includes returned.", 1, includeList.size());
	}
	
	public void testGetImports() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("style1.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		
		List<Import> includeList = stylesheet.getImports();
		assertEquals("Wrong number of includes returned.", 1, includeList.size());
		
	}
	
	public void testGetGlobalVariables() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		
		List<Variable> globalVariablesList = stylesheet.getGlobalVariables();
		assertEquals("Wrong number of global variables returned.", 1, globalVariablesList.size());
	}
	
	public void testGetLineNumber() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Incorrect line number.", 12, stylesheet.getLineNumber());
	}
	
	public void testGetColumnNumber() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Incorrect column number.", 0, stylesheet.getColumnNumber());
	}
	
	public void testGetVersion() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Wrong version number returned.", "1.0", stylesheet.getVersion());
	}
	
	public void testXSLT2GetVersion() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("XSLT20Test.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Wrong version number returned.", "2.0", stylesheet.getVersion());
	}
	
	public void testGetAttributes() {
		StylesheetBuilder builder = StylesheetBuilder.getInstance();
		Stylesheet stylesheet = builder.getStylesheet(getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		
		Map<String,XSLAttribute> attributeList = stylesheet.getAttributes();
		assertEquals("Incorrect number of attributes", 4, attributeList.size());
		XSLAttribute attribute = stylesheet.getAttribute("exclude-result-prefixes");
		assertEquals("Wrong attribute returned:", "exclude-result-prefixes", attribute.getName());
		
	}
	
	
	
}
