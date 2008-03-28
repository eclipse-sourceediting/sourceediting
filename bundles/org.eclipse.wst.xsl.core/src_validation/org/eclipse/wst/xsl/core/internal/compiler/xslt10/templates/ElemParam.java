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
 * $Id: ElemParam.java,v 1.2 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

import javax.xml.transform.TransformerException;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer.TransformerImpl;
import org.apache.xpath.VariableStack;
import org.apache.xpath.objects.XObject;

/**
 * Implement xsl:param.
 * 
 * <pre>
 * &lt;!ELEMENT xsl:param %template;&gt;
 * &lt;!ATTLIST xsl:param
 *   name %qname; #REQUIRED
 *   select %expr; #IMPLIED
 * &gt;
 * </pre>
 * 
 * @see <a href="http://www.w3.org/TR/xslt#variables">variables in XSLT
 *      Specification</a>
 * @xsl.usage advanced
 */
public class ElemParam extends ElemVariable {
	static final long serialVersionUID = -1131781475589006431L;
	int m_qnameID;

	/**
	 * Constructor ElemParam
	 * 
	 */
	public ElemParam() {
	}

	/**
	 * Get an int constant identifying the type of element.
	 * 
	 * @see org.apache.xalan.templates.Constants
	 * 
	 * @return The token ID of the element
	 */
	@Override
	public int getXSLToken() {
		return Constants.ELEMNAME_PARAMVARIABLE;
	}

	/**
	 * Return the node name.
	 * 
	 * @return The element's name
	 */
	@Override
	public String getNodeName() {
		return Constants.ELEMNAME_PARAMVARIABLE_STRING;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param param
	 *            Element from an xsl:param
	 * 
	 * @throws TransformerException
	 */
	public ElemParam(ElemParam param) throws TransformerException {
		super(param);
	}

	/**
	 * This function is called after everything else has been recomposed, and
	 * allows the template to set remaining values that may be based on some
	 * other property that depends on recomposition.
	 */
	@Override
	public void compose(StylesheetRoot sroot) throws TransformerException {
		super.compose(sroot);
		m_qnameID = sroot.getComposeState().getQNameID(m_qname);
		int parentToken = m_parentNode.getXSLToken();
		if (parentToken == Constants.ELEMNAME_TEMPLATE
				|| parentToken == Constants.EXSLT_ELEMNAME_FUNCTION)
			((ElemTemplate) m_parentNode).m_inArgsSize++;
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
	@Override
	public void execute(TransformerImpl transformer)
			throws TransformerException {
		if (transformer.getDebug())
			transformer.getTraceManager().fireTraceEvent(this);

		VariableStack vars = transformer.getXPathContext().getVarStack();

		if (!vars.isLocalSet(m_index)) {

			int sourceNode = transformer.getXPathContext().getCurrentNode();
			XObject var = getValue(transformer, sourceNode);

			// transformer.getXPathContext().getVarStack().pushVariable(m_qname,
			// var);
			transformer.getXPathContext().getVarStack().setLocalVariable(
					m_index, var);
		}

		if (transformer.getDebug())
			transformer.getTraceManager().fireTraceEndEvent(this);
	}

}
