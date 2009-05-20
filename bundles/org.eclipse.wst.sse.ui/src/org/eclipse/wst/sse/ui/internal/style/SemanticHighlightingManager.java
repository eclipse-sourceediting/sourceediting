/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.StructuredPresentationReconciler;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentRegionProcessor;

/**
 * Semantic highlighting manager. Responsible for maintaining the semantic highlightings
 * and the associated styles. Semantic highlighting preference changes are handled
 * through listeners in this class. Based on org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightingManager
 * 
 */
public class SemanticHighlightingManager {
	
	/**
	 * HighlightingStyle.
	 */
	static class HighlightingStyle {

		/** Text attribute */
		private TextAttribute fTextAttribute;
		/** Enabled state */
		private boolean fIsEnabled;

		/**
		 * Initialize with the given text attribute.
		 * @param textAttribute The text attribute
		 * @param isEnabled the enabled state
		 */
		public HighlightingStyle(TextAttribute textAttribute, boolean isEnabled) {
			setTextAttribute(textAttribute);
			setEnabled(isEnabled);
		}

		/**
		 * @return Returns the text attribute.
		 */
		public TextAttribute getTextAttribute() {
			return fTextAttribute;
		}

		/**
		 * @param textAttribute The background to set.
		 */
		public void setTextAttribute(TextAttribute textAttribute) {
			fTextAttribute = textAttribute;
		}

		/**
		 * @return the enabled state
		 */
		public boolean isEnabled() {
			return fIsEnabled;
		}

		/**
		 * @param isEnabled the new enabled state
		 */
		public void setEnabled(boolean isEnabled) {
			fIsEnabled = isEnabled;
		}
	}
	
	/**
	 * Highlighted Positions.
	 */
	static class HighlightedPosition extends Position {

		/** Highlighting of the position */
		private HighlightingStyle fStyle;

		private boolean fReadOnly;

		/** Lock object */
		private Object fLock;

		/**
		 * Initialize the styled positions with the given offset, length and foreground color.
		 *
		 * @param offset The position offset
		 * @param length The position length
		 * @param highlighting The position's highlighting
		 * @param lock The lock object
		 */
		public HighlightedPosition(int offset, int length, HighlightingStyle highlighting, Object lock, boolean isReadOnly) {
			super(offset, length);
			fStyle = highlighting;
			fLock = lock;
			fReadOnly = isReadOnly;
		}

		public HighlightedPosition(int offset, int length, HighlightingStyle highlighting, Object lock) {
			this(offset, length, highlighting, lock, false);
		}

		public HighlightedPosition(Position position, HighlightingStyle highlighting, Object lock) {
			this(position.offset, position.length, highlighting, lock, false);
		}

		public HighlightedPosition(Position position, HighlightingStyle highlighting, Object lock, boolean isReadOnly) {
			this(position.offset, position.length, highlighting, lock, isReadOnly);
		}

		/**
		 * @return Returns a corresponding style range.
		 */
		public StyleRange createStyleRange() {
			int len= 0;
			if (fStyle.isEnabled())
				len= getLength();

			TextAttribute textAttribute = fStyle.getTextAttribute();
			int style = textAttribute.getStyle();
			int fontStyle = style & (SWT.ITALIC | SWT.BOLD | SWT.NORMAL);
			StyleRange styleRange = new StyleRange(getOffset(), len, textAttribute.getForeground(), textAttribute.getBackground(), fontStyle);
			styleRange.strikeout = (style & TextAttribute.STRIKETHROUGH) != 0;
			styleRange.underline = (style & TextAttribute.UNDERLINE) != 0;

			return styleRange;
		}

		/**
		 * Uses reference equality for the highlighting.
		 *
		 * @param off The offset
		 * @param len The length
		 * @param highlighting The highlighting
		 * @return <code>true</code> iff the given offset, length and highlighting are equal to the internal ones.
		 */
		public boolean isEqual(int off, int len, HighlightingStyle highlighting) {
			synchronized (fLock) {
				return !isDeleted() && getOffset() == off && getLength() == len && fStyle == highlighting;
			}
		}
		
		/**
		 * Uses reference equality for the highlighting.
		 *
		 * @param pos The position
		 * @param highlighting The highlighting
		 * @return <code>true</code> iff the given offset, length and highlighting are equal to the internal ones.
		 */
		public boolean isEqual(Position pos, HighlightingStyle highlighting) {
			synchronized (fLock) {
				return !isDeleted() && getOffset() == pos.getOffset() && getLength() == pos.getLength() && fStyle == highlighting;
			}
		}

		/**
		 * Is this position contained in the given range (inclusive)? Synchronizes on position updater.
		 *
		 * @param off The range offset
		 * @param len The range length
		 * @return <code>true</code> iff this position is not delete and contained in the given range.
		 */
		public boolean isContained(int off, int len) {
			synchronized (fLock) {
				return !isDeleted() && off <= getOffset() && off + len >= getOffset() + getLength();
			}
		}

