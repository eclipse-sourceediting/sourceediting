package org.eclipse.wst.xsl.internal.model.tests;

import java.util.List;
import static org.junit.Assert.*;

import java.util.Map;

import org.eclipse.wst.xsl.core.internal.model.StylesheetBuilder;
import org.eclipse.wst.xsl.core.model.Function;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStylesheet extends AbstractModelTest {
	StylesheetBuilder builder = null;

	public TestStylesheet() {
		// TODO Auto-generated constructor stub
	}
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		builder = StylesheetBuilder.getInstance();
	}
	
	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		builder.release();
	}

	@Test
	public void testLoadModel() {
		Stylesheet model = builder.getStylesheet(getFile("style1.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", model);
	}

	@Test
	public void testgetLocalTemplatesTemplates() {
		Stylesheet model = builder.getStylesheet(getFile("style1.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", model);

		List<Template> templatesList = model.getTemplates();
		assertEquals("Wrong number of templates returned.", 2, templatesList
				.size());
	}

	@Test
	public void testGetIncludes() {
		Stylesheet stylesheet = builder.getStylesheet(getFile("style1.xsl"),
				false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		List<Include> includeList = stylesheet.getIncludes();
		assertEquals("Wrong number of includes returned.", 1, includeList
				.size());
	}

	@Test
	public void testGetImports() {
		Stylesheet stylesheet = builder.getStylesheet(getFile("style1.xsl"),
				false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		List<Import> includeList = stylesheet.getImports();
		assertEquals("Wrong number of includes returned.", 1, includeList
				.size());

	}

	@Test
	public void testGetGlobalVariables() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		List<Variable> globalVariablesList = stylesheet.getGlobalVariables();
		assertEquals("Wrong number of global variables returned.", 3,
				globalVariablesList.size());
	}

	@Test
	public void testGetLineNumber() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Incorrect line number.", 12, stylesheet.getLineNumber());
	}

	@Test
	public void testGetColumnNumber() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Incorrect column number.", 0, stylesheet
				.getColumnNumber());
	}

	@Test
	public void testGetVersion() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Wrong version number returned.", "1.0", stylesheet
				.getVersion());
	}

	@Test
	public void testXSLT2GetVersion() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("XSLT20Test.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);
		// Line Number is off by 1 from what is displayed in the Editor
		assertEquals("Wrong version number returned.", "2.0", stylesheet
				.getVersion());
	}

	@Test
	public void testGetAttributes() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("globalVariablesTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		Map<String, XSLAttribute> attributeList = stylesheet.getAttributes();
		assertEquals("Incorrect number of attributes", 4, attributeList.size());
		XSLAttribute attribute = stylesheet
				.getAttribute("exclude-result-prefixes");
		assertEquals("Wrong attribute returned:", "exclude-result-prefixes",
				attribute.getName());

	}

	@Test
	public void testGetFunction() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("XSLT20FunctionTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		List<Function> functionList = stylesheet.getFunctions();
		assertEquals("Wrong number of global variables returned.", 2,
				functionList.size());
	}
	
	@Test
	public void testGetFunctionFunc1() {
		Stylesheet stylesheet = builder.getStylesheet(
				getFile("XSLT20FunctionTest.xsl"), false);
		assertNotNull("Model failed to load, returned NULL", stylesheet);

		List<Function> functionList = stylesheet.getFunctions();
		for (Function function : functionList) {
			if (function.getName().equals("func1")) {
				return;
			}
		}
		fail("Did not find XSL func func1");
	}
		
}
