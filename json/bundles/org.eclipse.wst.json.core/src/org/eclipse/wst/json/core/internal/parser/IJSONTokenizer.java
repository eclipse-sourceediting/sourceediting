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
package org.eclipse.wst.json.core.internal.parser;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public interface IJSONTokenizer {

	void setInitialState(int initialState);

	void setInitialBufferSize(int bufsize);

	boolean isEOF();

	void reset(Reader reader, int i);

	ITextRegion getNextToken() throws IOException;

	void reset(char[] cs);

	int getOffset();
}