		public void update(int off, int len) {
			synchronized (fLock) {
				super.setOffset(off);
				super.setLength(len);
			}
		}

		/*
		 * @see org.eclipse.jface.text.Position#setLength(int)
		 */
		public void setLength(int length) {
			synchronized (fLock) {
				super.setLength(length);
			}
		}

		/*
		 * @see org.eclipse.jface.text.Position#setOffset(int)
		 */
		public void setOffset(int offset) {
			synchronized (fLock) {
				super.setOffset(offset);
			}
		}

		/*
		 * @see org.eclipse.jface.text.Position#delete()
		 */
		public void delete() {
			synchronized (fLock) {
				super.delete();
			}
		}

		/*
		 * @see org.eclipse.jface.text.Position#undelete()
		 */
		public void undelete() {
			synchronized (fLock) {
				super.undelete();
			}
		}

		/**
		 * @return Returns the highlighting.
		 */
		public HighlightingStyle getHighlighting() {
			return fStyle;
		}

		public boolean isReadOnly() {
			return fReadOnly;
		}
	}

	/**
	 * Highlighted ranges.
	 */
	public static class HighlightedRange extends Region {
		/** The highlighting key as returned by {@link ISemanticHighlighting#getPreferenceKey()}. */
		private String fKey;

		/**
		 * Initialize with the given offset, length and highlighting key.
		 *
		 * @param offset
		 * @param length
		 * @param key the highlighting key as returned by {@link ISemanticHighlighting#getPreferenceKey()}
		 */
		public HighlightedRange(int offset, int length, String key) {
			super(offset, length);
			fKey = key;
		}

		/**
		 * @return the highlighting key as returned by {@link ISemanticHighlighting#getPreferenceKey()}
		 */
		public String getKey() {
			return fKey;
		}

		/*
		 * @see org.eclipse.jface.text.Region#equals(java.lang.Object)
		 */
		public boolean equals(Object o) {
			return super.equals(o) && o instanceof HighlightedRange && fKey.equals(((HighlightedRange)o).getKey());
		}

		/*
		 * @see org.eclipse.jface.text.Region#hashCode()
		 */
		public int hashCode() {
			return super.hashCode() | fKey.hashCode();
		}
	}

	private StructuredTextEditor fEditor;
	private StructuredTextViewer fSourceViewer;
	private SourceViewerConfiguration fConfiguration;
	private StructuredPresentationReconciler fPresentationReconciler;
	
	private SemanticHighlightingPresenter fPresenter;
	private SemanticHighlightingReconciler fReconciler;
	
	public void install(StructuredTextEditor editor, StructuredTextViewer sourceViewer, IPreferenceStore preferenceStore, SourceViewerConfiguration configuration, String contentTypeId) {
		fEditor = editor;
		fSourceViewer = sourceViewer;
		fConfiguration = configuration;
//		fContentTypeId = contentTypeId;
		
		fPresentationReconciler = (StructuredPresentationReconciler) fConfiguration.getPresentationReconciler(fSourceViewer);
		
		if (isEnabled()) {
			enable();
		}
	}

	/**
	 * Load the semantic highlightings defined for this content type.
	 */
	private void loadSemanticHighlightings() {
	}

	/**
	 * Enable semantic highlighting.
	 */
	private void enable() {
		
		loadSemanticHighlightings();
		
		fPresenter = new SemanticHighlightingPresenter();
		fPresenter.install(fSourceViewer, fPresentationReconciler);
		if (fEditor != null) {
			fReconciler = new SemanticHighlightingReconciler();
			fReconciler.install(fEditor, fSourceViewer, fPresenter);
			IReconciler reconciler = fConfiguration.getReconciler(fSourceViewer);
			if (reconciler instanceof DocumentRegionProcessor)
				((DocumentRegionProcessor) reconciler).setSemanticHighlightingStrategy(fReconciler);
		} else {
//			fPresenter.updatePresentation(null, createHardcodedPositions(), new HighlightedPosition[0]);
		}
	}
	
	/**
	 * Disable semantic highlighting
	 */
	private void disable() {
		if (fReconciler != null) {
			fReconciler.uninstall();
			fReconciler = null;
		}
		
		if (fPresenter != null) {
			fPresenter.uninstall();
			fPresenter = null;
		}
		
	}
	
	/**
	 * @return <code>true</code> iff semantic highlighting is enabled in the preferences
	 */
	private boolean isEnabled() {
		return true;
	}
	
	public void uninstall() {
		disable();

		fEditor = null;
		fSourceViewer = null;
		fConfiguration = null;
		fPresentationReconciler = null;
	}

}
