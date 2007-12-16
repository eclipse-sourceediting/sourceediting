/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.CoreException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FastXMLInfo
{
	private boolean stylesheet;
	private boolean stylesheetPI;
	private String outputMethod;
	private static SAXParser parser;
	private static DefaultHandler handler = new DefaultHandler()
	{
		@Override
		public void processingInstruction(String target, String data) throws SAXException
		{
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			throw new StopParsingException(localName);
		}
	};

	static
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			if (factory != null)
				parser = factory.newSAXParser();
		}
		catch (Throwable e)
		{
			// do nothing
		}
	}

	public String getOutputMethod()
	{
		return outputMethod;
	}

	public void setOutputMethod(String outputMethod)
	{
		this.outputMethod = outputMethod;
	}

	public boolean isStylesheet()
	{
		return stylesheet;
	}

	public void setStylesheet(boolean stylesheet)
	{
		this.stylesheet = stylesheet;
	}

	public boolean hasStylesheetPI()
	{
		return stylesheetPI;
	}

	public void setStylesheetPI(boolean stylesheetPI)
	{
		this.stylesheetPI = stylesheetPI;
	}

	public static FastXMLInfo getBasicInfo(File file) throws CoreException
	{
		FastXMLInfo info = new FastXMLInfo();
		if (parser != null)
		{
			try
			{
				parser.parse(file, handler);
			}
			catch (IOException e)
			{
				info = null;
			}
			catch (StopParsingException e)
			{
				// Do nothing
			}
			catch (SAXException e)
			{
				info = null;
			}
		}
		else
		{
			info = null;
		}
		return info;
	}

	private static class StopParsingException extends SAXException
	{
		private static final long serialVersionUID = 1L;

		public StopParsingException(String message)
		{
			super(message);
		}

	}
}
