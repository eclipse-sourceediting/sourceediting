/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.emf.common.command.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISelectionValidator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineChangeHover;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IStatusField;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.IExecutionDelegatable;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.ExtendedEditorActionBuilder;
import org.eclipse.wst.sse.ui.internal.ExtendedEditorDropTargetAdapter;
import org.eclipse.wst.sse.ui.internal.IExtendedContributor;
import org.eclipse.wst.sse.ui.internal.IModelProvider;
import org.eclipse.wst.sse.ui.internal.IPopupMenuContributor;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.ReadOnlyAwareDropTargetAdapter;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StorageModelProvider;
import org.eclipse.wst.sse.ui.internal.StructuredLineChangeHover;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.UnknownContentTypeDialog;
import org.eclipse.wst.sse.ui.internal.actions.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.internal.actions.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.internal.contentoutline.ConfigurableContentOutlinePage;
import org.eclipse.wst.sse.ui.internal.debug.BreakpointRulerAction;
import org.eclipse.wst.sse.ui.internal.debug.EditBreakpointAction;
import org.eclipse.wst.sse.ui.internal.debug.ManageBreakpointAction;
import org.eclipse.wst.sse.ui.internal.debug.ToggleBreakpointAction;
import org.eclipse.wst.sse.ui.internal.debug.ToggleBreakpointsTarget;
import org.eclipse.wst.sse.ui.internal.derived.HTMLTextPresenter;
import org.eclipse.wst.sse.ui.internal.editor.EditorModelUtil;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.internal.editor.StructuredModelDocumentProvider;
import org.eclipse.wst.sse.ui.internal.extension.BreakpointProviderBuilder;
import org.eclipse.wst.sse.ui.internal.hyperlink.OpenHyperlinkAction;
import org.eclipse.wst.sse.ui.internal.preferences.EditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;
import org.eclipse.wst.sse.ui.internal.properties.ConfigurablePropertySheetPage;
import org.eclipse.wst.sse.ui.internal.properties.ShowPropertiesAction;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ConfigurationPointCalculator;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.NullSourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.eclipse.wst.sse.ui.internal.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.sse.ui.internal.util.Assert;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration;

/**
 * A Text Editor for editing structured models and structured documents.
 * <p>
 * This class is not meant to be subclassed.<br />
 * New content types may associate source viewer, content outline, and
 * property sheet configurations to extend the existing functionality.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
 * @see org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration
 * 
 * @since 1.0
 */

public class StructuredTextEditor extends TextEditor {


	// ISSUE: This use case is not clear to me.
	// Is this listner and dance with dirty state for non-editor driven
	// updates?
	class InternalDocumentListener implements IDocumentListener {
		// This is for the IDocumentListener interface
		public void documentAboutToBeChanged(DocumentEvent event) {
			fDirtyBeforeDocumentEvent = isDirty();
		}

		// This is for the IDocumentListener interface
		public void documentChanged(DocumentEvent event) {
			if (isEditorInputReadOnly()) {
				// stop listening to document event
				// caused by the undo after validateEdit
				final int offset = event.getOffset() + event.getLength();
				final IStructuredModel internalModel = getInternalModel();
				fCurrentRunnable = new Runnable() {
					public void run() {
						if (!fEditorDisposed) {
							boolean status = validateEditorInputState();
							if (!status) {
								if (internalModel != null) {
									internalModel.getUndoManager().undo();
									getSourceViewer().setSelectedRange(offset, 0);
									if (!fDirtyBeforeDocumentEvent) {
										// reset dirty state if
										// model not dirty before
										// document event
										internalModel.setDirtyState(false);
									}
								}
							}
						}
						fCurrentRunnable = null;
					}
				};
				/*
				 * We need to ensure that this is run via 'asyncExec' since
				 * these notifications can come from a non-ui thread.
				 * 
				 * The non-ui thread call would occur when creating a new file
				 * under ClearCase (or other library) control. The creation of
				 * the new file would trigger a validateEdit call, on another
				 * thread, that would prompt the user to add the new file to
				 * version control.
				 */
				Display display = getDisplay();
				if (display != null) {
					if (Thread.currentThread() != display.getThread())
						// future_TODO: there's probably a better
						// way than relying on asycnExec
						display.asyncExec(fCurrentRunnable);
					else
						fCurrentRunnable.run();
				}
			}
		}
	}

	private class InternalModelStateListener implements IModelStateListener {
		public void modelAboutToBeChanged(IStructuredModel model) {
			if (getTextViewer() != null) {
				// getTextViewer().setRedraw(false);
			}
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			if (getTextViewer() != null) {
				// getTextViewer().setRedraw(false);
				getTextViewer().unconfigure();
			}
		}

		public void modelChanged(IStructuredModel model) {
			if (getTextViewer() != null) {
				// getTextViewer().setRedraw(true);
				// Since the model can be changed on a background
				// thread, we will update menus on display thread,
				// if we are not already on display thread,
				// and if there is not an update already pending.
				// (we can get lots of 'modelChanged' events in rapid
				// succession, so only need to do one.
				if (!fUpdateMenuTextPending) {
					runOnDisplayThreadIfNeededed(new Runnable() {
						public void run() {
							updateMenuText();
							fUpdateMenuTextPending = false;
						}
					});
				}

			}
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			// do nothing
		}

		public void modelReinitialized(IStructuredModel structuredModel) {
			try {
				if (getSourceViewer() != null) {
					SourceViewerConfiguration cfg = getSourceViewerConfiguration();
					getSourceViewer().configure(cfg);
				}
			}
			catch (Exception e) {
				// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=1166
				// investigate each error case post beta
				Logger.logException("problem trying to configure after model change", e); //$NON-NLS-1$
			}
			finally {
				// so we don't freeze workbench (eg. during page language or
				// content type change)
				((ITextViewerExtension) getSourceViewer()).setRedraw(true);
			}
		}

		// Note: this one should probably be used to
		// control viewer
		// instead of viewer having its own listener
		public void modelResourceDeleted(IStructuredModel model) {
			// do nothing
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
			// do nothing
		}

		/**
		 * This 'Runnable' should be very brief, and should not "call out" to
		 * other code especially if it depends on the state of the model.
		 * 
		 * @param r
		 */
		private void runOnDisplayThreadIfNeededed(Runnable r) {
			// if there is no Display at all (that is, running headless),
			// or if we are already running on the display thread, then
			// simply execute the runnable.
			if (getDisplay() == null || (Thread.currentThread() == getDisplay().getThread())) {
				r.run();
			}
			else {
				// otherwise force the runnable to run on the display thread.
				getDisplay().asyncExec(r);
			}
		}
	}

	class MouseTracker extends MouseTrackAdapter implements MouseMoveListener {
		/** The tracker's subject control. */
		private Control fSubjectControl;

		/**
		 * Creates a new mouse tracker.
		 */
		public MouseTracker() {
			// do nothing
		}

