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
 * $Id: XSLTVisitable.java,v 1.2 2008/03/28 02:38:15 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates;

/**
 * A class that implements this interface will call a XSLTVisitor for itself and
 * members within it's heararchy. If the XSLTVistor's method returns false, the
 * sub-member heararchy will not be traversed.
 */
public interface XSLTVisitable {
	/**
	 * This will traverse the heararchy, calling the visitor for each member. If
	 * the called visitor method returns false, the subtree should not be
	 * called.
	 * 
	 * @param visitor
	 *            The visitor whose appropriate method will be called.
	 */
	public void callVisitors(XSLTVisitor visitor);
}
