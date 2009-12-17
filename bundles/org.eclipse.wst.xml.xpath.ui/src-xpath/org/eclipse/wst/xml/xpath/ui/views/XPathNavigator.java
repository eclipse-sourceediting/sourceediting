/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Orangevolt (www.orangevolt.com) - XSLT support
 *     Jesper Steen Moller - refactored Orangevolt XSLT support into WST
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import java.util.List;
import java.util.WeakHashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.ui.internal.provisional.IDOMSourceEditingTextTools;
import org.eclipse.wst.xml.xpath.ui.internal.Messages;
import org.eclipse.wst.xml.xpath.ui.internal.XPathUIPlugin;
import org.eclipse.wst.xml.xpath.ui.internal.views.DefaultNamespaceContext;
import org.eclipse.wst.xml.xpath.ui.internal.views.EditNamespacePrefixDialog;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * @deprecated This view should no longer be used.  Use XPathView instead.
 */
@Deprecated
public class XPathNavigator extends ViewPart {
	class XPathAction extends Action {
		public void run() {
			String expr = xpath.getText().trim();
			if (expr.length() == 0) {
				xpath.setFocus();
				return;
			}

			if (documents.getSelection().isEmpty()) {
				MessageDialog.openInformation(getSite().getShell(),
						Messages.XPathNavigator_XPath_Navigator,
						Messages.XPathNavigator_Select_source_first);
				documents.getCombo().setFocus();
				return;
			}

			Element contextElement = getQueryContext();
			if (contextElement == null) {
				return;
			}

			try {
				text.setText(""); //$NON-NLS-1$
				viewer.setInput(null);

				XPath newXPath = XPathFactory.newInstance().newXPath();

				final List<NamespaceInfo> namespaces = createNamespaceInfo(getSelectedDocument());
				if (namespaces != null) {
					newXPath.setNamespaceContext(new DefaultNamespaceContext(namespaces));
				}
				XPathExpression xpExp = newXPath.compile(expr);

				NodeList nl = null;
				String stringResult = null;

				// IMHO, this is really poor API design in javax.xpath.
				// We prefer the nodeset, but if there's a string instead, we
				// want that. It shouldn't be that hard!
				try {
					nl = (NodeList) xpExp.evaluate(contextElement,
							XPathConstants.NODESET);
				} catch (XPathExpressionException xee) {
					stringResult = xpExp.evaluate(contextElement); // Implicit
																	// XPathConstants.STRING
				}
				Document document = (Document) contextElement
						.getOwnerDocument().cloneNode(false);
				Element root = document.createElement("xpath-result"); //$NON-NLS-1$
				document.appendChild(root);

				boolean odd = false;

				StringBuffer sb = new StringBuffer();

				if (nl != null) {
					for (int i = 0; i < nl.getLength(); ++i) {
						Node node = nl.item(i);

						root.appendChild(document.importNode(node, true));
						switch (node.getNodeType()) {
						case org.w3c.dom.Node.ATTRIBUTE_NODE: {
							Attr attr = (Attr) node;
							sb
									.append("#attribute :: ").append(attr.getName()).append("=\"").append(attr.getValue()).append('"'); //$NON-NLS-1$ //$NON-NLS-2$

							break;
						}
						case org.w3c.dom.Node.CDATA_SECTION_NODE: {
							CDATASection cdata = (CDATASection) node;
							sb.append("#cdata :: ").append(cdata.toString()); //$NON-NLS-1$
							break;
						}
						case org.w3c.dom.Node.COMMENT_NODE: {
							Comment comment = (Comment) node;
							sb
									.append("#comment :: ").append(comment.toString()); //$NON-NLS-1$

							break;
						}
						case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE: {
							DocumentFragment fragment = (DocumentFragment) node;
							sb
									.append("#fragment :: ").append(fragment.toString()); //$NON-NLS-1$
							break;
						}
						case org.w3c.dom.Node.DOCUMENT_NODE: {
							Document doc = (Document) node;
							sb.append("#document :: "); //$NON-NLS-1$
							sb.append(((DocumentImpl) doc).getSource());
							break;
						}
						case org.w3c.dom.Node.ELEMENT_NODE: {
							Element element = (Element) node;
							sb.append("#element :: "); //$NON-NLS-1$
							sb.append(((ElementImpl) element).getSource());
							break;
						}
						case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE: {
							ProcessingInstruction pi = (ProcessingInstruction) node;

							sb.append("#pi :: ").append(pi.toString()); //$NON-NLS-1$

							break;
						}
						case org.w3c.dom.Node.TEXT_NODE: {
							org.w3c.dom.Text text = (org.w3c.dom.Text) node;
							sb.append("#text :: ").append(text.getNodeValue()); //$NON-NLS-1$

							break;
						}
						default: {
							sb
									.append("#unknown :: ").append(node.getNodeType()).append(node.toString()); //$NON-NLS-1$
						}
						}

						sb.append("\r\n"); //$NON-NLS-1$

						text.append(sb.toString());

						StyleRange sr = new StyleRange();
						sr.foreground = Display.getDefault().getSystemColor(
								odd ? SWT.COLOR_BLACK : SWT.COLOR_DARK_BLUE);
						sr.start = text.getText().length() - sb.length();
						sr.length = sb.length();
						text.setStyleRange(sr);

						odd = !odd;
						sb.setLength(0);
					}
				} else if (stringResult != null) {
					text.setText(stringResult);
					resultTabs.setSelection(1);
				} else {
					text.setText(""); //$NON-NLS-1$
				}

				text.setCaretOffset(0);

				viewer.setInput(document.getDocumentElement());
				viewer.refresh();
				viewer.expandToLevel(3);

				// scroll top element into visible area
				if (viewer.getTree().getItems().length > 0) {
					viewer.getTree().showItem(viewer.getTree().getItems()[0]);
				}
			} catch (XPathExpressionException pex) {
				MessageDialog.openError(getSite().getShell(),
						Messages.XPathNavigator_XPath_Navigator,
						Messages.XPathNavigator_XPath_Eval_Failed
								+ pex.getCause().getMessage());
				XPathUIPlugin.log(pex);
			} catch (Exception ex) {
				MessageDialog.openError(getSite().getShell(),
						Messages.XPathNavigator_XPath_Navigator,
						Messages.XPathNavigator_XPath_Eval_Failed
								+ ex.getMessage());
				XPathUIPlugin.log(ex);
			}
		}
	}

