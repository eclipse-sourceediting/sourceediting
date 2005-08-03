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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.Annotation;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Utilities;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;

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

		private void createAnnotationContents(Composite annotationsTabComposite) {
			annotationsTabComposite.setLayout(new GridLayout());
			annotationsTabComposite.setLayoutData(new GridData());

			Composite annotationsComposite = new Composite(annotationsTabComposite, SWT.NONE);
			annotationsComposite.setLayout(new GridLayout(2, false));
			annotationsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			final TableViewer annotationsTable = new TableViewer(annotationsComposite, SWT.FULL_SELECTION);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.horizontalSpan = 2;
			annotationsTable.getControl().setLayoutData(gd);
			annotationsTable.setContentProvider(new ArrayContentProvider());
			annotationsTable.getTable().setHeaderVisible(true);
			annotationsTable.getTable().setLinesVisible(true);
			String[] columns = new String[]{"Line", "Owner", "Type", "Message"};
			annotationsTable.setLabelProvider(new ITableLabelProvider() {
				public void addListener(ILabelProviderListener listener) {
				}

				public void dispose() {
				}

				public Image getColumnImage(Object element, int columnIndex) {
					return null;
				}

				public String getColumnText(Object element, int columnIndex) {
					Annotation annotation = (Annotation) element;
					String text = null;
					switch (columnIndex) {
						case 0 :
							text = (annotation instanceof MarkerAnnotation) ? Integer.toString(MarkerUtilities.getLineNumber(((MarkerAnnotation) annotation).getMarker())) : "-1";
							break;
						case 1 :
							text = (annotation instanceof MarkerAnnotation) ? ((MarkerAnnotation) annotation).getMarker().getAttribute("owner", "n/a") : "?";
							break;
						case 2 :
							text = (annotation instanceof MarkerAnnotation) ? MarkerUtilities.getMarkerType(((MarkerAnnotation) annotation).getMarker()) : "?";
							break;
						case 3 :
							text = annotation.getText();
							break;
					}
					if (text == null)
						text = ""; //$NON-NLS-1$
					return text;
				}

				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				public void removeListener(ILabelProviderListener listener) {
				}
			});

			TableLayout tlayout = new TableLayout();
			CellEditor[] cellEditors = new CellEditor[columns.length];
			int columnWidths[] = new int[]{Display.getCurrent().getBounds().width / 14, Display.getCurrent().getBounds().width / 7, Display.getCurrent().getBounds().width / 7, Display.getCurrent().getBounds().width / 7};
			for (int i = 0; i < columns.length; i++) {
				tlayout.addColumnData(new ColumnWeightData(1));
				TableColumn tc = new TableColumn(annotationsTable.getTable(), SWT.NONE);
				tc.setText(columns[i]);
				tc.setResizable(true);
				tc.setWidth(columnWidths[i]);
			}
			annotationsTable.setCellEditors(cellEditors);
			annotationsTable.setColumnProperties(columns);
			// textSelection.getOffset(), textSelection.getLength()
			List matchingAnnotations = new ArrayList(0);
			Iterator iterator = fTextEditor.getDocumentProvider().getAnnotationModel(fTextEditor.getEditorInput()).getAnnotationIterator();
			while (iterator.hasNext()) {
				Annotation element = (Annotation) iterator.next();
				if (true) {
					matchingAnnotations.add(element);
				}
			}
			annotationsTable.setInput(matchingAnnotations);

			final PropertySheetPage propertySheet = new PropertySheetPage();
			propertySheet.createControl(annotationsComposite);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.horizontalSpan = 2;
			propertySheet.getControl().setLayoutData(gd);
			propertySheet.setPropertySourceProvider(new IPropertySourceProvider() {
				public IPropertySource getPropertySource(Object object) {
					if (object instanceof MarkerAnnotation) {
						IPropertySource markerAnnotationPropertySource = new MarkerAnnotationPropertySource(((MarkerAnnotation) object));
						return markerAnnotationPropertySource;
					}
					return null;
				}
			});

			annotationsTable.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					propertySheet.selectionChanged(null, event.getSelection());
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			ISelection sel = fTextEditor.getSelectionProvider().getSelection();
			ITextSelection textSelection = (ITextSelection) sel;
			parent.getShell().setText(SSEUIMessages.OffsetStatusLineContributionItem_0 + textSelection.getOffset() + "-" + (textSelection.getOffset() + textSelection.getLength())); //$NON-NLS-1$ //$NON-NLS-2$
			Composite composite = (Composite) super.createDialogArea(parent);

			TabFolder tabfolder = new TabFolder(composite, SWT.NONE);
			tabfolder.setLayoutData(new GridData(GridData.FILL_BOTH));

			TabItem partitionTab = new TabItem(tabfolder, SWT.BORDER);
			partitionTab.setText(SSEUIMessages.OffsetStatusLineContributionItem_2); //$NON-NLS-1$
			Composite partitions = new Composite(tabfolder, SWT.NONE);
			partitionTab.setControl(partitions);
			createPartitionContents(partitions);

			// only create the ITextRegions tab for IStructuredDocuments
			if (fDocument instanceof IStructuredDocument) {
				TabItem regionTab = new TabItem(tabfolder, SWT.BORDER);
				regionTab.setText(SSEUIMessages.OffsetStatusLineContributionItem_3); //$NON-NLS-1$
				SashForm regions = new SashForm(tabfolder, SWT.NONE);
				regions.setOrientation(SWT.HORIZONTAL);
				regionTab.setControl(regions);
				createRegionsContents(regions);
			}

			TabItem annotationsTab = new TabItem(tabfolder, SWT.BORDER);
			annotationsTab.setText("Annotations"); //$NON-NLS-1$
			Composite annotations = new Composite(tabfolder, SWT.NONE);
			annotationsTab.setControl(annotations);
			createAnnotationContents(annotations);

			return composite;
		}

		/**
		 * @param area
		 */
		private void createPartitionContents(Composite area) {
			area.setLayout(new GridLayout());
			area.setLayoutData(new GridData());

			Composite partioningComposite = new Composite(area, SWT.NONE);
			partioningComposite.setLayout(new GridLayout(2, false));
			partioningComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			Text documentTypeLabel = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 2;
			documentTypeLabel.setLayoutData(gd);
			documentTypeLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_6 + fDocument.getClass().getName()); //$NON-NLS-1$

			Text documentProviderLabel = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 2;
			documentProviderLabel.setLayoutData(gd);
			documentProviderLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_7 + fTextEditor.getDocumentProvider().getClass().getName()); //$NON-NLS-1$

			Text editorInputLabel = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 2;
			editorInputLabel.setLayoutData(gd);
			editorInputLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_12 + fTextEditor.getEditorInput().getClass().getName()); //$NON-NLS-1$

			IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (model != null) {
				Text modelContentTypeLabel = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
				gd = new GridData(SWT.FILL, SWT.FILL, true, false);
				gd.horizontalSpan = 2;
				modelContentTypeLabel.setLayoutData(gd);
				modelContentTypeLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_4 + model.getContentTypeIdentifier()); //$NON-NLS-1$

				Text modelHandlerContentTypeLabel = new Text(partioningComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
				gd = new GridData(SWT.FILL, SWT.FILL, true, false);
				gd.horizontalSpan = 2;
				modelHandlerContentTypeLabel.setLayoutData(gd);
				modelHandlerContentTypeLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_5 + model.getModelHandler() + " (" + model.getModelHandler().getAssociatedContentTypeId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				Label blankRow = new Label(partioningComposite, SWT.NONE);
				gd = new GridData(SWT.FILL, SWT.FILL, true, false);
				gd.horizontalSpan = 2;
				blankRow.setLayoutData(gd);
			}
			if (model != null) {
				model.releaseFromRead();
			}

			Text label = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
			label.setText(SSEUIMessages.OffsetStatusLineContributionItem_8); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			final Combo partitioningCombo = new Combo(partioningComposite, SWT.READ_ONLY);
			partitioningCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			final Text partitionerInstanceLabel = new Text(partioningComposite, SWT.SINGLE | SWT.READ_ONLY);
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			gd.horizontalSpan = 2;
			partitionerInstanceLabel.setLayoutData(gd);

			final TableViewer fPartitionTable = new TableViewer(partioningComposite, SWT.FULL_SELECTION);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.horizontalSpan = 2;
			fPartitionTable.getControl().setLayoutData(gd);
			fPartitionTable.setContentProvider(new ArrayContentProvider());
			fPartitionTable.getTable().setHeaderVisible(true);
			fPartitionTable.getTable().setLinesVisible(true);
			String[] columns = new String[]{SSEUIMessages.OffsetStatusLineContributionItem_9, SSEUIMessages.OffsetStatusLineContributionItem_10, SSEUIMessages.OffsetStatusLineContributionItem_11}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
						text = ""; //$NON-NLS-1$
					return text;
				}

				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				public void removeListener(ILabelProviderListener listener) {
				}
			});
			TableLayout tlayout = new TableLayout();
			CellEditor[] cellEditors = new CellEditor[columns.length];
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
						partitionerInstanceLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_13 + partitionerText); //$NON-NLS-1$
						fPartitionTable.setInput(TextUtilities.computePartitioning(fDocument, partitioningCombo.getItem(partitioningCombo.getSelectionIndex()), textSelection.getOffset(), textSelection.getLength(), true));
					}
					catch (BadLocationException e1) {
						fPartitionTable.setInput(new ITypedRegion[0]);
					}
				}
			});
			try {
				String selectedPartitioning = partitioningCombo.getItem(0);
				if (Utilities.contains(partitionings, IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING)) {
					selectedPartitioning = IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING;
					for (int i = 0; i < partitionings.length; i++) {
						if (partitionings[i].equals(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING)) {
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
				partitionerInstanceLabel.setText(SSEUIMessages.OffsetStatusLineContributionItem_13 + partitionerText); //$NON-NLS-1$
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
		private Composite createRegionsContents(SashForm sashForm) {
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

			final TreeViewer tree = new TreeViewer(sashForm, SWT.V_SCROLL | SWT.H_SCROLL);
			final String START = SSEUIMessages.OffsetStatusLineContributionItem_15; //$NON-NLS-1$
			final String LENGTH = SSEUIMessages.OffsetStatusLineContributionItem_16; //$NON-NLS-1$
			final String TEXTLENGTH = SSEUIMessages.OffsetStatusLineContributionItem_17; //$NON-NLS-1$
			final String CONTEXT = SSEUIMessages.OffsetStatusLineContributionItem_18; //$NON-NLS-1$
			tree.setContentProvider(new ITreeContentProvider() {
				public void dispose() {
				}

				public Object[] getChildren(Object parentElement) {
					List children = new ArrayList(0);
					if (parentElement instanceof ITextSelection) {
						children.addAll(documentRegions);
					}
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
					return new Object[]{textSelection};
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
						return ((Pair) element).fKey.toString().toLowerCase() + ": " + ((Pair) element).fValue; //$NON-NLS-1$
					if (element instanceof IStructuredDocumentRegion) {
						IStructuredDocumentRegion documentRegion = (IStructuredDocumentRegion) element;
						int packageNameLength = documentRegion.getClass().getPackage().getName().length();
						if (packageNameLength > 0)
							packageNameLength++;
						String name = documentRegion.getClass().getName().substring(packageNameLength);
						String text = "[" + documentRegion.getStartOffset() + "-" + documentRegion.getEndOffset() + "] " + name + "@" + element.hashCode() + " " + documentRegion.getType(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						return text;
					}
					if (element instanceof ITextRegion) {
						ITextRegion textRegion = (ITextRegion) element;
						int packageNameLength = textRegion.getClass().getPackage().getName().length();
						if (packageNameLength > 0)
							packageNameLength++;
						String name = textRegion.getClass().getName().substring(packageNameLength);
						String text = "[" + textRegion.getStart() + "-" + textRegion.getEnd() + "] " + name + "@" + element.hashCode() + " " + textRegion.getType(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						return text;
					}
					return super.getText(element);
				}
			});
			tree.setInput(fDocument);
			final Text displayText = new Text(sashForm, SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.BORDER);
			displayText.setBackground(sashForm.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			tree.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					if (event.getSelection() instanceof IStructuredSelection) {
						Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
						if (o instanceof Pair)
							displayText.setText(((Pair) o).fValue.toString());
						else if (o instanceof ITextSelection) {
							ITextSelection text = (ITextSelection) o;
							try {
								displayText.setText(fDocument.get(text.getOffset(), text.getLength()));
							}
							catch (BadLocationException e) {
								displayText.setText(""); //$NON-NLS-1$
							}
						}
						else
							displayText.setText("" + o); //$NON-NLS-1$
					}
				}
			});
			sashForm.setWeights(new int[]{3, 2});
			return sashForm;
		}
	}

	class MarkerAnnotationPropertySource implements IPropertySource {
		MarkerAnnotation fMarkerAnnotation = null;
		IPropertyDescriptor[] fDescriptors = null;

		public MarkerAnnotationPropertySource(MarkerAnnotation markerAnnotation) {
			super();
			fMarkerAnnotation = markerAnnotation;
		}

		public Object getEditableValue() {
			return null;
		}

		public IPropertyDescriptor[] getPropertyDescriptors() {
			if (fDescriptors == null) {
				try {
					Map attrs = fMarkerAnnotation.getMarker().getAttributes();
					Object[] keys = attrs.keySet().toArray();
					fDescriptors = new IPropertyDescriptor[keys.length];
					for (int i = 0; i < keys.length; i++) {
						TextPropertyDescriptor descriptor = new TextPropertyDescriptor(keys[i].toString(), keys[i].toString());
						fDescriptors[i] = descriptor;
					}
				}
				catch (CoreException e) {
					e.printStackTrace();
				}
			}
			return fDescriptors;
		}

		public Object getPropertyValue(Object id) {
			return fMarkerAnnotation.getMarker().getAttribute(id.toString(), "");
		}

		public boolean isPropertySet(Object id) {
			return fMarkerAnnotation.getMarker().getAttribute(id.toString(), null) != null;
		}

		public void resetPropertyValue(Object id) {
			try {
				fMarkerAnnotation.getMarker().getAttributes().remove(id);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}

		public void setPropertyValue(Object id, Object value) {
			try {
				fMarkerAnnotation.getMarker().setAttribute(id.toString(), value);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
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
		setActionHandler(fShowPartitionAction);
	}
}
