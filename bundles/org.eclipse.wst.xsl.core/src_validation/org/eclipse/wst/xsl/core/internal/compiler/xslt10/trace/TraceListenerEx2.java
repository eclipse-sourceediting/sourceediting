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
 * $Id: TraceListenerEx2.java,v 1.2 2008/03/28 02:38:17 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.trace;

/**
 * Extends TraceListenerEx but adds a EndTrace event.
 * 
 * @xsl.usage advanced
 */
public interface TraceListenerEx2 extends TraceListenerEx {
	/**
	 * Method that is called when the end of a trace event occurs. The method is
	 * blocking. It must return before processing continues.
	 * 
	 * @param ev
	 *            the trace event.
	 */
	public void traceEnd(TracerEvent ev);
}
