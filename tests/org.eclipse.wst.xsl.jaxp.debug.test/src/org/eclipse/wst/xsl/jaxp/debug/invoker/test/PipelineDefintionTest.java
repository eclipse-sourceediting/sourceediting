package org.eclipse.wst.xsl.jaxp.debug.invoker.test;

import java.util.List;

import org.eclipse.wst.xsl.jaxp.debug.invoker.PipelineDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformDefinition;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformationException;

import junit.framework.TestCase;

public class PipelineDefintionTest extends TestCase {
	PipelineDefinition pldef = null;

	@Override
	protected void setUp() throws Exception {
		pldef = new PipelineDefinition();
	}
	
	public void testAddTransformationDefinition() throws Exception {
		TransformDefinition tdef = new TransformDefinition();
		pldef.addTransformDef(tdef);
		List<TransformDefinition> tdefs = pldef.getTransformDefs();
		assertNotNull(tdefs);
		assertEquals("Did not find expected transformation defs", 1, tdefs.size());
	}

}
