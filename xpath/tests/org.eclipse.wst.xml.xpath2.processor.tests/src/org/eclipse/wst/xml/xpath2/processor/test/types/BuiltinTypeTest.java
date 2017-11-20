package org.eclipse.wst.xml.xpath2.processor.test.types;

import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

import junit.framework.TestCase;

public class BuiltinTypeTest extends TestCase {

	public void testCreateAtomic() {
		BuiltinTypeLibrary.XS_BOOLEAN.constructNative(Boolean.TRUE);
	}
}
