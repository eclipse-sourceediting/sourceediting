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
 * $Id: ResultNameSpace.java,v 1.2 2008/03/28 02:38:16 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer;

/**
 * A representation of a result namespace. One of these will be pushed on the
 * result tree namespace stack for each result tree element.
 * 
 * @xsl.usage internal
 */
public class ResultNameSpace {

	/** Pointer to next ResultNameSpace */
	public ResultNameSpace m_next = null;

	/** Prefix of namespace */
	public String m_prefix;

	/** Namespace URI */
	public String m_uri; // if null, then Element namespace is empty.

	/**
	 * Construct a namespace for placement on the result tree namespace stack.
	 * 
	 * @param prefix
	 *            of result namespace
	 * @param uri
	 *            URI of result namespace
	 */
	public ResultNameSpace(String prefix, String uri) {
		m_prefix = prefix;
		m_uri = uri;
	}
}
