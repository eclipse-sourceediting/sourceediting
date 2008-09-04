/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManagerListener;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.xpath.messages.Messages;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathComputer
{
	private static final int UPDATE_DELAY = 200;
	private static final byte[] XPATH_LOCK = new byte[0];
	private XPathView xpathView;
	private CMDocumentManager cmDocumentManager;
	private CMDocumentManagerListener fCMDocumentManagerListener = new DocManagerListener();
	private Node node;
	private XPath path;
	private IStructuredModel model;
	private XPathExpression expression;
	private String text;
	private NodeList nodeList;

	public XPathComputer(XPathView xpathView)
	{
		this.xpathView = xpathView;
	}

	public void setModel(IStructuredModel model)
	{
		this.model = model;
		if (this.cmDocumentManager != null)
		{
			cmDocumentManager.removeListener(fCMDocumentManagerListener);
		}
		if (model != null)
		{
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(model);
			if (modelQuery != null)
			{
				cmDocumentManager = modelQuery.getCMDocumentManager();
				if (cmDocumentManager != null)
					cmDocumentManager.addListener(fCMDocumentManagerListener);
			}
			updateXPath();
		}
		else
		{
			cmDocumentManager = null;
			node = null;
			path = null;
		}
	}
	
	private void updateXPath()
	{
		this.path = XPathFactory.newInstance().newXPath();
		Document doc = (Document) model.getAdapter(Document.class);
		if (doc == null)
			return;

		Element rootEl = doc.getDocumentElement();
		if (rootEl != null)
		{
			final Map<String,String> namespaces = new HashMap<String,String>();
			findNamespaces(rootEl, namespaces);
			path.setNamespaceContext(new NamespaceContext(){

				public String getNamespaceURI(String arg0)
				{
					return namespaces.get(arg0);
				}

				public String getPrefix(String arg0)
				{
					for (Map.Entry<String, String> entry : namespaces.entrySet())
					{
						if (entry.getValue().equals(arg0))
							return entry.getKey();
					}
					return null;
				}

				@SuppressWarnings("unchecked") //$NON-NLS-1$
				public Iterator getPrefixes(String arg0)
				{
					List<String> vals = new ArrayList<String>(1);
					vals.add(getPrefix(arg0));
					return vals.iterator();
				}
				
			});
		}
		try
		{
			updateExpression();
		}
		catch (XPathExpressionException e)
		{
			// eat it
		}
	}		

	private void updateExpression() throws XPathExpressionException
	{
		synchronized (XPATH_LOCK)
		{
			if (path!=null && text != null)
				this.expression = path.compile(text);
			else
				this.expression = null;
		}
	}		

	public void setText(String text) throws XPathExpressionException
	{
		this.text = text;
		updateExpression();
	}

	private void findNamespaces(Element element, Map<String,String> namespaces)
	{
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Attr att = (Attr) attrs.item(i);
			if ("xmlns".equals(att.getNodeName())) //$NON-NLS-1$
				namespaces.put("", att.getNodeValue()); //$NON-NLS-1$
			else if ("xmlns".equals(att.getPrefix())) //$NON-NLS-1$
				namespaces.put(att.getLocalName(), att.getNodeValue());
		}

		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE)
				findNamespaces((Element) child, namespaces);
		}
	}

	public void setSelectedNode(Node node)
	{
		this.node = node;
	}
	
	public void compute()
	{
		// System.out.println(System.currentTimeMillis()+": "+"compute");
		final XPathExpression[] xps = new XPathExpression[1];
		synchronized (XPATH_LOCK)
		{
			xps[0] = expression;
		}
		Job refresh = new Job(Messages.XPathComputer_5)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (xps[0] != expression)
					return Status.CANCEL_STATUS;
				return doCompute(xps[0]);
			}
		};
		refresh.setSystem(true);
		refresh.setPriority(Job.SHORT);
		refresh.schedule(UPDATE_DELAY);
	}

	private IStatus doCompute(XPathExpression xp)
	{
		try
		{
			if (xp != null && node != null)
			{
				synchronized (XPATH_LOCK)
				{
		            this.nodeList = (NodeList) xp.evaluate(node, XPathConstants.NODESET);
				}
			}
		}
		catch (XPathExpressionException e)
		{
			return Status.CANCEL_STATUS;
		}
		xpathView.getSite().getShell().getDisplay().asyncExec(new Runnable(){

			public void run()
			{
				xpathView.xpathRecomputed(nodeList);
			}
		});
		return Status.OK_STATUS;
	}

	private class DocManagerListener implements CMDocumentManagerListener
	{
		public void propertyChanged(CMDocumentManager cmDocumentManager, String propertyName)
		{
			updateXPath();
			compute();
		}

		public void cacheCleared(CMDocumentCache cache)
		{
		}

		public void cacheUpdated(CMDocumentCache cache, String uri, int oldStatus, int newStatus, CMDocument cmDocument)
		{
			updateXPath();
			compute();
		}
	}

	public void dispose()
	{
		if (this.cmDocumentManager != null)
		{
			cmDocumentManager.removeListener(fCMDocumentManagerListener);
		}
	}
}
