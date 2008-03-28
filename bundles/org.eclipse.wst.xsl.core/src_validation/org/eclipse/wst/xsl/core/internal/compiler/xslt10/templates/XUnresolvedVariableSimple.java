/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 224197 - initial API and implementation
 *                    based on work from Apache Xalan 2.7.0
 *******************************************************************************/
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: XUnresolvedVariableSimple.java,v 1.2 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * This is the same as XUnresolvedVariable, but it assumes that the context is
 * already set up. For use with psuedo variables. Also, it holds an Expression
 * object, instead of an ElemVariable. It must only hold static context, since a
 * single copy will be held in the template.
 */
public class XUnresolvedVariableSimple extends XObject {
	static final long serialVersionUID = -1224413807443958985L;

	public XUnresolvedVariableSimple(ElemVariable obj) {
		super(obj);
	}

	/**
	 * For support of literal objects in xpaths.
	 * 
	 * @param xctxt
	 *            The XPath execution context.
	 * 
	 * @return This object.
	 * 
	 * @throws javax.xml.transform.TransformerException
	 */
	@Override
	public XObject execute(XPathContext xctxt)
			throws javax.xml.transform.TransformerException {
		Expression expr = ((ElemVariable) m_obj).getSelect().getExpression();
		XObject xobj = expr.execute(xctxt);
		xobj.allowDetachToRelease(false);
		return xobj;
	}

	/**
	 * Tell what kind of class this is.
	 * 
	 * @return CLASS_UNRESOLVEDVARIABLE
	 */
	@Override
	public int getType() {
		return CLASS_UNRESOLVEDVARIABLE;
	}

	/**
	 * Given a request type, return the equivalent string. For diagnostic
	 * purposes.
	 * 
	 * @return An informational string.
	 */
	@Override
	public String getTypeString() {
		return "XUnresolvedVariableSimple (" + object().getClass().getName()
				+ ")";
	}

}
