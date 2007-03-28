/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexDelta;
import org.eclipse.jst.jsp.core.taglib.ITaglibIndexListener;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.jst.jsp.core.taglib.TaglibIndexDelta;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.sse.core.utils.StringUtils;

public class TaglibIndexDeltaTraceView extends ViewPart {

	static final String MEM_LISTENING = "IS_LISTENING";
	boolean isListening = false;

	/**
	 * Resumes listening to TaglibIndexDeltas
	 */
	private class ResumeAction extends Action {
		public ResumeAction() {
			super();
			setText("Resume");
			setImageDescriptor(JSPUITestsPlugin.getDefault().getImageRegistry().getDescriptor(JSPUITestImages.IMG_ELCL_RESUME));
			setDisabledImageDescriptor(JSPUITestsPlugin.getDefault().getImageRegistry().getDescriptor(JSPUITestImages.IMG_DLCL_RESUME));
		}

		public void run() {
			super.run();
			TaglibIndex.addTaglibIndexListener(fInternalListener);
			isListening = true;
			fSuspendAction.setEnabled(true);
			setEnabled(false);
		}
	}

	/**
	 * Stops listening to TaglibIndexDeltas
	 */
	private class SuspendAction extends Action {
		public SuspendAction() {
			setText("Suspend");
			setImageDescriptor(JSPUITestsPlugin.getDefault().getImageRegistry().getDescriptor(JSPUITestImages.IMG_ELCL_SUSPEND));
			setDisabledImageDescriptor(JSPUITestsPlugin.getDefault().getImageRegistry().getDescriptor(JSPUITestImages.IMG_DLCL_SUSPEND));
		}

		public void run() {
			super.run();
			TaglibIndex.removeTaglibIndexListener(fInternalListener);
			isListening = false;
			setEnabled(false);
			fResumeAction.setEnabled(true);
		}
	}

	/**
	 * Empties deltas from viewer
	 */
	private class ClearAction extends Action {
		public ClearAction() {
			setText("Clear");
			setImageDescriptor(JSPUITestsPlugin.getDefault().getImageRegistry().getDescriptor(JSPUITestImages.IMG_ELCL_REMOVE_ALL));
			setDisabledImageDescriptor(JSPUITestsPlugin.getDefault().getImageRegistry().getDescriptor(JSPUITestImages.IMG_DLCL_REMOVE_ALL));
		}

		public void run() {
			super.run();
			fInput.clear();
			fViewer.refresh();
			setEnabled(false);
		}
	}

	class TaglibIndexListener implements ITaglibIndexListener {
		public void indexChanged(final ITaglibIndexDelta delta) {
			getControl().getDisplay().syncExec(new Runnable() {
				public void run() {
					fInput.add(delta);
					fViewer.refresh();
					fViewer.setSelection(new StructuredSelection(delta));
					fClearAction.setEnabled(true);
				}
			});
		}
	}

	ITaglibIndexListener fInternalListener = null;

	List fInput = new ArrayList();
	TableViewer fViewer = null;
	ResumeAction fResumeAction;
	SuspendAction fSuspendAction;
	IAction fClearAction;

	public TaglibIndexDeltaTraceView() {
		super();
		fInternalListener = new TaglibIndexListener();
	}

