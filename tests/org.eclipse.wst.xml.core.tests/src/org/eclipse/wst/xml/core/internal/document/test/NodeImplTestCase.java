/*******************************************************************************
 * Copyright (c) 2006 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *******************************************************************************/


package org.eclipse.wst.xml.core.internal.document.test;
import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;


public class NodeImplTestCase extends TestCase {
	/**
	 * Casts to implementation classes because the interface methods being
	 * there isn't the interesting part, just whether our behavior with those
	 * methods is correct.
	 */	

	NodeImpl element;
	AttrImpl attribute;
	NodeImpl destinationNode;
	Document document;
	
	Element elementToImport;
	
	private static final String KEY="key";
	private static final String VALUE="value";
	private static final String KEY2="key2";
	private static final String VALUE2="value2";
	private static final boolean DEBUG = false;
	
	
	/**
     * Sets up the test fixture. 
     * (Called before every test case method.) 
     */ 
    protected void setUp() { 
        
    	DOMModelImpl model = new DOMModelImpl();
    	document = model.getDocument();
    	element = (NodeImpl)document.createElement("testelement");
    	attribute = (AttrImpl)document.createAttribute("attribute");
    } 
	
    /*
     * Test for null;
     */
	public void testGetUserData0() {
		assertEquals(null, element.getUserData(KEY));
		assertEquals(null, element.getUserData(null));
	}
    
	/*
	 * Test for one key/value
	 */
    public void testGetUserData1() {
		element.setUserData(KEY, VALUE, null);
		assertEquals(VALUE,element.getUserData(KEY));
		assertEquals(null, attribute.getUserData(KEY));
	}
	
    /*
     * Test for multiple nodes
     */
	public void testGetUserData2() {
		element.setUserData(KEY, VALUE, null);
		assertEquals(VALUE,element.getUserData(KEY));
		attribute.setUserData(KEY2, VALUE2, null);
		assertEquals(VALUE2,attribute.getUserData(KEY2));
	}
	
	/*
	 * Test for multiple keys
	 */
	public void testGetUserData3() {
		element.setUserData(KEY, VALUE, null);
		element.setUserData(KEY2, VALUE2, null);
		assertEquals(VALUE,element.getUserData(KEY));
		assertEquals(VALUE2,element.getUserData(KEY2));
	}

	/*
	 * Test for null key
	 */
	public void testSetUserData1() {
		assertEquals(null, element.setUserData(null, VALUE, null));
	}

	/*
	 * Test for null data with no previous value
	 */
	public void testSetUserData2() {
		assertEquals(null, element.setUserData(KEY, null, null));
	}
	
	/*
	 * Test for overwriting
	 */
	public void testSetUserData3() {
		element.setUserData(KEY, VALUE, null);
		element.setUserData(KEY, VALUE2, null);
		assertEquals(VALUE2, element.getUserData(KEY));
	}
	
	/* 
	 * Test for deleting
	 */
	public void testSetUserData4() {
		element.setUserData(KEY, VALUE, null);
		assertEquals(VALUE, element.getUserData(KEY));
		element.setUserData(KEY, null, null);
		assertEquals(null, element.getUserData(KEY));
	}
	
	/*
	 * Test for notifying the UserDataHandler=null
	 */
	public void testNotifyUserDataHandler0() {
		
		attribute.setUserData(KEY, VALUE, null);
		destinationNode=(NodeImpl)attribute.cloneNode(true);
	}
	
	/*
	 * Test for notifying the UserDataHandler when cloning an attribute
	 */
	public void testNotifyUserDataHandler1() {
		
		attribute.setUserData(KEY, VALUE, new UserDataHandler(){

			public void handle(short operation, String key, Object data, Node src, Node dst) {
				assertEquals(UserDataHandler.NODE_CLONED, operation);
				assertEquals(VALUE, data);
				assertEquals(KEY, key);
				assertEquals(src, attribute); 
				
				if(DEBUG)
				System.out.println("Operation: "+operation+" Key:"+ key 
						+ " Object:"+data+" SourceNode:"+src.getLocalName()+" DestinationNode:"+dst.getLocalName());
				
			}
		});
		//event occurs before the destinationNode returns....
		destinationNode=(AttrImpl)attribute.cloneNode(true);
	}
	
	/*
	 * Test for notifying the UserDataHandler when importing
	 */
	public void testNotifyUserDataHandler2() {
    	elementToImport = document.createElement("ElementToImport");
    	((NodeImpl)elementToImport).setUserData(KEY, VALUE, new UserDataHandler(){

			public void handle(short operation, String key, Object data, Node src, Node dst) {

				//see if NODE.IMPORTED notification occurs with the right
				//parameters
				if (operation==UserDataHandler.NODE_IMPORTED) {	
				assertEquals(VALUE, data);
				assertEquals(KEY, key);
				assertEquals(src, elementToImport);
				assertEquals(dst, null);  
				if(DEBUG)
				System.out.println("Operation: "+operation+" Key:"+ key 
						+ " Object:"+data+" SourceNode:"+src.getLocalName()+" DestinationNode:"+dst);
				}
			}
		});
		document.importNode(elementToImport, true);
	}
}
