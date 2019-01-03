/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;

public class JSONGeneratorImpl implements ISourceGenerator {

	private static final ISourceGenerator INSTANCE = new JSONGeneratorImpl();

	public static ISourceGenerator getInstance() {
		return INSTANCE;
	}

	@Override
	public String generateStartTag(IJSONObject element) {
		return "{";
	}
	
	@Override
	public String generateEndTag(IJSONObject element) {
		return "}";
	}
	
	@Override
	public String generateSource(IJSONNode node) {
		return null;
	}

}
