package org.eclipse.wst.xsl.jaxp.debug.test;

import org.eclipse.wst.xsl.jaxp.debug.invoker.test.PipelineDefintionTest;
import org.eclipse.wst.xsl.jaxp.debug.invoker.test.TestJAXPProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.debug.invoker.test.TransformDefinitonTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TransformDefinitonTest.class, PipelineDefintionTest.class, TestJAXPProcessorInvoker.class})
public class AllJAXPDebugTests {


}
