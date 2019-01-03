/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

class TagTranslationAdapter extends JSPTranslationAdapter {
	TagTranslationAdapter(IDOMModel xmlModel) {
		super(xmlModel);
	}

	JSPTranslator createTranslator() {
		return new TagTranslator();
	}
}
