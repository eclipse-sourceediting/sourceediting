/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.editor;

import org.eclipse.jface.text.source.Annotation;

/**
 * Overwrite and override indicator annotation.
 * 
 * @since 1.0
 */
class OverrideIndicator extends Annotation
{

	//private String fAstNodeKey;

	/**
	 * Creates a new override annotation.
	 * 
	 * @param isOverwriteIndicator
	 *            <code>true</code> if this annotation is an overwrite indicator, <code>false</code> otherwise
	 * @param text
	 *            the text associated with this annotation
	 * @param key
	 *            the method binding key
	 * @since 1.0
	 */
	OverrideIndicator(String text, String key)
	{
		super(OverrideIndicatorManager.ANNOTATION_TYPE, false, text);
		//fAstNodeKey = key;
	}

	/**
	 * Opens and reveals the defining method.
	 */
	public void open()
	{
		// CompilationUnit ast= SharedASTProvider.getAST(fJavaElement, SharedASTProvider.WAIT_ACTIVE_ONLY, null);
		// if (ast != null) {
		// ASTNode node= ast.findDeclaringNode(fAstNodeKey);
		// if (node instanceof MethodDeclaration) {
		// try {
		// IMethodBinding methodBinding= ((MethodDeclaration)node).resolveBinding();
		// IMethodBinding definingMethodBinding= Bindings.findOverriddenMethod(methodBinding, true);
		// if (definingMethodBinding != null) {
		// IJavaElement definingMethod= definingMethodBinding.getJavaElement();
		// if (definingMethod != null) {
		// JavaUI.openInEditor(definingMethod, true, true);
		// return;
		// }
		// }
		// } catch (CoreException e) {
		// ExceptionHandler.handle(e, JavaEditorMessages.OverrideIndicatorManager_open_error_title, JavaEditorMessages.OverrideIndicatorManager_open_error_messageHasLogEntry);
		// return;
		// }
		// }
		// }
		// String title= JavaEditorMessages.OverrideIndicatorManager_open_error_title;
		// String message= JavaEditorMessages.OverrideIndicatorManager_open_error_message;
		// MessageDialog.openError(JavaPlugin.getActiveWorkbenchShell(), title, message);
	}
}
