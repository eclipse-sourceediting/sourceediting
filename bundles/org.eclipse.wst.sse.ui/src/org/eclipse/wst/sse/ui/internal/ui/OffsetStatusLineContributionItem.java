/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.Utilities;

/**
 * @author nsd A Status Line contribution intended to display the selected
 *         offsets in an editor. Double-clicking shows information about
 *         partitions and the Structured Document regions.
 */
public class OffsetStatusLineContributionItem extends StatusLineContributionItem {

	class InformationDialog extends Dialog {
		IDocument fDocument = fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput());

		public InformationDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			parent.getShell().setText("Selection Information");
			Composite composite = (Composite) super.createDialogArea(parent);

			TabFolder tabfolder = new TabFolder(composite, SWT.NONE);
			tabfolder.setLayoutData(new GridData(GridData.FILL_BOTH));

			TabItem partitionTab = new TabItem(tabfolder, SWT.BORDER);
			partitionTab.setText("Partitions");
			Composite partitions = new Composite(tabfolder, SWT.NONE);
			partitionTab.setControl(partitions);
			createPartitionContents(partitions);

			// only create the ITextRegions tab for IStructuredDocuments
			if (fDocument instanceof IStructuredDocument) {
				TabItem regionTab = new TabItem(tabfolder, SWT.BORDER);
				regionTab.setText("ITextRegions");
				SashForm regions = new SashForm(tabfolder, SWT.NONE);
				regions.setOrientation(SWT.HORIZONTAL);
				regionTab.setControl(regions);
				createRegionsContents(regions);
			}

