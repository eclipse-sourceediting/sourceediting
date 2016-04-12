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
