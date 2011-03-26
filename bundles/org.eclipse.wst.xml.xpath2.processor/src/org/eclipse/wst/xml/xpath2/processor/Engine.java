package org.eclipse.wst.xml.xpath2.processor;

import org.eclipse.wst.xml.xpath2.api.StaticContext;
import org.eclipse.wst.xml.xpath2.api.XPath2Engine;
import org.eclipse.wst.xml.xpath2.api.XPath2Expression;
import org.eclipse.wst.xml.xpath2.api.XPath2PatternSet;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;

/**
 * @since 2.0
 */
public class Engine implements XPath2Engine {

	public XPath2Expression parseExpression(String expression, StaticContext context) {

		XPath xPath = new JFlexCupParser().parse(expression);
		xPath.setStaticContext(context);
		StaticChecker name_check = new StaticNameResolver(context);
		name_check.check(xPath);
		return xPath;
	}

	public XPath2PatternSet createPatternSet() {
		// Not yet...
		throw new UnsupportedOperationException("Not yet");
	}

}
