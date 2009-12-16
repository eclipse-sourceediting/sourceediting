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
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xml.xpath.ui.internal.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathComputer {
	private static final int UPDATE_DELAY = 500;
	private static final byte[] XPATH_LOCK = new byte[0];
	private XPathView xpathView;
	private IModelStateListener modelStateListener = new IModelStateListener() {

		public void modelAboutToBeChanged(IStructuredModel model) {
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		}

		public void modelChanged(IStructuredModel model) {
			updateXPath();
			compute();
		}

		public void modelDirtyStateChanged(IStructuredModel model,
				boolean isDirty) {
		}

		public void modelReinitialized(IStructuredModel structuredModel) {
		}

		public void modelResourceDeleted(IStructuredModel model) {
		}

		public void modelResourceMoved(IStructuredModel oldModel,
				IStructuredModel newModel) {
		}
	};
	private Node node;
	private IStructuredModel model;
	private String expression;
	private String text;
	private NodeList nodeList;

	public XPathComputer(XPathView xpathView) {
		this.xpathView = xpathView;
	}

	public void setModel(IStructuredModel model) {
		if (this.model != null) {
			this.model.removeModelStateListener(modelStateListener);
		}
		this.model = model;
		if (model != null) {
			model.addModelStateListener(modelStateListener);
			updateXPath();
		} else {
			node = null;
		}
	}

	private void updateXPath() {
		Document doc = (Document) model.getAdapter(Document.class);
		if (doc == null) {
			return;
		}

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
				if (!xps[0].equals(expression)) {
					return Status.CANCEL_STATUS;
				}
				return doCompute(xps[0]);
			}
		};
		refresh.setSystem(true);
		refresh.setPriority(Job.SHORT);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) xpathView
				.getSite().getService(IWorkbenchSiteProgressService.class);
		service.schedule(refresh, UPDATE_DELAY);
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
			if ((xp != null) && (node != null)) {
				synchronized (XPATH_LOCK) {
					this.nodeList = XSLTXPathHelper.selectNodeList(node, xp);
				}
			}
		} catch (TransformerException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public void dispose() {
		if (model != null) {
			model.removeModelStateListener(modelStateListener);
			model = null;
		}
	}
}
