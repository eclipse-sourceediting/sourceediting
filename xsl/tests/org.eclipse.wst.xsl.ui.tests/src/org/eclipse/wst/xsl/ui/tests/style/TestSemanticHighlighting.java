/*******************************************************************************
 * Copyright (c) 2010 Intalio Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - bug 307924 - NPE when region is null.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.style;

import org.eclipse.jface.text.Position;
import static org.junit.Assert.*;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;
import org.eclipse.wst.xsl.ui.internal.style.XSLTagDelimsSemanticHighlighting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestSemanticHighlighting {

    XSLTagDelimsSemanticHighlighting semantich = null;
    
    @Before
    public void setUp() throws Exception {
    	semantich = new XSLTagDelimsSemanticHighlighting();
    }
        
    //bug 307924
    @Test
    public void testNPECheck() throws Exception{
    	IStructuredDocumentRegion region = new FakeStructuredRegion();
    	Position[] p = semantich.consumes(region);
    	assertNotNull(p);
    	assertEquals("Expected zero positions", 0, p.length);
    }
}
