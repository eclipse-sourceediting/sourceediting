/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IChangeRulerColumn;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineChangeHover;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.wst.common.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.IModelLifecycleListener;
import org.eclipse.wst.sse.core.IModelStateListenerExtended;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.ModelPlugin;
import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.text.IExecutionDelegatable;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.core.util.Utilities;
import org.eclipse.wst.sse.ui.edit.util.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.edit.util.StructuredTextEditorActionConstants;
import org.eclipse.wst.sse.ui.extension.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.extension.ExtendedEditorActionBuilder;
import org.eclipse.wst.sse.ui.extension.ExtendedEditorDropTargetAdapter;
import org.eclipse.wst.sse.ui.extension.IExtendedContributor;
import org.eclipse.wst.sse.ui.extension.IExtendedMarkupEditor;
import org.eclipse.wst.sse.ui.extension.IExtendedMarkupEditorExtension;
import org.eclipse.wst.sse.ui.extension.IPopupMenuContributor;
import org.eclipse.wst.sse.ui.extensions.ConfigurationPointCalculator;
import org.eclipse.wst.sse.ui.extensions.breakpoint.NullSourceEditingTextTools;
import org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckTarget;
import org.eclipse.wst.sse.ui.internal.debug.BreakpointRulerAction;
import org.eclipse.wst.sse.ui.internal.debug.EditBreakpointAction;
import org.eclipse.wst.sse.ui.internal.debug.ManageBreakpointAction;
import org.eclipse.wst.sse.ui.internal.debug.ToggleBreakpointAction;
import org.eclipse.wst.sse.ui.internal.editor.EditorExecutionContext;
import org.eclipse.wst.sse.ui.internal.editor.EditorModelUtil;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.internal.editor.StructuredModelDocumentProvider;
import org.eclipse.wst.sse.ui.internal.extension.BreakpointProviderBuilder;
import org.eclipse.wst.sse.ui.internal.openon.OpenFileHyperlinkTracker;
import org.eclipse.wst.sse.ui.internal.openon.OpenOnAction;
import org.eclipse.wst.sse.ui.internal.selection.SelectionHistory;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectEnclosingAction;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectHistoryAction;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectNextAction;
import org.eclipse.wst.sse.ui.internal.selection.StructureSelectPreviousAction;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.views.contentoutline.StructuredTextEditorContentOutlinePage;
import org.eclipse.wst.sse.ui.views.properties.ConfigurablePropertySheetPage;
import org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration;
import org.eclipse.wst.sse.ui.views.properties.ShowPropertiesAction;
import org.eclipse.wst.sse.ui.views.properties.StructuredPropertySheetConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class StructuredTextEditor extends TextEditor implements IExtendedMarkupEditor, IExtendedMarkupEditorExtension, IDocumentListener, IShowInTargetList {

	class InternalElementStateListener implements IElementStateListener {

		public void elementContentAboutToBeReplaced(Object element) {
			// we just forward the event
			handleElementContentAboutToBeReplaced(element);
		}

		public void elementContentReplaced(Object element) {
			// we just forward the event
			handleElementContentReplaced(element);
		}

		public void elementDeleted(Object element) {
			// we just forward the event
			handleElementDeleted(element);
		}

		public void elementDirtyStateChanged(Object element, boolean isDirty) {
			// we just forward the event
			handleElementDirtyStateChanged(element, isDirty);
		}

		public void elementMoved(Object originalElement, Object movedElement) {
			// we just forward the event
			handleElementMoved(originalElement, movedElement);
		}
	}

	class InternalModelStateListener implements IModelStateListenerExtended {
		public void modelAboutToBeChanged(IStructuredModel model) {
			if (getTextViewer() != null) {
				//getTextViewer().setRedraw(false);
			}
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			if (getTextViewer() != null) {
				//getTextViewer().setRedraw(false);
				getTextViewer().unconfigure();
				SourceViewerConfiguration config = getSourceViewerConfiguration();
				if (config instanceof StructuredTextViewerConfiguration) {
					((StructuredTextViewerConfiguration) config).unConfigure(getSourceViewer());
				}
			}
		}

		public void modelChanged(IStructuredModel model) {
			if (getTextViewer() != null) {
				//getTextViewer().setRedraw(true);
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
					getSourceViewer().configure(getSourceViewerConfiguration());
				}
			} catch (Exception e) {
				// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=1166
				// investigate each error case post beta
				Logger.logException("problem trying to configure after model change", e); //$NON-NLS-1$
			} finally {
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
			} else {
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

	class TimeOutExpired extends TimerTask {

		public void run() {
			//ILock lock = Platform.getJobManager().newLock();
			//try {
			//lock.acquire();
			getDisplay().syncExec(new Runnable() {
				public void run() {
					if (getDisplay() != null && !getDisplay().isDisposed())
						endBusyStateInternal();
				}
			});
			//			}
			//			finally {
			//				lock.release();
			//			}
		}

	}

	private class ViewerModelLifecycleListener implements IModelLifecycleListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelLifecycleListener#processPostModelEvent(org.eclipse.wst.sse.core.ModelLifecycleEvent)
		 */
		public void processPostModelEvent(ModelLifecycleEvent event) {
			// reconnect the textviewer on document instance change
			if (event.getType() == ModelLifecycleEvent.MODEL_DOCUMENT_CHANGED) {
				if (getTextViewer() != null && getTextViewer().getControl() != null && !getTextViewer().getControl().isDisposed() && event.getModel().equals(getModel())) {
					getTextViewer().setModel(event.getModel(), getDocumentProvider().getAnnotationModel(getEditorInput()));
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelLifecycleListener#processPreModelEvent(org.eclipse.wst.sse.core.ModelLifecycleEvent)
		 */
		public void processPreModelEvent(ModelLifecycleEvent event) {
		}
	}

	protected final static char[] BRACKETS = {'{', '}', '(', ')', '[', ']'};
	private static final long BUSY_STATE_DELAY = 1000;
	public static final String CORE_SSE_ACTIVITY_ID = "com.ibm.wtp.xml.core"; //$NON-NLS-1$
	protected static final String DOT = "."; //$NON-NLS-1$

	/** Non-NLS strings */
	private static final String EDITOR_KEYBINDING_SCOPE_ID = "org.eclipse.wst.sse.ui.structuredTextEditorScope"; //$NON-NLS-1$
	private static final String EDITOR_CONTEXT_MENU_ID = "org.eclipse.wst.sse.ui.StructuredTextEditor.context"; //$NON-NLS-1$
	public static final String GROUP_NAME_ADDITIONS = "additions"; //$NON-NLS-1$
	public static final String GROUP_NAME_FORMAT = "Format"; //$NON-NLS-1$
	public static final String GROUP_NAME_FORMAT_EXT = "Format.ext"; //$NON-NLS-1$
	private static final String REDO_ACTION_DESC = ResourceHandler.getString("Redo__{0}._UI_"); //$NON-NLS-1$ = "Redo: {0}."
	private static final String REDO_ACTION_DESC_DEFAULT = ResourceHandler.getString("Redo_Text_Change._UI_"); //$NON-NLS-1$ = "Redo Text Change."
	private static final String REDO_ACTION_TEXT = ResourceHandler.getString("&Redo_{0}_@Ctrl+Y_UI_"); //$NON-NLS-1$ = "&Redo {0} @Ctrl+Y"
	private static final String REDO_ACTION_TEXT_DEFAULT = ResourceHandler.getString("&Redo_Text_Change_@Ctrl+Y_UI_"); //$NON-NLS-1$ = "&Redo Text Change @Ctrl+Y"
	protected static final String SSE_MODEL_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$
	/**
	 * Constant for representing an error status. This is considered a value
	 * object.
	 */
	static final protected IStatus STATUS_ERROR = new Status(IStatus.ERROR, EditorPlugin.ID, IStatus.INFO, "ERROR", null); //$NON-NLS-1$
	/**
	 * Constant for representing an ok status. This is considered a value
	 * object.
	 */
	static final protected IStatus STATUS_OK = new Status(IStatus.OK, EditorPlugin.ID, IStatus.OK, "OK", null); //$NON-NLS-1$

	/** Translatable strings */
	private static final String UNDO_ACTION_DESC = ResourceHandler.getString("Undo__{0}._UI_"); //$NON-NLS-1$ = "Undo: {0}."
	private static final String UNDO_ACTION_DESC_DEFAULT = ResourceHandler.getString("Undo_Text_Change._UI_"); //$NON-NLS-1$ = "Undo Text Change."
	private static final String UNDO_ACTION_TEXT = ResourceHandler.getString("&Undo_{0}_@Ctrl+Z_UI_"); //$NON-NLS-1$ = "&Undo {0} @Ctrl+Z"
	private static final String UNDO_ACTION_TEXT_DEFAULT = ResourceHandler.getString("&Undo_Text_Change_@Ctrl+Z_UI_"); //$NON-NLS-1$ = "&Undo Text Change @Ctrl+Z"

	/**
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(String[] args) {
		StructuredTextEditor editor = null;
		try {
			editor = new StructuredTextEditor();
			System.out.println("Created: " + editor); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// development time/debug variables only
	private int adapterRequests;
	private long adapterTime;
	private boolean fBackgroundJobEnded;
	private boolean fBusyState;
	private Timer fBusyTimer;
	Runnable fCurrentRunnable = null;
	boolean fDirtyBeforeDocumentEvent = false;
	protected ExtendedEditorDropTargetAdapter fDropAdapter;
	protected DropTarget fDropTarget;
	protected boolean fEditorDisposed = false;
	private IEditorPart fEditorPart;
	/** The open file hyperlink tracker */
	private OpenFileHyperlinkTracker fHyperlinkTracker;
	private IModelLifecycleListener fInternalLifeCycleListener = new ViewerModelLifecycleListener();
	private InternalModelStateListener fInternalModelStateListener;

	protected MouseTracker fMouseTracker;
	private boolean forceReadOnly = false;
	protected IContentOutlinePage fOutlinePage;
	protected IPropertySheetPage fPropertySheetPage;
	private String fRememberTitle;
	String[] fShowInTargetIds = new String[]{IPageLayout.ID_RES_NAV};
	private IAction fShowPropertiesAction = null;
	private SpellCheckTarget fSpellCheckTarget = null;
	private IStructuredModel fStructuredModel;

	private boolean fUpdateMenuTextPending;
	protected int hoverX = -1;
	protected int hoverY = -1;
	private InternalElementStateListener internalElementStateListener = new InternalElementStateListener();
	private boolean shouldClose = false;
	private long startPerfTime;

	public StructuredTextEditor() {

		super();
		initializeDocumentProvider(null);
	}

	/**
	 * @deprecated
	 */
	public StructuredTextEditor(boolean forceReadOnly2) {
	}

	/*
	 * This method is just to make firePropertyChanged accessbible from some
	 * (anonomous) inner classes.
	 */
	protected void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

	protected void abstractTextEditorContextMenuAboutToShow(IMenuManager menu) {
		menu.add(new Separator(ITextEditorActionConstants.GROUP_UNDO));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_COPY));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_PRINT));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_FIND));
		menu.add(new Separator(IWorkbenchActionConstants.GROUP_ADD));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(ITextEditorActionConstants.GROUP_SAVE));

		if (isEditable()) {
			addAction(menu, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.UNDO);
			addAction(menu, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REVERT_TO_SAVED);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.CUT);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.COPY);
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.PASTE);
			addAction(menu, ITextEditorActionConstants.GROUP_SAVE, ITextEditorActionConstants.SAVE);
		} else {
			addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.COPY);
		}
	}

	protected void addContextMenuActions(IMenuManager menu) {
		// Only offer actions that affect the text if the viewer allows
		// modification and supports any of these operations
		IAction formatAll = getAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT);
		IAction formatSelection = getAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS);
		IAction cleanupAll = getAction(StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT);
		boolean enableFormatMenu = (formatAll != null && formatAll.isEnabled()) || (formatSelection != null && formatSelection.isEnabled()) || (cleanupAll != null && cleanupAll.isEnabled());

		if (getSourceViewer().isEditable() && enableFormatMenu) {
			String label = ResourceHandler.getString("FormatMenu.label"); //$NON-NLS-1$ = "Format"
			MenuManager subMenu = new MenuManager(label, GROUP_NAME_FORMAT);
			subMenu.add(new GroupMarker(GROUP_NAME_FORMAT_EXT));
			addAction(subMenu, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT);
			addAction(subMenu, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS);
			subMenu.add(new GroupMarker(GROUP_NAME_ADDITIONS));
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, StructuredTextEditorActionConstants.ACTION_NAME_CLEANUP_DOCUMENT);
			menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, subMenu);
		}

		// Some Design editors (DTD) rely on this view for their own uses
		menu.appendToGroup(IWorkbenchActionConstants.VIEW_EXT, fShowPropertiesAction);
	}

	protected void addExtendedContextMenuActions(IMenuManager menu) {
		IEditorActionBarContributor c = getEditorSite().getActionBarContributor();
		if (c instanceof IPopupMenuContributor) {
			((IPopupMenuContributor) c).contributeToPopupMenu(menu);
		} else {
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
	 *  
	 */
	public void beginBackgroundOperation() {
		fBackgroundJobEnded = false;
		// if already in busy state, no need to do anything
		// and, we only start, or reset, the timed busy
		// state when we get the "endBackgroundOperation" call.
		if (!inBusyState()) {
			beginBusyStateInternal();
		}
	}

	/**
	 *  
	 */
	private void beginBusyStateInternal() {

		fBusyState = true;
		startBusyTimer();

		ISourceViewer viewer = getSourceViewer();
		if (viewer instanceof StructuredTextViewer) {
			((StructuredTextViewer) viewer).beginBackgroundUpdate();

		}
		showBusy(true);
	}

	//    private void addFindOccurrencesAction(String matchType, String
	// matchText, IMenuManager menu) {
	//
	//        AbstractFindOccurrencesAction action = new
	// AbstractFindOccurrencesAction(getFileInEditor(), new
	// SearchUIConfiguration(), (IStructuredDocument) getDocument(),
	// matchType, matchText, getProgressMonitor());
	//        action.setText("Occurrences of \"" + matchText + "\" in File");
	//        menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT, action);
	//    }

	/**
	 * Instead of us closing directly, we have to close with our containing
	 * (multipage) editor, if it exists.
	 */
	public void close(final boolean save) {
		if (getSite() == null) {
			// if site hasn't been set yet, then we're not
			// completely open
			// so set a flag not to open
			shouldClose = true;
		} else {
			if (getEditorPart() != null) {
				Display display = getSite().getShell().getDisplay();
				display.asyncExec(new Runnable() {

					public void run() {
						getSite().getPage().closeEditor(getEditorPart(), save);
					}
				});
			} else {
				super.close(save);
			}
		}
	}

	/**
	 * Compute and set double-click action for the source editor, depending on
	 * the model being used.
	 */
	protected void computeAndSetDoubleClickAction(IStructuredModel model) {
		if (model == null)
			return;
		// If we're editing a JSP file, make
		// double-clicking on the ruler set
		// a breakpoint instead of setting a bookmark.
		String ext = BreakpointRulerAction.getFileExtension(getEditorInput());
		if (BreakpointProviderBuilder.getInstance().isAvailable(model.getContentTypeIdentifier(), ext)) {
			setAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK, getAction(ActionDefinitionIds.ADD_BREAKPOINTS));
		} else {
			// note: the following set action to bookmarks
			// (normally the
			// default)
			// is normally set in super 'createActions'
			// method,
			// [which is just called once, during init],
			// but we'll put it
			// here in this else clause too. The only time
			// it would really be
			// needed is if
			// the same instance of the editor
			// was used first to edit an JSP model, then
			// used for non-JSP model
			// (e.g. XHTML) ...
			// which should be rare, but it is technically
			// possible when using
			// frames
			// and may be more common in future versions
			setAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK, getAction(IDEActionFactory.BOOKMARK.getId()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#configureSourceViewerDecorationSupport(org.eclipse.ui.texteditor.SourceViewerDecorationSupport)
	 */
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(createCharacterPairMatcher());
		support.setMatchingCharacterPainterPreferenceKeys(CommonEditorPreferenceNames.MATCHING_BRACKETS, CommonEditorPreferenceNames.MATCHING_BRACKETS_COLOR);

		super.configureSourceViewerDecorationSupport(support);
	}

	protected void createActions() {
		super.createActions();
		ResourceBundle resourceBundle = ResourceHandler.getResourceBundle();
		// TextView Action - moving the selected text to
		// the clipboard
		// override the cut/paste/delete action to make
		// them run on read-only
		// files
		Action action = new TextOperationAction(resourceBundle, "Editor.Cut.", this, ITextOperationTarget.CUT, true); //$NON-NLS-1$
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);
		setAction(ITextEditorActionConstants.CUT, action);
		WorkbenchHelp.setHelp(action, IAbstractTextEditorHelpContextIds.CUT_ACTION);
		// TextView Action - inserting the clipboard
		// content at the current
		// position
		// override the cut/paste/delete action to make
		// them run on read-only
		// files
		action = new TextOperationAction(resourceBundle, "Editor.Paste.", this, ITextOperationTarget.PASTE, true); //$NON-NLS-1$
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
		setAction(ITextEditorActionConstants.PASTE, action);
		WorkbenchHelp.setHelp(action, IAbstractTextEditorHelpContextIds.PASTE_ACTION);
		// TextView Action - deleting the selected text or
		// if selection is
		// empty the character at the right of the current
		// position
		// override the cut/paste/delete action to make
		// them run on read-only
		// files
		action = new TextOperationAction(resourceBundle, "Editor.Delete.", this, ITextOperationTarget.DELETE, true); //$NON-NLS-1$
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		setAction(ITextEditorActionConstants.DELETE, action);
		WorkbenchHelp.setHelp(action, IAbstractTextEditorHelpContextIds.DELETE_ACTION);
		// SourceView Action - requesting information at
		// the current insertion
		// position
		action = new TextOperationAction(ResourceHandler.getResourceBundle(), StructuredTextEditorActionConstants.ACTION_NAME_INFORMATION + DOT, this, ISourceViewer.INFORMATION, true);
		action.setActionDefinitionId(ActionDefinitionIds.INFORMATION);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_INFORMATION, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_INFORMATION, true);
		// SourceView Action - requesting content assist to
		// show completetion
		// proposals for the current insert position
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS + DOT, this, ISourceViewer.CONTENTASSIST_PROPOSALS, true);
		WorkbenchHelp.setHelp(action, IHelpContextIds.CONTMNU_CONTENTASSIST_HELPID);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS, true);
		// SourceView Action - requesting content assist to
		// show the content
		// information for the current insert position
		action = new TextOperationAction(ResourceHandler.getResourceBundle(), StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_CONTEXT_INFORMATION + DOT, this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_CONTEXT_INFORMATION, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_CONTEXT_INFORMATION, true);
		// StructuredTextViewer Action - requesting
		// correction assist to show
		// correction proposals for the current position
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_QUICK_FIX + DOT, this, StructuredTextViewer.QUICK_FIX, true);
		action.setActionDefinitionId(ActionDefinitionIds.QUICK_FIX);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_QUICK_FIX, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_QUICK_FIX, true);
		// StructuredTextViewer Action - requesting format
		// of the whole
		// document
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT + DOT, this, StructuredTextViewer.FORMAT_DOCUMENT);
		WorkbenchHelp.setHelp(action, IHelpContextIds.CONTMNU_FORMAT_DOC_HELPID);
		action.setActionDefinitionId(ActionDefinitionIds.FORMAT_DOCUMENT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT, true);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_DOCUMENT, true);
		// StructuredTextViewer Action - requesting format
		// of the active
		// elements
		action = new TextOperationAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS + DOT, this, StructuredTextViewer.FORMAT_ACTIVE_ELEMENTS);
		WorkbenchHelp.setHelp(action, IHelpContextIds.CONTMNU_FORMAT_ELEMENTS_HELPID);
		action.setActionDefinitionId(ActionDefinitionIds.FORMAT_ACTIVE_ELEMENTS);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS, action);
		markAsStateDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS, true);
		markAsSelectionDependentAction(StructuredTextEditorActionConstants.ACTION_NAME_FORMAT_ACTIVE_ELEMENTS, true);
		// StructuredTextEditor Action - add breakpoints
		action = new ToggleBreakpointAction(this, getVerticalRuler());
		setAction(ActionDefinitionIds.ADD_BREAKPOINTS, action);
		// StructuredTextEditor Action - manage breakpoints
		action = new ManageBreakpointAction(this, getVerticalRuler());
		setAction(ActionDefinitionIds.MANAGE_BREAKPOINTS, action);
		// StructuredTextEditor Action - edit breakpoints
		action = new EditBreakpointAction(this, getVerticalRuler());
		setAction(ActionDefinitionIds.EDIT_BREAKPOINTS, action);
		// StructuredTextViewer Action - open file on selection
		action = new OpenOnAction(resourceBundle, StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE + DOT, this);
		action.setActionDefinitionId(ActionDefinitionIds.OPEN_FILE);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_OPEN_FILE, action);

		fShowPropertiesAction = new ShowPropertiesAction();

		SelectionHistory selectionHistory = new SelectionHistory(this);
		action = new StructureSelectEnclosingAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_ENCLOSING);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_ENCLOSING, action);

		action = new StructureSelectNextAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_NEXT);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_NEXT, action);

		action = new StructureSelectPreviousAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_PREVIOUS);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_PREVIOUS, action);

		action = new StructureSelectHistoryAction(this, selectionHistory);
		action.setActionDefinitionId(ActionDefinitionIds.STRUCTURE_SELECT_HISTORY);
		setAction(StructuredTextEditorActionConstants.ACTION_NAME_STRUCTURE_SELECT_HISTORY, action);
		selectionHistory.setHistoryAction((StructureSelectHistoryAction) action);
	}

	/**
	 * Creates and returns a <code>LineChangeHover</code> to be used on this
	 * editor's change ruler column. This default implementation returns a
	 * plain <code>LineChangeHover</code>. Subclasses may override.
	 * 
	 * @return the change hover to be used by this editors quick diff display
	 */
	protected LineChangeHover createChangeHover() {
		//return new LineChangeHover();
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
			matcher = new ICharacterPairMatcher() {

				public void clear() {
				}

				public void dispose() {
				}

				public int getAnchor() {
					return ICharacterPairMatcher.LEFT;
				}

				public IRegion match(IDocument iDocument, int i) {
					return null;
				}
			};
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
		IPreferenceStore sseEditorPrefs = ((AbstractUIPlugin) Platform.getPlugin(EditorPlugin.ID)).getPreferenceStore();
		IPreferenceStore baseEditorPrefs = EditorsUI.getPreferenceStore();
		return new ChainedPreferenceStore(new IPreferenceStore[]{sseEditorPrefs, baseEditorPrefs});
	}

	protected ContentOutlineConfiguration createContentOutlineConfiguration() {
		ContentOutlineConfiguration cfg = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; cfg == null && i < ids.length; i++) {
			cfg = (ContentOutlineConfiguration) builder.getConfiguration(ContentOutlineConfiguration.ID, ids[i]);
		}
		return cfg;
	}

	protected void createModelDependentFields() {
		// none at this level
	}

	/**
	 * We override the super version of this method for the sole purpose of
	 * using one of our own special viewer configuration objects.
	 */
	public void createPartControl(Composite parent) {
		StructuredTextViewerConfiguration newViewerConfiguration = createSourceViewerConfiguration();
		SourceViewerConfiguration oldViewerConfiguration = getSourceViewerConfiguration();
		if (oldViewerConfiguration instanceof StructuredTextViewerConfiguration && !((StructuredTextViewerConfiguration) oldViewerConfiguration).getDeclaringID().equals(newViewerConfiguration.getDeclaringID()))
			setSourceViewerConfiguration(newViewerConfiguration);

		super.createPartControl(parent);

		// reset the input now that the editor is
		// initialized and can handle it
		// properly
		// TODO - urgent, SSE v6: THIS SHOULDN'T BE DONE HERE
		// ANYMORE - but for now, have to to get 'configure' to work right?
		// but causes two pass initialization! Does fixing this require base
		// fix?
		setInput(getEditorInput());

		if (isBrowserLikeLinks())
			enableBrowserLikeLinks();
	}

	protected PropertySheetConfiguration createPropertySheetConfiguration() {
		PropertySheetConfiguration cfg = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; cfg == null && i < ids.length; i++) {
			cfg = (PropertySheetConfiguration) builder.getConfiguration(PropertySheetConfiguration.ID, ids[i]);
		}
		return cfg;
	}

	/**
	 * Loads the Show In Target IDs from the Extended Configuration extension
	 * point.
	 * 
	 * @return
	 */
	protected String[] createShowInTargetIds() {
		List allIds = new ArrayList(0);
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] configurationIds = getConfigurationPoints();
		for (int i = 0; i < configurationIds.length; i++) {
			IConfigurationElement el = builder.getConfigurationElement("showintarget", configurationIds[i]); //$NON-NLS-1$
			if (el != null) {
				String someIds = el.getAttribute("ids"); //$NON-NLS-1$
				if (someIds != null && someIds.length() > 0) {
					String[] ids = StringUtils.unpack(someIds);
					for (int j = 0; j < ids.length; j++) {
						// trim, just to keep things clean
						String id = ids[j].trim();
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
	protected SourceEditingTextTools createSourceEditingTextTools() {
		SourceEditingTextTools tools = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; tools == null && i < ids.length; i++) {
			tools = (SourceEditingTextTools) builder.getConfiguration(NullSourceEditingTextTools.ID, ids[i]);
		}
		if (tools == null) {
			tools = NullSourceEditingTextTools.getInstance();
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

	protected StructuredTextViewerConfiguration createSourceViewerConfiguration() {
		StructuredTextViewerConfiguration cfg = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; cfg == null && i < ids.length; i++) {
			cfg = (StructuredTextViewerConfiguration) builder.getConfiguration(StructuredTextViewerConfiguration.ID, ids[i]);
		}
		if (cfg == null) {
			cfg = new StructuredTextViewerConfiguration();
			cfg.setDeclaringID(getClass().getName() + "#default"); //$NON-NLS-1$
		}
		cfg.setEditorPart(this);
		return cfg;
	}

	protected SpellCheckTarget createSpellCheckTarget() {
		SpellCheckTarget target = null;
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();
		String[] ids = getConfigurationPoints();
		for (int i = 0; target == null && i < ids.length; i++) {
			target = (SpellCheckTarget) builder.getConfiguration(SpellCheckTargetImpl.ID, ids[i]);
		}
		if (target == null) {
			target = new SpellCheckTargetImpl();
		}
		target.setTextEditor(this);
		return target;
	}

	protected StructuredTextViewer createStructedTextViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {
		return new StructuredTextViewer(parent, verticalRuler, getOverviewRuler(), isOverviewRulerVisible(), styles);
	}

	/**
	 * Disables browser like links.
	 */
	private void disableBrowserLikeLinks() {
		if (fHyperlinkTracker != null) {
			fHyperlinkTracker.uninstall();
			fHyperlinkTracker = null;
		}
	}

	/**
	 * @see DekstopPart#dispose
	 */
	public void dispose() {
		Logger.trace("Source Editor", "StructuredTextEditor::dispose entry"); //$NON-NLS-1$ //$NON-NLS-2$
		if (org.eclipse.wst.sse.core.util.Debug.perfTestAdapterClassLoading) {
			System.out.println("Total calls to getAdapter: " + adapterRequests); //$NON-NLS-1$
			System.out.println("Total time in getAdapter: " + adapterTime); //$NON-NLS-1$
			System.out.println("Average time per call: " + (adapterTime / adapterRequests)); //$NON-NLS-1$
		}

		// just for work around for document memory leak
		// related to last position (stored in plugin!)
		// holding on to 'selection' which holds instance
		// of document
		// caution, this is making use of internal classes

		int caretOffset = getCaretPosition();
		// safeguard values used in the Position below
		if (caretOffset < 0) {
			caretOffset = 0;
		}

		// TODO: remove the code for now, need to track down the leak
		// FIXME: this was put in, to overcome memory leak (via the document
		// in the selection).
		// we need to fix the leak and/or find a better way.
		//TextEditorPlugin.getDefault().setLastEditPosition(new
		// EditPosition(getEditorInput(), getEditorSite().getId(),
		// TextSelection.emptySelection(), new Position(caretOffset)));

		// subclass may not have mouse tracker created
		// need to check for null before stopping
		if (fMouseTracker != null) {
			fMouseTracker.stop();
			fMouseTracker = null;
		}

		if (isBrowserLikeLinks())
			disableBrowserLikeLinks();

		// added this 2/19/2004 to match the 'add' in
		// intializeDocumentProvider.
		if (getDocumentProvider() != null)
			getDocumentProvider().removeElementStateListener(internalElementStateListener);

		// added this 2/20/2004 based on probe results --
		// seems should be handled by setModel(null), but
		// that's a more radical change.
		// and, technically speaking, should not be needed,
		// but makes a memory leak
		// less severe.
		if (fStructuredModel != null) {
			if (fStructuredModel.getStructuredDocument() != null)
				fStructuredModel.getStructuredDocument().removeDocumentListener(this);
			fStructuredModel.removeModelStateListener(getInternalModelStateListener());
			fStructuredModel.removeModelLifecycleListener(fInternalLifeCycleListener);
		}

		if (getDocument() != null) {
			IDocument doc = getDocument();
			doc.removeDocumentListener(this);
			if (doc instanceof IExecutionDelegatable) {
				((IExecutionDelegatable) doc).setExecutionDelegate(null);
			}
		}
		// check if we've been canceled ... that is,
		// changes to model
		// have not been saved. If so, and if someone else
		// is sharing
		// us in read mode, then we need to force a reload
		// of model.
		IStructuredModel model = fStructuredModel;
		model.releaseFromEdit();

		// disabled==the IDocument form may still be in use by others
		//		boolean needReload = isDirty() && (model.getReferenceCountForEdit()
		// == 0) && (model.getReferenceCountForRead() > 0);
		//		IEditorInput input = getEditorInput();
		//		if (needReload) {
		//			doReload(model, input);
		//		}
		fEditorDisposed = true;
		disposeModelDependentFields();
		// some things in the configuration need to clean
		// up after themselves
		SourceViewerConfiguration config = getSourceViewerConfiguration();
		if (config instanceof StructuredTextViewerConfiguration) {
			((StructuredTextViewerConfiguration) config).unConfigure(getSourceViewer());
		}

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

	/**
	 * Disposes model specific editor helpers such as statusLineHelper.
	 * Basically any code repeated in update() & dispose() should be placed
	 * here.
	 */
	protected void disposeModelDependentFields() {

		// none at this level
	}

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
			fCurrentRunnable = new Runnable() {
				public void run() {
					if (!fEditorDisposed) {
						IStatus status = validateEdit(getSite().getShell());
						if (status != null && status.isOK()) {
							// nothing to do if 'ok'
						} else {
							getModel().getUndoManager().undo();
							getSourceViewer().setSelectedRange(offset, 0);
							if (!fDirtyBeforeDocumentEvent) {
								// reset dirty state if
								// model not dirty before
								// document event
								getModel().setDirtyState(false);
							}
						}
					}
					fCurrentRunnable = null;
				}
			};
			// We need to ensure that this is run via
			// 'asyncExec' since these
			// notifications can come from a non-ui thread.
			//
			// The non-ui thread call would occur when
			// creating a new file
			// under
			// ClearCase (or other library) control. The
			// creation of the new
			// file
			// would trigger a validateEdit call, on
			// another thread, that would
			// prompt the user to add the new file to
			// version control.
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

	/**
	 * This method is marked as public temporarily for Page Designer to fix a
	 * validateEdit problem. This method should not be used by anyone else as
	 * it may be removed in a future release. Please let us know if you think
	 * you really need this.
	 * 
	 * @deprecated
	 */
	public void doReload(IStructuredModel model, IEditorInput input) {
		if (input instanceof IFileEditorInput)
			doReload(model, input);
		else if (input instanceof IStorageEditorInput) {
			InputStream inStream = null;
			try {
				inStream = Utilities.getMarkSupportedStream(((IStorageEditorInput) input).getStorage().getContents());
			} catch (CoreException ce) {
				// no op
			}
			try {
				model.reload(inStream);
			} catch (IOException e) {
				// shouldn't be possible for IStorage.
			}
		}
	}

	/**
	 * @see ITextEditor#doRevertToSaved
	 */
	public void doRevertToSaved() {
		super.doRevertToSaved();
		if (fOutlinePage != null && fOutlinePage instanceof IUpdate)
			((IUpdate) fOutlinePage).update();
		// update menu text
		updateMenuText();
	}

	public void doSave(IProgressMonitor progressMonitor) {
		FileModelProvider fileModelProvider = null;
		try {
			getModel().aboutToChangeModel();
			updateEncodingMemento();
			IDocumentProvider documentProvider = getDocumentProvider();
			if (documentProvider instanceof FileModelProvider) {
				fileModelProvider = (FileModelProvider) documentProvider;
				fileModelProvider.setActiveShell(this.getDisplay().getActiveShell());
			}
			super.doSave(progressMonitor);
		} finally {
			getModel().changedModel();
			if (fileModelProvider != null) {
				fileModelProvider.setActiveShell(null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSetInput(org.eclipse.ui.IEditorInput)
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		try {
			if (isBrowserLikeLinks())
				disableBrowserLikeLinks();

			// TODO: if opened in more than one editor, this will cause
			// problems.
			IEditorInput oldInput = getEditorInput();
			if (oldInput != null) {
				IDocument olddoc = getDocumentProvider().getDocument(oldInput);
				if (olddoc != null && olddoc instanceof IExecutionDelegatable) {
					((IExecutionDelegatable) olddoc).setExecutionDelegate(null);
				}
			}

			if (fStructuredModel != null) {
				fStructuredModel.releaseFromEdit();
			}

			super.doSetInput(input);

			if (getDocument() instanceof IExecutionDelegatable) {
				((IExecutionDelegatable) getDocument()).setExecutionDelegate(new EditorExecutionContext(this));
			}

			if (isBrowserLikeLinks()) {
				enableBrowserLikeLinks();
			}

			IStructuredModel model = null;
			// if we have a Model provider, get the model
			if (getDocumentProvider() instanceof IModelProvider) {
				model = ((IModelProvider) getDocumentProvider()).getModel(getEditorInput());
				if (!model.isShared()) {
					EditorModelUtil.addFactoriesTo(model);
				}
			} else {
				IDocument doc = getDocument();
				Assert.isTrue(doc instanceof IStructuredDocument, "Editing document must be an IStructuredDocument");
				model = ModelPlugin.getDefault().getModelManager().getExistingModelForEdit(doc);
				if (model == null) {
					model = ModelPlugin.getDefault().getModelManager().getModelForEdit((IStructuredDocument) doc);
					EditorModelUtil.addFactoriesTo(model);
				}
			}

			if (fStructuredModel != null || model != null) {
				setModel(model);
			}

			// start editor with smart insert mode
			setInsertMode(SMART_INSERT);
		} catch (CoreException exception) {
			// dispose editor
			dispose();

			throw new CoreException(exception.getStatus());
		}
	}

	/**
	 * Sets up this editor's context menu before it is made visible.
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

		//super.editorContextMenuAboutToShow(menu);
		abstractTextEditorContextMenuAboutToShow(menu);

		addContextMenuActions(menu);
		addExtendedContextMenuActions(menu);
	}

	/**
	 * Enables browser like links.
	 */
	private void enableBrowserLikeLinks() {
		if (fHyperlinkTracker == null) {
			fHyperlinkTracker = new OpenFileHyperlinkTracker(getSourceViewer());
			fHyperlinkTracker.setHyperlinkPreferenceKeys(CommonEditorPreferenceNames.LINK_COLOR, CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER);
			fHyperlinkTracker.install(getPreferenceStore());
		}
	}

	/**
	 * This is the public method to be called to notifiy us that document is
	 * being updated by backround job.
	 */
	public void endBackgroundOperation() {
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
		} else {
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

	public Object getAdapter(Class required) {
		if (org.eclipse.wst.sse.core.util.Debug.perfTestAdapterClassLoading) {
			startPerfTime = System.currentTimeMillis();
		}
		Object result = null;
		// text editor
		if (ITextEditor.class.equals(required)) {
			result = this;
		} else if (IWorkbenchSiteProgressService.class.equals(required)) {
			return getSite().getAdapter(IWorkbenchSiteProgressService.class);
		}
		// content outline page
		else if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null || fOutlinePage.getControl() == null || fOutlinePage.getControl().isDisposed()) {
				ContentOutlineConfiguration cfg = createContentOutlineConfiguration();
				if (cfg != null) {
					if (cfg instanceof StructuredContentOutlineConfiguration) {
						((StructuredContentOutlineConfiguration) cfg).setEditor(this);
					}
					StructuredTextEditorContentOutlinePage outlinePage = new StructuredTextEditorContentOutlinePage();
					outlinePage.setConfiguration(cfg);
					outlinePage.setViewerSelectionManager(getViewerSelectionManager());
					outlinePage.setModel(getModel());
					fOutlinePage = outlinePage;
				}
			}
			result = fOutlinePage;
		}
		// property sheet page, but only if the input's
		// editable
		else if (IPropertySheetPage.class.equals(required) && !forceReadOnly && isEditable()) {
			if (fPropertySheetPage == null || fPropertySheetPage.getControl() == null || fPropertySheetPage.getControl().isDisposed()) {
				PropertySheetConfiguration cfg = createPropertySheetConfiguration();
				if (cfg != null) {
					if (cfg instanceof StructuredPropertySheetConfiguration) {
						((StructuredPropertySheetConfiguration) cfg).setEditor(this);
					}
					ConfigurablePropertySheetPage propertySheetPage = new ConfigurablePropertySheetPage();
					propertySheetPage.setConfiguration(cfg);
					propertySheetPage.setViewerSelectionManager(getViewerSelectionManager());
					propertySheetPage.setModel(getModel());
					fPropertySheetPage = propertySheetPage;
				}
			}
			result = fPropertySheetPage;
		} else if (SpellCheckTarget.class.equals(required)) {
			result = getSpellCheckTarget();
		} else if (SourceEditingTextTools.class.equals(required)) {
			result = createSourceEditingTextTools();
		} else {
			Document document = getDOMDocument();
			if (document != null && document instanceof INodeNotifier) {
				result = ((INodeNotifier) document).getAdapterFor(required);
			}
			if (result == null) {
				result = getModel().getAdapter(required);
			}
			// others
			if (result == null)
				result = super.getAdapter(required);
		}
		if (org.eclipse.wst.sse.core.util.Debug.perfTestAdapterClassLoading) {
			long stop = System.currentTimeMillis();
			adapterRequests++;
			adapterTime += (stop - startPerfTime);
		}
		if (org.eclipse.wst.sse.core.util.Debug.perfTestAdapterClassLoading) {
			System.out.println("Total calls to getAdapter: " + adapterRequests); //$NON-NLS-1$
			System.out.println("Total time in getAdapter: " + adapterTime); //$NON-NLS-1$
			System.out.println("Average time per call: " + (adapterTime / adapterRequests)); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public Node getCaretNode() {
		IStructuredModel model = getModel();
		if (model == null)
			return null;
		int pos = getCaretPosition();
		IndexedRegion inode = model.getIndexedRegion(pos);
		if (inode == null)
			inode = model.getIndexedRegion(pos - 1);
		return (inode instanceof Node) ? (Node) inode : null;
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public int getCaretPosition() {
		ViewerSelectionManager vsm = getViewerSelectionManager();
		if (vsm == null)
			return -1;
		// nsd_TODO: are we being overly paranoid?
		StructuredTextViewer stv = getTextViewer();
		if (stv != null && stv.getControl() != null && !stv.getControl().isDisposed() && getSourceViewer().getVisibleRegion().getOffset() != 0) {
			return vsm.getCaretPosition() + getSourceViewer().getVisibleRegion().getOffset();
		}
		return vsm.getCaretPosition();
	}

	protected String[] getConfigurationPoints() {
		String contentTypeIdentifierID = null;
		if (getModel() != null)
			contentTypeIdentifierID = getModel().getContentTypeIdentifier();
		return ConfigurationPointCalculator.getConfigurationPoints(this, contentTypeIdentifierID, ConfigurationPointCalculator.SOURCE, StructuredTextEditor.class);
	}

	/**
	 * IExtendedMarkupEditorExtension method
	 */
	public Node getCursorNode() {
		if (getModel() != null)
			return (Node) getModel().getIndexedRegion(getCursorOffset());
		else
			return null;
	}

	/**
	 * IExtendedMarkupEditorExtension method
	 */
	public int getCursorOffset() {
		if (hoverX >= 0 && hoverY >= 0)
			return getOffsetAtLocation(hoverX, hoverY);
		return getCaretPosition();
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
		} else {
			result = "0:0"; //$NON-NLS-1$
		}
		return result;
	}

	Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public IDocument getDocument() {
		// ITextViewer tv = getTextViewer();
		// return (tv != null) ? tv.getDocument() : null;
		// The TextViewer may not be available at init
		// time.
		// The right way to get the document is thru
		// DocumentProvider.
		IDocumentProvider dp = getDocumentProvider();
		return (dp != null) ? dp.getDocument(getEditorInput()) : null;
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public Document getDOMDocument() {
		IStructuredModel model = getModel();
		if (model != null) {
			return (Document) model.getAdapter(Document.class);
		}
		return null;
	}

	/**
	 * @see com.ibm.sed.edit.extension.IExtendedSimpleEditor#getEditorPart()
	 */
	public IEditorPart getEditorPart() {
		if (fEditorPart == null)
			return this;
		return fEditorPart;
	}

	/**
	 * @return the IFile from the currently active editor
	 */
	public IFile getFileInEditor() {
		IStructuredModel model = getModel();
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(model.getBaseLocation()));
	}

	/**
	 * Subclasses should override to allow find occurrences for different
	 * region types.
	 * 
	 * @return
	 */
	protected String[] getFindOccurrencesRegionTypes() {
		return new String[0];
	}

	private InternalModelStateListener getInternalModelStateListener() {
		if (fInternalModelStateListener == null) {
			fInternalModelStateListener = new InternalModelStateListener();
		}
		return fInternalModelStateListener;
	}

	/**
	 * This value is set in initialize from input
	 */
	public IStructuredModel getModel() {
		// was causing an exception when several editors
		// open
		// and then "close all"
		// com.ibm.sed.util.Assert.isNotNull(getDocumentProvider());
		// while we did put in protection in 'isDirty'
		// for the null document provider assert failure,
		// we'll
		// just log this error.
		if (getDocumentProvider() == null) {
			// this indicated an error in startup sequence
			Logger.trace("Model Centric Editor", "Program Info Only: document provider was null when model requested"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		boolean initialModelNull = false;
		if (fStructuredModel == null)
			initialModelNull = true;
		if (fStructuredModel == null) {
			if (getDocumentProvider() instanceof IModelProvider) {
				fStructuredModel = ((IModelProvider) getDocumentProvider()).getModel(getEditorInput());
			} else { // nsd_TODO: FileBuffer cleanup
				IDocument doc = getDocument();
				Assert.isTrue(doc instanceof IStructuredDocument);
				IStructuredModel model = ModelPlugin.getDefault().getModelManager().getExistingModelForEdit(doc);
				if (model == null) {
					model = ModelPlugin.getDefault().getModelManager().getModelForEdit((IStructuredDocument) doc);
					EditorModelUtil.addFactoriesTo(model);
				}
				fStructuredModel = model;
			}
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

	/**
	 * @deprecated
	 */
	protected IDocumentProvider getModelProviderFor(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			return FileModelProvider.getInstance();
		} else if (input instanceof IStorageEditorInput) {
			return StorageModelProvider.getInstance();
		} else {
			return NullModelProvider.getInstance();
		}
	}

	/**
	 * Computes the document offset underlying the given text widget graphics
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate inside the text widget
	 * @param y
	 *            the y coordinate inside the text widget
	 * @return the document offset corresponding to the given point
	 */
	protected int getOffsetAtLocation(int x, int y) {
		StyledText styledText = getSourceViewer().getTextWidget();
		if (styledText != null && !styledText.isDisposed()) {
			try {
				int widgetOffset = styledText.getOffsetAtLocation(new Point(x, y));
				if (getSourceViewer() instanceof ITextViewerExtension5) {
					ITextViewerExtension5 extension = (ITextViewerExtension5) getSourceViewer();
					return extension.widgetOffset2ModelOffset(widgetOffset);
				}
				return widgetOffset + getSourceViewer().getVisibleRegion().getOffset();
			} catch (IllegalArgumentException e) {
				return getSourceViewer().getVisibleRegion().getLength();
			}
		}
		return getCaretPosition();
	}

	/**
	 * @return the IStructuredDocumentRegion that the text selection in the
	 *         editor resolves to, or <code>null</code> if the range covers
	 *         more than one region.
	 */
	public IStructuredDocumentRegion getSelectedDocumentRegion() {
		Point p = getSelectionRange();
		int start = p.x;
		int end = p.x + p.y;

		IStructuredDocumentRegion firstSdRegion = ((IStructuredDocument) getDocument()).getRegionAtCharacterOffset(start);
		IStructuredDocumentRegion secondSdRegion = ((IStructuredDocument) getDocument()).getRegionAtCharacterOffset(end);
		if (firstSdRegion != null && secondSdRegion != null) {
			if (firstSdRegion.equals(secondSdRegion) || firstSdRegion.getEndOffset() == end) {
				// the selection is on the same region or
				// the selection ends right on the end of the first region
				return firstSdRegion;
			}
		}
		return null;
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public List getSelectedNodes() {
		ViewerSelectionManager vsm = getViewerSelectionManager();
		return (vsm != null) ? vsm.getSelectedNodes() : null;
	}

	/**
	 * @param sdRegion
	 *            the IStructuredDocumentRegion that you want to check
	 *            selection on
	 * @return the ITextRegion that the text selection in the editor resolves
	 *         to, or <code>null</code> if the range covers more than one
	 *         region or none.
	 */
	public ITextRegion getSelectedTextRegion(IStructuredDocumentRegion sdRegion) {
		Assert.isNotNull(sdRegion);

		Point p = getSelectionRange();
		int start = p.x;
		int end = p.x + p.y;

		ITextRegion first = sdRegion.getRegionAtCharacterOffset(start);
		ITextRegion second = sdRegion.getRegionAtCharacterOffset(end);
		if (first != null && second != null) {
			if (first.equals(second) || sdRegion.getEndOffset(first) == end) {
				// the selection is on the same region
				// the selection ends right on the end of the first region
				return first;
			}
		}
		return null;
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public Point getSelectionRange() {
		ITextViewer tv = getSourceViewer();
		if (tv == null)
			return new Point(-1, -1);
		else
			return tv.getSelectedRange();
	}

	/**
	 * Array of ID Strings that define the default show in targets for this
	 * editor.
	 * 
	 * @see org.eclipse.ui.part.IShowInTargetList#getShowInTargetIds()
	 * @return the array of ID Strings that define the default show in targets
	 *         for this editor.
	 */
	public String[] getShowInTargetIds() {
		return fShowInTargetIds;
	}

	public SpellCheckTarget getSpellCheckTarget() {
		if (fSpellCheckTarget == null) {
			fSpellCheckTarget = createSpellCheckTarget();
		}
		return fSpellCheckTarget;
	}

	private IStatusLineManager getStatusLineManager() {
		AbstractUIPlugin plugin = (AbstractUIPlugin) Platform.getPlugin(PlatformUI.PLUGIN_ID);
		IWorkbenchWindow window = plugin.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		IWorkbenchPage page = window.getActivePage();
		if (page == null)
			return null;
		IEditorPart editor = page.getActiveEditor();
		if (editor == null)
			return null;
		IEditorActionBarContributor contributor = editor.getEditorSite().getActionBarContributor();
		if (contributor instanceof EditorActionBarContributor) {
			return ((EditorActionBarContributor) contributor).getActionBars().getStatusLineManager();
		}
		return null;
	}

	/**
	 * Returns the editor's source viewer. This method was created to expose
	 * the protected final getSourceViewer() method.
	 * 
	 * @return the editor's source viewer
	 */
	public StructuredTextViewer getTextViewer() {
		return (StructuredTextViewer) getSourceViewer();
	}

	public ViewerSelectionManager getViewerSelectionManager() {
		if (getTextViewer() != null)
			return getTextViewer().getViewerSelectionManager();
		return null;
		//if (fViewerSelectionManager == null) {
		////
		//// its required to pass along the underlying
		// widget so that the
		// ViewerSectionManager
		//// can listen to changes in it, via the
		// CaretMediator
		//// Note: the getViewer != null check was put in
		// to avoid a null
		// pointer, if in fact
		//// there was an error that prevented complete
		// instantiation.
		//if (fTextViewer != null) {
		//StyledText textWidget =
		// fTextViewer.getTextWidget();
		//fViewerSelectionManager = new
		// ViewerSelectionManagerImpl(fTextViewer);
		//}
		//}
		//return fViewerSelectionManager;
	}

	protected void handleElementContentAboutToBeReplaced(Object element) {
		// do nothing
	}

	protected void handleElementContentReplaced(Object element) {
		// do nothing, the model provider should reload the
		// model
	}

	protected void handleElementDeleted(Object element) {
		// do nothing, the superclass will close() us
	}

	protected void handleElementDirtyStateChanged(Object element, boolean isDirty) {
		// do nothing, the superclass will fire a proeprty
		// change
	}

	protected void handleElementMoved(Object oldElement, Object newElement) {
		// do nothing, the editor input will be changed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		String property = event.getProperty();

		if (CommonEditorPreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS.equals(property)) {
			updateHoverBehavior();
		}
		if (CommonEditorPreferenceNames.BROWSER_LIKE_LINKS.equals(property)) {
			if (isBrowserLikeLinks())
				enableBrowserLikeLinks();
			else
				disableBrowserLikeLinks();
			return;
		}
		super.handlePreferenceStoreChanged(event);
	}

	/**
	 * @return
	 */
	private boolean inBusyState() {
		return fBusyState;
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// if we've gotten an error elsewhere, before
		// we've actually opened, then don't open.
		if (shouldClose) {
			setSite(site);
			close(false);
		} else {
			super.init(site, input);
		}
	}

	public void initializeDocumentProvider(IDocumentProvider documentProvider) {
		if (getDocumentProvider() != null)
			getDocumentProvider().removeElementStateListener(internalElementStateListener);
		if (documentProvider != null) {
			setDocumentProvider(documentProvider);
		}
		if (documentProvider != null)
			documentProvider.addElementStateListener(internalElementStateListener);
	}

	protected void initializeDrop(ITextViewer textViewer) {
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		fDropTarget = new DropTarget(textViewer.getTextWidget(), operations);
		fDropAdapter = new ReadOnlyAwareDropTargetAdapter();
		fDropAdapter.setTargetEditor(this);
		fDropAdapter.setTargetIDs(getConfigurationPoints());
		fDropAdapter.setTextViewer(textViewer);
		fDropTarget.setTransfer(fDropAdapter.getTransfers());
		fDropTarget.addDropListener(fDropAdapter);
	}

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
		//setRulerContextMenuId(RULER_CONTEXT_MENU_ID);
		configureInsertMode(SMART_INSERT, true);

		// enable the base source editor activity when editor opens
		try {
			//FIXME: - commented out to avoid minor dependancy during transition to org.eclipse
			//WTPActivityBridge.getInstance().enableActivity(CORE_SSE_ACTIVITY_ID, true);
		} catch (Exception t) {
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

	protected void initSourceViewer(StructuredTextViewer sourceViewer) {
		// ensure decoration support is configured
		getSourceViewerDecorationSupport(sourceViewer);
		fMouseTracker = new MouseTracker();
		fMouseTracker.start(sourceViewer.getTextWidget());
		initializeDrop(sourceViewer);
	}

	protected void installEncodingSupport() {
		// TODO: install our custom support that can
		// update document appropriately
		//super.installEncodingSupport();
	}

	/**
	 * Return whether the browser like links should be enabled according to
	 * the preference store settings.
	 * 
	 * @return <code>true</code> if the browser like links should be enabled
	 */
	private boolean isBrowserLikeLinks() {
		IPreferenceStore store = getPreferenceStore();
		return store.getBoolean(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS);
	}

	/*
	 * @see IEditorPart#isDirty
	 */
	public boolean isDirty() {
		// because we're not perfectly forced read only ...
		if (forceReadOnly)
			return false;
		//		IDocumentProvider p = getDocumentProvider();
		//		if (p == null)
		//			return false;
		//
		//		if (getModel() == null)
		//			return false;
		//		return getModel().isDirty();
		return super.isDirty();
	}

	/*
	 * temp impl for V2 (eventually will use super's method, keying off of
	 * IDocumentProvider ... or IDocumentProviderExtension
	 */
	public boolean isEditable() {
		boolean result = true;
		if (forceReadOnly) {
			result = false;
		} else {
			result = super.isEditable();
		}
		return result;
	}

	/*
	 * @see ITextEditorExtension#isEditorInputReadOnly()
	 */
	public boolean isEditorInputReadOnly() {
		if (forceReadOnly)
			return true;
		return super.isEditorInputReadOnly();
	}

	public boolean isFindOccurrencesRegionType(String type) {
		String[] accept = getFindOccurrencesRegionTypes();
		for (int i = 0; i < accept.length; i++) {
			if (accept[i].equals(type))
				return true;
		}
		return false;
	}

	/**
	 * Returns whether the given annotation type is configured as a target
	 * type for the "Go to Next/Previous Annotation" actions. Copied from
	 * org.eclipse.jdt.internal.ui.javaeditor.JavaEditor
	 * 
	 * @param type
	 *            the annotation type
	 * @return <code>true</code> if this is a target type,
	 *         <code>false</code> otherwise
	 * @since 3.0
	 */
	protected boolean isNavigationTargetType(Annotation annotation) {
		Preferences preferences = Platform.getPlugin("org.eclipse.ui.editors").getPluginPreferences(); //$NON-NLS-1$
		AnnotationPreference preference = getAnnotationPreferenceLookup().getAnnotationPreference(annotation);
		//		See bug 41689
		//		String key= forward ?
		// preference.getIsGoToNextNavigationTargetKey()
		// :
		// preference.getIsGoToPreviousNavigationTargetKey();
		String key = preference == null ? null : preference.getIsGoToNextNavigationTargetKey();
		return (key != null && preferences.getBoolean(key));
	}

	/*
	 * @see IEditorPart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		// because we're not perfect, never allow
		// save attempt on forceReadOnly
		if (forceReadOnly || getModel() == null)
			return false;
		return getModel().isSaveNeeded();
	}

	/**
	 * This method was made public for use by editors that use
	 * StructuredTextEditor (like PageDesigner)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#rememberSelection()
	 */
	public void rememberSelection() {
		super.rememberSelection();
	}

	/**
	 * Remove the QuickDiff annotation model from the SourceViewer's
	 * Annotation model if it exists
	 */
	private void removeDiffer() {
		// get annotation model extension
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return;

		IAnnotationModel m = viewer.getAnnotationModel();
		IAnnotationModelExtension model;
		if (m instanceof IAnnotationModelExtension)
			model = (IAnnotationModelExtension) m;
		else
			return;

		// remove the quick differ if it already exists in the annotation
		// model
		model.removeAnnotationModel(IChangeRulerColumn.QUICK_DIFF_MODEL_ID);
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
	 * This method was made public for use by editors that use
	 * StructuredTextEditor (like PageDesigner)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#restoreSelection()
	 */
	public void restoreSelection() {
		// catch odd case where source viewer has no text
		// widget (defect
		// 227670)
		if ((getSourceViewer() != null) && (getSourceViewer().getTextWidget() != null))
			super.restoreSelection();
	}

	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		boolean debuggingAvailable = BreakpointProviderBuilder.getInstance().isAvailable(getModel().getContentTypeIdentifier(), BreakpointRulerAction.getFileExtension(getEditorInput()));
		if (debuggingAvailable) {
			menu.add(getAction(ActionDefinitionIds.ADD_BREAKPOINTS));
			menu.add(getAction(ActionDefinitionIds.MANAGE_BREAKPOINTS));
			menu.add(getAction(ActionDefinitionIds.EDIT_BREAKPOINTS));
			menu.add(new Separator());
		}
		super.rulerContextMenuAboutToShow(menu);
		addExtendedRulerContextMenuActions(menu);
	}

	/**
	 * Overridden to expose part activation handling for multi-page editors
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#safelySanityCheckState(org.eclipse.ui.IEditorInput)
	 */
	public void safelySanityCheckState(IEditorInput input) {
		super.safelySanityCheckState(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#selectAndReveal(int,
	 *      int, int, int)
	 */
	protected void selectAndReveal(int selectionStart, int selectionLength, int revealStart, int revealLength) {
		super.selectAndReveal(selectionStart, selectionLength, revealStart, revealLength);
		getTextViewer().notifyViewerSelectionManager(selectionStart, selectionLength);
	}

	/**
	 * Ensure that the correct IDocumentProvider is used. For IFile and Files,
	 * the default provider with a specified AnnotationModelFactory is used.
	 * For StorageEditorInputs, use a custom provider that creates a usable
	 * ResourceAnnotationModel
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#setDocumentProvider(org.eclipse.ui.IEditorInput)
	 */
	protected void setDocumentProvider(IEditorInput input) {
		if (input instanceof IStructuredModel) {
			setDocumentProvider(StructuredModelDocumentProvider.getInstance());
		} else if (input instanceof IStorageEditorInput && !(input instanceof IFileEditorInput)) {
			setDocumentProvider(StorageModelProvider.getInstance());
		} else {
			super.setDocumentProvider(input);
		}
	}

	public void setEditorPart(IEditorPart editorPart) {
		fEditorPart = editorPart;
	}

	/**
	 * We expose this normally protected method so clients can provide their
	 * own help.
	 * 
	 * @param helpContextId
	 *            the help context id
	 */
	public void setHelpContextId(String helpContextId) {
		// used by (requested by) WSED
		super.setHelpContextId(helpContextId);
		// allows help to be set at any time (not just on
		// AbstractTextEditor's
		// creation)
		if ((getHelpContextId() != null) && (getSourceViewer() != null) && (getSourceViewer().getTextWidget() != null))
			WorkbenchHelp.setHelp(getSourceViewer().getTextWidget(), getHelpContextId());
	}

	/**
	 * @deprecated - use initializeDocumentProvider/setInput
	 * 
	 * Note: this weird API, setModel which takes input as parameter. Is
	 * provided for those editors which don't otherwise have to know about
	 * models's. (Is hard/impossible to override the setInput method.)
	 */
	public void setModel(IFileEditorInput input) {
		org.eclipse.wst.sse.core.util.Assert.isNotNull(getDocumentProvider());
		if (fStructuredModel != null) {
			if (getDocument() != null) {
				getDocument().removeDocumentListener(this);
				fStructuredModel.removeModelStateListener(getInternalModelStateListener());
			}
		}
		if (!(getDocumentProvider() instanceof FileModelProvider)) {
			initializeDocumentProvider(FileModelProvider.getInstance());
		}
		//		((FileModelProvider)
		// getDocumentProvider()).createModelInfo(input);
		//		fStructuredModel = ((FileModelProvider)
		// getDocumentProvider()).getModel(input);
		// model will be null in some error conditions.
		if (fStructuredModel == null) {
			close(false);
		}
		// DMW: checked for site after moving to 3/22
		// (2.1M4) Eclipse base.
		/// Later code in super classes were causing NPE's
		// because site, etc.,
		// hasn't been
		//      initialized yet. If site is still null at this
		// point, we are
		// assuming
		//      setInput and update are called later, perhaps
		// elsewhere.
		//      But if site is not null (such as in DTD Editor)
		// then setInput and
		// update must
		//      be called here.
		//		if (getSite() != null) {
		setInput(input);
		fStructuredModel = ((FileModelProvider) getDocumentProvider()).getModel(input);
		if (fStructuredModel != null) {
			getDocument().addDocumentListener(this);
			fStructuredModel.addModelStateListener(getInternalModelStateListener());
		}
		// update() should be called whenever the model is
		// set or changed
		update();
		//		}
	}

	/**
	 * Sets the model field within this editor, use only when: 1) there is no
	 * IEditorInput (very special case, not well tested) 2) there is an
	 * IEditorInput but the corresponding model needs to be set separately
	 */
	public void setModel(IStructuredModel newModel) {
		org.eclipse.wst.sse.core.util.Assert.isNotNull(getDocumentProvider());
		if (fStructuredModel != null) {
			fStructuredModel.removeModelLifecycleListener(fInternalLifeCycleListener);
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().removeDocumentListener(this);
			}
			fStructuredModel.removeModelStateListener(getInternalModelStateListener());
		}
		fStructuredModel = newModel;
		if (fStructuredModel != null) {
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().addDocumentListener(this);
			}
			fStructuredModel.addModelStateListener(getInternalModelStateListener());
			fStructuredModel.addModelLifecycleListener(fInternalLifeCycleListener);
		}
		// update() should be called whenever the model is
		// set or changed
		update();
	}

	/**
	 * @deprecated - initialize with a document provider and use setInput or
	 *             setModel(IStructuredModel)
	 * @param newModel
	 * @param input
	 */
	public void setModel(IStructuredModel newModel, IFileEditorInput input) {
		//  _setAnnotationModel(input);
		//  setModel(newModel);
		org.eclipse.wst.sse.core.util.Assert.isNotNull(getDocumentProvider());
		if (fStructuredModel != null) {
			fStructuredModel.removeModelLifecycleListener(fInternalLifeCycleListener);
			fStructuredModel.removeModelStateListener(getInternalModelStateListener());
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().removeDocumentListener(this);
			}
		}
		if (getDocumentProvider() instanceof FileModelProvider)
			((FileModelProvider) getDocumentProvider()).createModelInfo(input, newModel, false);
		fStructuredModel = newModel;
		// setInput in super will allow titles to be
		// updated, etc.
		setInput(input);
		if (fStructuredModel != null) {
			if (fStructuredModel.getStructuredDocument() != null) {
				fStructuredModel.getStructuredDocument().addDocumentListener(this);
			}
			fStructuredModel.addModelStateListener(getInternalModelStateListener());
			fStructuredModel.addModelLifecycleListener(fInternalLifeCycleListener);
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

	/**
	 * Overridden to allow custom tab title for multi-page editors
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		super.setTitle(title);
	}

	public void showBusy(boolean busy) {
		if (busy) {
			fRememberTitle = getPartName();
			// update title and/or fonts and/or background
			//
			// temp solution, for testing, uses "busy"
			setPartName("busy");
		} else {
			// reset to what it was
			setPartName(fRememberTitle);
		}
	}

	private void startBusyTimer() {
		// TODO: we need a resetable timer, so not so
		// many created
		fBusyTimer = new Timer(true);
		fBusyTimer.schedule(new TimeOutExpired(), BUSY_STATE_DELAY);
	}


	private void statusError(IStatus status) {
		statusError(status.getMessage());
		ErrorDialog.openError(getSite().getShell(), null, null, status);
	}

	private void statusError(String message) {
		IStatusLineManager manager = getStatusLineManager();
		if (manager == null)
			return;
		manager.setErrorMessage(message);
		getSite().getShell().getDisplay().beep();
	}

	/**
	 * update() should be called whenever the model is set or changed (as in
	 * swapped)
	 */
	public void update() {
		IAnnotationModel annotationModel = getDocumentProvider().getAnnotationModel(getEditorInput());
		if (getTextViewer() != null)
			getTextViewer().setModel(getModel(), annotationModel);
		if (fOutlinePage != null && fOutlinePage instanceof StructuredTextEditorContentOutlinePage) {
			ContentOutlineConfiguration cfg = createContentOutlineConfiguration();
			if (cfg instanceof StructuredContentOutlineConfiguration) {
				((StructuredContentOutlineConfiguration) cfg).setEditor(this);
			}
			((StructuredTextEditorContentOutlinePage) fOutlinePage).setModel(getModel());
			((StructuredTextEditorContentOutlinePage) fOutlinePage).setViewerSelectionManager(getViewerSelectionManager());
			((StructuredTextEditorContentOutlinePage) fOutlinePage).setConfiguration(cfg);
		}
		if (fPropertySheetPage != null && fPropertySheetPage instanceof ConfigurablePropertySheetPage) {
			PropertySheetConfiguration cfg = createPropertySheetConfiguration();
			if (cfg instanceof StructuredPropertySheetConfiguration) {
				((StructuredPropertySheetConfiguration) cfg).setEditor(this);
			}
			((ConfigurablePropertySheetPage) fPropertySheetPage).setModel(getModel());
			((ConfigurablePropertySheetPage) fPropertySheetPage).setViewerSelectionManager(getViewerSelectionManager());
			((ConfigurablePropertySheetPage) fPropertySheetPage).setConfiguration(cfg);
		}
		if (getViewerSelectionManager() != null)
			getViewerSelectionManager().setModel(getModel());
		disposeModelDependentFields();

		fShowInTargetIds = createShowInTargetIds();

		// workaround for bugzilla#56801
		// updates/reinstalls QuickDiff because some QuickDiff info is lost
		// when SourceViewer.setModel/setDocument is called
		updateDiffer();
		// setSourceViewerConfiguration() was called once
		// in
		// StructuredTextMultiPageEditorPart.createSourcePage()
		// to set the SourceViewerConfiguration first so
		// the text editor won't
		// use the default configuration first
		// and switch to the
		// StructuredTextViewerConfiguration later.
		// However, the source viewer was not created yet
		// at the time. Need to
		// call setSourceViewerConfiguration() again here
		// so getSourceViewer().configure(configuration) in
		// setSourceViewerConfiguration() will be called.
		// DMW: removed setSouceViewerConfiguration since
		// shouldn't need the
		// general case
		// an longer, but added 'updateConfiguration' so it
		// can still be
		// sensitive
		// to resource/model changes.
		// setSourceViewerConfiguration();
		updateSourceViewerConfiguration();
		// (nsd) we never change it, so why null it out?
		//		else {
		//			super.setPreferenceStore(null);
		//		}
		createModelDependentFields();
		computeAndSetDoubleClickAction(getModel());
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
	 * Updates/reinstalls QuickDiff
	 */
	private void updateDiffer() {
		// workaround for bugzilla#56801
		// updates/reinstalls QuickDiff because some QuickDiff info is lost
		// when SourceViewer.setModel/setDocument is called

		if (isChangeInformationShowing()) {
			showChangeInformation(false);
			removeDiffer();
			showChangeInformation(true);
		}
	}

	protected void updateEncodingMemento() {
		boolean failed = false;
		IStructuredDocument doc = getModel().getStructuredDocument();
		EncodingMemento memento = doc.getEncodingMemento();
		IDocumentCharsetDetector detector = getModel().getModelHandler().getEncodingDetector();
		if (memento != null && detector != null)
			detector.set(doc);
		String enc = null;
		try {
			enc = detector.getEncoding();
		} catch (IOException e) {
			failed = true;
		}
		// be sure to use the new instance
		// but only if no exception occurred.
		// (we may find cases we need to do more error recover there)
		// should be near impossible to get IOException from processing the
		// *document*
		if (!failed) {
			doc.setEncodingMemento(memento);
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
				} else {
					ITextHover textHover = configuration.getTextHover(sourceViewer, t);
					((ITextViewerExtension2) sourceViewer).setTextHover(textHover, t, ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
				}
			} else
				sourceViewer.setTextHover(configuration.getTextHover(sourceViewer, t), t);
		}
	}


	protected void updateMenuText() {
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
				} else if (getEditorPart() != null && getEditorPart().getEditorSite().getActionBars() != null) {
					getEditorPart().getEditorSite().getActionBars().updateActionBars();
				}
			}
		}
	}

	protected void updateSourceViewerConfiguration() {
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		if ((configuration == null) || !(configuration instanceof StructuredTextViewerConfiguration)) {
			configuration = createSourceViewerConfiguration();
			setSourceViewerConfiguration(configuration);
		} else {
			StructuredTextViewerConfiguration newViewerConfiguration = createSourceViewerConfiguration();
			if (!((StructuredTextViewerConfiguration) configuration).getDeclaringID().equals(newViewerConfiguration.getDeclaringID())) {
				// d282894 use newViewerConfiguration
				configuration = newViewerConfiguration;
				setSourceViewerConfiguration(configuration);
			}
		}
		IResource resource = null;
		if (getEditorInput() instanceof IFileEditorInput) {
			resource = ((IFileEditorInput) getEditorInput()).getFile();
			if (resource.getType() != IResource.PROJECT)
				resource = resource.getProject();
			((StructuredTextViewerConfiguration) configuration).configureOn(resource);
		}
		if (getSourceViewer() != null) {
			getSourceViewer().configure(configuration);
			IAction contentAssistAction = getAction(StructuredTextEditorActionConstants.ACTION_NAME_CONTENTASSIST_PROPOSALS);
			if (contentAssistAction instanceof IUpdate) {
				((IUpdate) contentAssistAction).update();
			}
		}
		// eventually will replace above with something
		// like what follows
		// it, but some of our "processors" require too
		// much initialization
		// during configuration.
		//		SourceViewerConfiguration configuration =
		// getSourceViewerConfiguration();
		//
		//		// should always be an instance of our special
		// configuration, but
		// just in case
		//		// not, we'll do nothing if it isn't.
		//		if (configuration!= null && configuration
		// instanceof
		// StructuredTextViewerConfiguration) {
		//
		//			IResource resource = null;
		//			if (getEditorInput() instanceof
		// IFileEditorInput) {
		//				resource = ((IFileEditorInput)
		// getEditorInput()).getFile();
		//				if (resource.getType() != IResource.PROJECT)
		//					resource = resource.getProject();
		//					// note: configureOn is responsible for updating
		// what ever
		//					// in our configuration is sensitive to resource
		//				((StructuredTextViewerConfiguration)
		// configuration).configureOn(resource);
		//			}
		//
		//		}
	}

	public IStatus validateEdit(Shell context) {
		IStatus status = STATUS_OK;
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			if (input == null) {
				String msg = ResourceHandler.getString("Error_opening_file_UI_"); //$NON-NLS-1$
				status = new Status(IStatus.ERROR, EditorPlugin.ID, IStatus.INFO, msg, null);
			} else {
				validateState(input);
				sanityCheckState(input);
				if (isEditorInputReadOnly()) {
					String fname = input.getName();
					if (input instanceof IStorageEditorInput) {
						try {
							IStorage s = ((IStorageEditorInput) input).getStorage();
							if (s != null) {
								IPath path = s.getFullPath();
								if (path != null) {
									fname += path.toString();
								} else {
									fname += s.getName();
								}
							}
						} catch (CoreException e) { // IStorage is just for
							// file name,
							// and it's an optional,
							// therefore
							// it is safe to ignore this
							// exception.
						}
					}
					String msg = ResourceHandler.getString("_UI_File_is_read_only", new Object[]{fname}); //$NON-NLS-1$ = "File {0}is read-only."
					status = new Status(IStatus.ERROR, EditorPlugin.ID, IStatus.INFO, msg, null);
				}
			}
		}
		return status;
	}

	protected void validateState(IEditorInput input) {
		IDocumentProvider provider = getDocumentProvider();
		if (provider instanceof IDocumentProviderExtension) {
			IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;
			try {
				boolean wasReadOnly = isEditorInputReadOnly();
				extension.validateState(input, getSite().getShell());
				if (getSourceViewer() != null)
					getSourceViewer().setEditable(isEditable());
				if (wasReadOnly != isEditorInputReadOnly())
					updateStateDependentActions();
			} catch (CoreException x) {
				ILog log = Platform.getPlugin(PlatformUI.PLUGIN_ID).getLog();
				log.log(x.getStatus());
				statusError(x.getStatus());
			}
		}
	}
}
