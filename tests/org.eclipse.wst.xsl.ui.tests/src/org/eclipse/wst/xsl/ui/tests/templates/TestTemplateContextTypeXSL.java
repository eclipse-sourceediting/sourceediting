package org.eclipse.wst.xsl.ui.tests.templates;

import java.util.Iterator;

import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.wst.xsl.ui.internal.templates.TemplateContextTypeXSL;

import junit.framework.TestCase;

public class TestTemplateContextTypeXSL extends TestCase {

	public void testXSLTemplateContextType() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
	}
	
	public void testCursorVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("cursor")) {
			   return;
		   }
		}
		
		fail("Cursor variable resolver was not found.");
	}
	
	public void testDateVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("date")) {
			   return;
		   }
		}
		
		fail("Date variable resolver was not found.");
	}

	public void testYearVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("year")) {
			   return;
		   }
		}
		
		fail("Year variable resolver was not found.");
	}
	
	public void testTimeVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("time")) {
			   return;
		   }
		}
		
		fail("Time variable resolver was not found.");
	}
	
	public void testUserVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("user")) {
			   return;
		   }
		}
		
		fail("User variable resolver was not found.");
	}
	
	public void testDollarVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("dollar")) {
			   return;
		   }
		}
		
		fail("User variable resolver was not found.");
	}
	
	public void testWordSelectionVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("word_selection")) {
			   return;
		   }
		}
		
		fail("Word Selection variable resolver was not found.");
	}

	public void testLineSelectionVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("line_selection")) {
			   return;
		   }
		}
		
		fail("Line Selection variable resolver was not found.");
	}
	
	public void testXMLEncodingVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("encoding")) {
			   return;
		   }
		}
		
		fail("Encoding Selection variable resolver was not found.");
	}
	
	public void testXSLVersionVariableAvailable() throws Exception {
		TemplateContextTypeXSL contextType = new TemplateContextTypeXSL();
		Iterator<TemplateVariableResolver> variables = contextType.resolvers();
		
		while (variables.hasNext()) {
		   TemplateVariableResolver resolver = variables.next();	
		   if (resolver.getType().equals("xsl_version")) {
			   return;
		   }
		}
		
		fail("XSL Version variable resolver was not found.");
	}
	
	
	
}
