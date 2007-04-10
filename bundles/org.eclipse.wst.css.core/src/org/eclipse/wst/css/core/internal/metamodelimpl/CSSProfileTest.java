/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.CSSMetaModel;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfile;


class CSSProfileTest {


	/**
	 * Test use only
	 */
	public static void main(String[] args) {
		if (args.length == 0)
			return;
		CSSMetaModel metamodel = null;
		try {
			CSSProfile profile = new CSSProfileImpl("test", new URL(args[0])); //$NON-NLS-1$
			metamodel = profile.getMetaModel();
		}
		catch (MalformedURLException e) {
			Logger.logException(e);
		}
		if (metamodel == null) {
			return;
		}
		/*
		 * CSSMMTypeCollector collector = new CSSMMTypeCollector();
		 * collector.apply(metamodel, CSSMMNode.TYPE_PROPERTY); Iterator
		 * iProperty = collector.getNodes(); while (iProperty.hasNext()) {
		 * CSSMMNode node = (CSSMMNode)iProperty.next();
		 * System.out.println("Property " + node.getName() + " can contain:");
		 * //$NON-NLS-2$ //$NON-NLS-1$ Iterator iValue =
		 * node.getDescendants(); while (iValue.hasNext()) {
		 * debugOut((CSSMMNode)iValue.next(), 0, false); } }
		 */
		debugOut(metamodel, 0, true);
	}

	static void debugOut(CSSMMNode node, int depth, boolean recursive) {
		String type = node.getType();
		for (int i = 0; i < depth; i++) {
			System.out.print("\t"); //$NON-NLS-1$
		}
		System.out.print("[ " + node.getName() + "(" + type + ") ]"); //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		if (type == CSSMMNode.TYPE_UNIT || type == CSSMMNode.TYPE_KEYWORD || type == CSSMMNode.TYPE_FUNCTION || type == CSSMMNode.TYPE_SELECTOR) {
			System.out.print(" -> " + node.toString()); //$NON-NLS-1$
		}
		System.out.println();

		if (recursive) {
			java.util.Iterator iNode = node.getChildNodes();
			while (iNode.hasNext()) {
				debugOut((CSSMMNode) iNode.next(), depth + 1, true);
			}
		}
	}
}