	class RefreshAction extends Action {
		public void run() {
			update();
		}
	}

	static class ShowInSourceAction extends Action {
		public void run() {
			// try
			// {
			// NodeImpl nodeImpl =
			// (NodeImpl)((IStructuredSelection)viewer.getSelection()).getFirstElement();
			//
			// int start = nodeImpl.getStartOffset();
			// int end = nodeImpl.getEndOffset();
			// IStructuredSelection selection =
			// (IStructuredSelection)documents.getSelection();
			// IEditorReference editorReference =
			// (IEditorReference)selection.getFirstElement();
			// XMLMultiPageEditorPart structuredTextEditor =
			// (XMLMultiPageEditorPart)editorReference.getEditor( true);
			// structuredTextEditor.selectAndReveal( start, end-start);
			// }
			// catch( Exception ex)
			// {
			// MessageDialog.openError( getSite().getShell(),
			// XPathUIMessages.XPathNavigator_XPath_Navigator,
			// XPathUIMessages.XPathNavigator_XPath_Show_In_Source_Failed +
			// ex.getMessage());
			//                 XPathViewPlugin.getDefault().log( "XPath Navigator : Show in source failed.", ex); //$NON-NLS-1$
			// }
		}
	}

	private TreeViewer viewer;
	private StyledText text;
	private CTabFolder resultTabs;

	private XPathAction query;

	private RefreshAction refresh;

	private ShowInSourceAction showInSource;

	private Text xpath;

	private ComboViewer documents;

	private Button queryByContext, queryByDocument, namespaceButton;

