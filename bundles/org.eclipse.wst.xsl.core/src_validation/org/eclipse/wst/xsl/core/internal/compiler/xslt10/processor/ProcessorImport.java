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
 * $Id: ProcessorImport.java,v 1.2 2008/03/28 02:38:16 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * This class processes parse events for an xsl:import element.
 * 
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
 * 
 * @xsl.usage internal
 */
public class ProcessorImport extends ProcessorInclude {
	static final long serialVersionUID = -8247537698214245237L;

	/**
	 * Get the stylesheet type associated with an imported stylesheet
	 * 
	 * @return the type of the stylesheet
	 */
	@Override
	protected int getStylesheetType() {
		return StylesheetHandler.STYPE_IMPORT;
	}

	/**
	 * Get the error number associated with this type of stylesheet importing
	 * itself
	 * 
	 * @return the appropriate error number
	 */
	@Override
	protected String getStylesheetInclErr() {
		return XSLTErrorResources.ER_IMPORTING_ITSELF;
	}

}
