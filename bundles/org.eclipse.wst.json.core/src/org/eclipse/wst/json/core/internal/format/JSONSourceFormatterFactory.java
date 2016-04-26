/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

public class JSONSourceFormatterFactory {

	private JSONSourceFormatterFactory() {
		super();
	}

	public IJSONSourceFormatter getSourceFormatter(INodeNotifier target) {
		IJSONNode node = (IJSONNode) target;
		short type = node.getNodeType();
		switch (type) {
		case IJSONNode.DOCUMENT_NODE:
			return JSONDocumentFormatter.getInstance();
		case IJSONNode.OBJECT_NODE:
			return JSONObjectFormatter.getInstance();
		case IJSONNode.ARRAY_NODE:
			return JSONArrayFormatter.getInstance();
		case IJSONNode.PAIR_NODE:
			return JSONPairFormatter.getInstance();
		default:
			return UnknownRuleFormatter.getInstance();
		}
	}

	public synchronized static JSONSourceFormatterFactory getInstance() {
		if (fInstance == null) {
			fInstance = new JSONSourceFormatterFactory();
		}
		return fInstance;
	}

	private static JSONSourceFormatterFactory fInstance;
}