	protected IMemento memento;

	protected WeakHashMap<Document, List<NamespaceInfo>> namespaceInfo = new WeakHashMap<Document, List<NamespaceInfo>>();

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 5;
		gridLayout.marginWidth = 0;
		comp.setLayout(gridLayout);

		Label label = new Label(comp, SWT.NONE);
		label.setText(Messages.XPathNavigator_XML_Source_Document);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalIndent = gridLayout.horizontalSpacing / 2;
		label.setLayoutData(data);

		documents = new ComboViewer(comp, SWT.READ_ONLY);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalIndent = gridLayout.marginTop;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		documents.getCombo().setLayoutData(data);
		documents.setUseHashlookup(true);
		documents.setContentProvider(new XMLEditorsContentProvider());
		documents.setLabelProvider(new EditorReferenceLabelProvider());
		documents.setInput(Boolean.TRUE);

		Group queryGroup = new Group(comp, SWT.SHADOW_NONE);
		queryGroup.setText(Messages.XPathNavigator_Context);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.verticalIndent = gridLayout.marginTop;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		queryGroup.setLayoutData(data);
		GridLayout _gridLayout = new GridLayout();
		_gridLayout.numColumns = 3;
		queryGroup.setLayout(_gridLayout);

		queryByContext = new Button(queryGroup, SWT.RADIO);
		queryByContext.setText(Messages.XPathNavigator_Selection);

		queryByDocument = new Button(queryGroup, SWT.RADIO);
		queryByDocument.setText(Messages.XPathNavigator_Document);
		queryByDocument.setSelection(true);

		namespaceButton = new Button(queryGroup, SWT.PUSH);
		namespaceButton.setText(Messages.XPathNavigator_Namespaces);
		namespaceButton.setToolTipText(Messages.XPathNavigator_Namespaces_Tip);
		namespaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Document selectedDocument = getSelectedDocument();

