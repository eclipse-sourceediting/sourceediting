package org.eclipse.wst.css.ui.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;

public class ExistenceTest extends TestCase {
	public void testExists() {
		Plugin p = CSSUIPlugin.getDefault();
		assertNotNull("couldn't load CSS UI plugin", p);
	}
}
