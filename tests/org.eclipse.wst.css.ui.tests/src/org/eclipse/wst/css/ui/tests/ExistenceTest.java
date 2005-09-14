package org.eclipse.wst.css.ui.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;

/**
 * 
 * @since 1.0
 */
public class ExistenceTest extends TestCase {
	/**
	 * tests if CSSUIPlugin can be loaded
	 */
	public void testExists() {
		Plugin p = CSSUIPlugin.getDefault();
		assertNotNull("couldn't load CSS UI plugin", p);
	}
}