			return composite;
		}

		/**
		 * @param area
		 */
		private void createPartitionContents(Composite area) {
			area.setLayout(new GridLayout());
			area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			Composite partioningComposite = new Composite(area, SWT.NONE);
			partioningComposite.setLayout(new GridLayout(2, false));
			partioningComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			Text label = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
			label.setText("Partitioning: ");
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
			final Combo partitioningCombo = new Combo(partioningComposite, SWT.READ_ONLY);
			partitioningCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			final Text partitioningLabel = new Text(area, SWT.SINGLE | SWT.READ_ONLY);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 2;
			partitioningLabel.setLayoutData(gd);

			final TableViewer fPartitionTable = new TableViewer(area, SWT.FULL_SELECTION);
			fPartitionTable.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
			fPartitionTable.setContentProvider(new ArrayContentProvider());
			fPartitionTable.getTable().setHeaderVisible(true);
			fPartitionTable.getTable().setLinesVisible(true);
			String[] columns = new String[]{"Start", "Length", "Type"};
			fPartitionTable.setLabelProvider(new ITableLabelProvider() {

				public void addListener(ILabelProviderListener listener) {
				}

				public void dispose() {
				}

				public Image getColumnImage(Object element, int columnIndex) {
					return null;
				}

				public String getColumnText(Object element, int columnIndex) {
					ITypedRegion partition = (ITypedRegion) element;
					String text = null;
					switch (columnIndex) {
						case 0 :
							text = Integer.toString(partition.getOffset());
							break;
						case 1 :
							text = Integer.toString(partition.getLength());
							break;
						case 2 :
							text = partition.getType();
							break;
					}
					if (text == null)
						text = "";
					return text;
				}

				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				public void removeListener(ILabelProviderListener listener) {
				}
			});
			TableLayout tlayout = new TableLayout();
			CellEditor[] cellEditors = new CellEditor[2];
			int columnWidths[] = new int[]{Display.getCurrent().getBounds().width / 14, Display.getCurrent().getBounds().width / 14, Display.getCurrent().getBounds().width / 5};
			for (int i = 0; i < columns.length; i++) {
				tlayout.addColumnData(new ColumnWeightData(1));
				TableColumn tc = new TableColumn(fPartitionTable.getTable(), SWT.NONE);
				tc.setText(columns[i]);
				tc.setResizable(true);
				tc.setWidth(columnWidths[i]);
			}
			fPartitionTable.setCellEditors(cellEditors);
			fPartitionTable.setColumnProperties(columns);
			final String[] partitionings = (fDocument instanceof IDocumentExtension3) ? ((IDocumentExtension3) fDocument).getPartitionings() : new String[]{IDocumentExtension3.DEFAULT_PARTITIONING};
			partitioningCombo.setItems(partitionings);
			partitioningCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					ISelection sel = fTextEditor.getSelectionProvider().getSelection();
					ITextSelection textSelection = (ITextSelection) sel;
					try {
						String partitionerText = fDocument instanceof IDocumentExtension3 ? ((IDocumentExtension3) fDocument).getDocumentPartitioner(partitioningCombo.getItem(partitioningCombo.getSelectionIndex())).toString() : fDocument.getDocumentPartitioner().toString();
						partitioningLabel.setText("Partitioner: " + partitionerText);
						fPartitionTable.setInput(TextUtilities.computePartitioning(fDocument, partitioningCombo.getItem(partitioningCombo.getSelectionIndex()), textSelection.getOffset(), textSelection.getLength(), true));
					}
					catch (BadLocationException e1) {
						fPartitionTable.setInput(new ITypedRegion[0]);
					}
				}
			});
			try {
				String selectedPartitioning = partitioningCombo.getItem(0);
				if (Utilities.contains(partitionings, IStructuredDocument.DEFAULT_STRUCTURED_PARTITIONING)) {
					selectedPartitioning = IStructuredDocument.DEFAULT_STRUCTURED_PARTITIONING;
					for (int i = 0; i < partitionings.length; i++) {
						if (partitionings[i].equals(IStructuredDocument.DEFAULT_STRUCTURED_PARTITIONING)) {
							partitioningCombo.select(i);
						}
					}
				}
				else {
					partitioningCombo.select(0);
				}
				ISelection sel = fTextEditor.getSelectionProvider().getSelection();
				ITextSelection textSelection = (ITextSelection) sel;
				fPartitionTable.setInput(TextUtilities.computePartitioning(fDocument, selectedPartitioning, textSelection.getOffset(), textSelection.getLength(), true));
				String partitionerText = fDocument instanceof IDocumentExtension3 ? ((IDocumentExtension3) fDocument).getDocumentPartitioner(partitioningCombo.getItem(partitioningCombo.getSelectionIndex())).toString() : fDocument.getDocumentPartitioner().toString();
				partitioningLabel.setText("Partitioner: " + partitionerText);
			}
			catch (BadLocationException e1) {
				fPartitionTable.setInput(new ITypedRegion[0]);
			}
			partitioningCombo.setFocus();
		}

		/**
		 * @param composite
		 * @return
		 */
		private Composite createRegionsContents(SashForm area) {
			ISelection sel = fTextEditor.getSelectionProvider().getSelection();
			final ITextSelection textSelection = (ITextSelection) sel;
			final List documentRegions = new ArrayList();
			if (fDocument instanceof IStructuredDocument) {
				IStructuredDocument structuredDocument = (IStructuredDocument) fDocument;
				int pos = textSelection.getOffset();
				int end = textSelection.getOffset() + textSelection.getLength();
				IStructuredDocumentRegion docRegion = structuredDocument.getRegionAtCharacterOffset(pos);
				IStructuredDocumentRegion endRegion = structuredDocument.getRegionAtCharacterOffset(end);
				if (pos < end) {
					while (docRegion != endRegion) {
						documentRegions.add(docRegion);
						docRegion = docRegion.getNext();
					}
				}
				documentRegions.add(docRegion);
			}

			final TreeViewer tree = new TreeViewer(area, SWT.V_SCROLL | SWT.H_SCROLL);
			final String START = "Start";
			final String LENGTH = "Length";
			final String TEXTLENGTH = "Text Length";
			final String CONTEXT = "Context";
			tree.setContentProvider(new ITreeContentProvider() {
				public void dispose() {
				}

				public Object[] getChildren(Object parentElement) {
					List children = new ArrayList(0);
					if (parentElement instanceof ITextRegionCollection) {
						children.add(((ITextRegionCollection) parentElement).getRegions().toArray());
					}
					if (parentElement instanceof ITextRegion) {
						children.add(new Pair(CONTEXT, ((ITextRegion) parentElement).getType()));
						children.add(new Pair(START, Integer.toString(((ITextRegion) parentElement).getStart())));
						children.add(new Pair(TEXTLENGTH, Integer.toString(((ITextRegion) parentElement).getTextLength())));
						children.add(new Pair(LENGTH, Integer.toString(((ITextRegion) parentElement).getLength())));
					}
					if (parentElement instanceof ITextRegionList) {
						children.add(Arrays.asList(((ITextRegionList) parentElement).toArray()));
					}
					if (parentElement instanceof Collection) {
						children.addAll((Collection) parentElement);
					}
					if (parentElement instanceof Object[]) {
						children.addAll(Arrays.asList((Object[]) parentElement));
					}
					return children.toArray();
				}

				public Object[] getElements(Object inputElement) {
					return documentRegions.toArray();
				}

				public Object getParent(Object element) {
					if (element instanceof IStructuredDocumentRegion)
						return ((IStructuredDocumentRegion) element).getParentDocument();
					if (element instanceof ITextRegionContainer) {
						return ((ITextRegionContainer) element).getParent();
					}
					return null;
				}

				public boolean hasChildren(Object element) {
					return !(element instanceof Pair);
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
			});
			tree.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					if (element instanceof Pair)
						return ((Pair) element).fKey.toString().toLowerCase() + ": " + ((Pair) element).fValue;
					if (element instanceof IStructuredDocumentRegion) {
						IStructuredDocumentRegion documentRegion = (IStructuredDocumentRegion) element;
						int packageNameLength = documentRegion.getClass().getPackage().getName().length();
						if (packageNameLength > 0)
							packageNameLength++;
						String name = documentRegion.getClass().getName().substring(packageNameLength);
						String text = "[" + documentRegion.getStartOffset() + "-" + documentRegion.getEndOffset() + "] " + name + "@" + element.hashCode() + " " + documentRegion.getType();
						return text;
					}
					if (element instanceof ITextRegion) {
						ITextRegion textRegion = (ITextRegion) element;
						int packageNameLength = textRegion.getClass().getPackage().getName().length();
						if (packageNameLength > 0)
							packageNameLength++;
						String name = textRegion.getClass().getName().substring(packageNameLength);
						String text = "[" + textRegion.getStart() + "-" + textRegion.getEnd() + "] " + name + "@" + element.hashCode() + " " + textRegion.getType();
						return text;
					}
					return super.getText(element);
				}
			});
			tree.setInput(fDocument);
			final Text displayText = new Text(area, SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.BORDER);
			displayText.setBackground(area.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			tree.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					if (event.getSelection() instanceof IStructuredSelection) {
						Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
						if (o instanceof Pair)
							displayText.setText(((Pair) o).fValue.toString());
						else
							displayText.setText("" + o);
					}
				}
			});
			area.setWeights(new int[]{2, 1});
			return area;
		}
	}

	static class Pair {
		Object fKey;
		String fValue;

		public Pair(Object key, String value) {
			fKey = key;
			fValue = value;
		}
	}

	class ShowPartitionAction extends Action {
		public ShowPartitionAction() {
			super();
		}

		public void run() {
			/**
			 * TODO: Provide a more useful control, maybe a table where the
			 * selection shows you the partition's text in a StyledText pane
			 * beneath it.
			 */
			super.run();
			new InformationDialog(((Control) fTextEditor.getAdapter(Control.class)).getShell()).open();
		}
	}

	IAction fShowPartitionAction = new ShowPartitionAction();

	ITextEditor fTextEditor = null;

	/**
	 * @param id
	 */
	public OffsetStatusLineContributionItem(String id) {
		super(id);
	}

	/**
	 * @param id
	 * @param visible
	 * @param widthInChars
	 */
	public OffsetStatusLineContributionItem(String id, boolean visible, int widthInChars) {
		super(id, visible, widthInChars);
	}

	public void setActiveEditor(ITextEditor textEditor) {
		fTextEditor = textEditor;
		setToolTipText("Show Selection Information...");
		setActionHandler(fShowPartitionAction);
	}
}