		public void mouseHover(MouseEvent event) {
			// System.out.println("hover: "+event.x + "x" + event.y);
			hoverX = event.x;
			hoverY = event.y;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseMove(MouseEvent e) {
			hoverX = e.x;
			hoverY = e.y;
		}

		/**
		 * Starts this mouse tracker. The given control becomes this tracker's
		 * subject control. Installs itself as mouse track listener on the
		 * subject control.
		 * 
		 * @param subjectControl
		 *            the subject control
		 */
		public void start(Control subjectControl) {
			fSubjectControl = subjectControl;
			if (fSubjectControl != null && !fSubjectControl.isDisposed()) {
				fSubjectControl.addMouseMoveListener(this);
				fSubjectControl.addMouseTrackListener(this);
				fSubjectControl.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						stop();
					}
				});
			}
		}

		/**
		 * Stops this mouse tracker. Removes itself as mouse track, mouse
		 * move, and shell listener from the subject control.
		 */
		public void stop() {
			if (fSubjectControl != null && !fSubjectControl.isDisposed()) {
				fSubjectControl.removeMouseMoveListener(this);
				fSubjectControl.removeMouseTrackListener(this);
				fSubjectControl = null;
			}
		}
	}

	/**
	 * Listens to double-click and selection from the outline page
	 */
	private class OutlinePageListener implements IDoubleClickListener, ISelectionChangedListener {
		public void doubleClick(DoubleClickEvent event) {
			if (event.getSelection().isEmpty())
				return;

			int start = -1;
			int length = 0;
			if (event.getSelection() instanceof IStructuredSelection) {
				ISelection currentSelection = getSelectionProvider().getSelection();
				if (currentSelection instanceof IStructuredSelection) {
					Object current = ((IStructuredSelection) currentSelection).toArray();
					Object newSelection = ((IStructuredSelection) event.getSelection()).toArray();
					if (!current.equals(newSelection)) {
						IStructuredSelection selection = (IStructuredSelection) event.getSelection();
						Object o = selection.getFirstElement();
						if (o instanceof IndexedRegion) {
							start = ((IndexedRegion) o).getStartOffset();
							length = ((IndexedRegion) o).getEndOffset() - start;
						}
						else if (o instanceof ITextRegion) {
							start = ((ITextRegion) o).getStart();
							length = ((ITextRegion) o).getEnd() - start;
						}
						else if (o instanceof IRegion) {
							start = ((ITextRegion) o).getStart();
							length = ((ITextRegion) o).getLength();
						}
					}
				}
			}
			else if (event.getSelection() instanceof ITextSelection) {
				start = ((ITextSelection) event.getSelection()).getOffset();
				length = ((ITextSelection) event.getSelection()).getLength();
			}
			if (start > -1) {
				getSourceViewer().setRangeIndication(start, length, false);
				selectAndReveal(start, length);
			}
		}

		public void selectionChanged(SelectionChangedEvent event) {
			/*
			 * Do not allow selection from other parts to affect selection in
			 * the text widget if it has focus, or if we're still firing a
			 * change of selection. Selection events "bouncing" off of other
			 * parts are all that we can receive if we have focus (since we
			 * forwarded our selection to the service just a moment ago), and
			 * only the user should affect selection if we have focus.
			 */

			/* The isFiringSelection check only works if a selection listener */
			if (event.getSelection().isEmpty() || fStructuredSelectionProvider.isFiringSelection())
				return;

			if (getSourceViewer() != null && getSourceViewer().getTextWidget() != null && !getSourceViewer().getTextWidget().isDisposed() && !getSourceViewer().getTextWidget().isFocusControl()) {
				int start = -1;
				int length = 0;
				if (event.getSelection() instanceof IStructuredSelection) {
					ISelection current = getSelectionProvider().getSelection();
					if (current instanceof IStructuredSelection) {
						Object[] currentSelection = ((IStructuredSelection) current).toArray();
						Object[] newSelection = ((IStructuredSelection) event.getSelection()).toArray();
						if (!Arrays.equals(currentSelection, newSelection)) {
							if (newSelection.length > 0) {
								/*
								 * No ordering is guaranteed for multiple
								 * selection
								 */
								Object o = newSelection[0];
								if (o instanceof IndexedRegion) {
									start = ((IndexedRegion) o).getStartOffset();
									int end = ((IndexedRegion) o).getEndOffset();
									if (newSelection.length > 1) {
										for (int i = 1; i < newSelection.length; i++) {
											start = Math.min(start, ((IndexedRegion) newSelection[i]).getStartOffset());
											end = Math.max(end, ((IndexedRegion) newSelection[i]).getEndOffset());
										}
										length = end - start;
									}
								}
								else if (o instanceof ITextRegion) {
									start = ((ITextRegion) o).getStart();
									int end = ((ITextRegion) o).getEnd();
									if (newSelection.length > 1) {
										for (int i = 1; i < newSelection.length; i++) {
											start = Math.min(start, ((ITextRegion) newSelection[i]).getStart());
											end = Math.max(end, ((ITextRegion) newSelection[i]).getEnd());
										}
										length = end - start;
									}
								}
								else if (o instanceof IRegion) {
									start = ((IRegion) o).getOffset();
									int end = start + ((IRegion) o).getLength();
									if (newSelection.length > 1) {
										for (int i = 1; i < newSelection.length; i++) {
											start = Math.min(start, ((IRegion) newSelection[i]).getOffset());
											end = Math.max(end, ((IRegion) newSelection[i]).getOffset() + ((IRegion) newSelection[i]).getLength());
										}
										length = end - start;
									}
								}
							}
						}
					}
				}
				else if (event.getSelection() instanceof ITextSelection) {
					start = ((ITextSelection) event.getSelection()).getOffset();
				}
				if (start > -1) {
					updateRangeIndication(event.getSelection());
					selectAndReveal(start, length);
				}
			}
		}
	}

	private class ShowInTargetListAdapter implements IShowInTargetList {
		/**
		 * Array of ID Strings that define the default show in targets for
		 * this editor.
		 * 
		 * @see org.eclipse.ui.part.IShowInTargetList#getShowInTargetIds()
		 * @return the array of ID Strings that define the default show in
		 *         targets for this editor.
		 */
		public String[] getShowInTargetIds() {
			return fShowInTargetIds;
		}
	}

	/**
	 * A post selection provider that wraps the provider implemented in
	 * AbstractTextEditor to provide a StructuredTextSelection to post
	 * selection listeners. Listens to selection changes from the source
	 * viewer.
	 */
	private static class StructuredSelectionProvider implements IPostSelectionProvider, ISelectionValidator {
		/**
		 * A "hybrid" text and structured selection class. Converts the source
		 * viewer text selection to a generic "getIndexedRegion"-derived
		 * structured selection, allowing selection changed listeners to
		 * possibly not need to reference the model directly.
		 */
		private static class StructuredTextSelection extends TextSelection implements IStructuredSelection, ITextSelection {
			private Reference selectedStructured;
			private InternalTextSelection fInternalTextSelection;

			StructuredTextSelection(ITextSelection selection, IDocument document, IStructuredModel model) {
				// note: we do not currently use super class at all, but, only
				// subclass TextSelection,
				// because some infrastructure code uses "instanceof
				// TextSelection" instead of ITextSelection.
				super(selection.getOffset(), selection.getLength());
				fInternalTextSelection = new InternalTextSelection(document, selection.getOffset(), selection.getLength());
				selectedStructured = new SoftReference(initializeInferredSelectedObjects(selection, model));
			}

			StructuredTextSelection(ITextSelection selection, Object[] selectedObjects, IDocument document) {
				super(selection.getOffset(), selection.getLength());
				fInternalTextSelection = new InternalTextSelection(document, selection.getOffset(), selection.getLength());
				selectedStructured = new SoftReference(selectedObjects);
			}

			StructuredTextSelection(IDocument document, int offset, int length, Object[] selectedObjects) {
				super(offset, length);
				fInternalTextSelection = new InternalTextSelection(document, offset, length);
				selectedStructured = new SoftReference(selectedObjects);
			}

			public Object getFirstElement() {
				Object[] selectedStructures = getSelectedStructures();
				return selectedStructures.length > 0 ? selectedStructures[0] : null;
			}

			private Object[] getSelectedStructures() {
				Object[] selectedStructures = (Object[]) selectedStructured.get();
				if (selectedStructures == null) {
					selectedStructures = new Object[0];
				}
				return selectedStructures;
			}

			private Object[] initializeInferredSelectedObjects(ITextSelection selection, IStructuredModel model) {
				Object[] localSelectedStructures = null;
				if (model != null) {
					IndexedRegion region = model.getIndexedRegion(selection.getOffset());
					int end = selection.getOffset() + selection.getLength();
					if (region != null) {
						if (end <= region.getEndOffset()) {
							// single selection
							localSelectedStructures = new Object[1];
							localSelectedStructures[0] = region;
						}
						else {
							// multiple selection
							int maxLength = model.getStructuredDocument().getLength();
							List structures = new ArrayList(2);
							while (region != null && region.getEndOffset() <= end && region.getEndOffset() < maxLength) {
								structures.add(region);
								region = model.getIndexedRegion(region.getEndOffset() + 1);
							}
							localSelectedStructures = structures.toArray();
						}
					}
				}
				if (localSelectedStructures == null) {
					localSelectedStructures = new Object[0];
				}
				return localSelectedStructures;
			}

			public boolean isEmpty() {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=191327
				return fInternalTextSelection.isEmpty() || getSelectedStructures().length == 0;
			}

			public Iterator iterator() {
				return toList().iterator();
			}

			public int size() {
				return getSelectedStructures().length;
			}

			public Object[] toArray() {
				return getSelectedStructures();
			}

			public List toList() {
				return Arrays.asList(getSelectedStructures());
			}

			public String toString() {
				return fInternalTextSelection.getOffset() + ":" + fInternalTextSelection.getLength() + "@" + getSelectedStructures(); //$NON-NLS-1$ //$NON-NLS-2$
			}


			private static class InternalTextSelection implements ITextSelection {


				private SoftReference weakDocument;

				/** Offset of the selection */
				private int fOffset;
				/** Length of the selection */
				private int fLength;

				/**
				 * Creates a text selection for the given range. This
				 * selection object describes generically a text range and is
				 * intended to be an argument for the
				 * <code>setSelection</code> method of selection providers.
				 * 
				 * @param offset
				 *            the offset of the range
				 * @param length
				 *            the length of the range
				 */
				InternalTextSelection(int offset, int length) {
					this(null, offset, length);
				}

				/**
				 * Creates a text selection for the given range of the given
				 * document. This selection object is created by selection
				 * providers in responds <code>getSelection</code>.
				 * 
				 * @param document
				 *            the document whose text range is selected in a
				 *            viewer
				 * @param offset
				 *            the offset of the selected range
				 * @param length
				 *            the length of the selected range
				 */
				InternalTextSelection(IDocument document, int offset, int length) {
					weakDocument = new SoftReference(document);
					fOffset = offset;
					fLength = length;
				}

				/**
				 * 
				 * Returns true if the offset and length are smaller than 0. A
				 * selection of length 0, is a valid text selection as it
				 * describes, e.g., the cursor position in a viewer.
				 * 
				 * @return <code>true</code> if this selection is empty
				 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
				 */
				public boolean isEmpty() {
					return fOffset < 0 || fLength < 0;
				}

				/*
				 * @see org.eclipse.jface.text.ITextSelection#getOffset()
				 */
				public int getOffset() {
					return fOffset;
				}

				/*
				 * @see org.eclipse.jface.text.ITextSelection#getLength()
				 */
				public int getLength() {
					return fLength;
				}

				/*
				 * @see org.eclipse.jface.text.ITextSelection#getStartLine()
				 */
				public int getStartLine() {

					IDocument document = (IDocument) weakDocument.get();
					try {
						if (document != null)
							return document.getLineOfOffset(fOffset);
					}
					catch (BadLocationException x) {
					}

					return -1;
				}

				/*
				 * @see org.eclipse.jface.text.ITextSelection#getEndLine()
				 */
				public int getEndLine() {
					IDocument document = (IDocument) weakDocument.get();
					try {
						if (document != null) {
							int endOffset = fOffset + fLength;
							if (fLength != 0)
								endOffset--;
							return document.getLineOfOffset(endOffset);
						}
					}
					catch (BadLocationException x) {
					}

					return -1;
				}

				/*
				 * @see org.eclipse.jface.text.ITextSelection#getText()
				 */
				public String getText() {
					IDocument document = (IDocument) weakDocument.get();
					try {
						if (document != null)
							return document.get(fOffset, fLength);
					}
					catch (BadLocationException x) {
					}

					return null;
				}

				/*
				 * @see java.lang.Object#equals(Object)
				 */
				public boolean equals(Object obj) {
					if (obj == this)
						return true;

					if (obj == null || getClass() != obj.getClass())
						return false;

					InternalTextSelection s = (InternalTextSelection) obj;
					boolean sameRange = (s.fOffset == fOffset && s.fLength == fLength);
					if (sameRange) {

						IDocument document = (IDocument) weakDocument.get();
						IDocument sDocument = s.getDocument();

						// ISSUE: why does not IDocument .equals suffice?
						if (sDocument == null && document == null)
							return true;
						if (sDocument == null || document == null)
							return false;

						try {
							// ISSUE: pricey! (a cached hash might be in
							// order, if this
							// was ever really ever used very often.
							String sContent = sDocument.get(fOffset, fLength);
							String content = document.get(fOffset, fLength);
							return sContent.equals(content);
						}
						catch (BadLocationException x) {
							// return false, can not be equal
						}
					}

					return false;
				}

				/*
				 * @see java.lang.Object#hashCode()
				 */
				public int hashCode() {
					IDocument document = (IDocument) weakDocument.get();
					int low = document != null ? document.hashCode() : 0;
					return (fOffset << 24) | (fLength << 16) | low;
				}

				private IDocument getDocument() {
					if (weakDocument == null)
						return null;
					return (IDocument) weakDocument.get();
				}
			}

			public int getOffset() {
				return fInternalTextSelection.getOffset();
			}


			public int getLength() {
				return fInternalTextSelection.getLength();
			}

			public int getStartLine() {
				return fInternalTextSelection.getStartLine();
			}

			public int getEndLine() {
				return fInternalTextSelection.getEndLine();
			}

			public String getText() {
				return fInternalTextSelection.getText();
			}
		}

		private SoftReference weakDocument;
		private ISelectionProvider fParentProvider = null;
		private boolean isFiringSelection = false;
		private ListenerList listeners = new ListenerList();
		private ListenerList postListeners = new ListenerList();
		private ISelection fLastSelection = null;
		private ISelectionProvider fLastSelectionProvider = null;
		private SelectionChangedEvent fLastUpdatedSelectionChangedEvent = null;
		private SoftReference weakEditor;


		StructuredSelectionProvider(ISelectionProvider parentProvider, StructuredTextEditor structuredTextEditor) {
			fParentProvider = parentProvider;
			weakEditor = new SoftReference(structuredTextEditor);
			IDocument document = structuredTextEditor.getDocumentProvider().getDocument(structuredTextEditor.getEditorInput());
			if (document != null) {
				setDocument(document);
			}
			fParentProvider.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					handleSelectionChanged(event);
				}
			});
			if (fParentProvider instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) fParentProvider).addPostSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						handlePostSelectionChanged(event);
					}
				});
			}
		}

		public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
			postListeners.add(listener);
		}

		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		private void fireSelectionChanged(final SelectionChangedEvent event, ListenerList listenerList) {
			Object[] listeners = listenerList.getListeners();
			isFiringSelection = true;
			for (int i = 0; i < listeners.length; ++i) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
				SafeRunner.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(event);
					}
				});
			}
			isFiringSelection = false;
		}

		private ISelectionProvider getParentProvider() {
			return fParentProvider;
		}

		public ISelection getSelection() {
			fLastSelection = null;
			fLastSelectionProvider = null;
			fLastUpdatedSelectionChangedEvent = null;

			/*
			 * When a client explicitly asks for selection, provide the hybrid
			 * result.
			 */
			ISelection selection = getParentProvider().getSelection();
			if (!(selection instanceof IStructuredSelection) && selection instanceof ITextSelection) {
				IStructuredModel model = null;
				StructuredTextEditor localEditor = getStructuredTextEditor();
				if (localEditor != null) {
					model = localEditor.getModel();
					selection = new StructuredTextSelection((ITextSelection) selection, getDocument(), model);
				}
				else {
					selection = new StructuredTextSelection((ITextSelection) selection, getDocument(), null);
				}
			}
			return selection;
		}

		private StructuredTextEditor getStructuredTextEditor() {
			StructuredTextEditor editor = null;
			if (weakEditor != null) {
				editor = (StructuredTextEditor) weakEditor.get();
			}
			return editor;
		}

		void handlePostSelectionChanged(SelectionChangedEvent event) {
			SelectionChangedEvent updatedEvent = null;
			if (fLastSelection == event.getSelection() && fLastSelectionProvider == event.getSelectionProvider()) {
				updatedEvent = fLastUpdatedSelectionChangedEvent;
			}
			else {
				updatedEvent = updateEvent(event);
			}
			// only update the range indicator on post selection
			StructuredTextEditor localEditor = (StructuredTextEditor) weakEditor.get();

			if (localEditor != null) {
				localEditor.updateRangeIndication(updatedEvent.getSelection());
				fireSelectionChanged(updatedEvent, postListeners);
			}
		}

		void handleSelectionChanged(SelectionChangedEvent event) {
			SelectionChangedEvent updatedEvent = event;
			if (fLastSelection != event.getSelection() || fLastSelectionProvider != event.getSelectionProvider()) {
				fLastSelection = event.getSelection();
				fLastSelectionProvider = event.getSelectionProvider();
				fLastUpdatedSelectionChangedEvent = updatedEvent = updateEvent(event);
			}
			fireSelectionChanged(updatedEvent, listeners);
		}

		IDocument getDocument() {
			IDocument document = null;
			if (weakDocument != null) {
				document = (IDocument) weakDocument.get();
			}
			return document;
		}


		boolean isFiringSelection() {
			return isFiringSelection;
		}

		public boolean isValid(ISelection selection) {
			// ISSUE: is not clear default behavior should be true?
			// But not clear is this default would apply for our editor.
			boolean result = true;
			// if editor is "gone", can not be valid
			StructuredTextEditor e = getStructuredTextEditor();
			if (e == null || e.fEditorDisposed) {
				result = false;
			}
			// else defer to parent
			else if (getParentProvider() instanceof ISelectionValidator) {
				result = ((ISelectionValidator) getParentProvider()).isValid(selection);
			}
			return result;
		}

		public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
			postListeners.remove(listener);
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			if (isFiringSelection()) {
				return;
			}

			fLastSelection = null;
			fLastSelectionProvider = null;
			fLastUpdatedSelectionChangedEvent = null;

			ISelection textSelection = updateSelection(selection);
			getParentProvider().setSelection(textSelection);
			StructuredTextEditor localEditor = getStructuredTextEditor();
			if (localEditor != null) {
				localEditor.updateRangeIndication(textSelection);
			}
		}

		/**
		 * Create a corresponding event that contains a
		 * StructuredTextselection
		 * 
		 * @param event
		 * @return
		 */
		private SelectionChangedEvent updateEvent(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			if (selection instanceof ITextSelection && !(selection instanceof IStructuredSelection)) {
				IStructuredModel structuredModel = null;
				StructuredTextEditor localEditor = getStructuredTextEditor();
				if (localEditor != null) {
					structuredModel = localEditor.getInternalModel();
				}
				selection = new StructuredTextSelection((ITextSelection) event.getSelection(), getDocument(), structuredModel);
			}
			SelectionChangedEvent newEvent = new SelectionChangedEvent(event.getSelectionProvider(), selection);
			return newEvent;
		}

		/**
		 * Create a corresponding StructuredTextselection
		 * 
		 * @param selection
		 * @return
		 */
		private ISelection updateSelection(ISelection selection) {
			ISelection updated = selection;
			if (selection instanceof IStructuredSelection && !(selection instanceof ITextSelection) && !selection.isEmpty()) {
				Object[] selectedObjects = ((IStructuredSelection) selection).toArray();
				if (selectedObjects.length > 0) {
					int start = -1;
					int length = 0;

					// no ordering is guaranteed for multiple selection
					Object o = selectedObjects[0];
					if (o instanceof IndexedRegion) {
						start = ((IndexedRegion) o).getStartOffset();
						int end = ((IndexedRegion) o).getEndOffset();
						if (selectedObjects.length > 1) {
							for (int i = 1; i < selectedObjects.length; i++) {
								start = Math.min(start, ((IndexedRegion) selectedObjects[i]).getStartOffset());
								end = Math.max(end, ((IndexedRegion) selectedObjects[i]).getEndOffset());
							}
							length = end - start;
						}
					}
					else if (o instanceof ITextRegion) {
						start = ((ITextRegion) o).getStart();
						int end = ((ITextRegion) o).getEnd();
						if (selectedObjects.length > 1) {
							for (int i = 1; i < selectedObjects.length; i++) {
								start = Math.min(start, ((ITextRegion) selectedObjects[i]).getStart());
								end = Math.max(end, ((ITextRegion) selectedObjects[i]).getEnd());
							}
							length = end - start;
						}
					}

					if (start > -1) {
						updated = new StructuredTextSelection(getDocument(), start, length, selectedObjects);
					}
				}
			}
			return updated;
		}

		public void setDocument(IDocument document) {
			weakDocument = new SoftReference(document);
		}
	}

	class TimeOutExpired extends TimerTask {
		public void run() {
			getDisplay().syncExec(new Runnable() {
				public void run() {
					if (getDisplay() != null && !getDisplay().isDisposed())
						endBusyStateInternal();
				}
			});
		}

	}

	private class ConfigurationAndTarget {
		private String fTargetId;
		private StructuredTextViewerConfiguration fConfiguration;

		public ConfigurationAndTarget(String targetId, StructuredTextViewerConfiguration config) {
			fTargetId = targetId;
			fConfiguration = config;
		}

		public String getTargetId() {
			return fTargetId;
		}

		public StructuredTextViewerConfiguration getConfiguration() {
			return fConfiguration;
		}
	}

	/**
	 * This action behaves in two different ways: If there is no current text
	 * hover, the javadoc is displayed using information presenter. If there
	 * is a current text hover, it is converted into a information presenter
	 * in order to make it sticky.
	 * 
	 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#InformationDispatchAction
	 */
	class InformationDispatchAction extends TextEditorAction {

		/** The wrapped text operation action. */
		private final TextOperationAction fTextOperationAction;

		/**
		 * Creates a dispatch action.
		 * 
		 * @param resourceBundle
		 *            the resource bundle
		 * @param prefix
		 *            the prefix
		 * @param textOperationAction
		 *            the text operation action
		 */
		public InformationDispatchAction(ResourceBundle resourceBundle, String prefix, final TextOperationAction textOperationAction) {
			super(resourceBundle, prefix, StructuredTextEditor.this);
			if (textOperationAction == null)
				throw new IllegalArgumentException();
			fTextOperationAction = textOperationAction;
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				fTextOperationAction.run();
				return;
			}

			if (sourceViewer instanceof ITextViewerExtension4) {
				ITextViewerExtension4 extension4 = (ITextViewerExtension4) sourceViewer;
				if (extension4.moveFocusToWidgetToken())
					return;
			}

			if (!(sourceViewer instanceof ITextViewerExtension2)) {
				fTextOperationAction.run();
				return;
			}

			ITextViewerExtension2 textViewerExtension2 = (ITextViewerExtension2) sourceViewer;

			// does a text hover exist?
			ITextHover textHover = textViewerExtension2.getCurrentTextHover();
			if (textHover == null) {
				fTextOperationAction.run();
				return;
			}

			Point hoverEventLocation = textViewerExtension2.getHoverEventLocation();
			int offset = computeOffsetAtLocation(sourceViewer, hoverEventLocation.x, hoverEventLocation.y);
			if (offset == -1) {
				fTextOperationAction.run();
				return;
			}

			try {
				// get the text hover content
				String contentType = TextUtilities.getContentType(sourceViewer.getDocument(), IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, offset, true);

				IRegion hoverRegion = textHover.getHoverRegion(sourceViewer, offset);
				if (hoverRegion == null)
					return;

				String hoverInfo = textHover.getHoverInfo(sourceViewer, hoverRegion);

				IInformationControlCreator controlCreator = null;
				if (textHover instanceof IInformationProviderExtension2)
					controlCreator = ((IInformationProviderExtension2) textHover).getInformationPresenterControlCreator();

				IInformationProvider informationProvider = new InformationProvider(hoverRegion, hoverInfo, controlCreator);

				fInformationPresenter.setOffset(offset);
				fInformationPresenter.setDocumentPartitioning(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
				fInformationPresenter.setInformationProvider(informationProvider, contentType);
				fInformationPresenter.showInformation();

			}
			catch (BadLocationException e) {
				// No good information to display
			}
		}

		// modified version from TextViewer
		private int computeOffsetAtLocation(ITextViewer textViewer, int x, int y) {

			StyledText styledText = textViewer.getTextWidget();
			IDocument document = textViewer.getDocument();

			if (document == null)
				return -1;

			try {
				int widgetLocation = styledText.getOffsetAtLocation(new Point(x, y));
				if (textViewer instanceof ITextViewerExtension5) {
					ITextViewerExtension5 extension = (ITextViewerExtension5) textViewer;
					return extension.widgetOffset2ModelOffset(widgetLocation);
				}
				else {
					IRegion visibleRegion = textViewer.getVisibleRegion();
					return widgetLocation + visibleRegion.getOffset();
				}
			}
			catch (IllegalArgumentException e) {
				return -1;
			}

		}
	}

	/**
	 * Not API. May be removed in the future.
	 */
	protected final static char[] BRACKETS = {'{', '}', '(', ')', '[', ']'};

	private static final long BUSY_STATE_DELAY = 1000;
	/**
	 * Not API. May be removed in the future.
	 */
	protected static final String DOT = "."; //$NON-NLS-1$
	private static final String EDITOR_CONTEXT_MENU_ID = "org.eclipse.wst.sse.ui.StructuredTextEditor.EditorContext"; //$NON-NLS-1$
	private static final String EDITOR_CONTEXT_MENU_SUFFIX = ".source.EditorContext"; //$NON-NLS-1$
	/** Non-NLS strings */
	private static final String EDITOR_KEYBINDING_SCOPE_ID = "org.eclipse.wst.sse.ui.structuredTextEditorScope"; //$NON-NLS-1$
	/**
	 * Not API. May be removed in the future.
	 */
	public static final String GROUP_NAME_ADDITIONS = "additions"; //$NON-NLS-1$

	private static final String REDO_ACTION_DESC = SSEUIMessages.Redo___0___UI_; //$NON-NLS-1$ = "Redo: {0}."
	private static final String REDO_ACTION_DESC_DEFAULT = SSEUIMessages.Redo_Text_Change__UI_; //$NON-NLS-1$ = "Redo Text Change."
	private static final String REDO_ACTION_TEXT = SSEUIMessages._Redo__0___Ctrl_Y_UI_; //$NON-NLS-1$ = "&Redo {0} @Ctrl+Y"
	private static final String REDO_ACTION_TEXT_DEFAULT = SSEUIMessages._Redo_Text_Change__Ctrl_Y_UI_; //$NON-NLS-1$ = "&Redo Text Change @Ctrl+Y"
	private static final String RULER_CONTEXT_MENU_ID = "org.eclipse.wst.sse.ui.StructuredTextEditor.RulerContext"; //$NON-NLS-1$
	private static final String RULER_CONTEXT_MENU_SUFFIX = ".source.RulerContext"; //$NON-NLS-1$

	private final static String UNDERSCORE = "_"; //$NON-NLS-1$
	/** Translatable strings */
	private static final String UNDO_ACTION_DESC = SSEUIMessages.Undo___0___UI_; //$NON-NLS-1$ = "Undo: {0}."

	private static final String UNDO_ACTION_DESC_DEFAULT = SSEUIMessages.Undo_Text_Change__UI_; //$NON-NLS-1$ = "Undo Text Change."
	private static final String UNDO_ACTION_TEXT = SSEUIMessages._Undo__0___Ctrl_Z_UI_; //$NON-NLS-1$ = "&Undo {0} @Ctrl+Z"
	private static final String UNDO_ACTION_TEXT_DEFAULT = SSEUIMessages._Undo_Text_Change__Ctrl_Z_UI_; //$NON-NLS-1$ = "&Undo Text Change @Ctrl+Z"
	// development time/debug variables only
	private int adapterRequests;

	private long adapterTime;
	private boolean fBackgroundJobEnded;
	private boolean fBusyState;
	private Timer fBusyTimer;
	Runnable fCurrentRunnable = null;
	boolean fDirtyBeforeDocumentEvent = false;
	private ExtendedEditorDropTargetAdapter fDropAdapter;
	private DropTarget fDropTarget;
	boolean fEditorDisposed = false;
	private IEditorPart fEditorPart;
	private IDocumentListener fInternalDocumentListener;
	private InternalModelStateListener fInternalModelStateListener;
	private MouseTracker fMouseTracker;
	private IContentOutlinePage fOutlinePage;

	private OutlinePageListener fOutlinePageListener = null;
	/** This editor's projection model updater */
	private IStructuredTextFoldingProvider fProjectionModelUpdater;
	/** This editor's projection support */
	private ProjectionSupport fProjectionSupport;
	private IPropertySheetPage fPropertySheetPage;
	private String fRememberTitle;
	/** The ruler context menu to be disposed. */
	private Menu fRulerContextMenu;
	/** The ruler context menu manager to be disposed. */
	private MenuManager fRulerContextMenuManager;

	String[] fShowInTargetIds = new String[]{IPageLayout.ID_RES_NAV};

	private IAction fShowPropertiesAction = null;
	private IStructuredModel fStructuredModel;
	StructuredSelectionProvider fStructuredSelectionProvider = null;
	/** The text context menu to be disposed. */
	private Menu fTextContextMenu;
	/** The text context menu manager to be disposed. */
	private MenuManager fTextContextMenuManager;
	private String fViewerConfigurationTargetId;
	/** The selection history of the editor */
	private SelectionHistory fSelectionHistory;
	/** The information presenter. */
	private InformationPresenter fInformationPresenter;
	private boolean fUpdateMenuTextPending;
	int hoverX = -1;
	int hoverY = -1;

	private boolean shouldClose = false;
	private long startPerfTime;
	private boolean fisReleased;
	/**
	 * The action group for folding.
	 */
	private FoldingActionGroup fFoldingGroup;

	private ILabelProvider fStatusLineLabelProvider;

	/**
	 * Creates a new Structured Text Editor.
	 */
	public StructuredTextEditor() {
		super();
		initializeDocumentProvider(null);
	}

	private void aboutToSaveModel() {
		if (getInternalModel() != null) {
			getInternalModel().aboutToChangeModel();
		}
	}

	private void abstractTextEditorContextMenuAboutToShow(IMenuManager menu) {
		menu.add(new Separator(ITextEditorActionConstants.GROUP_UNDO));
		menu.add(new GroupMarker(ITextEditorActionConstants.GROUP_SAVE));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_COPY));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_PRINT));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_FIND));
		menu.add(new Separator(IWorkbenchActionConstants.GROUP_ADD));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		if (isEditable()) {
			addAction(menu, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.UNDO);
			addAction(menu, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REVERT_TO_SAVED);
			addAction(menu, ITextEditorActionConstants.GROUP_SAVE, ITextEditorActionConstants.SAVE);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.CUT);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.COPY);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.PASTE);
		}
		else {
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.COPY);
		}

		// from AbstractDecoratedTextEditor
		IAction preferencesAction = getAction(ITextEditorActionConstants.CONTEXT_PREFERENCES);
		menu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new Separator(ITextEditorActionConstants.GROUP_SETTINGS));
		menu.appendToGroup(ITextEditorActionConstants.GROUP_SETTINGS, preferencesAction);
	}

	protected void addContextMenuActions(IMenuManager menu) {
		// Only offer actions that affect the text if the viewer allows
		// modification and supports any of these operations
		IAction formatAll = getAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT);
		IAction formatSelection = getAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS);
		IAction cleanupAll = getAction(StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT);
		boolean enableFormatMenu = (formatAll != null && formatAll.isEnabled()) || (formatSelection != null && formatSelection.isEnabled()) || (cleanupAll != null && cleanupAll.isEnabled());

		if (getSourceViewer().isEditable() && enableFormatMenu) {
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT);
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS);
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT);
		}

		// Some Design editors (DTD) rely on this view for their own uses
		menu.appendToGroup(IWorkbenchActionConstants.GROUP_ADD, fShowPropertiesAction);
	}

	private void addExtendedContextMenuActions(IMenuManager menu) {
		IEditorActionBarContributor c = getEditorSite().getActionBarContributor();
		if (c instanceof IPopupMenuContributor) {
			((IPopupMenuContributor) c).contributeToPopupMenu(menu);
		}
		else {
			ExtendedEditorActionBuilder builder = new ExtendedEditorActionBuilder();
			IExtendedContributor pmc = builder.readActionExtensions(getConfigurationPoints());
			if (pmc != null) {
				pmc.setActiveEditor(this);
				pmc.contributeToPopupMenu(menu);
			}
		}
	}

	protected void addExtendedRulerContextMenuActions(IMenuManager menu) {
		// none at this level
	}



	/**
	 * Starts background mode.
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 */
	void beginBackgroundOperation() {
		fBackgroundJobEnded = false;
		// if already in busy state, no need to do anything
		// and, we only start, or reset, the timed busy
		// state when we get the "endBackgroundOperation" call.
		if (!inBusyState()) {
			beginBusyStateInternal();
		}
	}

	private void beginBusyStateInternal() {

		fBusyState = true;
		startBusyTimer();

		ISourceViewer viewer = getSourceViewer();
		if (viewer instanceof StructuredTextViewer) {
			((StructuredTextViewer) viewer).beginBackgroundUpdate();

		}
		showBusy(true);
	}

	// private void addFindOccurrencesAction(String matchType, String
	// matchText, IMenuManager menu) {
	//
	// AbstractFindOccurrencesAction action = new
	// AbstractFindOccurrencesAction(getFileInEditor(), new
	// SearchUIConfiguration(), (IStructuredDocument) getDocument(),
	// matchType, matchText, getProgressMonitor());
	// action.setText("Occurrences of \"" + matchText + "\" in File");
	// menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, action);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#close(boolean)
	 */
	public void close(final boolean save) {
		/*
		 * Instead of us closing directly, we have to close with our
		 * containing (multipage) editor, if it exists.
		 */
		if (getSite() == null) {
			// if site hasn't been set yet, then we're not
			// completely open
			// so set a flag not to open
			shouldClose = true;
		}
		else {
			if (getEditorPart() != null) {
				Display display = getSite().getShell().getDisplay();
				display.asyncExec(new Runnable() {

					public void run() {
						getSite().getPage().closeEditor(getEditorPart(), save);
					}
				});
			}
			else {
				super.close(save);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#collectContextMenuPreferencePages()
	 */
	protected String[] collectContextMenuPreferencePages() {
		List allIds = new ArrayList(0);

		// get contributed preference pages
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] configurationIds = getConfigurationPoints();
		for (int i = 0; i < configurationIds.length; i++) {
			String[] definitions = builder.getDefinitions("preferencepages", configurationIds[i]); //$NON-NLS-1$
			for (int j = 0; j < definitions.length; j++) {
				String someIds = definitions[j];
				if (someIds != null && someIds.length() > 0) {
					// supports multiple comma-delimited page IDs in one
					// element
					String[] ids = StringUtils.unpack(someIds);
					for (int k = 0; k < ids.length; k++) {
						// trim, just to keep things clean
						String id = ids[k].trim();
						if (!allIds.contains(id)) {
							allIds.add(id);
						}
					}
				}
			}
		}

		// add pages contributed by super
		String[] superPages = super.collectContextMenuPreferencePages();
		for (int m = 0; m < superPages.length; m++) {
			// trim, just to keep things clean
			String id = superPages[m].trim();
			if (!allIds.contains(id)) {
				allIds.add(id);
			}
		}

		return (String[]) allIds.toArray(new String[0]);
	}

	/**
	 * Compute and set double-click action for the vertical ruler
	 */
	private void computeAndSetDoubleClickAction() {
		/*
		 * Make double-clicking on the ruler toggle a breakpoint instead of
		 * toggling a bookmark. For lines where a breakpoint won't be created,
		 * create a bookmark through the contributed RulerDoubleClick action.
		 */
		setAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK, new ToggleBreakpointAction(this, getVerticalRuler(), getAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#configureSourceViewerDecorationSupport(org.eclipse.ui.texteditor.SourceViewerDecorationSupport)
	 */
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(createCharacterPairMatcher());
		support.setMatchingCharacterPainterPreferenceKeys(EditorPreferenceNames.MATCHING_BRACKETS, EditorPreferenceNames.MATCHING_BRACKETS_COLOR);

		super.configureSourceViewerDecorationSupport(support);
	}

	protected void createActions() {
		super.createActions();
		ResourceBundle resourceBundle = SSEUIMessages.getResourceBundle();
		IWorkbenchHelpSystem helpSystem = SSEUIPlugin.getDefault().getWorkbench().getHelpSystem();
		// TextView Action - moving the selected text to
		// the clipboard
		// override the cut/paste/delete action to make
		// them run on read-only
		// files
		Action action = new TextOperationAction(resourceBundle, "Editor_Cut_", this, ITextOperationTarget.CUT, true); //$NON-NLS-1$
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);
		setAction(ITextEditorActionConstants.CUT, action);
		helpSystem.setHelp(action, IAbstractTextEditorHelpContextIds.CUT_ACTION);
		// TextView Action - inserting the clipboard
		// content at the current
		// position
		// override the cut/paste/delete action to make
		// them run on read-only
		// files
		action = new TextOperationAction(resourceBundle, "Editor_Paste_", this, ITextOperationTarget.PASTE, true); //$NON-NLS-1$
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
		setAction(ITextEditorActionConstants.PASTE, action);
		helpSystem.setHelp(action, IAbstractTextEditorHelpContextIds.PASTE_ACTION);
		// TextView Action - deleting the selected text or
		// if selection is
		// empty the character at the right of the current
		// position
		// override the cut/paste/delete action to make
		// them run on read-only
		// files
		action = new TextOperationAction(resourceBundle, "Editor_Delete_", this, ITextOperationTarget.DELETE, true); //$NON-NLS-1$
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		setAction(ITextEditorActionConstants.DELETE, action);
		helpSystem.setHelp(action, IAbstractTextEditorHelpContextIds.DELETE_ACTION);
		// SourceView Action - requesting content assist to
		// show completetion
		// proposals for the current insert position
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS + UNDERSCORE, this, ISourceViewer.CONTENTASSIST_PROPOSALS, true);
		helpSystem.setHelp(action, IHelpContextIds.CONTMNU_CONTENTASSIST_HELPID);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS, true);
		// SourceView Action - requesting content assist to
		// show the content
		// information for the current insert position
		action = new TextOperationAction(SSEUIMessages.getResourceBundle(), StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_CONTEXT_INFORMATION + UNDERSCORE, this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_CONTEXT_INFORMATION, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_CONTEXT_INFORMATION, true);
		// StructuredTextViewer Action - requesting format
		// of the whole
		// document
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT + UNDERSCORE, this, StructuredTextViewer.FORMAT_DOCUMENT);
		helpSystem.setHelp(action, IHelpContextIds.CONTMNU_FORMAT_DOC_HELPID);
		action.setActionDefinitionId(ActionDefinitionIds.FORMAT_DOCUMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT, true);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT, true);
		// StructuredTextViewer Action - requesting format
		// of the active
		// elements
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS + UNDERSCORE, this, StructuredTextViewer.FORMAT_ACTIVE_ELEMENTS);
		helpSystem.setHelp(action, IHelpContextIds.CONTMNU_FORMAT_ELEMENTS_HELPID);
		action.setActionDefinitionId(ActionDefinitionIds.FORMAT_ACTIVE_ELEMENTS);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS, true);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS, true);

		// StructuredTextEditor Action - add breakpoints (falling back to the
		// current double-click if they can't be added)
		action = new ToggleBreakpointAction(this, getVerticalRuler());
		setAction(ActionDefinitionIds.TOGGLE_BREAKPOINTS, action);
		// StructuredTextEditor Action - manage breakpoints
		action = new ManageBreakpointAction(this, getVerticalRuler());
		setAction(ActionDefinitionIds.MANAGE_BREAKPOINTS, action);
		// StructuredTextEditor Action - edit breakpoints
		action = new EditBreakpointAction(this, getVerticalRuler());
		setAction(ActionDefinitionIds.EDIT_BREAKPOINTS, action);
		// StructuredTextViewer Action - open file on selection
		action = new OpenHyperlinkAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE + UNDERSCORE, this, getSourceViewer());
		action.setActionDefinitionId(ActionDefinitionIds.OPEN_FILE);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE, action);

		computeAndSetDoubleClickAction();

		fShowPropertiesAction = new ShowPropertiesAction();
		fFoldingGroup = new FoldingActionGroup(this, getSourceViewer());
	}

	protected LineChangeHover createChangeHover() {
		return new StructuredLineChangeHover();
	}

	protected ICharacterPairMatcher createCharacterPairMatcher() {
		ICharacterPairMatcher matcher = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; matcher == null && i < ids.length; i++) {
			matcher = (ICharacterPairMatcher) builder.getConfiguration(DocumentRegionEdgeMatcher.ID, ids[i]);
		}
		if (matcher == null) {
			matcher = new DefaultCharacterPairMatcher(new char[]{'(', ')', '{', '}', '[', ']', '<', '>'});
		}
		return matcher;
	}

	/**
	 * Create a preference store that combines the source editor preferences
	 * with the base editor's preferences.
	 * 
	 * @return IPreferenceStore
	 */
	private IPreferenceStore createCombinedPreferenceStore() {
		IPreferenceStore sseEditorPrefs = SSEUIPlugin.getDefault().getPreferenceStore();
		IPreferenceStore baseEditorPrefs = EditorsUI.getPreferenceStore();
		return new ChainedPreferenceStore(new IPreferenceStore[]{sseEditorPrefs, baseEditorPrefs});
	}

	private ContentOutlineConfiguration createContentOutlineConfiguration() {
		ContentOutlineConfiguration cfg = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; cfg == null && i < ids.length; i++) {
			cfg = (ContentOutlineConfiguration) builder.getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ids[i]);
		}
		return cfg;
	}

	protected void createModelDependentFields() {
		// none at this level
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Use StructuredTextViewerConfiguration if a viewerconfiguration has not
	 * already been set. Also initialize StructuredTextViewer.
	 * </p>
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		if (getSourceViewerConfiguration() == null) {
			ConfigurationAndTarget cat = createSourceViewerConfiguration();
			fViewerConfigurationTargetId = cat.getTargetId();
			StructuredTextViewerConfiguration newViewerConfiguration = cat.getConfiguration();
			setSourceViewerConfiguration(newViewerConfiguration);
		}

		super.createPartControl(parent);

		// instead of calling setInput twice, use initializeSourceViewer() to
		// handle source viewer initialization previously handled by setInput
		initializeSourceViewer();

		// update editor context menu, vertical ruler context menu, infopop
		if (getInternalModel() != null) {
			updateEditorControlsForContentType(getInternalModel().getContentTypeIdentifier());
		}
		else {
			updateEditorControlsForContentType(null);
		}

		// used for Show Tooltip Description
		IInformationControlCreator informationControlCreator = new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell shell) {
				boolean cutDown = false;
				int style = cutDown ? SWT.NONE : (SWT.V_SCROLL | SWT.H_SCROLL);
				return new DefaultInformationControl(shell, SWT.RESIZE | SWT.TOOL, style, new HTMLTextPresenter(cutDown));
			}
		};

		fInformationPresenter = new InformationPresenter(informationControlCreator);
		fInformationPresenter.setSizeConstraints(60, 10, true, true);
		fInformationPresenter.install(getSourceViewer());
	}

	protected PropertySheetConfiguration createPropertySheetConfiguration() {
		PropertySheetConfiguration cfg = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; cfg == null && i < ids.length; i++) {
			cfg = (PropertySheetConfiguration) builder.getConfiguration(ExtendedConfigurationBuilder.PROPERTYSHEETCONFIGURATION, ids[i]);
		}
		return cfg;
	}

	/**
	 * Loads the Show In Target IDs from the Extended Configuration extension
	 * point.
	 * 
	 * @return
	 */
	private String[] createShowInTargetIds() {
		List allIds = new ArrayList(0);
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] configurationIds = getConfigurationPoints();
		for (int i = 0; i < configurationIds.length; i++) {
			String[] definitions = builder.getDefinitions("showintarget", configurationIds[i]); //$NON-NLS-1$
			for (int j = 0; j < definitions.length; j++) {
				String someIds = definitions[j];
				if (someIds != null && someIds.length() > 0) {
					String[] ids = StringUtils.unpack(someIds);
					for (int k = 0; k < ids.length; k++) {
						// trim, just to keep things clean
						String id = ids[k].trim();
						if (!allIds.contains(id)) {
							allIds.add(id);
						}
					}
				}
			}
		}

		if (!allIds.contains(IPageLayout.ID_RES_NAV)) {
			allIds.add(IPageLayout.ID_RES_NAV);
		}
		if (!allIds.contains(IPageLayout.ID_OUTLINE)) {
			allIds.add(IPageLayout.ID_OUTLINE);
		}
		return (String[]) allIds.toArray(new String[0]);
	}

	/**
	 * @return
	 */
	private ISourceEditingTextTools createSourceEditingTextTools() {
		ISourceEditingTextTools tools = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; tools == null && i < ids.length; i++) {
			tools = (ISourceEditingTextTools) builder.getConfiguration(NullSourceEditingTextTools.ID, ids[i]);
		}
		if (tools == null) {
			tools = NullSourceEditingTextTools.getInstance();
			((NullSourceEditingTextTools) tools).setTextEditor(this);
		}
		Method method = null; //$NON-NLS-1$
		try {
			method = tools.getClass().getMethod("setTextEditor", new Class[]{StructuredTextEditor.class}); //$NON-NLS-1$
		}
		catch (NoSuchMethodException e) {
		}
		if (method == null) {
			try {
				method = tools.getClass().getMethod("setTextEditor", new Class[]{ITextEditor.class}); //$NON-NLS-1$
			}
			catch (NoSuchMethodException e) {
			}
		}
		if (method == null) {
			try {
				method = tools.getClass().getMethod("setTextEditor", new Class[]{IEditorPart.class}); //$NON-NLS-1$
			}
			catch (NoSuchMethodException e) {
			}
		}
		if (method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(tools, new Object[]{this});
			}
			catch (Exception e) {
				Logger.logException("Problem creating ISourceEditingTextTools implementation", e); //$NON-NLS-1$
			}
		}

		return tools;
	}

	/**
	 * Creates the source viewer to be used by this editor
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {
		fAnnotationAccess = createAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		StructuredTextViewer sourceViewer = createStructedTextViewer(parent, verticalRuler, styles);
		initSourceViewer(sourceViewer);
		return sourceViewer;
	}

	private ConfigurationAndTarget createSourceViewerConfiguration() {
		ConfigurationAndTarget cat = null;
		StructuredTextViewerConfiguration cfg = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; cfg == null && i < ids.length; i++) {
			cfg = (StructuredTextViewerConfiguration) builder.getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ids[i]);
			cat = new ConfigurationAndTarget(ids[i], cfg);
		}
		if (cfg == null) {
			cfg = new StructuredTextViewerConfiguration();
			String targetid = getClass().getName() + "#default"; //$NON-NLS-1$
			cat = new ConfigurationAndTarget(targetid, cfg);
		}
		return cat;
	}

	protected StructuredTextViewer createStructedTextViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {
		return new StructuredTextViewer(parent, verticalRuler, getOverviewRuler(), isOverviewRulerVisible(), styles);
	}

	protected void createUndoRedoActions() {
		// overridden to add icons to actions
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=111877
		super.createUndoRedoActions();
		IAction action = getAction(ITextEditorActionConstants.UNDO);
		if (action != null) {
			action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		}

		action = getAction(ITextEditorActionConstants.REDO);
		if (action != null) {
			action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		Logger.trace("Source Editor", "StructuredTextEditor::dispose entry"); //$NON-NLS-1$ //$NON-NLS-2$
		if (org.eclipse.wst.sse.core.internal.util.Debug.perfTestAdapterClassLoading) {
			System.out.println("Total calls to getAdapter: " + adapterRequests); //$NON-NLS-1$
			System.out.println("Total time in getAdapter: " + adapterTime); //$NON-NLS-1$
			System.out.println("Average time per call: " + (adapterTime / adapterRequests)); //$NON-NLS-1$
		}

		// dispose of information presenter
		if (fInformationPresenter != null) {
			fInformationPresenter.dispose();
			fInformationPresenter = null;
		}

		// dispose of selection history
		if (fSelectionHistory != null) {
			fSelectionHistory.dispose();
			fSelectionHistory = null;
		}

		// dispose of document folding support
		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.uninstall();
			fProjectionModelUpdater = null;
		}

		if (fProjectionSupport != null) {
			fProjectionSupport.dispose();
			fProjectionSupport = null;
		}

		if (fFoldingGroup != null) {
			fFoldingGroup.dispose();
			fFoldingGroup = null;
		}

		// subclass may not have mouse tracker created
		// need to check for null before stopping
		if (fMouseTracker != null) {
			fMouseTracker.stop();
			fMouseTracker = null;
		}

		// dispose of menus that were being tracked
		if (fTextContextMenu != null) {
			fTextContextMenu.dispose();
		}
		if (fRulerContextMenu != null) {
			fRulerContextMenu.dispose();
		}
		if (fTextContextMenuManager != null) {
			fTextContextMenuManager.removeMenuListener(getContextMenuListener());
			fTextContextMenuManager.removeAll();
			fTextContextMenuManager.dispose();
		}
		if (fRulerContextMenuManager != null) {
			fRulerContextMenuManager.removeMenuListener(getContextMenuListener());
			fRulerContextMenuManager.removeAll();
			fRulerContextMenuManager.dispose();
		}

		// added this 2/20/2004 based on probe results --
		// seems should be handled by setModel(null), but
		// that's a more radical change.
		// and, technically speaking, should not be needed,
		// but makes a memory leak
		// less severe.
		if (fStructuredModel != null) {
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().removeDocumentListener(getInternalDocumentListener());
			}
			fStructuredModel.removeModelStateListener(getInternalModelStateListener());
		}

		// BUG155335 - if there was no document provider, there was nothing
		// added
		// to document, so nothing to remove
		if (getDocumentProvider() != null) {
			IDocument doc = getDocumentProvider().getDocument(getEditorInput());
			if (doc != null) {
				doc.removeDocumentListener(getInternalDocumentListener());
				if (doc instanceof IExecutionDelegatable) {
					((IExecutionDelegatable) doc).setExecutionDelegate(null);
				}
			}
		}

		// some things in the configuration need to clean
		// up after themselves
		if (fOutlinePage != null) {
			if (fOutlinePage instanceof ConfigurableContentOutlinePage && fOutlinePageListener != null) {
				((ConfigurableContentOutlinePage) fOutlinePage).removeDoubleClickListener(fOutlinePageListener);
			}
			if (fOutlinePageListener != null) {
				fOutlinePage.removeSelectionChangedListener(fOutlinePageListener);
			}
		}

		fEditorDisposed = true;
		disposeModelDependentFields();

		if (fDropTarget != null)
			fDropTarget.dispose();

		setPreferenceStore(null);

		// strictly speaking, but following null outs
		// should not be needed,
		// but in the event of a memory leak, they make the
		// memory leak less
		// severe
		fDropAdapter = null;
		fDropTarget = null;

		super.dispose();

		Logger.trace("Source Editor", "StructuredTextEditor::dispose exit"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#disposeDocumentProvider()
	 */
	protected void disposeDocumentProvider() {
		if (fStructuredModel != null && !fisReleased && !(getDocumentProvider() instanceof IModelProvider)) {
			fStructuredModel.releaseFromEdit();
			fisReleased = true;
		}
		super.disposeDocumentProvider();
	}

	/**
	 * Disposes model specific editor helpers such as statusLineHelper.
	 * Basically any code repeated in update() & dispose() should be placed
	 * here.
	 */
	private void disposeModelDependentFields() {
		// none at this level
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#doRevertToSaved()
	 */
	public void doRevertToSaved() {
		super.doRevertToSaved();
		if (fOutlinePage != null && fOutlinePage instanceof IUpdate) {
			((IUpdate) fOutlinePage).update();
		}
		// reset undo
		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		if (doc instanceof IStructuredDocument) {
			((IStructuredDocument) doc).getUndoManager().getCommandStack().flush();
		}

		// update menu text
		updateMenuText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor progressMonitor) {
		try {
			aboutToSaveModel();
			updateEncodingMemento();
			super.doSave(progressMonitor);
		}
		finally {
			savedModel();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSetInput(org.eclipse.ui.IEditorInput)
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		// TODO: if opened in more than one editor, this will cause
		// problems.
		IEditorInput oldInput = getEditorInput();
		if (oldInput != null) {
			IDocument olddoc = getDocumentProvider().getDocument(oldInput);
			if (olddoc != null && olddoc instanceof IExecutionDelegatable) {
				((IExecutionDelegatable) olddoc).setExecutionDelegate(null);
			}
		}

		if (fStructuredModel != null && !(getDocumentProvider() instanceof IModelProvider)) {
			fStructuredModel.releaseFromEdit();
		}

		super.doSetInput(input);

		IDocument newDocument = getDocumentProvider().getDocument(input);
		if (newDocument instanceof IExecutionDelegatable) {
			((IExecutionDelegatable) newDocument).setExecutionDelegate(new EditorExecutionContext(this));
		}

		IStructuredModel model = null;
		// if we have a Model provider, get the model from it
		if (getDocumentProvider() instanceof IModelProvider) {
			model = ((IModelProvider) getDocumentProvider()).getModel(getEditorInput());
			if (!model.isShared()) {
				EditorModelUtil.addFactoriesTo(model);
			}
		}
		else {
			if (newDocument instanceof IStructuredDocument) {
				// corresponding releaseFromEdit occurs in
				// dispose()
				model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) newDocument);
				EditorModelUtil.addFactoriesTo(model);
			}

			else {
				logUnexpectedDocumentKind(input);
			}
		}

		if (fStructuredModel != null || model != null) {
			setModel(model);
		}

		if (getInternalModel() != null) {
			updateEditorControlsForContentType(getInternalModel().getContentTypeIdentifier());
		}
		else {
			updateEditorControlsForContentType(null);
		}

		if (fProjectionModelUpdater != null)
			updateProjectionSupport();

		// start editor with smart insert mode
		setInsertMode(SMART_INSERT);
	}

	/**
	 * Sets up this editor's context menu before it is made visible.
	 * <p>
	 * Not API. May be reduced to protected method in the future.
	 * </p>
	 * 
	 * @param menu
	 *            the menu
	 */
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		// To be consistant with the Java Editor, we want
		// to remove
		// ShiftRight and ShiftLeft from the context menu.
		//
		// ShiftRight and ShiftLeft were added in the super
		// implemenation of
		// this method. We want to skip it and call
		// AbstractTextEditor's
		// implementation directly. The easiest way is to
		// copy the method here.

		// super.editorContextMenuAboutToShow(menu);
		abstractTextEditorContextMenuAboutToShow(menu);

		addContextMenuActions(menu);
		addExtendedContextMenuActions(menu);
	}

	/**
	 * End background mode.
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 */
	void endBackgroundOperation() {
		fBackgroundJobEnded = true;
		// note, we don't immediately end our 'internal busy' state,
		// since we may get many calls in a short period of
		// time. We always wait for the time out.
		resetBusyState();
	}

	/**
	 * Note this method can be called indirectly from background job operation
	 * ... but expected to be gaurded there with ILock, plus, can be called
	 * directly from timer thread, so the timer's run method guards with ILock
	 * too.
	 */
	private void endBusyStateInternal() {
		if (fBackgroundJobEnded) {
			fBusyTimer.cancel();
			showBusy(false);

			ISourceViewer viewer = getSourceViewer();
			if (viewer instanceof StructuredTextViewer) {
				((StructuredTextViewer) viewer).endBackgroundUpdate();
			}
			fBusyState = false;
		}
		else {
			// we will only be in this branch for a back ground job that is
			// taking
			// longer than our normal time-out period (meaning we got notified
			// of
			// the timeout "inbetween" calls to 'begin' and
			// 'endBackgroundOperation'.
			// (which, remember, can only happen since there are many calls to
			// begin/end in a short period of time, and we only "reset" on the
			// 'ends').
			// In this event, there's really nothing to do, we're still in
			// "busy state"
			// and should start a new reset cycle once endBackgroundjob is
			// called.
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {
		if (org.eclipse.wst.sse.core.internal.util.Debug.perfTestAdapterClassLoading) {
			startPerfTime = System.currentTimeMillis();
		}
		Object result = null;
		// text editor
		IStructuredModel internalModel = getInternalModel();
		if (ITextEditor.class.equals(required)) {
			result = this;
		}
		else if (IWorkbenchSiteProgressService.class.equals(required)) {
			return getEditorPart().getSite().getAdapter(IWorkbenchSiteProgressService.class);
		}
		// content outline page
		else if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null || fOutlinePage.getControl() == null || fOutlinePage.getControl().isDisposed()) {
				ContentOutlineConfiguration cfg = createContentOutlineConfiguration();
				if (cfg != null) {
					ConfigurableContentOutlinePage outlinePage = new ConfigurableContentOutlinePage();
					outlinePage.setConfiguration(cfg);
					if (internalModel != null) {
						outlinePage.setInputContentTypeIdentifier(internalModel.getContentTypeIdentifier());
						outlinePage.setInput(internalModel);
					}

					if (fOutlinePageListener == null) {
						fOutlinePageListener = new OutlinePageListener();
					}

					outlinePage.addSelectionChangedListener(fOutlinePageListener);
					outlinePage.addDoubleClickListener(fOutlinePageListener);

					fOutlinePage = outlinePage;
				}
			}
			result = fOutlinePage;
		}
		// property sheet page, but only if the input's editable
		else if (IPropertySheetPage.class.equals(required) && isEditable()) {
			if (fPropertySheetPage == null || fPropertySheetPage.getControl() == null || fPropertySheetPage.getControl().isDisposed()) {
				PropertySheetConfiguration cfg = createPropertySheetConfiguration();
				if (cfg != null) {
					ConfigurablePropertySheetPage propertySheetPage = new ConfigurablePropertySheetPage();
					propertySheetPage.setConfiguration(cfg);
					fPropertySheetPage = propertySheetPage;
				}
			}
			result = fPropertySheetPage;
		}
		else if (IDocument.class.equals(required)) {
			result = getDocumentProvider().getDocument(getEditorInput());
		}
		else if (ISourceEditingTextTools.class.equals(required)) {
			result = createSourceEditingTextTools();
		}
		else if (IToggleBreakpointsTarget.class.equals(required)) {
			result = ToggleBreakpointsTarget.getInstance();
		}
		else if (IShowInTargetList.class.equals(required)) {
			return new ShowInTargetListAdapter();
		}
		else if (SelectionHistory.class.equals(required)) {
			if (fSelectionHistory == null)
				fSelectionHistory = new SelectionHistory(this);
			return fSelectionHistory;
		}
		else {
			if (result == null && internalModel != null) {
				result = internalModel.getAdapter(required);
			}
			// others
			if (result == null)
				result = super.getAdapter(required);
		}
		if (org.eclipse.wst.sse.core.internal.util.Debug.perfTestAdapterClassLoading) {
			long stop = System.currentTimeMillis();
			adapterRequests++;
			adapterTime += (stop - startPerfTime);
		}
		if (org.eclipse.wst.sse.core.internal.util.Debug.perfTestAdapterClassLoading) {
			System.out.println("Total calls to getAdapter: " + adapterRequests); //$NON-NLS-1$
			System.out.println("Total time in getAdapter: " + adapterTime); //$NON-NLS-1$
			System.out.println("Average time per call: " + (adapterTime / adapterRequests)); //$NON-NLS-1$
		}
		return result;
	}

	private String[] getConfigurationPoints() {
		String contentTypeIdentifierID = null;
		if (getInternalModel() != null) {
			contentTypeIdentifierID = getInternalModel().getContentTypeIdentifier();
		}
		return ConfigurationPointCalculator.getConfigurationPoints(this, contentTypeIdentifierID, ConfigurationPointCalculator.SOURCE, StructuredTextEditor.class);
	}

	/**
	 * added checks to overcome bug such that if we are shutting down in an
	 * error condition, then viewer will have already been disposed.
	 */
	protected String getCursorPosition() {
		String result = null;
		// this may be too expensive in terms of
		// performance, to do this check
		// every time, just to gaurd against error
		// condition.
		// perhaps there's a better way?
		if (getSourceViewer() != null && getSourceViewer().getTextWidget() != null && !getSourceViewer().getTextWidget().isDisposed()) {
			result = super.getCursorPosition();
		}
		else {
			result = "0:0"; //$NON-NLS-1$
		}
		return result;
	}


	Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * Returns this editor part.
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 * 
	 * @return this editor part
	 */
	public IEditorPart getEditorPart() {
		if (fEditorPart == null)
			return this;
		return fEditorPart;
	}

	private IDocumentListener getInternalDocumentListener() {
		if (fInternalDocumentListener == null) {
			fInternalDocumentListener = new InternalDocumentListener();
		}
		return fInternalDocumentListener;
	}

	IStructuredModel getInternalModel() {
		return fStructuredModel;
	}

	private InternalModelStateListener getInternalModelStateListener() {
		if (fInternalModelStateListener == null) {
			fInternalModelStateListener = new InternalModelStateListener();
		}
		return fInternalModelStateListener;
	}

	/**
	 * Returns this editor's StructuredModel.
	 * <p>
	 * Not API. Will be removed in the future.
	 * </p>
	 * 
	 * @return returns this editor's IStructuredModel
	 * @deprecated - This method allowed for uncontrolled access to the model
	 *             instance and will be removed in the future. It is
	 *             recommended that the current document provider be asked for
	 *             the current document and the IModelManager then asked for
	 *             the corresponding model with
	 *             getExistingModelFor*(IDocument).
	 */
	public IStructuredModel getModel() {
		if (getDocumentProvider() == null) {
			// this indicated an error in startup sequence
			Logger.trace(getClass().getName(), "Program Info Only: document provider was null when model requested"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		boolean initialModelNull = false;
		if (fStructuredModel == null)
			initialModelNull = true;
		if (fStructuredModel == null) {
			// lazily set the model instance, although this is an ABNORMAL
			// CODE PATH
			if (getDocumentProvider() instanceof IModelProvider) {
				fStructuredModel = ((IModelProvider) getDocumentProvider()).getModel(getEditorInput());
				fisReleased = false;
			}
			else {
				IDocument doc = getDocumentProvider().getDocument(getEditorInput());
				if (doc instanceof IStructuredDocument) {
					IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForEdit(doc);
					if (model == null) {
						model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) doc);
					}
					fStructuredModel = model;
					fisReleased = false;
				}
			}

			// ISSUE: this looks bad ... edit-time factories not initialized
			// unless someone calls getModel?
			// factories will not be re-added if already exists
			EditorModelUtil.addFactoriesTo(fStructuredModel);

			if (initialModelNull && fStructuredModel != null) {
				/*
				 * DMW: 9/1/2002 -- why is update called here? No change has
				 * been indicated? I'd like to remove, but will leave for now
				 * to avoid breaking this hack. Should measure/breakpoint to
				 * see how large the problem is. May cause performance
				 * problems.
				 * 
				 * DMW: 9/8/2002 -- not sure why this was here initially, but
				 * the intent/hack must have been to call update if this was
				 * the first time fStructuredModel was set. So, I added the
				 * logic to check for that "first time" case. It would appear
				 * we don't really need. may remove in future when can test
				 * more.
				 */
				update();
			}
		}
		return fStructuredModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IWorkbenchPartOrientation#getOrientation()
	 */
	public int getOrientation() {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88714
		return SWT.LEFT_TO_RIGHT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider() {
		if (fStructuredSelectionProvider == null) {
			ISelectionProvider parentProvider = super.getSelectionProvider();
			if (parentProvider != null) {
				fStructuredSelectionProvider = new StructuredSelectionProvider(parentProvider, this);
				fStructuredSelectionProvider.addPostSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateStatusLine(event.getSelection());
					}
				});
			}
		}
		if (fStructuredSelectionProvider == null) {
			return super.getSelectionProvider();
		}
		return fStructuredSelectionProvider;
	}

	/**
	 * Returns the editor's source viewer. This method was created to expose
	 * the protected final getSourceViewer() method.
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 * 
	 * @return the editor's source viewer
	 */
	public StructuredTextViewer getTextViewer() {
		return (StructuredTextViewer) getSourceViewer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handleCursorPositionChanged()
	 */
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		updateStatusField(StructuredTextEditorActionConstants.STATUS_CATEGORY_OFFSET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		String property = event.getProperty();

		if (EditorPreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS.equals(property)) {
			updateHoverBehavior();
		}

		if (IStructuredTextFoldingProvider.FOLDING_ENABLED.equals(property)) {
			if (getSourceViewer() instanceof ProjectionViewer) {
				// install projection support if it has not even been
				// installed yet
				if (isFoldingEnabled() && (fProjectionSupport == null) && (fProjectionModelUpdater == null)) {
					installProjectionSupport();
				}
				ProjectionViewer pv = (ProjectionViewer) getSourceViewer();
				if (pv.isProjectionMode() != isFoldingEnabled()) {
					if (pv.canDoOperation(ProjectionViewer.TOGGLE))
						pv.doOperation(ProjectionViewer.TOGGLE);
				}
			}
			return;
		}

		// update content assist preferences
		if (EditorPreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND.equals(property)) {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null) {
				SourceViewerConfiguration configuration = getSourceViewerConfiguration();
				if (configuration != null) {
					IContentAssistant contentAssistant = configuration.getContentAssistant(sourceViewer);
					if (contentAssistant instanceof ContentAssistant) {
						ContentAssistant assistant = (ContentAssistant) contentAssistant;
						RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), EditorPreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND);
						Color color = EditorUtility.getColor(rgb);
						assistant.setProposalSelectorBackground(color);
					}
				}
			}
		}

		// update content assist preferences
		if (EditorPreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND.equals(property)) {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null) {
				SourceViewerConfiguration configuration = getSourceViewerConfiguration();
				if (configuration != null) {
					IContentAssistant contentAssistant = configuration.getContentAssistant(sourceViewer);
					if (contentAssistant instanceof ContentAssistant) {
						ContentAssistant assistant = (ContentAssistant) contentAssistant;
						RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), EditorPreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND);
						Color color = EditorUtility.getColor(rgb);
						assistant.setProposalSelectorForeground(color);
					}
				}
			}
		}

		// update content assist preferences
		if (EditorPreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND.equals(property)) {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null) {
				SourceViewerConfiguration configuration = getSourceViewerConfiguration();
				if (configuration != null) {
					IContentAssistant contentAssistant = configuration.getContentAssistant(sourceViewer);
					if (contentAssistant instanceof ContentAssistant) {
						ContentAssistant assistant = (ContentAssistant) contentAssistant;
						RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), EditorPreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND);
						Color color = EditorUtility.getColor(rgb);
						assistant.setContextInformationPopupBackground(color);
						assistant.setContextSelectorBackground(color);
					}
				}
			}
		}

		// update content assist preferences
		if (EditorPreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND.equals(property)) {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null) {
				SourceViewerConfiguration configuration = getSourceViewerConfiguration();
				if (configuration != null) {
					IContentAssistant contentAssistant = configuration.getContentAssistant(sourceViewer);
					if (contentAssistant instanceof ContentAssistant) {
						ContentAssistant assistant = (ContentAssistant) contentAssistant;
						RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), EditorPreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND);
						Color color = EditorUtility.getColor(rgb);
						assistant.setContextInformationPopupForeground(color);
						assistant.setContextSelectorForeground(color);
					}
				}
			}
		}

		super.handlePreferenceStoreChanged(event);
	}

	private boolean inBusyState() {
		return fBusyState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// if we've gotten an error elsewhere, before
		// we've actually opened, then don't open.
		if (shouldClose) {
			setSite(site);
			close(false);
		}
		else {
			super.init(site, input);
		}
	}

	/**
	 * Set the document provider for this editor.
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 * 
	 * @param documentProvider
	 *            documentProvider to initialize
	 */
	public void initializeDocumentProvider(IDocumentProvider documentProvider) {
		if (documentProvider != null) {
			setDocumentProvider(documentProvider);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#initializeDragAndDrop(org.eclipse.jface.text.source.ISourceViewer)
	 */
	protected void initializeDragAndDrop(ISourceViewer viewer) {
		IPreferenceStore store = getPreferenceStore();
		if (store != null && store.getBoolean(PREFERENCE_TEXT_DRAG_AND_DROP_ENABLED))
			initializeDrop(viewer);
	}

	protected void initializeDrop(ITextViewer textViewer) {
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		fDropTarget = new DropTarget(textViewer.getTextWidget(), operations);
		fDropAdapter = new ReadOnlyAwareDropTargetAdapter(true);
		fDropAdapter.setTargetEditor(this);
		fDropAdapter.setTargetIDs(getConfigurationPoints());
		fDropAdapter.setTextViewer(textViewer);
		fDropTarget.setTransfer(fDropAdapter.getTransfers());
		fDropTarget.addDropListener(fDropAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
	 */
	protected void initializeEditor() {
		super.initializeEditor();
		// FIXME: here's where to add back in our custom encoding support
		fEncodingSupport = null;
		setPreferenceStore(createCombinedPreferenceStore());

		setRangeIndicator(new DefaultRangeIndicator());
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		initializeDocumentProvider(null);
		// set the infopop for source viewer
		String helpId = getHelpContextId();
		// no infopop set or using default text editor help, use default
		if (helpId == null || ITextEditorHelpContextIds.TEXT_EDITOR.equals(helpId))
			helpId = IHelpContextIds.XML_SOURCE_VIEW_HELPID;
		setHelpContextId(helpId);
		// defect 203158 - disable ruler context menu for
		// beta
		// setRulerContextMenuId(RULER_CONTEXT_MENU_ID);
		configureInsertMode(SMART_INSERT, true);

		// enable the base source editor activity when editor opens
		try {
			// FIXME: - commented out to avoid minor dependancy during
			// transition to org.eclipse
			// WTPActivityBridge.getInstance().enableActivity(CORE_SSE_ACTIVITY_ID,
			// true);
		}
		catch (Exception t) {
			// if something goes wrong with enabling activity, just log the
			// error but dont
			// have it break the editor
			Logger.log(Logger.WARNING_DEBUG, t.getMessage(), t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextEditor#initializeKeyBindingScopes()
	 */
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[]{EDITOR_KEYBINDING_SCOPE_ID});
	}

	/**
	 * Initializes the editor's source viewer and other items that were source
	 * viewer-dependent.
	 */
	private void initializeSourceViewer() {
		IAction contentAssistAction = getAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS);
		if (contentAssistAction instanceof IUpdate) {
			((IUpdate) contentAssistAction).update();
		}
		IAction openHyperlinkAction = getAction(StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE);
		if (openHyperlinkAction instanceof OpenHyperlinkAction) {
			((OpenHyperlinkAction) openHyperlinkAction).setHyperlinkDetectors(getSourceViewerConfiguration().getHyperlinkDetectors(getSourceViewer()));
		}

		// do not even install projection support until folding is actually
		// enabled
		if (isFoldingEnabled()) {
			installProjectionSupport();
		}
	}

	protected void initSourceViewer(StructuredTextViewer sourceViewer) {
		// ensure decoration support is configured
		getSourceViewerDecorationSupport(sourceViewer);
		fMouseTracker = new MouseTracker();
		fMouseTracker.start(sourceViewer.getTextWidget());
	}

	protected void installTextDragAndDrop(ISourceViewer viewer) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextEditor#installEncodingSupport()
	 */
	protected void installEncodingSupport() {
		// TODO: install our custom support that can
		// update document appropriately
		// super.installEncodingSupport();
	}

	/**
	 * Install everything necessary to get document folding working and enable
	 * document folding
	 */
	private void installProjectionSupport() {
		ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();

		fProjectionSupport = new ProjectionSupport(projectionViewer, getAnnotationAccess(), getSharedColors());
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
		fProjectionSupport.setHoverControlCreator(new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent);
			}
		});
		fProjectionSupport.install();

		IStructuredTextFoldingProvider updater = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; updater == null && i < ids.length; i++) {
			updater = (IStructuredTextFoldingProvider) builder.getConfiguration(IStructuredTextFoldingProvider.ID, ids[i]);
		}

		fProjectionModelUpdater = updater;
		if (fProjectionModelUpdater != null)
			fProjectionModelUpdater.install(projectionViewer);

		if (isFoldingEnabled())
			projectionViewer.doOperation(ProjectionViewer.TOGGLE);
	}

	/**
	 * Install everything necessary to get document folding working and enable
	 * document folding
	 */
	private void updateProjectionSupport() {
		// dispose of previous document folding support
		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.uninstall();
			fProjectionModelUpdater = null;
		}

		ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
		IStructuredTextFoldingProvider updater = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; updater == null && i < ids.length; i++) {
			updater = (IStructuredTextFoldingProvider) builder.getConfiguration(IStructuredTextFoldingProvider.ID, ids[i]);
		}

		fProjectionModelUpdater = updater;
		if (fProjectionModelUpdater != null)
			fProjectionModelUpdater.install(projectionViewer);

		if (fProjectionModelUpdater != null)
			fProjectionModelUpdater.initialize();
	}

	/**
	 * Return whether document folding should be enabled according to the
	 * preference store settings.
	 * 
	 * @return <code>true</code> if document folding should be enabled
	 */
	private boolean isFoldingEnabled() {
		IPreferenceStore store = getPreferenceStore();
		// check both preference store and vm argument
		return (store.getBoolean(IStructuredTextFoldingProvider.FOLDING_ENABLED));
	}

	private void logUnexpectedDocumentKind(IEditorInput input) {
		// display a dialog informing user of uknown content type
		if (SSEUIPlugin.getDefault().getPreferenceStore().getBoolean(EditorPreferenceNames.SHOW_UNKNOWN_CONTENT_TYPE_MSG)) {
			Job job = new UIJob(SSEUIMessages.StructuredTextEditor_0) {
				public IStatus runInUIThread(IProgressMonitor monitor) {
					UnknownContentTypeDialog dialog = new UnknownContentTypeDialog(getSite().getShell(), SSEUIPlugin.getDefault().getPreferenceStore(), EditorPreferenceNames.SHOW_UNKNOWN_CONTENT_TYPE_MSG);
					dialog.open();
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}

		Logger.log(Logger.WARNING, "StructuredTextEditor being used without StructuredDocument"); //$NON-NLS-1$
		String name = null;
		if (input != null) {
			name = input.getName();
		}
		else {
			name = "input was null"; //$NON-NLS-1$
		}
		Logger.log(Logger.WARNING, "         Input Name: " + name); //$NON-NLS-1$
		String implClass = null;
		IDocument document = getDocumentProvider().getDocument(input);
		if (document != null) {
			implClass = document.getClass().getName();
		}
		else {
			implClass = "document was null"; //$NON-NLS-1$
		}
		Logger.log(Logger.WARNING, "        Unexpected IDocumentProvider implementation: " + getDocumentProvider().getClass().getName()); //$NON-NLS-1$
		Logger.log(Logger.WARNING, "        Unexpected IDocument implementation: " + implClass); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#performRevert()
	 */
	protected void performRevert() {
		ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
		projectionViewer.setRedraw(false);
		try {

			boolean projectionMode = projectionViewer.isProjectionMode();
			if (projectionMode) {
				projectionViewer.disableProjection();
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.uninstall();
			}

			super.performRevert();

			if (projectionMode) {
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.install(projectionViewer);
				projectionViewer.enableProjection();
			}

		}
		finally {
			projectionViewer.setRedraw(true);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Not API. May be reduced to protected method in the future.
	 * </p>
	 */
	public void rememberSelection() {
		/*
		 * This method was made public for use by editors that use
		 * StructuredTextEditor (like some clients)
		 */
		super.rememberSelection();
	}


	/**
	 * both starts and resets the busy state timer
	 */
	private void resetBusyState() {
		// reset the "busy" timeout
		if (fBusyTimer != null) {
			fBusyTimer.cancel();
		}
		startBusyTimer();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Not API. May be reduced to protected method in the future.
	 * </p>
	 */
	public void restoreSelection() {
		/*
		 * This method was made public for use by editors that use
		 * StructuredTextEditor (like some clients)
		 */
		// catch odd case where source viewer has no text
		// widget (defect
		// 227670)
		if ((getSourceViewer() != null) && (getSourceViewer().getTextWidget() != null))
			super.restoreSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#rulerContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);

		IMenuManager foldingMenu = new MenuManager(SSEUIMessages.Folding, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

		IAction action = getAction("FoldingToggle"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		foldingMenu.add(action);

		IStructuredModel internalModel = getInternalModel();
		if (internalModel != null) {
			boolean debuggingAvailable = BreakpointProviderBuilder.getInstance().isAvailable(internalModel.getContentTypeIdentifier(), BreakpointRulerAction.getFileExtension(getEditorInput()));
			if (debuggingAvailable) {
				// append actions to "debug" group (created in
				// AbstractDecoratedTextEditor.rulerContextMenuAboutToShow(IMenuManager)
				menu.appendToGroup("debug", getAction(ActionDefinitionIds.TOGGLE_BREAKPOINTS)); //$NON-NLS-1$
				menu.appendToGroup("debug", getAction(ActionDefinitionIds.MANAGE_BREAKPOINTS)); //$NON-NLS-1$
				menu.appendToGroup("debug", getAction(ActionDefinitionIds.EDIT_BREAKPOINTS)); //$NON-NLS-1$
			}
			addExtendedRulerContextMenuActions(menu);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to expose part activation handling for multi-page editors.
	 * </p>
	 * <p>
	 * Not API. May be reduced to protected method in the future.
	 * </p>
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#safelySanityCheckState(org.eclipse.ui.IEditorInput)
	 */
	public void safelySanityCheckState(IEditorInput input) {
		super.safelySanityCheckState(input);
	}

	private void savedModel() {
		if (getInternalModel() != null) {
			getInternalModel().changedModel();
		}
	}

	/**
	 * Ensure that the correct IDocumentProvider is used. For direct models, a
	 * special provider is used. For StorageEditorInputs, use a custom
	 * provider that creates a usable ResourceAnnotationModel. For everything
	 * else, use the base support.
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#setDocumentProvider(org.eclipse.ui.IEditorInput)
	 */
	protected void setDocumentProvider(IEditorInput input) {
		if (input instanceof IStructuredModel) {
			// largely untested
			setDocumentProvider(StructuredModelDocumentProvider.getInstance());
		}
		else if (input instanceof IStorageEditorInput && !(input instanceof IFileEditorInput)) {
			setDocumentProvider(StorageModelProvider.getInstance());
		}
		else {
			super.setDocumentProvider(input);
		}
	}

	/**
	 * Set editor part associated with this editor.
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 * 
	 * @param editorPart
	 *            editor part associated with this editor
	 */
	public void setEditorPart(IEditorPart editorPart) {
		fEditorPart = editorPart;
	}

	/**
	 * Sets the model field within this editor.
	 * 
	 * @deprecated - can eventually be eliminated
	 */
	private void setModel(IStructuredModel newModel) {
		Assert.isNotNull(getDocumentProvider(), "document provider can not be null when setting model"); //$NON-NLS-1$
		if (fStructuredModel != null) {
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().removeDocumentListener(getInternalDocumentListener());
			}
			fStructuredModel.removeModelStateListener(getInternalModelStateListener());
		}
		fStructuredModel = newModel;
		if (fStructuredModel != null) {
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().addDocumentListener(getInternalDocumentListener());
			}
			fStructuredModel.addModelStateListener(getInternalModelStateListener());
		}
		// update() should be called whenever the model is
		// set or changed
		update();
	}

	/**
	 * Sets the editor's source viewer configuration which it uses to
	 * configure it's internal source viewer. This method was overwritten so
	 * that viewer configuration could be set after editor part was created.
	 */
	protected void setSourceViewerConfiguration(SourceViewerConfiguration config) {
		super.setSourceViewerConfiguration(config);
		StructuredTextViewer stv = getTextViewer();
		if (stv != null) {
			// there should be no need to unconfigure
			// before configure because
			// configure will
			// also unconfigure before configuring
			stv.unconfigure();
			stv.configure(config);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#showBusy(boolean)
	 */
	public void showBusy(boolean busy) {
		if (busy) {
			fRememberTitle = getPartName();
			// update title and/or fonts and/or background
			//
			// temp solution, for testing, uses "busy"
			setPartName(SSEUIMessages.busy); //$NON-NLS-1$
		}
		else {
			// reset to what it was
			setPartName(fRememberTitle);
		}
	}

	private void startBusyTimer() {
		// TODO: we need a resettable timer, so not so
		// many are created
		fBusyTimer = new Timer(true);
		fBusyTimer.schedule(new TimeOutExpired(), BUSY_STATE_DELAY);
	}

	protected void uninstallTextDragAndDrop(ISourceViewer viewer) {
		// do nothing
	}

	/**
	 * Update should be called whenever the model is set or changed (as in
	 * swapped)
	 * <p>
	 * Not API. May be removed in the future.
	 * </p>
	 */
	public void update() {
		if (fOutlinePage != null && fOutlinePage instanceof ConfigurableContentOutlinePage) {
			ContentOutlineConfiguration cfg = createContentOutlineConfiguration();
			((ConfigurableContentOutlinePage) fOutlinePage).setConfiguration(cfg);
			IStructuredModel internalModel = getInternalModel();
			((ConfigurableContentOutlinePage) fOutlinePage).setInputContentTypeIdentifier(internalModel.getContentTypeIdentifier());
			((ConfigurableContentOutlinePage) fOutlinePage).setInput(internalModel);
		}
		if (fPropertySheetPage != null && fPropertySheetPage instanceof ConfigurablePropertySheetPage) {
			PropertySheetConfiguration cfg = createPropertySheetConfiguration();
			((ConfigurablePropertySheetPage) fPropertySheetPage).setConfiguration(cfg);
		}
		disposeModelDependentFields();

		fShowInTargetIds = createShowInTargetIds();

		if (getSourceViewerConfiguration() instanceof StructuredTextViewerConfiguration && fStatusLineLabelProvider != null) {
			fStatusLineLabelProvider.dispose();
		}

		updateSourceViewerConfiguration();

		if (getSourceViewerConfiguration() instanceof StructuredTextViewerConfiguration) {
			fStatusLineLabelProvider = ((StructuredTextViewerConfiguration) getSourceViewerConfiguration()).getStatusLineLabelProvider(getSourceViewer());
			updateStatusLine(null);
		}

		if (fStructuredSelectionProvider != null) {
			fStructuredSelectionProvider.setDocument(getInternalModel().getStructuredDocument());
		}

		createModelDependentFields();
	}

	/**
	 * Updates all content dependent actions.
	 */
	protected void updateContentDependentActions() {
		super.updateContentDependentActions();
		// super.updateContentDependentActions only updates
		// the enable/disable
		// state of all
		// the content dependent actions.
		// StructuredTextEditor's undo and redo actions
		// have a detail label and
		// description.
		// They needed to be updated.
		if (!fEditorDisposed)
			updateMenuText();
	}


	/**
	 * Updates the editor context menu by creating a new context menu with the
	 * given menu id
	 * 
	 * @param contextMenuId
	 *            Cannot be null
	 */
	private void updateEditorContextMenuId(String contextMenuId) {
		// update editor context menu id if updating to a new id or if context
		// menu is not already set up
		if (!contextMenuId.equals(getEditorContextMenuId()) || (fTextContextMenu == null)) {
			setEditorContextMenuId(contextMenuId);

			if (getSourceViewer() != null) {
				StyledText styledText = getSourceViewer().getTextWidget();
				if (styledText != null) {
					// dispose of previous context menu
					if (fTextContextMenu != null) {
						fTextContextMenu.dispose();
					}
					if (fTextContextMenuManager != null) {
						fTextContextMenuManager.removeMenuListener(getContextMenuListener());
						fTextContextMenuManager.removeAll();
						fTextContextMenuManager.dispose();
					}

					fTextContextMenuManager = new MenuManager(getEditorContextMenuId(), getEditorContextMenuId());
					fTextContextMenuManager.setRemoveAllWhenShown(true);
					fTextContextMenuManager.addMenuListener(getContextMenuListener());

					fTextContextMenu = fTextContextMenuManager.createContextMenu(styledText);
					styledText.setMenu(fTextContextMenu);

					getSite().registerContextMenu(getEditorContextMenuId(), fTextContextMenuManager, getSelectionProvider());

					// also register this menu for source page part and
					// structured text editor ids
					String partId = getSite().getId();
					if (partId != null) {
						getSite().registerContextMenu(partId + EDITOR_CONTEXT_MENU_SUFFIX, fTextContextMenuManager, getSelectionProvider());
					}
					getSite().registerContextMenu(EDITOR_CONTEXT_MENU_ID, fTextContextMenuManager, getSelectionProvider());
				}
			}
		}
	}

	/**
	 * Updates editor context menu, vertical ruler menu, help context id for
	 * new content type
	 * 
	 * @param contentType
	 */
	private void updateEditorControlsForContentType(String contentType) {
		if (contentType == null) {
			updateEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
			updateRulerContextMenuId(RULER_CONTEXT_MENU_ID);
			updateHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
		}
		else {
			updateEditorContextMenuId(contentType + EDITOR_CONTEXT_MENU_SUFFIX);
			updateRulerContextMenuId(contentType + RULER_CONTEXT_MENU_SUFFIX);
			updateHelpContextId(contentType + "_source_HelpId"); //$NON-NLS-1$
		}
	}

	private void updateEncodingMemento() {
		boolean failed = false;
		IStructuredModel internalModel = getInternalModel();
		if (internalModel != null) {
			IStructuredDocument doc = internalModel.getStructuredDocument();
			EncodingMemento memento = doc.getEncodingMemento();
			IDocumentCharsetDetector detector = internalModel.getModelHandler().getEncodingDetector();
			if (memento != null && detector != null)
				detector.set(doc);
			try {
				detector.getEncoding();
			}
			catch (IOException e) {
				failed = true;
			}
			// be sure to use the new instance
			// but only if no exception occurred.
			// (we may find cases we need to do more error recover there)
			// should be near impossible to get IOException from processing
			// the
			// *document*
			if (!failed) {
				doc.setEncodingMemento(memento);
			}
		}
	}

	/**
	 * Updates the help context of the editor with the given help context id
	 * 
	 * @param helpContextId
	 *            Cannot be null
	 */
	private void updateHelpContextId(String helpContextId) {
		if (!helpContextId.equals(getHelpContextId())) {
			setHelpContextId(helpContextId);

			if (getSourceViewer() != null) {
				StyledText styledText = getSourceViewer().getTextWidget();
				if (styledText != null) {
					IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
					helpSystem.setHelp(styledText, getHelpContextId());
				}
			}
		}
	}

	/*
	 * Update the hovering behavior depending on the preferences.
	 */
	private void updateHoverBehavior() {
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		String[] types = configuration.getConfiguredContentTypes(getSourceViewer());

		for (int i = 0; i < types.length; i++) {

			String t = types[i];

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer instanceof ITextViewerExtension2) {
				// Remove existing hovers
				((ITextViewerExtension2) sourceViewer).removeTextHovers(t);

				int[] stateMasks = configuration.getConfiguredTextHoverStateMasks(getSourceViewer(), t);

				if (stateMasks != null) {
					for (int j = 0; j < stateMasks.length; j++) {
						int stateMask = stateMasks[j];
						ITextHover textHover = configuration.getTextHover(sourceViewer, t, stateMask);
						((ITextViewerExtension2) sourceViewer).setTextHover(textHover, t, stateMask);
					}
				}
				else {
					ITextHover textHover = configuration.getTextHover(sourceViewer, t);
					((ITextViewerExtension2) sourceViewer).setTextHover(textHover, t, ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
				}
			}
			else
				sourceViewer.setTextHover(configuration.getTextHover(sourceViewer, t), t);
		}
	}

	private void updateMenuText() {
		if (fStructuredModel != null && !fStructuredModel.isModelStateChanging() && getTextViewer().getTextWidget() != null) {
			// performance: don't force an update of the action bars unless
			// required as it is expensive
			String previousUndoText = null;
			String previousUndoDesc = null;
			String previousRedoText = null;
			String previousRedoDesc = null;
			boolean updateActions = false;
			IAction undoAction = getAction(ITextEditorActionConstants.UNDO);
			IAction redoAction = getAction(ITextEditorActionConstants.REDO);
			if (undoAction != null) {
				previousUndoText = undoAction.getText();
				previousUndoDesc = undoAction.getDescription();
				updateActions = updateActions || previousUndoText == null || previousUndoDesc == null;
				undoAction.setText(UNDO_ACTION_TEXT_DEFAULT);
				undoAction.setDescription(UNDO_ACTION_DESC_DEFAULT);
			}
			if (redoAction != null) {
				previousRedoText = redoAction.getText();
				previousRedoDesc = redoAction.getDescription();
				updateActions = updateActions || previousRedoText == null || previousRedoDesc == null;
				redoAction.setText(REDO_ACTION_TEXT_DEFAULT);
				redoAction.setDescription(REDO_ACTION_DESC_DEFAULT);
			}
			if (fStructuredModel.getUndoManager() != null) {
				IStructuredTextUndoManager undoManager = fStructuredModel.getUndoManager();
				// get undo command
				Command undoCommand = undoManager.getUndoCommand();
				// set undo label and description
				undoAction.setEnabled(undoManager.undoable());
				if (undoCommand != null) {
					String label = undoCommand.getLabel();
					if (label != null) {
						String customText = MessageFormat.format(UNDO_ACTION_TEXT, new String[]{label});
						updateActions = updateActions || customText == null || previousUndoText == null || !customText.equals(previousUndoText);
						undoAction.setText(customText);
					}
					String desc = undoCommand.getDescription();
					if (desc != null) {
						String customDesc = MessageFormat.format(UNDO_ACTION_DESC, new String[]{desc});
						updateActions = updateActions || customDesc == null || previousRedoDesc == null || !customDesc.equals(previousUndoDesc);
						undoAction.setDescription(customDesc);
					}
				}
				// get redo command
				Command redoCommand = undoManager.getRedoCommand();
				// set redo label and description
				redoAction.setEnabled(undoManager.redoable());
				if (redoCommand != null) {
					String label = redoCommand.getLabel();
					if (label != null) {
						String customText = MessageFormat.format(REDO_ACTION_TEXT, new String[]{label});
						updateActions = updateActions || customText == null || previousRedoText == null || !customText.equals(previousRedoText);
						redoAction.setText(customText);
					}
					String desc = redoCommand.getDescription();
					if (desc != null) {
						String customDesc = MessageFormat.format(REDO_ACTION_DESC, new String[]{desc});
						updateActions = updateActions || customDesc == null || previousRedoDesc == null || !customDesc.equals(previousRedoDesc);
						redoAction.setDescription(customDesc);
					}
				}
			}
			// tell the action bars to update
			if (updateActions) {
				if (getEditorSite().getActionBars() != null) {
					getEditorSite().getActionBars().updateActionBars();
				}
				else if (getEditorPart() != null && getEditorPart().getEditorSite().getActionBars() != null) {
					getEditorPart().getEditorSite().getActionBars().updateActionBars();
				}
			}
		}
	}

	void updateRangeIndication(ISelection selection) {
		boolean rangeUpdated = false;
		if (selection instanceof IStructuredSelection && !((IStructuredSelection) selection).isEmpty()) {
			Object[] objects = ((IStructuredSelection) selection).toArray();
			if (objects.length > 0 && objects[0] instanceof IndexedRegion) {
				int start = ((IndexedRegion) objects[0]).getStartOffset();
				int end = ((IndexedRegion) objects[0]).getEndOffset();
				if (objects.length > 1) {
					for (int i = 1; i < objects.length; i++) {
						start = Math.min(start, ((IndexedRegion) objects[i]).getStartOffset());
						end = Math.max(end, ((IndexedRegion) objects[i]).getEndOffset());
					}
				}
				getSourceViewer().setRangeIndication(start, end - start, false);
				rangeUpdated = true;
			}
		}
		if (!rangeUpdated) {
			if (selection instanceof ITextSelection) {
				getSourceViewer().setRangeIndication(((ITextSelection) selection).getOffset(), ((ITextSelection) selection).getLength(), false);
			}
			else {
				getSourceViewer().removeRangeIndication();
			}
		}
	}


	/**
	 * Updates the editor vertical ruler menu by creating a new vertical ruler
	 * context menu with the given menu id
	 * 
	 * @param rulerMenuId
	 *            Cannot be null
	 */
	private void updateRulerContextMenuId(String rulerMenuId) {
		// update ruler context menu id if updating to a new id or if context
		// menu is not already set up
		if (!rulerMenuId.equals(getRulerContextMenuId()) || (fRulerContextMenu == null)) {
			setRulerContextMenuId(rulerMenuId);

			if (getVerticalRuler() != null) {
				// dispose of previous ruler context menu
				if (fRulerContextMenu != null) {
					fRulerContextMenu.dispose();
				}
				if (fRulerContextMenuManager != null) {
					fRulerContextMenuManager.removeMenuListener(getContextMenuListener());
					fRulerContextMenuManager.removeAll();
					fRulerContextMenuManager.dispose();
				}

				fRulerContextMenuManager = new MenuManager(getRulerContextMenuId(), getRulerContextMenuId());
				fRulerContextMenuManager.setRemoveAllWhenShown(true);
				fRulerContextMenuManager.addMenuListener(getContextMenuListener());

				Control rulerControl = getVerticalRuler().getControl();
				fRulerContextMenu = fRulerContextMenuManager.createContextMenu(rulerControl);
				rulerControl.setMenu(fRulerContextMenu);

				getSite().registerContextMenu(getRulerContextMenuId(), fRulerContextMenuManager, getSelectionProvider());

				// also register this menu for source page part and structured
				// text editor ids
				String partId = getSite().getId();
				if (partId != null) {
					getSite().registerContextMenu(partId + RULER_CONTEXT_MENU_SUFFIX, fRulerContextMenuManager, getSelectionProvider());
				}
				getSite().registerContextMenu(RULER_CONTEXT_MENU_ID, fRulerContextMenuManager, getSelectionProvider());
			}
		}
	}

	private void updateSourceViewerConfiguration() {
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		// no need to update source viewer configuration if one does not exist
		// yet
		if (configuration == null) {
			return;
		}
		// do not configure source viewer configuration twice
		boolean configured = false;

		// structuredtextviewer only works with
		// structuredtextviewerconfiguration
		if (!(configuration instanceof StructuredTextViewerConfiguration)) {
			ConfigurationAndTarget cat = createSourceViewerConfiguration();
			fViewerConfigurationTargetId = cat.getTargetId();
			configuration = cat.getConfiguration();
			setSourceViewerConfiguration(configuration);
			configured = true;
		}
		else {
			ConfigurationAndTarget cat = createSourceViewerConfiguration();
			StructuredTextViewerConfiguration newViewerConfiguration = cat.getConfiguration();
			if (!(cat.getTargetId().equals(fViewerConfigurationTargetId))) {
				// d282894 use newViewerConfiguration
				fViewerConfigurationTargetId = cat.getTargetId();
				configuration = newViewerConfiguration;
				setSourceViewerConfiguration(configuration);
				configured = true;
			}
		}

		if (getSourceViewer() != null) {
			// not sure if really need to reconfigure when input changes
			// (maybe only need to reset viewerconfig's document)
			if (!configured)
				getSourceViewer().configure(configuration);
			IAction contentAssistAction = getAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS);
			if (contentAssistAction instanceof IUpdate) {
				((IUpdate) contentAssistAction).update();
			}
			IAction openHyperlinkAction = getAction(StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE);
			if (openHyperlinkAction instanceof OpenHyperlinkAction) {
				((OpenHyperlinkAction) openHyperlinkAction).setHyperlinkDetectors(getSourceViewerConfiguration().getHyperlinkDetectors(getSourceViewer()));
			}
		}
	}

	protected void updateStatusField(String category) {
		super.updateStatusField(category);

		if (category == null)
			return;

		if (StructuredTextEditorActionConstants.STATUS_CATEGORY_OFFSET.equals(category)) {
			IStatusField field = getStatusField(category);
			if (field != null) {
				Point selection = getTextViewer().getTextWidget().getSelection();
				int offset1 = widgetOffset2ModelOffset(getSourceViewer(), selection.x);
				int offset2 = widgetOffset2ModelOffset(getSourceViewer(), selection.y);
				String text = null;
				if (offset1 != offset2)
					text = "[" + offset1 + "-" + offset2 + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				else
					text = "[ " + offset1 + " ]"; //$NON-NLS-1$ //$NON-NLS-2$
				field.setText(text == null ? fErrorLabel : text);
			}
		}
	}

	void updateStatusLine(ISelection selection) {
		IStatusLineManager statusLineManager = getEditorSite().getActionBars().getStatusLineManager();
		if (fStatusLineLabelProvider != null && statusLineManager != null) {
			String text = null;
			Image image = null;
			if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement != null) {
					text = fStatusLineLabelProvider.getText(firstElement);
					image = fStatusLineLabelProvider.getImage((firstElement));
				}
			}
			if (image == null) {
				statusLineManager.setMessage(text);
			}
			else {
				statusLineManager.setMessage(image, text);
			}
		}
	}

	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer) {
		/*
		 * Need to override this method to use special
		 * StructuredSourceViewerDecorationSupport. See
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=201928
		 */
		if (fSourceViewerDecorationSupport == null) {
			fSourceViewerDecorationSupport = new StructuredSourceViewerDecorationSupport(viewer, getOverviewRuler(), getAnnotationAccess(), getSharedColors());
			configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
		}
		return fSourceViewerDecorationSupport;
	}
}
