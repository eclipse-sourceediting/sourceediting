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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.preferences.EditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.StructuredPresentationReconciler;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentRegionProcessor;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;

/**
 * Semantic highlighting manager. Responsible for maintaining the semantic highlightings
 * and the associated styles. Semantic highlighting preference changes are handled
 * through listeners in this class. Based on org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightingManager
 * 
 * @since 3.1
 */
public class SemanticHighlightingManager implements IPropertyChangeListener {
	
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
	
	private static final String SEMANTIC_HIGHLIGHTING_EXTENSION_POINT = "semanticHighlighting"; //$NON-NLS-1$
	private static final String TARGET_ATTR = "target";
	private static final String CLASS_ATTR = "class";

	private StructuredTextEditor fEditor;
	private StructuredTextViewer fSourceViewer;
	private IPreferenceStore fPreferenceStore;
	private SourceViewerConfiguration fConfiguration;
	private StructuredPresentationReconciler fPresentationReconciler;
	private String fContentTypeId;
	
	private SemanticHighlightingPresenter fPresenter;
	private SemanticHighlightingReconciler fReconciler;
	
	/** The semantic highlightings for the content type */
	private ISemanticHighlighting[] fHighlightings;
	/** The semantic highlighting styles associated with the semantic highlightings */
	private HighlightingStyle[] fHighlightingStyles;
	
