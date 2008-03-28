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
 * $Id: ElemVariablePsuedo.java,v 1.3 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.xpath.XPath;

public class ElemVariablePsuedo extends ElemVariable {
	static final long serialVersionUID = 692295692732588486L;
	XUnresolvedVariableSimple m_lazyVar;

	/**
	 * Set the "select" attribute. If the variable-binding element has a select
	 * attribute, then the value of the attribute must be an expression and the
	 * value of the variable is the object that results from evaluating the
	 * expression. In this case, the content of the variable must be empty.
	 * 
	 * @param v
	 *            Value to set for the "select" attribute.
	 */
	@Override
	public void setSelect(XPath v) {
		super.setSelect(v);
		m_lazyVar = new XUnresolvedVariableSimple(this);
	}

	/**
	 * Execute a variable declaration and push it onto the variable stack.
	 * 
	 * @see <a href="http://www.w3.org/TR/xslt#variables">variables in XSLT
	 *      Specification</a>
	 * 
	 * @param transformer
	 *            non-null reference to the the current transform-time state.
	 * 
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer)
			throws TransformerException {

		// if (TransformerImpl.S_DEBUG)
		// transformer.getTraceManager().fireTraceEvent(this);

		// transformer.getXPathContext().getVarStack().pushVariable(m_qname,
		// var);
		transformer.getXPathContext().getVarStack().setLocalVariable(m_index,
				m_lazyVar);
	}

}
