package org.eclipse.wst.sse.core.tests;


import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.SSECorePlugin;


public class ExistenceTest extends TestCase {

	public void testPluginExists(){
		assertNotNull(SSECorePlugin.getDefault());
	}
}
