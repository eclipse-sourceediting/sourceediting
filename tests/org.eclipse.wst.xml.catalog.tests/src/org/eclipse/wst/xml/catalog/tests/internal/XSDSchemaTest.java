package org.eclipse.wst.xml.catalog.tests.internal;

import org.eclipse.xsd.impl.XSDSchemaImpl;

import junit.framework.TestCase;

public class XSDSchemaTest extends TestCase {

	public static void main(String[] args) {
	}
	
	public void testSchemaForSchema(){
		
		  XSDSchemaImpl.getSchemaForSchema("http://www.w3.org/2001/XMLSchema");  
	}

}
