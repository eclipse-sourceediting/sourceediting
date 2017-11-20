/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal.validation;

import javax.xml.transform.*;
import java.util.logging.*;

/**
 * This class listens for Errors tossed by a XSL Processor.
 * 
 * @author dcarver
 * 
 */
public class XSLErrorListener implements ErrorListener {

	private Logger logger;

	/**
	 * TODO: Add Javadoc
	 * @param logger
	 */
	public XSLErrorListener(Logger logger) {
		this.logger = logger;
	}

	/**
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	public void warning(TransformerException exception) {

		logger.log(Level.WARNING, exception.getMessage(), exception);

		// Don't throw an exception and stop the processor
		// just for a warning; but do log the problem
	}

	/** 
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	public void error(TransformerException exception)
			throws TransformerException {

		logger.log(Level.SEVERE, exception.getMessage(), exception);
		// XSLT is not as draconian as XML. There are numerous errors
		// which the processor may but does not have to recover from;
		// e.g. multiple templates that match a node with the same
		// priority. I do not want to allow that so I throw this
		// exception here.
		throw exception;

	}

	/**
	 *  (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	public void fatalError(TransformerException exception)
			throws TransformerException {

		logger.log(Level.SEVERE, exception.getMessage(), exception);

		// This is an error which the processor cannot recover from;
		// e.g. a malformed stylesheet or input document
		// so I must throw this exception here.
		throw exception;

	}

}
