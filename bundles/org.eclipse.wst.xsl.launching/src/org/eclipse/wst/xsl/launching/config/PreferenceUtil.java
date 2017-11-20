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
package org.eclipse.wst.xsl.launching.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @since 1.0
 */
public class PreferenceUtil
{

	public static void createCoreException(Throwable e) throws CoreException
	{
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, 0, e.getMessage(), e));
	}

	public static Document getDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	public static String serializeDocument(Document doc) throws IOException, TransformerException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");  //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");  //$NON-NLS-1$

		DOMSource source = new DOMSource(doc);
		StreamResult outputTarget = new StreamResult(s);
		transformer.transform(source, outputTarget);

		return s.toString("UTF8"); 			 //$NON-NLS-1$
	}

	public static Element[] getChildElements(Element parent, String name)
	{
		List<Element> children = new ArrayList<Element>();
		NodeList list = parent.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i)
		{
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE)
			{
				Element processorElement = (Element) node;
				if (processorElement.getNodeName().equals(name))
				{
					children.add(processorElement);
				}
			}
		}
		return children.toArray(new Element[0]);
	}

	public static String getNodeText(Node node)
	{
		switch (node.getNodeType())
		{
			case Node.ELEMENT_NODE:
				NodeList childNodes = node.getChildNodes();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < childNodes.getLength(); i++)
				{
					Node child = childNodes.item(i);
					if (child.getNodeType() == Node.TEXT_NODE)
					{
						sb.append(child.getNodeValue());
					}
				}
				return sb.toString();
			case Node.TEXT_NODE:
			case Node.ATTRIBUTE_NODE:
			default:
				return node.getNodeValue();
		}
	}

	public static Document getDocument(InputStream stream) throws CoreException
	{
		Document doc = null;
		try
		{
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			doc = parser.parse(new InputSource(stream));
		}
		catch (SAXException e)
		{
			PreferenceUtil.createCoreException(e);
		}
		catch (IOException e)
		{
			PreferenceUtil.createCoreException(e);
		}
		catch (ParserConfigurationException e)
		{
			PreferenceUtil.createCoreException(e);
		}
		catch (FactoryConfigurationError e)
		{
			PreferenceUtil.createCoreException(e);
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				PreferenceUtil.createCoreException(e);
			}
		}
		return doc;
	}
}
