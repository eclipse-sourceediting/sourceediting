/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.stylesheets;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.tests.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;

public class StyleSheetTest extends TestCase {

	public static final String PROJECT_NAME = "testImports";

	protected void setUp() throws Exception {
		super.setUp();
		if (!ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME).exists()) {
			ProjectUtil.createSimpleProject(PROJECT_NAME, null, new String[] {});
			ProjectUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Tests for cycles in imports. If a cycle is encountered, we should not get stuck
	 * in a loop or re-import the stylesheet.
	 * @throws Exception
	 */
	public void testImportCycle() throws Exception {
		String filePath = "/" + PROJECT_NAME + "/WebContent/cycle0.css";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		assertTrue("File doesn't exist: " + filePath, file.exists());

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (model instanceof ICSSModel) {
				ICSSDocument document = ((ICSSModel) model).getDocument();
				if (document instanceof ICSSStyleSheet) {
					CSSRuleList list = ((ICSSStyleSheet) document).getCssRules(true);
					assertTrue("There should be a total of 3 rules supplied stylesheet.", list.getLength() == 3);
					for (int i = 0; i < list.getLength(); i++) {
						CSSRule rule = list.item(i);
						assertTrue("Rule should be a style rule. rule.getType() == " + rule.getType(), rule.getType() == ICSSNode.STYLERULE_NODE);
						// Check that the rules are imported, it would start with the last imported stylesheet's rules and move up
						assertEquals("Style rules are not equal.", ((CSSStyleRule) rule).getSelectorText(), "#cycle" + (2-i));
					}
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Tests the simple case of a basic @import or a stylesheet
	 * @throws Exception
	 */
	public void testSimpleImport() throws Exception {
		String filePath = "/" + PROJECT_NAME + "/WebContent/site.css";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		assertTrue("File doesn't exist: " + filePath, file.exists());

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (model instanceof ICSSModel) {
				ICSSDocument document = ((ICSSModel) model).getDocument();
				if (document instanceof ICSSStyleSheet) {
					CSSRuleList list = ((ICSSStyleSheet) document).getCssRules(true);
					String[] rules = new String[] {"#content", "a", "a:hover"};
					assertTrue("There should be a total of 3 rules supplied stylesheet.", list.getLength() == rules.length);

					for (int i = 0; i < list.getLength(); i++) {
						CSSRule rule = list.item(i);
						assertTrue("Rule should be a style rule. rule.getType() == " + rule.getType(), rule.getType() == ICSSNode.STYLERULE_NODE);
						CSSStyleRule style = (CSSStyleRule)rule;
						assertEquals("Selector text did not match.", rules[i], style.getSelectorText().trim());
					}
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Tests for the import of a non-existent stylesheet
	 * @throws Exception
	 */
	public void testNonExistentImport() throws Exception {
		String filePath = "/" + PROJECT_NAME + "/WebContent/importDNE.css";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		assertTrue("File doesn't exist: " + filePath, file.exists());

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (model instanceof ICSSModel) {
				ICSSDocument document = ((ICSSModel) model).getDocument();
				if (document instanceof ICSSStyleSheet) {
					CSSRuleList list = ((ICSSStyleSheet) document).getCssRules(true);
					assertTrue("There should be a total of 1 rule.", list.getLength() == 1);
					CSSRule rule = list.item(0);
					assertTrue("Rule should be a style rule. rule.getType() == " + rule.getType(), rule.getType() == ICSSNode.IMPORTRULE_NODE);
					assertEquals("Stylesheet reference is different than expected.", ((CSSImportRule) rule).getHref(), "thisfiledoesntexist.css");
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Tests that imports can be resolved using the various syntaxes for import.
	 * <ul>
	 * <li>@import url("example.css");</li>
	 * <li>@import url(example.css);</li>
	 * <li>@import "example.css";</li>
	 * </ul>
	 * @throws Exception
	 */
	public void testImportMethods() throws Exception {
		String filePath = "/" + PROJECT_NAME + "/WebContent/importMethods.css";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		assertTrue("File doesn't exist: " + filePath, file.exists());

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (model instanceof ICSSModel) {
				ICSSDocument document = ((ICSSModel) model).getDocument();
				if (document instanceof ICSSStyleSheet) {
					CSSRuleList list = ((ICSSStyleSheet) document).getCssRules(true);
					assertTrue("There should be a total of 3 rules.", list.getLength() == 3);
					for (int i = 0; i < list.getLength(); i++) {
						CSSRule rule = list.item(i);
						assertTrue("Rule should be a style rule. rule.getType() == " + rule.getType(), rule.getType() == ICSSNode.STYLERULE_NODE);
						CSSStyleRule style = (CSSStyleRule)rule;
						assertEquals("Selector text did not match.", "#content", style.getSelectorText().trim());
					}
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Tests for the import of a stylesheet in a parent directory
	 * @throws Exception
	 */
	public void testImportFromParentDirectory() throws Exception {
		String filePath = "/" + PROJECT_NAME + "/WebContent/style/sub.css";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		assertTrue("File doesn't exist: " + filePath, file.exists());

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (model instanceof ICSSModel) {
				ICSSDocument document = ((ICSSModel) model).getDocument();
				if (document instanceof ICSSStyleSheet) {
					CSSRuleList list = ((ICSSStyleSheet) document).getCssRules(true);
					assertTrue("There should be a total of 1 rule.", list.getLength() == 1);
					CSSRule rule = list.item(0);
					assertTrue("Rule should be a style rule. rule.getType() == " + rule.getType(), rule.getType() == ICSSNode.STYLERULE_NODE);
					assertEquals("Stylesheet reference is different than expected.", ((CSSStyleRule) rule).getSelectorText(), "#content");
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
}