	void showSelectionDetail(final Composite composite) {
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		if (!selection.isEmpty()) {
			final TaglibIndexDelta selectedDelta = (TaglibIndexDelta) selection.getFirstElement();
			final ITableLabelProvider tableLabelProvider = ((ITableLabelProvider) fViewer.getLabelProvider());
			String columnText = tableLabelProvider.getColumnText(selectedDelta, 1);
			columnText = columnText + ":" + tableLabelProvider.getColumnText(selectedDelta, 2);
			new Dialog(composite.getShell()) {
				public void create() {
					setShellStyle(getShellStyle() | SWT.RESIZE);
					super.create();
				}

				protected Control createDialogArea(final Composite parent) {
					final Composite inner = new Composite(parent, SWT.NONE);
					inner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
					final Sash sash = new Sash(inner, SWT.HORIZONTAL);

					final TreeViewer treeViewer = new TreeViewer(inner);
					treeViewer.setContentProvider(new ITreeContentProvider() {
						public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
						}

						public void dispose() {
						}

						public Object[] getElements(Object inputElement) {
							return getChildren(inputElement);
						}

						public boolean hasChildren(Object element) {
							return getChildren(element).length > 0;
						}

						public Object getParent(Object element) {
							return selectedDelta;
						}

						public Object[] getChildren(Object parentElement) {
							if (parentElement instanceof TaglibIndexDelta) {
								TaglibIndexDelta taglibIndexDelta = ((TaglibIndexDelta) parentElement);
								if (taglibIndexDelta.getTrigger() != null) {
									List combined = new ArrayList();
									combined.add(taglibIndexDelta.getTrigger());
									combined.addAll(Arrays.asList(taglibIndexDelta.getAffectedChildren()));
									return combined.toArray();
								}
								return taglibIndexDelta.getAffectedChildren();
							}
							return new Object[0];
						}
					});
					treeViewer.setLabelProvider(new LabelProvider() {
						public String getText(Object element) {
							if (element instanceof ITaglibIndexDelta) {
								ITaglibIndexDelta taglibIndexDelta = ((ITaglibIndexDelta) element);
								if (taglibIndexDelta.getTaglibRecord() != null)
									return taglibIndexDelta.toString();
								String text = tableLabelProvider.getColumnText(selectedDelta, 1);
								text = text + ":" + tableLabelProvider.getColumnText(selectedDelta, 2);
								return text;
							}
							return StringUtils.firstLineOf(super.getText(element));
						}
					});
					treeViewer.setInput(selectedDelta);

					final Text text = new Text(inner, SWT.MULTI);

					treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
						public void selectionChanged(SelectionChangedEvent event) {
							IStructuredSelection selection2 = (IStructuredSelection) treeViewer.getSelection();
							if (!selection2.isEmpty())
								text.setText("" + (selection2).getFirstElement());
							else
								text.setText("");
						}
					});

					final FormLayout form = new FormLayout();
					inner.setLayout(form);

					FormData firstData = new FormData();
					firstData.top = new FormAttachment(0, 0);
					firstData.bottom = new FormAttachment(sash, 2);
					firstData.left = new FormAttachment(0, 0);
					firstData.right = new FormAttachment(100, 0);
					treeViewer.getControl().setLayoutData(firstData);

					FormData secondData = new FormData();
					secondData.top = new FormAttachment(sash, 2);
					secondData.left = new FormAttachment(0, 0);
					secondData.right = new FormAttachment(100, 0);
					secondData.bottom = new FormAttachment(100, 0);
					text.setLayoutData(secondData);

					final FormData sashData = new FormData();
					sashData.top = new FormAttachment(60, 0);
					sashData.left = new FormAttachment(0, 0);
					sashData.right = new FormAttachment(100, 0);
					sash.setLayoutData(sashData);
					sash.addListener(SWT.Selection, new org.eclipse.swt.widgets.Listener() {
						public void handleEvent(Event e) {
							sashData.top = new FormAttachment(0, e.y);
							inner.layout();
						}
					});
					return sash;
				}
			}.open();
		}
	}

	public void createPartControl(final Composite parent) {
		fViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				showSelectionDetail(parent);
			}

		});
		fViewer.setContentProvider(new ArrayContentProvider());
		fViewer.setLabelProvider(new ITableLabelProvider() {
			public void removeListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0 : {
						return Long.toString(((TaglibIndexDelta) element).getTime());
					}
					case 1 : {
						String kind = null;
						switch (((ITaglibIndexDelta) element).getKind()) {
							case ITaglibIndexDelta.ADDED :
								kind = "added";
								break;
							case ITaglibIndexDelta.REMOVED :
								kind = "removed";
								break;
							case ITaglibIndexDelta.CHANGED :
								kind = "changed";
								break;
						}
						return kind;
					}
					case 2 :
						return ((ITaglibIndexDelta) element).getProject().getName();
					case 3 :
						return ((TaglibIndexDelta) element).getTrigger().toString();
				}
				return "";
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

		});

		String[] columnProperties = new String[]{"time", "Kind", "Project", "Trigger"};
		TableLayout tlayout = new TableLayout();
		CellEditor[] cellEditors = new CellEditor[columnProperties.length];
		int columnWidths[] = new int[]{Display.getCurrent().getBounds().width / 14, Display.getCurrent().getBounds().width / 7, Display.getCurrent().getBounds().width / 7, Display.getCurrent().getBounds().width / 14, Display.getCurrent().getBounds().width / 7};
		for (int i = 0; i < columnProperties.length; i++) {
			tlayout.addColumnData(new ColumnWeightData(1));
			TableColumn tc = new TableColumn(fViewer.getTable(), SWT.NONE);
			tc.setText(columnProperties[i]);
			tc.setResizable(true);
			tc.setWidth(columnWidths[i]);
		}
		fViewer.setCellEditors(cellEditors);
		fViewer.setColumnProperties(columnProperties);

		fViewer.getTable().setHeaderVisible(true);
		fViewer.getTable().setLinesVisible(true);
		fViewer.setColumnProperties(columnProperties);

		MenuManager contextMenuManager = new MenuManager();
		contextMenuManager.setRemoveAllWhenShown(false);
		contextMenuManager.add(new Action() {
			public void run() {
				super.run();
				showSelectionDetail(parent);
			}

			public String getText() {
				return "Details...";
			}

			public boolean isEnabled() {
				return !fViewer.getSelection().isEmpty();
			}
		});

		Menu contextMenu = contextMenuManager.createContextMenu(fViewer.getControl());
		getControl().setMenu(contextMenu);

		fViewer.setInput(fInput);
	}

	public void dispose() {
		super.dispose();
		TaglibIndex.removeTaglibIndexListener(fInternalListener);
	}

	Control getControl() {
		return fViewer.getControl();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		boolean shouldListen = memento != null && Boolean.valueOf(memento.getString(MEM_LISTENING)).booleanValue();
		if (shouldListen) {
			TaglibIndex.addTaglibIndexListener(fInternalListener);
			isListening = true;
		}
		fResumeAction = new ResumeAction();
		fSuspendAction = new SuspendAction();
		fClearAction = new ClearAction();
		fSuspendAction.setEnabled(isListening);
		fResumeAction.setEnabled(!isListening);

		IToolBarManager mgr = site.getActionBars().getToolBarManager();
		mgr.add(fResumeAction);
		mgr.add(fSuspendAction);
		mgr.add(fClearAction);
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putString(MEM_LISTENING, Boolean.toString(isListening));
	}

	public void setFocus() {
		fViewer.getControl().setFocus();
	}
}
