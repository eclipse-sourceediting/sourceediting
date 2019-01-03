/*******************************************************************************
 * Copyright (c) 2010, 2018 IBM Corporation and others.
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
package org.eclipse.wst.xsl.ui.tests.style;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;

public class FakeStructuredRegion extends BasicStructuredDocumentRegion {

	@Override
	public ITextRegion getFirstRegion() {
		return null;
	}
}
