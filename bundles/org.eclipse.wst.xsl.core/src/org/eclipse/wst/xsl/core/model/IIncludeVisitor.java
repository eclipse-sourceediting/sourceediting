package org.eclipse.wst.xsl.core.model;

import org.eclipse.wst.xsl.core.internal.model.Include;

public interface IIncludeVisitor
{
	boolean visit(Include include);
}
