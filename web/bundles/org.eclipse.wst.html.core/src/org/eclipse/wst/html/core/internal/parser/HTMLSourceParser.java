/*******************************************************************************
 * Copyright (c) 2016, 2018 IBM Corporation and others.
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
package org.eclipse.wst.html.core.internal.parser;

import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTokenizer;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;

public class HTMLSourceParser extends XMLSourceParser {
	protected BlockTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new HTMLTokenizer();
		}
		return fTokenizer;
	}

}
