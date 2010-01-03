package org.eclipse.wst.xsl.jaxp.debug.test;

import org.eclipse.wst.xsl.jaxp.debug.invoker.test.PipelineDefintionTest;
import org.eclipse.wst.xsl.jaxp.debug.invoker.test.TransformDefinitonTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllJAXPDebugTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllJAXPDebugTests.class.getName());
		//$JUnit-BEGIN$
		   suite.addTestSuite(TransformDefinitonTest.class);
		   suite.addTestSuite(PipelineDefintionTest.class);
		//$JUnit-END$
		return suite;
	}

}
