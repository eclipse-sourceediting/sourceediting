/*******************************************************************************
 * Copyright (c) 2010,2011 Jesper Steen Moller - jesper@selskabet.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moller - initial API and implementation
 *     Jesper Steen Moller - bug 323448 - XPath view doesn't show runtime error information well (or at all)
 *******************************************************************************/

package org.eclipse.wst.xml.xpath.ui.internal.views.tests;

import java.lang.reflect.InvocationTargetException;

import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.xpath.core.XPathProcessorPreferences;
import org.eclipse.wst.xml.xpath.core.util.XPathCoreHelper;
import org.eclipse.wst.xml.xpath.ui.internal.hander.tests.StubXPathProcessorHandler;
import org.eclipse.wst.xml.xpath.ui.internal.views.XPathComputer;
import org.eclipse.wst.xml.xpath.ui.internal.views.XPathView;
import org.osgi.service.prefs.Preferences;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

@SuppressWarnings("deprecation")
public class TestXPathComputer extends TestCase {

	private final class MockXPathView extends XPathView {
		
		private NodeList lastList = null;

		public NodeList getLastList() {
			return lastList;
		}
		
		protected void xpathRecomputed(NodeList nodeList, final IStatus error) {
			this.lastList = nodeList;
		}
		
		@Override
		public IWorkbenchPartSite getSite() {
			return new IWorkbenchPartSite() {
				
				public boolean hasService(Class api) {	return IWorkbenchSiteProgressService.class.equals(api);}
				
				public Object getService(Class api) {
					if (IWorkbenchSiteProgressService.class.equals(api)) {
						return new IWorkbenchSiteProgressService() {

							public int getLongOperationTime() {	return 0; }

							public void registerIconForFamily(
									ImageDescriptor icon, Object family) { }

							public void runInUI(IRunnableContext context,
									IRunnableWithProgress runnable,
									ISchedulingRule rule)
									throws InvocationTargetException,
									InterruptedException { runnable.run(new NullProgressMonitor()); }

							public Image getIconFor(Job job) { return null; }

							public void busyCursorWhile(
									IRunnableWithProgress runnable)
									throws InvocationTargetException,
									InterruptedException { }

							public void run(boolean fork, boolean cancelable,
									IRunnableWithProgress runnable)
									throws InvocationTargetException,
									InterruptedException { runnable.run(new NullProgressMonitor()); }

							public void showInDialog(Shell shell, Job job) { }

							public void schedule(Job job, long delay,
									boolean useHalfBusyCursor) {
								this.schedule(job, delay);
							}

							public void schedule(Job job, long delay) {
								job.schedule(delay);
								try {
									job.join();
								} catch (InterruptedException e) {
								}
							}

							public void schedule(Job job) { job.schedule(); }

							public void showBusyForFamily(Object family) { }

							public void warnOfContentChange() { }

							public void incrementBusy() { }

							public void decrementBusy() { }
							
						};
					} else return null;
				}
				
				public Object getAdapter(Class adapter) { return null; }
				public void setSelectionProvider(ISelectionProvider provider) {	}
				public IWorkbenchWindow getWorkbenchWindow() { return null; }
				public Shell getShell() { return null; }
				public ISelectionProvider getSelectionProvider() { return null; }
				public IWorkbenchPage getPage() { return null; }
				public void registerContextMenu(String menuId, MenuManager menuManager,
						ISelectionProvider selectionProvider) { }
				public void registerContextMenu(MenuManager menuManager,
						ISelectionProvider selectionProvider) { }
				public String getRegisteredName() { return null; }
				public String getPluginId() { return null; }
				public IWorkbenchPart getPart() { return null; }
				@SuppressWarnings("deprecation")
				public IKeyBindingService getKeyBindingService() { return null; }
				public String getId() { return null; }
			};
		}
	}

	Preferences prefs = null;
	StubXPathProcessorHandler handler = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prefs = XPathCoreHelper.getPreferences();
		prefs.putBoolean(XPathProcessorPreferences.XPATH_1_0_PROCESSOR, false);
		prefs.putBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, false);
		prefs.flush();
		handler = new StubXPathProcessorHandler();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		prefs = null;
		handler = null;
	}
	
	
	public Document createSimpleDocument() throws Exception {
		IDOMModel domImpl = new DOMModelImpl();
		domImpl.setId("dummy");
		Document document = domImpl.getDocument();
		document.appendChild(document.createElement("test"));

		Element rootelem = document.getDocumentElement();
		Element elem1 = document.createElement("testNode1");
		Element elem2 = document.createElement("testNode1");
		rootelem.appendChild(elem1);
		rootelem.appendChild(elem2);
		
		return document;
	}

	public void testComputeNodeXpath10() throws Exception {
		handler.toggleState("xpath10");

		NodeList nl = computeXPathInView("string(count(//*)) < '04'", createSimpleDocument());
		assertEquals(1, nl.getLength());
		assertEquals("true", ((Text)nl.item(0)).getTextContent());
	}

	public void testComputeNodeXpath2() throws Exception {
		handler.toggleState("xpath2");

		NodeList nl = computeXPathInView("'2' > '04'", createSimpleDocument());
		assertEquals(1, nl.getLength());
		assertEquals("true", ((Text)nl.item(0)).getTextContent());
	}

	private NodeList computeXPathInView(String xpathExpression, Node node) throws Exception, XPathExpressionException {
		MockXPathView myMockView = new MockXPathView();
		XPathComputer pathComputer = new XPathComputer(myMockView);
		pathComputer.setSelectedNode(node);
		pathComputer.setText(xpathExpression);
		pathComputer.compute();
		NodeList nl = myMockView.getLastList();
		return nl;
	}
	
	public void testToggleStateXpath2() throws Exception {
		handler.toggleState("xpath2");

	}	
}
