/*******************************************************************************
 * Copyright (c) 2008-2011 Chase Technology Ltd - http://www.chasetechnology.co.uk and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 261428 - XPath View not respecting namespaces.
 *     Jesper Steen Moller - bug 313992 - XPath evaluation does not show atomics
 *     Jesper Steen Moller - bug 323448 - XPath view doesn't show runtime error information well (or at all)
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.xpath.jaxp.XPathFactoryImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.xpath.core.XPathCorePlugin;
import org.eclipse.wst.xml.xpath.core.XPathProcessorPreferences;
import org.eclipse.wst.xml.xpath.core.util.NodeListImpl;
import org.eclipse.wst.xml.xpath.core.util.SimpleXPathEngine;
import org.eclipse.wst.xml.xpath.core.util.XPath20Helper;
import org.eclipse.wst.xml.xpath.core.util.XPathCoreHelper;
import org.eclipse.wst.xml.xpath.ui.internal.Messages;
import org.eclipse.wst.xml.xpath.ui.internal.XPathUIPlugin;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.internal.DefaultResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSString;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathComputer {
	private static final int UPDATE_DELAY = 500;
	private static final byte[] XPATH_LOCK = new byte[0];
	private boolean xpath20 = true;
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
	
	private SimpleXPathEngine xpath2Engine;
	private SimpleXPathEngine xpath1Engine;

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
				SimpleXPathEngine engine = getCurrentEngine();
				
				IDOMDocument doc = (node.getNodeType() == Node.DOCUMENT_NODE) ? (IDOMDocument) node : (IDOMDocument) node.getOwnerDocument();
				updateNamespaces(engine, doc);

				engine.parse(text);
				this.expression = text;
			} else {
				this.expression = null;
			}
		}
	}

	private SimpleXPathEngine getCurrentEngine() {
		xpath20 = XPathCoreHelper.getPreferences().getBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, false);
		if (xpath20) {
			if (xpath2Engine == null) xpath2Engine = new XPath20Helper.XPath2Engine();
			return xpath2Engine;
		} else {
			if (xpath1Engine == null) xpath1Engine = new XPath10Engine();
			return xpath1Engine;
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
		synchronized (XPATH_LOCK) {
		}
		Job refresh = new Job(Messages.XPathComputer_5) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
//				if (!xps[0].equals(expression)) {
//					return Status.CANCEL_STATUS;
//				}
				return doCompute();
			}
		};
		refresh.setSystem(true);
		refresh.setPriority(Job.SHORT);
		IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) xpathView
				.getSite().getService(IWorkbenchSiteProgressService.class);
		service.schedule(refresh, UPDATE_DELAY);
	}

	private IStatus doCompute() {
		IStatus status = executeXPath();

		xpathView.xpathRecomputed(nodeList, status);
		return status;
	}

	private IStatus executeXPath() {
		IStatus status = Status.CANCEL_STATUS;
		try {
			if (node != null) {
				synchronized (XPATH_LOCK) {
					
					SimpleXPathEngine engine = getCurrentEngine();
					if (! engine.isValid()) engine.parse(text);
					
					status = evaluateXPath(engine);
				}
			}
		} catch (XPathExpressionException e) {
			return new Status(IStatus.CANCEL, XPathCorePlugin.PLUGIN_ID, e.getMessage());
		}
		return status;
	}

	protected IStatus evaluateXPath(SimpleXPathEngine engine) throws XPathExpressionException {
		IDOMDocument doc = null;
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			doc = (IDOMDocument) node;
		} else {
			doc = (IDOMDocument) node.getOwnerDocument();
		}
		updateNamespaces(engine, doc);

		try {
			this.nodeList = (NodeList) engine.execute(node);
			return Status.OK_STATUS;
		} catch (DynamicError de) {
			return new Status(IStatus.CANCEL, XPathCorePlugin.PLUGIN_ID, de.getMessage() + " (" + de.code() + ")");
		} catch (Throwable t) {
			return new Status(IStatus.ERROR, XPathCorePlugin.PLUGIN_ID, t.getMessage());
		}
	}

	private void updateNamespaces(SimpleXPathEngine engine, IDOMDocument doc) {
		final List<NamespaceInfo> namespaces = XPathUIPlugin.getDefault().getNamespaceInfo(doc);
		if (namespaces != null) {
			engine.setNamespaceContext(new DefaultNamespaceContext(namespaces));
		} else {
			engine.setNamespaceContext(null);
		}
	}
	
	public static class XPath10Engine implements SimpleXPathEngine {

		private javax.xml.xpath.XPathExpression goodXPath = null;

		private NamespaceContext namespaceContext;

		public void setNamespaceContext(NamespaceContext namespaceContext) {
			this.namespaceContext = namespaceContext; 
		}
		
		private javax.xml.xpath.XPath newXPath = new XPathFactoryImpl().newXPath();

		public void parse(String expression) throws XPathExpressionException {
			goodXPath = null;
			if (namespaceContext != null) {
				newXPath.setNamespaceContext(namespaceContext);
			}
			goodXPath = newXPath.compile(expression);
		}			

		public NodeList execute(Node contextNode) throws XPathExpressionException {
				try {
					return (NodeList) goodXPath.evaluate(contextNode, XPathConstants.NODESET);
				} catch (XPathExpressionException xee) {
					if (xee.getCause() != null && xee.getCause().getMessage().indexOf("Can not convert ") >= 0) {
						String value = (String) goodXPath.evaluate(contextNode, XPathConstants.STRING);
						return new NodeListImpl(new DefaultResultSequence(new XSString(value)));
					} else throw xee;
				}
			}
			
		public boolean isValid() {
			return goodXPath != null;
		}
	}
	
	public void dispose() {
		if (model != null) {
			model.removeModelStateListener(modelStateListener);
			model = null;
		}
	}
}