				if (selectedDocument != null) {
					List<NamespaceInfo> info = createNamespaceInfo(selectedDocument);

					IStructuredSelection selection = (IStructuredSelection) documents
							.getSelection();
					IEditorReference editorReference = (IEditorReference) selection
							.getFirstElement();
					IPathEditorInput editorInput = (IPathEditorInput) editorReference
							.getEditor(true).getEditorInput();

					EditNamespacePrefixDialog dlg = new EditNamespacePrefixDialog(
							XPathNavigator.this.getSite().getShell(),
							editorInput.getPath());
					dlg.setNamespaceInfoList(info);
					if (SWT.OK == dlg.open()) {
						// Apply changes
					}
				}
			}
		});

		label = new Label(comp, SWT.NONE);
		label.setText(Messages.XPathNavigator_Expression);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalIndent = gridLayout.horizontalSpacing;
		data.grabExcessHorizontalSpace = true;
		data.horizontalIndent = gridLayout.horizontalSpacing / 2;
		label.setLayoutData(data);

		xpath = new Text(comp, SWT.BORDER);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.verticalIndent = gridLayout.marginTop;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		xpath.setLayoutData(data);
		xpath.addKeyListener(new KeyListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt
			 * .events.KeyEvent)
			 */
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == '\r') {
					query.run();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt
			 * .events.KeyEvent)
			 */
			public void keyPressed(KeyEvent e) {
			}
		});

		resultTabs = new CTabFolder(comp, SWT.BOTTOM);
		data = new GridData();
		data.verticalIndent = gridLayout.marginTop;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		resultTabs.setLayoutData(data);

		viewer = new TreeViewer(resultTabs, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setLabelProvider(new DOMNodeLabelProvider());
		viewer.setContentProvider(new DOMTreeContentProvider());
		viewer.addFilter(new DOMViewerFilter());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged
			 * (org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event) {
				showInSource.setEnabled(!event.getSelection().isEmpty());
			}
		});
		CTabItem item = new CTabItem(resultTabs, SWT.NULL);
		item.setText(Messages.XPathNavigator_DOM_Tree);
		item.setControl(viewer.getControl());

		resultTabs.setSelection(item);

		text = new StyledText(resultTabs, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		Color white = text.getBackground();
		text.setEditable(false);
		text.setBackground(white);
		item = new CTabItem(resultTabs, SWT.NULL);
		item.setText(Messages.XPathNavigator_Text);
		item.setControl(text);

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		if (memento != null) {
			restoreState();
			memento = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
	}

	public static final String MEMENTO_XPATHNAVIGATOR_SECTION = "xpath-navigator-view"; //$NON-NLS-1$

	public static final String MEMENTO_QUERYCONTEXT_KEY = "query-context"; //$NON-NLS-1$
	public static final String MEMENTO_QUERYCONTEXT_DOCUMENT = "document"; //$NON-NLS-1$
	public static final String MEMENTO_QUERYCONTEXT_SELECTION = "selection"; //$NON-NLS-1$

	public static final String MEMENTO_XPATHQUERY_KEY = "xpath-query"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		IMemento settings = memento.createChild(MEMENTO_XPATHNAVIGATOR_SECTION);
		// System.out.println( "queryByDocument.getSelection()=" +
		// queryByDocument.getSelection());
		settings.putString(MEMENTO_QUERYCONTEXT_KEY, queryByDocument
				.getSelection() ? MEMENTO_QUERYCONTEXT_DOCUMENT
				: MEMENTO_QUERYCONTEXT_SELECTION);
		settings.putString(MEMENTO_XPATHQUERY_KEY, xpath.getText());
		super.saveState(memento);
	}

	/**
     * 
     */
	protected void restoreState() {
		IMemento settings = memento.getChild(MEMENTO_XPATHNAVIGATOR_SECTION);
		if (settings != null) {
			String queryContext = settings.getString(MEMENTO_QUERYCONTEXT_KEY);
			if (MEMENTO_QUERYCONTEXT_DOCUMENT.equals(queryContext)) {
				queryByDocument.setSelection(true);
				queryByContext.setSelection(false);
			} else {
				queryByDocument.setSelection(false);
				queryByContext.setSelection(true);
			}

			xpath.setText(settings.getString(MEMENTO_XPATHQUERY_KEY));
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				XPathNavigator.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(query);
		manager.add(refresh);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(query);
		manager.add(refresh);
		manager.add(new Separator());
		manager.add(showInSource);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(query);
		manager.add(refresh);
	}

	private void makeActions() {
		query = new XPathAction();
		query.setText(Messages.XPathNavigator_Run_XPath_Query);
		query.setToolTipText(Messages.XPathNavigator_Run_on_selected);
		query.setImageDescriptor(XPathUIPlugin.getDefault().getImageRegistry()
				.getDescriptor("run")); //$NON-NLS-1$

		refresh = new RefreshAction();
		refresh.setText(Messages.XPathNavigator_Refresh_Source_Docs);
		refresh.setToolTipText(Messages.XPathNavigator_Refresh_Source_Docs_Tip);
		refresh.setImageDescriptor(XPathUIPlugin.getDefault()
				.getImageRegistry().getDescriptor("refresh")); //$NON-NLS-1$

		showInSource = new ShowInSourceAction();
		showInSource.setText(Messages.XPathNavigator_Show_In_Source);
		showInSource.setToolTipText(Messages.XPathNavigator_Show_In_Source_Tip);
	}

	protected Document getSelectedDocument() {
		IStructuredSelection selection = (IStructuredSelection) documents
				.getSelection();
		IEditorReference editorReference = (IEditorReference) selection
				.getFirstElement();

		return (Document) editorReference.getEditor(true).getAdapter(
				Document.class);
	}

	protected Element getQueryContext() {
		IStructuredSelection selection = (IStructuredSelection) documents
				.getSelection();
		IEditorReference editorReference = (IEditorReference) selection
				.getFirstElement();

		IEditorPart structuredTextEditor = editorReference.getEditor(true);

		if (queryByContext.getSelection()) {
			ISourceEditingTextTools sett = (ISourceEditingTextTools) structuredTextEditor
					.getAdapter(ISourceEditingTextTools.class);
			if (sett instanceof IDOMSourceEditingTextTools) {
				IDOMSourceEditingTextTools idsett = (IDOMSourceEditingTextTools) sett;
				Node n = null;
				try {
					n = idsett.getNode(idsett.getCaretOffset());
				} catch (BadLocationException e) {
					MessageDialog.openInformation(getSite().getShell(),
							Messages.XPathNavigator_XPath_Navigator,
							Messages.XPathNavigator_Node_could_not_be_selected);
				}

				// Go upwards to an element
				while ((n != null)
						&& !((n instanceof Element) || (n instanceof Document))) {
					n = n.getParentNode();
				}

				if (n instanceof Document) {
					n = ((Document) n).getDocumentElement();
				}

				if (n == null) {
					MessageDialog.openInformation(getSite().getShell(),
							Messages.XPathNavigator_XPath_Navigator,
							Messages.XPathNavigator_Nothing_selected);
					structuredTextEditor.setFocus();
					return null;
				}
				return (Element) n;
			}
		}
		return ((Document) structuredTextEditor.getAdapter(Document.class))
				.getDocumentElement();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		xpath.setFocus();
	}

	public void update() {
		ISelection selection = documents.getSelection();

		// dummy call to force refresh viewer
		documents.setInput(documents.getInput() == Boolean.FALSE ? Boolean.TRUE
				: Boolean.FALSE);

		documents.setSelection(selection);
	}

	@SuppressWarnings("unchecked")
	private List<NamespaceInfo> createNamespaceInfo(Document document) {
		List<NamespaceInfo> info = namespaceInfo.get(document);
		if (info == null) {
			info = new ArrayList<NamespaceInfo>();
			NamespaceTable namespaceTable = new NamespaceTable(document);
			namespaceTable.visitElement(document.getDocumentElement());
			Collection<?> namespaces = namespaceTable
					.getNamespaceInfoCollection();
			info.addAll((Collection<NamespaceInfo>) namespaces);
			namespaceInfo.put(document, info);
		}
		return info;
	}

	static class XMLEditorsContentProvider implements
			IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			ArrayList<IEditorReference> editorReferences = new ArrayList<IEditorReference>();

			IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
					.getWorkbenchWindows();

			for (int i = 0; i < windows.length; i++) {
				IWorkbenchWindow window = windows[i];

				IWorkbenchPage[] pages = window.getPages();
				for (int j = 0; j < pages.length; j++) {
					IWorkbenchPage page = pages[j];
					IEditorReference[] editors = page.getEditorReferences();

					editorReferences.addAll(Arrays.asList(editors));
				}
			}
			ArrayList<IEditorReference> aClone = new ArrayList<IEditorReference>();
			aClone.addAll(editorReferences);

			for (IEditorReference ref : aClone) {
				if (!(ref.getEditor(false) instanceof IEditorPart)) {
					editorReferences.remove(ref);
				}
			}

			return editorReferences.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// System.out.println( "inputChanged");
		}
	}

	static class EditorReferenceLabelProvider extends LabelProvider {
		/**
		 * A string used to indicate that an editor is dirty
		 */
		public static char DIRTY_INDICATOR = '*';

		/**
		 * @see ILabelProvider#getImage(Object)
		 */
		public Image getImage(Object element) {
			if (element instanceof IEditorReference) {
				return ((IEditorReference) element).getTitleImage();
			}
			return super.getImage(element);
		}

		/**
		 * @see ILabelProvider#getText(Object)
		 */
		public String getText(Object element) {
			if (element instanceof IEditorReference) {
				IEditorReference reference = ((IEditorReference) element);
				StringBuffer buffer = new StringBuffer();
				if (reference.isDirty()) {
					buffer.append(DIRTY_INDICATOR);
				}
				buffer.append(reference.getTitle());
				return buffer.toString();
			}
			return super.getText(element);
		}
	}
}
