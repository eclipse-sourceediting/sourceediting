package org.eclipse.wst.sse.core.tests;


import org.eclipse.wst.sse.core.ModelPlugin;

import junit.framework.TestCase;


public class ExistenceTest extends TestCase {

	public void testPluginExists(){
		assertNotNull(ModelPlugin.getDefault());
	}
}
