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
 * Copyright 2002-2004 The Apache Software Foundation.
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
 * $Id: ExtensionEvent.java,v 1.2 2008/03/28 02:38:17 dacarver Exp $
 */

package org.eclipse.wst.xsl.core.internal.compiler.xslt10.trace;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.transformer.TransformerImpl;

/**
 * An event representing an extension call.
 */
public class ExtensionEvent {

	public static final int DEFAULT_CONSTRUCTOR = 0;
	public static final int METHOD = 1;
	public static final int CONSTRUCTOR = 2;

	public final int m_callType;
	public final TransformerImpl m_transformer;
	public final Object m_method;
	public final Object m_instance;
	public final Object[] m_arguments;

	public ExtensionEvent(TransformerImpl transformer, Method method,
			Object instance, Object[] arguments) {
		m_transformer = transformer;
		m_method = method;
		m_instance = instance;
		m_arguments = arguments;
		m_callType = METHOD;
	}

	public ExtensionEvent(TransformerImpl transformer, Constructor constructor,
			Object[] arguments) {
		m_transformer = transformer;
		m_instance = null;
		m_arguments = arguments;
		m_method = constructor;
		m_callType = CONSTRUCTOR;
	}

	public ExtensionEvent(TransformerImpl transformer, Class clazz) {
		m_transformer = transformer;
		m_instance = null;
		m_arguments = null;
		m_method = clazz;
		m_callType = DEFAULT_CONSTRUCTOR;
	}

}
