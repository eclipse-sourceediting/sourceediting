/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 261428 - XPath View not respecting namespaces.
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;


import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

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
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xml.xpath.messages.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathComputer {
	private static final int UPDATE_DELAY = 200;
	private static final byte[] XPATH_LOCK = new byte[0];
	private XPathView xpathView;
	private CMDocumentManager cmDocumentManager;
	private CMDocumentManagerListener fCMDocumentManagerListener = new DocManagerListener();
	private Node node;
	private XPath path;
	private IStructuredModel model;
	private String expression;
	private String text;
	private NodeList nodeList;

	public XPathComputer(XPathView xpathView) {
		this.xpathView = xpathView;
	}

	public void setModel(IStructuredModel model) {
		this.model = model;
		if (this.cmDocumentManager != null) {
			cmDocumentManager.removeListener(fCMDocumentManagerListener);
		}
		if (model != null) {
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(model);
			if (modelQuery != null) {
				cmDocumentManager = modelQuery.getCMDocumentManager();
				if (cmDocumentManager != null)
					cmDocumentManager.addListener(fCMDocumentManagerListener);
			}
			updateXPath();
		} else {
			cmDocumentManager = null;
			node = null;
			path = null;
		}
	}

	private void updateXPath() {
		Document doc = (Document) model.getAdapter(Document.class);
		if (doc == null)
			return;

		try {
			updateExpression();
		} catch (XPathExpressionException e) {

		}
	}

	private void updateExpression() throws XPathExpressionException {
		synchronized (XPATH_LOCK) {
			if (text != null) {
				XSLTXPathHelper.compile(text);
				this.expression = text;
			} else {
				this.expression = null;
			}
		}
	}

	public void setText(String text) throws XPathExpressionException {
		this.text = text;
		updateExpression();
	}

	public void setSelectedNode(Node node) {
		this.node = node;
	}

	public void compute() {
		final String[] xps = new String[1];
		synchronized (XPATH_LOCK) {
			xps[0] = expression;
		}
		Job refresh = new Job(Messages.XPathComputer_5) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (xps[0] != expression)
					return Status.CANCEL_STATUS;
				return doCompute(xps[0]);
			}
		};
		refresh.setSystem(true);
		refresh.setPriority(Job.SHORT);
		refresh.schedule(UPDATE_DELAY);
	}

	private IStatus doCompute(String xp) {
		IStatus status = executeXPath(xp);

		xpathView.getSite().getShell().getDisplay().asyncExec(new Runnable() {

			public void run() {
				xpathView.xpathRecomputed(nodeList);
			}
		});
		return status;
	}

	private IStatus executeXPath(String xp) {
		try {
			if (xp != null && node != null) {
				synchronized (XPATH_LOCK) {
					this.nodeList = (NodeList) XSLTXPathHelper.selectNodeList(
							node, xp);
				}
			}
		} catch (TransformerException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	private class DocManagerListener implements CMDocumentManagerListener {
		public void propertyChanged(CMDocumentManager cmDocumentManager,
				String propertyName) {
			updateXPath();
			compute();
		}

		public void cacheCleared(CMDocumentCache cache) {
		}

		public void cacheUpdated(CMDocumentCache cache, String uri,
				int oldStatus, int newStatus, CMDocument cmDocument) {
			updateXPath();
			compute();
		}
	}

	public void dispose() {
		if (this.cmDocumentManager != null) {
			cmDocumentManager.removeListener(fCMDocumentManagerListener);
		}
	}
}
