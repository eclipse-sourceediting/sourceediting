/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.tests.viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.ui.StructuredTextViewerConfigurationDTD;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.w3c.dom.Attr;

public class ViewerTestDTD extends ViewPart {
	/**
	 * Sets the viewer's highlighting text range to the text range indicated
	 * by the selected Nodes.
	 */
	protected class NodeRangeSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IndexedRegion startNode = (IndexedRegion) selection.getFirstElement();
				IndexedRegion endNode = (IndexedRegion) selection.toArray()[selection.size() - 1];

				if (startNode instanceof Attr)
					startNode = (IndexedRegion) ((Attr) startNode).getOwnerElement();
				if (endNode instanceof Attr)
					endNode = (IndexedRegion) ((Attr) endNode).getOwnerElement();

				int start = startNode.getStartOffset();
				int end = endNode.getEndOffset();

				fSourceViewer.resetVisibleRegion();
				fSourceViewer.setVisibleRegion(start, end - start);
				fSourceViewer.setSelectedRange(start, 0);
			}
			else {
				fSourceViewer.resetVisibleRegion();
			}
		}
	}

	protected class NumberInputDialog extends Dialog {
		public Text length;

		int lengthValue;
		public Text start;
		int startValue;

		public NumberInputDialog(Shell shell) {
			super(shell);
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			Composite container = new Composite(composite, SWT.NULL);
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			container.setLayout(new GridLayout(2, true));
			setShellStyle(getShell().getStyle() | SWT.RESIZE);

			Label label = new Label(container, SWT.NULL);
			label.setText("Start");
			label.setLayoutData(new GridData(GridData.FILL_BOTH));

			label = new Label(container, SWT.NULL);
			label.setText("Length");
			label.setLayoutData(new GridData(GridData.FILL_BOTH));

			start = new Text(container, SWT.BORDER);
			startValue = fSourceViewer.getVisibleRegion().getOffset();
			start.setText("" + startValue);
			start.setLayoutData(new GridData(GridData.FILL_BOTH));

			length = new Text(container, SWT.BORDER);
			lengthValue = fSourceViewer.getVisibleRegion().getLength();
			length.setText("" + lengthValue);
			length.setLayoutData(new GridData(GridData.FILL_BOTH));

			// start.addModifyListener(new ModifyListener() {
			// public void modifyText(ModifyEvent e) {
			// if (e.widget == start) {
			// try {
			// startValue = Integer.decode(start.getText()).intValue();
			// }
			// catch (NumberFormatException e2) {
			// startValue = 0;
			// }
			// }
			// }
			// });
			// length.addModifyListener(new ModifyListener() {
			// public void modifyText(ModifyEvent e) {
			// if (e.widget == length) {
			// try {
			// lengthValue = Integer.decode(length.getText()).intValue();
			// }
			// catch (NumberFormatException e2) {
			// lengthValue = 0;
			// }
			// }
			// }
			// });

			return composite;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
		 */
		protected void okPressed() {
			try {
				startValue = Integer.decode(start.getText()).intValue();
			}
			catch (NumberFormatException e2) {
				startValue = 0;
			}
			try {
				lengthValue = Integer.decode(length.getText()).intValue();
			}
			catch (NumberFormatException e2) {
				lengthValue = 0;
			}
			super.okPressed();
		}
	}

	private final String DEFAULT_VIEWER_CONTENTS = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<HTML>\n	<HEAD>\n		<META http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n		<TITLE>place title here</TITLE>\n	</HEAD>\n	<BODY>\n		place content here	\n<script>\n\"text\";\n</SCRIPT>\n</BODY>\n</HTML>";
	private StructuredTextViewerConfiguration fConfig = null;
	private IContentOutlinePage fContentOutlinePage = null;
	private ISelectionChangedListener fHighlightRangeListener = null;

	private StructuredTextViewer fSourceViewer = null;


	private final String SSE_EDITOR_FONT = "org.eclipse.wst.sse.ui.textfont";

	protected void addActions(IContributionManager mgr) {
		if (mgr != null) {
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "New HTML";
				}

				public void run() {
					super.run();
					BusyIndicator.showWhile(getSite().getShell().getDisplay(), new Runnable() {
						public void run() {
							setupViewerForNew();
							fSourceViewer.setEditable(true);
						}
					});
				}
			});
			mgr.add(new Separator());
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Change Visibility";
				}

				public void run() {
					super.run();
					NumberInputDialog dlg = new NumberInputDialog(fSourceViewer.getControl().getShell());
					int proceed = dlg.open();
					if (proceed == Window.CANCEL)
						return;
					fSourceViewer.resetVisibleRegion();
					fSourceViewer.setVisibleRegion(dlg.startValue, dlg.lengthValue);
				}
			});
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Show All";
				}

				public void run() {
					super.run();
					fSourceViewer.resetVisibleRegion();
				}
			});
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Change Visibility in Editor";
				}

				public void run() {
					super.run();
					StructuredTextViewer sourceViewer = null;
					IEditorPart part = getViewSite().getWorkbenchWindow().getActivePage().getActiveEditor();
					if (part != null && part instanceof StructuredTextEditor) {
						sourceViewer = ((StructuredTextEditor) part).getTextViewer();
					}
					if (sourceViewer != null) {
						NumberInputDialog dlg = new NumberInputDialog(sourceViewer.getControl().getShell());
						int proceed = dlg.open();
						if (proceed == Window.CANCEL)
							return;
						sourceViewer.resetVisibleRegion();
						sourceViewer.setVisibleRegion(dlg.startValue, dlg.lengthValue);
					}
				}
			});
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Show All in Editor";
				}

				public void run() {
					super.run();
					StructuredTextViewer sourceViewer = null;
					IEditorPart part = getViewSite().getWorkbenchWindow().getActivePage().getActiveEditor();
					if (part != null && part instanceof StructuredTextEditor) {
						sourceViewer = ((StructuredTextEditor) part).getTextViewer();
					}
					if (sourceViewer != null) {
						sourceViewer.resetVisibleRegion();
					}
				}
			});
			mgr.add(new Separator());
			// no longer able to set fInput to NULL
			// mgr.add(new Action() {
			// public String getText() {
			// return getToolTipText();
			// }
			//
			// public String getToolTipText() {
			// return "Set Input to NULL";
			// }
			// public void run() {
			// super.run();
			// viewer.setInput(null);
			// }
			// });
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Take Input from Active Editor";
				}

				public void run() {
					super.run();
					ITextEditor textEditor = getActiveEditor();
					if (textEditor != null) {
						setupViewerForEditor(textEditor);
						fSourceViewer.setEditable(true);
					}
				}
			});
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Take Input and Follow Selection";
				}

				public void run() {
					super.run();
					followSelection();
					fSourceViewer.setEditable(true);
				}
			});
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Take Input and Follow Selection As ReadOnly";
				}

				public void run() {
					super.run();
					followSelection();
					fSourceViewer.setEditable(false);
				}
			});
			mgr.add(new Action() {
				public String getText() {
					return getToolTipText();
				}

				public String getToolTipText() {
					return "Stop Following Selection";
				}

				public void run() {
					super.run();
					stopFollowSelection();
				}
			});
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		IContributionManager mgr = getViewSite().getActionBars().getMenuManager();
		addActions(mgr);

		// create source viewer & its content type-specific viewer
		// configuration
		fSourceViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
		fConfig = new StructuredTextViewerConfigurationDTD();

		// set up the viewer with a document & viewer config
		setupViewerForNew();

		setupViewerPreferences();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		stopFollowSelection();
		fSourceViewer.unconfigure();
	}

	/**
	 * Hooks up the viewer to follow the fSelection made in the active editor
	 */
	private void followSelection() {
		ITextEditor editor = getActiveEditor();
		if (editor != null) {
			setupViewerForEditor(editor);
			if (fHighlightRangeListener == null)
				fHighlightRangeListener = new NodeRangeSelectionListener();

			fContentOutlinePage = ((IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class));
			if (fContentOutlinePage != null) {
				fContentOutlinePage.addSelectionChangedListener(fHighlightRangeListener);

				if (!fContentOutlinePage.getSelection().isEmpty() && fContentOutlinePage.getSelection() instanceof IStructuredSelection) {
					fSourceViewer.resetVisibleRegion();

					Object[] nodes = ((IStructuredSelection) fContentOutlinePage.getSelection()).toArray();
					IndexedRegion startNode = (IndexedRegion) nodes[0];
					IndexedRegion endNode = (IndexedRegion) nodes[nodes.length - 1];

					int start = startNode.getStartOffset();
					int end = endNode.getEndOffset();

					fSourceViewer.setVisibleRegion(start, end - start);
					fSourceViewer.setSelectedRange(start, 0);
				}
			}
		}
	}

	/**
	 * Returns the current active text editor if possible
	 * 
	 * @return ITextEditor
	 */
	private ITextEditor getActiveEditor() {
		ITextEditor editor = null;
		IEditorPart editorPart = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart instanceof ITextEditor)
			editor = (ITextEditor) editorPart;
		if (editor == null && editorPart != null)
			editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		return editor;
	}

	/**
	 * @see org.eclipse.ui.IViewPart#init(IViewSite, IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (fSourceViewer.getControl() != null && !fSourceViewer.getControl().isDisposed())
			fSourceViewer.getControl().setFocus();
	}

	/**
	 * Sets up the viewer with the same document/fInput as the given editor
	 * 
	 * @param ITextEditor
	 *            editor - the editor to use *cannot to be null*
	 */
	private void setupViewerForEditor(ITextEditor editor) {
		stopFollowSelection(); // if was following fSelection, stop
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		fSourceViewer.setDocument(doc);

		// need to reconfigure after set document just so highlighter works
		fSourceViewer.configure(new StructuredTextViewerConfigurationDTD());
	}

	/**
	 * Set up source viewer with a new document & configure it
	 */
	private void setupViewerForNew() {
		stopFollowSelection(); // if was following fSelection, stop

		IModelManager modelManager = StructuredModelManager.getModelManager();
		IDocument doc = modelManager.createStructuredDocumentFor(ContentTypeIdForDTD.ContentTypeID_DTD);
		doc.set(DEFAULT_VIEWER_CONTENTS);

		fSourceViewer.setDocument(doc);
		// need to reconfigure after set document just so highlighter works
		fSourceViewer.configure(fConfig);
	}

	/**
	 * Set up source viewer with any additional preferences it should have Ex:
	 * font, tab width
	 */
	private void setupViewerPreferences() {
		fSourceViewer.getTextWidget().setFont(JFaceResources.getFont(SSE_EDITOR_FONT));
	}

	/**
	 * Cease following the fSelection made in the editor
	 */
	private void stopFollowSelection() {
		if (fContentOutlinePage != null) {
			fContentOutlinePage.removeSelectionChangedListener(fHighlightRangeListener);
			fSourceViewer.resetVisibleRegion();
			fContentOutlinePage = null;
		}
	}

}
