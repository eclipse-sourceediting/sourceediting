/*******************************************************************************
 * Copyright (c) 2007 Standards for Technology in Automotive Retail
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
 * $Id: ExtensionHandlerExsltFunction.java,v 1.3 2008/03/28 02:38:17 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.extensions;

import java.io.IOException;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.eclipse.wst.xsl.core.compiler.xslt10.res.Messages;
import org.eclipse.wst.xsl.core.compiler.xslt10.res.XSLTErrorResources;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.Constants;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemExsltFuncResult;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemExsltFunction;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplate;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplateElement;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.Stylesheet;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.StylesheetRoot;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

/**
 * Execute EXSLT functions, determine the availability of EXSLT functions, and
 * the availability of an EXSLT result element.
 */
public class ExtensionHandlerExsltFunction extends ExtensionHandler {
	private String m_namespace;
	private StylesheetRoot m_stylesheet;
	private static final QName RESULTQNAME = new QName(
			org.apache.xml.utils.Constants.S_EXSLT_FUNCTIONS_URL,
			Constants.EXSLT_ELEMNAME_FUNCRESULT_STRING);

	/**
	 * Constructor called from ElemExsltFunction runtimeInit().
	 */
	public ExtensionHandlerExsltFunction(String ns, StylesheetRoot stylesheet) {
		super(ns, "xml"); // required by ExtensionHandler interface.
		m_namespace = ns;
		m_stylesheet = stylesheet;
	}

	/**
	 * Required by ExtensionHandler (an abstract method). No-op.
	 */
	@Override
	public void processElement(String localPart, ElemTemplateElement element,
			TransformerImpl transformer, Stylesheet stylesheetTree,
			Object methodKey) throws TransformerException, IOException {
	}

	/**
	 * Get the ElemExsltFunction element associated with the function.
	 * 
	 * @param funcName
	 *            Local name of the function.
	 * @return the ElemExsltFunction element associated with the function, null
	 *         if none exists.
	 */
	public ElemExsltFunction getFunction(String funcName) {
		QName qname = new QName(m_namespace, funcName);
		ElemTemplate templ = m_stylesheet.getTemplateComposed(qname);
		if (templ != null && templ instanceof ElemExsltFunction)
			return (ElemExsltFunction) templ;
		else
			return null;
	}

	/**
	 * Does the EXSLT function exist?
	 * 
	 * @param funcName
	 *            Local name of the function.
	 * @return true if the function exists.
	 */
	@Override
	public boolean isFunctionAvailable(String funcName) {
		return getFunction(funcName) != null;
	}

	/**
	 * If an element-available() call applies to an EXSLT result element within
	 * an EXSLT function element, return true.
	 * 
	 * Note: The EXSLT function element is a template-level element, and
	 * element-available() returns false for it.
	 * 
	 * @param elemName
	 *            name of the element.
	 * @return true if the function is available.
	 */
	@Override
	public boolean isElementAvailable(String elemName) {
		if (!(new QName(m_namespace, elemName).equals(RESULTQNAME))) {
			return false;
		} else {
			ElemTemplateElement elem = m_stylesheet.getFirstChildElem();
			while (elem != null && elem != m_stylesheet) {
				if (elem instanceof ElemExsltFuncResult
						&& ancestorIsFunction(elem))
					return true;
				ElemTemplateElement nextElem = elem.getFirstChildElem();
				if (nextElem == null)
					nextElem = elem.getNextSiblingElem();
				if (nextElem == null)
					nextElem = elem.getParentElem();
				elem = nextElem;
			}
		}
		return false;
	}

	/**
	 * Determine whether the func:result element is within a func:function
	 * element. If not, it is illegal.
	 */
	private boolean ancestorIsFunction(ElemTemplateElement child) {
		while (child.getParentElem() != null
				&& !(child.getParentElem() instanceof StylesheetRoot)) {
			if (child.getParentElem() instanceof ElemExsltFunction)
				return true;
			child = child.getParentElem();
		}
		return false;
	}

	/**
	 * Execute the EXSLT function and return the result value.
	 * 
	 * @param funcName
	 *            Name of the EXSLT function.
	 * @param args
	 *            The arguments of the function call.
	 * @param methodKey
	 *            Not used.
	 * @param exprContext
	 *            Used to get the XPathContext.
	 * @return the return value of the function evaluation.
	 * @throws TransformerException
	 */
	@Override
	public Object callFunction(String funcName, Vector args, Object methodKey,
			ExpressionContext exprContext) throws TransformerException {
		throw new TransformerException("This method should not be called.");
	}

	/**
	 * Execute the EXSLT function and return the result value.
	 * 
	 * @param extFunction
	 *            The XPath extension function
	 * @param args
	 *            The arguments of the function call.
	 * @param exprContext
	 *            The context in which this expression is being executed.
	 * @return the return value of the function evaluation.
	 * @throws TransformerException
	 */
	@Override
	public Object callFunction(FuncExtFunction extFunction, Vector args,
			ExpressionContext exprContext) throws TransformerException {
		// Find the template which invokes this EXSLT function.
		ExpressionNode parent = extFunction.exprGetParent();
		while (parent != null && !(parent instanceof ElemTemplate)) {
			parent = parent.exprGetParent();
		}

		ElemTemplate callerTemplate = (parent != null) ? (ElemTemplate) parent
				: null;

		XObject[] methodArgs;
		methodArgs = new XObject[args.size()];
		try {
			for (int i = 0; i < methodArgs.length; i++) {
				methodArgs[i] = XObject.create(args.elementAt(i));
			}

			ElemExsltFunction elemFunc = getFunction(extFunction
					.getFunctionName());

			if (null != elemFunc) {
				XPathContext context = exprContext.getXPathContext();
				TransformerImpl transformer = (TransformerImpl) context
						.getOwnerObject();
				transformer.pushCurrentFuncResult(null);

				elemFunc.execute(transformer, methodArgs);

				XObject val = (XObject) transformer.popCurrentFuncResult();
				return (val == null) ? new XString("") // value if no result
														// element.
						: val;
			} else {
				throw new TransformerException(Messages.createMessage(
						XSLTErrorResources.ER_FUNCTION_NOT_FOUND,
						new Object[] { extFunction.getFunctionName() }));
			}
		} catch (TransformerException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

}