	private IPropertyChangeListener fHighlightingChangeListener = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
				handleHighlightingPropertyChange(event);
		}
	};
	
	public void propertyChange(PropertyChangeEvent event) {
		handlePropertyChange(event);
	}
	
	public void install(StructuredTextEditor editor, StructuredTextViewer sourceViewer, IPreferenceStore preferenceStore, SourceViewerConfiguration configuration, String contentTypeId) {
		fEditor = editor;
		fSourceViewer = sourceViewer;
		fPreferenceStore = preferenceStore;
		fConfiguration = configuration;
		fContentTypeId = contentTypeId;
		
		fPreferenceStore.addPropertyChangeListener(this);
		fPresentationReconciler = (StructuredPresentationReconciler) fConfiguration.getPresentationReconciler(fSourceViewer);
		
		if (isEnabled()) {
			enable();
		}
	}
	
	/**
	 * Load the semantic highlightings defined for this content type.
	 */
	private void loadSemanticHighlightings() {
		List semantics = new ArrayList(0);
		
		ISemanticHighlighting highlighting = null;
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(SSEUIPlugin.ID, SEMANTIC_HIGHLIGHTING_EXTENSION_POINT);
		
		IContentType contentType = Platform.getContentTypeManager().getContentType(fContentTypeId);
		
		for (int i = 0; i < elements.length; i++) {
			String[] targets = StringUtils.unpack(elements[i].getAttribute(TARGET_ATTR));
			for (int j = 0; j < targets.length; j++) {
				IContentType targetContentType = Platform.getContentTypeManager().getContentType(targets[j]);
				/* Apply semantic highlighting to kinds of targetContentType */
				if (contentType.isKindOf(targetContentType)) {
					try {
						highlighting = (ISemanticHighlighting) elements[i].createExecutableExtension(CLASS_ATTR);
					} catch (CoreException e) {
						Logger.logException(e);
					}
					if (highlighting != null)
						semantics.add(new SemanticContent(targetContentType, highlighting));

					break;
				}
			}
		}
		/* Sort the semantics, so that styles will be applied from general to specific */
		Collections.sort(semantics);
		fHighlightings = new ISemanticHighlighting[semantics.size()];
		fHighlightingStyles = new HighlightingStyle[semantics.size()];
		
		for (int i = 0; i < semantics.size(); i++) {
			fHighlightings[i] = ((SemanticContent) semantics.get(i)).getHighlighting();
			fHighlightingStyles[i] = createHighlightingStyle(((SemanticContent) semantics.get(i)).getHighlighting());
		}
	}
	
	/**
	 * This class is used for relating a semantic highlighting to a content type.
	 * The content type is used in the sorting algorithm. Content types that are more specific
	 * to the file's content type will cause styles to be applied from general to specific
	 */
	private class SemanticContent implements Comparable {
		public IContentType type;
		public ISemanticHighlighting highlighting;
		
		public SemanticContent(IContentType type, ISemanticHighlighting highlighting) {
			this.type = type;
			this.highlighting = highlighting;
		}

		public int compareTo(Object arg0) {
			SemanticContent other = (SemanticContent) arg0;
			/* Equal weighting for the same types */
			if (this.type.equals(other.type))
				return 0;
			/* Subtypes have more weight than base types */
			if (this.type.isKindOf(other.type))
				return 1;
			return -1;
		}
		
		public ISemanticHighlighting getHighlighting() {
			return highlighting;
		}
	}
	
	/**
	 * Creates a highlighting style based on the preferences defined in the semantic highlighting
	 * @param highlighting the semantic highlighting
	 * @return a highlighting style based on the preferences of the semantic highlighting
	 */
	private HighlightingStyle createHighlightingStyle(ISemanticHighlighting highlighting) {
		IPreferenceStore store = highlighting.getPreferenceStore();
		HighlightingStyle highlightingStyle = null;
		if (store != null) {
			int style = getBoolean(store, highlighting.getBoldPreferenceKey()) ? SWT.BOLD : SWT.NORMAL;
			
			if (getBoolean(store, highlighting.getItalicPreferenceKey()))
				style |= SWT.ITALIC;
			if (getBoolean(store, highlighting.getStrikethroughPreferenceKey()))
				style |= TextAttribute.STRIKETHROUGH;
			if (getBoolean(store, highlighting.getUnderlinePreferenceKey()))
				style |= TextAttribute.UNDERLINE;
			store.addPropertyChangeListener(fHighlightingChangeListener);
			boolean isEnabled = getBoolean(store, highlighting.getEnabledPreferenceKey());
			String rgbString = getString(store, highlighting.getColorPreferenceKey());
			Color color = null;
			
			if (rgbString != null)
				color = EditorUtility.getColor(ColorHelper.toRGB(rgbString));
			
			highlightingStyle = new HighlightingStyle(new TextAttribute(color, null, style), isEnabled);
		}
		return highlightingStyle;
	}
	
	/**
	 * Looks up a boolean preference by <code>key</code> from the preference store
	 * @param store the preference store to lookup the preference from
	 * @param key the key the preference is stored under
	 * @return the preference value from the preference store iff key is not null
	 */
	private boolean getBoolean(IPreferenceStore store, String key) {
		return (key == null) ? false : store.getBoolean(key);
	}
	
	/**
	 * Looks up a String preference by <code>key</code> from the preference store
	 * @param store the preference store to lookup the preference from
	 * @param key the key the preference is stored under
	 * @return the preference value from the preference store iff key is not null
	 */
	private String getString(IPreferenceStore store, String key) {
		return (key == null) ? null : store.getString(key);
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
			fReconciler.install(fEditor, fSourceViewer, fPresenter, fHighlightings, fHighlightingStyles);
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
		
		if (fHighlightings != null)
			disposeHighlightings();
	}
	
	private void disposeHighlightings() {
		/* Remove the property change listener before clearing the lists */
		if (fHighlightings != null) {
			for (int i = 0; i < fHighlightings.length; i++) {
				IPreferenceStore store = fHighlightings[i].getPreferenceStore();
				if (store != null)
					store.removePropertyChangeListener(fHighlightingChangeListener);
			}
		}
		
		fHighlightings = null;
		fHighlightingStyles = null;
	}
	
	/**
	 * Handles property change events for individual semantic highlightings.
	 * @param event
	 */
	private void handleHighlightingPropertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property == null)
			return;
		
		boolean refreshRequired = false;
		
		for (int i = 0; i < fHighlightings.length; i++) {
			ISemanticHighlighting highlighting = fHighlightings[i];
			
			if (property.equals(highlighting.getBoldPreferenceKey())) {
				adaptToTextStyleChange(fHighlightingStyles[i], event, SWT.BOLD);
				fPresenter.highlightingStyleChanged(fHighlightingStyles[i]);
				refreshRequired = true;
				continue;
			}
			
			if (property.equals(highlighting.getColorPreferenceKey())) {
				adaptToTextForegroundChange(fHighlightingStyles[i], event);
				fPresenter.highlightingStyleChanged(fHighlightingStyles[i]);
				refreshRequired = true;
				continue;
			}
			
			if (property.equals(highlighting.getEnabledPreferenceKey())) {
				adaptToEnablementChange(fHighlightingStyles[i], event);
				fPresenter.highlightingStyleChanged(fHighlightingStyles[i]);
				refreshRequired = true;
				continue;
			}
			
			if (property.equals(highlighting.getItalicPreferenceKey())) {
				adaptToTextStyleChange(fHighlightingStyles[i], event, SWT.ITALIC);
				fPresenter.highlightingStyleChanged(fHighlightingStyles[i]);
				refreshRequired = true;
				continue;
			}
			
			if (property.equals(highlighting.getStrikethroughPreferenceKey())) {
				adaptToTextStyleChange(fHighlightingStyles[i], event, TextAttribute.STRIKETHROUGH);
				fPresenter.highlightingStyleChanged(fHighlightingStyles[i]);
				refreshRequired = true;
				continue;
			}
			
			if (property.equals(highlighting.getUnderlinePreferenceKey())) {
				adaptToTextStyleChange(fHighlightingStyles[i], event, TextAttribute.UNDERLINE);
				fPresenter.highlightingStyleChanged(fHighlightingStyles[i]);
				refreshRequired = true;
				continue;
			}
		}
		
		if (refreshRequired && fReconciler != null)
			fReconciler.refresh();
	}
	
	/**
	 * Handles property changes for enabling and disabling semantic highlighting for
	 * Structured Source Editors
	 * @param event
	 */
	private void handlePropertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property == null || !property.equals(EditorPreferenceNames.SEMANTIC_HIGHLIGHTING))
			return;
		
		Object newValue = event.getNewValue();
		if (newValue instanceof Boolean) {
			if (((Boolean) newValue).booleanValue())
				enable();
			else
				disable();
		}
		
		if (fReconciler != null)
			fReconciler.refresh();
	}
	
	private void adaptToEnablementChange(HighlightingStyle highlighting, PropertyChangeEvent event) {
		Object value = event.getNewValue();
		boolean eventValue;
		if (value instanceof Boolean)
			eventValue = ((Boolean) value).booleanValue();
		else if (IPreferenceStore.TRUE.equals(value))
			eventValue = true;
		else
			eventValue = false;
		highlighting.setEnabled(eventValue);
	}

	private void adaptToTextForegroundChange(HighlightingStyle highlighting, PropertyChangeEvent event) {
		RGB rgb = null;

		Object value = event.getNewValue();
		if (value instanceof RGB)
			rgb= (RGB) value;
		else if (value instanceof String)
			rgb= ColorHelper.toRGB( (String) value);

		if (rgb != null) {
			Color color= EditorUtility.getColor(rgb);
			TextAttribute oldAttr= highlighting.getTextAttribute();
			highlighting.setTextAttribute(new TextAttribute(color, oldAttr.getBackground(), oldAttr.getStyle()));
		}
	}
	
	private void adaptToTextStyleChange(HighlightingStyle highlighting, PropertyChangeEvent event, int styleAttribute) {
		boolean eventValue = false;
		Object value = event.getNewValue();
		if (value instanceof Boolean)
			eventValue = ((Boolean) value).booleanValue();
		else if (IPreferenceStore.TRUE.equals(value))
			eventValue = true;

		TextAttribute oldAttr = highlighting.getTextAttribute();
		boolean activeValue = (oldAttr.getStyle() & styleAttribute) == styleAttribute;

		if (activeValue != eventValue)
			highlighting.setTextAttribute(new TextAttribute(oldAttr.getForeground(), oldAttr.getBackground(), eventValue ? oldAttr.getStyle() | styleAttribute : oldAttr.getStyle() & ~styleAttribute));
	}
	
	/**
	 * @return <code>true</code> iff semantic highlighting is enabled in the preferences
	 */
	private boolean isEnabled() {
		return (fPreferenceStore != null) ? fPreferenceStore.getBoolean(EditorPreferenceNames.SEMANTIC_HIGHLIGHTING) : false;
	}
	
	public void uninstall() {
		disable();
		
		if (fPreferenceStore != null) {
			fPreferenceStore.removePropertyChangeListener(this);
			fPreferenceStore = null;
		}
		
		fEditor = null;
		fSourceViewer = null;
		fConfiguration = null;
		fPresentationReconciler = null;
	}

}
