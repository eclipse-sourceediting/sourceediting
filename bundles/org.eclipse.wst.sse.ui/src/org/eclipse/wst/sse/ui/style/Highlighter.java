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
package org.eclipse.wst.sse.ui.style;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.util.EditorUtility;


/**
 * This class is to directly mediate between the Structured Document data
 * structure and the text widget's text and events. It assumes there only the
 * model is interested in text events, and all other views will work from that
 * model. Changes to the text widgets input can cause changes in the model,
 * which in turn cause changes to the widget's display.
 *  
 */
public class Highlighter implements IHighlighter {

	private final boolean DEBUG = false;
	private final StyleRange[] EMPTY_STYLE_RANGE = new StyleRange[0];

	private IPropertyChangeListener fForegroundScaleListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (CommonEditorPreferenceNames.READ_ONLY_FOREGROUND_SCALE.equals(event.getProperty())) {
				IPreferenceStore editorStore = SSEUIPlugin.getDefault().getPreferenceStore();
				readOnlyForegroundScaleFactor = editorStore.getInt(CommonEditorPreferenceNames.READ_ONLY_FOREGROUND_SCALE);
				disposeColorTable();
				refreshDisplay();
			}
		}
	};
	private int fSavedLength = -1;

	private int fSavedOffset = -1;
	private StyleRange[] fSavedRanges = null;

	private IStructuredDocument fStructuredDocument;
	private HashMap fTableOfProviders;
	private ArrayList holdStyleResults;

	protected final LineStyleProvider NOOP_PROVIDER = new LineStyleProviderForNoOp();
	private double readOnlyBackgroundScaleFactor = 10;
	private Hashtable readOnlyColorTable;
	double readOnlyForegroundScaleFactor = 30;

	private YUV_RGBConverter rgbConverter;
	private ITextViewer textViewer;
	private StyledText textWidget;

	public Highlighter() {
		super();
	}

	protected void addEmptyRange(int start, int length, Collection holdResults) {
		StyleRange result = new StyleRange();
		result.start = start;
		result.length = length;
		holdResults.add(result);
	}

	public void addProvider(String partitionType, LineStyleProvider provider) {
		getTableOfProviders().put(partitionType, provider);
	}

	protected void adjust(StyleRange[] ranges, int adjustment) {
		for (int i = 0; i < ranges.length; i++) {
			ranges[i].start += adjustment;
		}
	}

	// never used
	void adjustBackground(StyleRange styleRange) {

		RGB oldRGB = null;
		Color oldColor = styleRange.background;
		if (oldColor == null) {
			oldColor = getTextWidget().getBackground();
		}
		oldRGB = oldColor.getRGB();
		Color newColor = getCachedColorFor(oldRGB);
		if (newColor == null) {
			double target = getRGBConverter().calculateYComponent(oldColor);
			// if background is "light" make it darker, and vice versa
			if (target < 0.5)
				target = 1.0;
			else
				target = 0.0;
			RGB newRGB = getRGBConverter().transformRGB(oldRGB, readOnlyBackgroundScaleFactor / 100.0, target);

			cacheColor(oldRGB, newRGB);
			newColor = getCachedColorFor(oldRGB);
		}
		styleRange.background = newColor;
	}

	private void adjustForground(StyleRange styleRange) {
		RGB oldRGB = null;
		//Color oldColor = styleRange.foreground;
		Color oldColor = styleRange.background;
		if (oldColor == null) {
			//oldRGB = getTextWidget().getForeground().getRGB();
			oldColor = getTextWidget().getBackground();
			oldRGB = oldColor.getRGB();
		} else {
			oldRGB = oldColor.getRGB();
		}
		Color newColor = getCachedColorFor(oldRGB);
		if (newColor == null) {
			// make text "closer to" background lumanence
			double target = getRGBConverter().calculateYComponent(oldColor);
			RGB newRGB = getRGBConverter().transformRGBToGrey(oldRGB, readOnlyForegroundScaleFactor / 100.0, target);

			// save conversion, so calculations only need to be done once
			cacheColor(oldRGB, newRGB);
			newColor = getCachedColorFor(oldRGB);
		}
		styleRange.foreground = newColor;
	}

	/**
	 * Cache read-only color.
	 * 
	 * @param oldRGB
	 * @param newColor
	 */
	private void cacheColor(RGB oldRGB, RGB newColor) {
		if (readOnlyColorTable == null) {
			readOnlyColorTable = new Hashtable();
		}
		readOnlyColorTable.put(oldRGB, newColor);
	}

	/**
	 * @param result
	 * @return
	 */
	private StyleRange[] convertReadOnlyRegions(StyleRange[] result, int start, int length) {
		IStructuredDocument structuredDocument = getDocument();

		// for client/provider simplicity (and consisten look and feel)
		// we'll handle readonly regions in one spot, here in highlighter.
		// though I suspect may have to be more sophisticated later.
		// For example, it a fair assumption that each readonly region
		// be on an ITextRegion boundry, but we do combine consequtive
		// styles, when found to be equivilent.
		// Plus, for now, we'll just adjust background. Eventually
		// will us a "dimming" algrorightm. to adjust color's
		// satuation/brightness
		if (structuredDocument.containsReadOnly(start, length)) {
			// something is read-only in the line, so go through each style,
			// and adjust
			for (int i = 0; i < result.length; i++) {
				StyleRange styleRange = result[i];
				if (structuredDocument.containsReadOnly(styleRange.start, styleRange.length)) {
					// should do background first. Its used by forground
					//adjustBackground(styleRange);
					adjustForground(styleRange);
				}
			}
		}

		return result;
	}

	/**
	 * Clear out the readOnlyColorTable
	 */
	void disposeColorTable() {
		if (readOnlyColorTable != null) {
			readOnlyColorTable.clear();
		}
		readOnlyColorTable = null;
	}

	/**
	 * This method is just to get existing read-only colors.
	 */
	private Color getCachedColorFor(RGB oldRGB) {
		Color result = null;

		if (readOnlyColorTable != null) {
			RGB readOnlyRGB = (RGB) readOnlyColorTable.get(oldRGB);
			result = EditorUtility.getColor(readOnlyRGB);
		}

		return result;
	}

	// TODO: never used
	 Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

	protected IStructuredDocument getDocument() {

		return fStructuredDocument;
	}

	/**
	 * Method getProviderFor.
	 * 
	 * @param typedRegion
	 * @return LineStyleProvider
	 */
	private LineStyleProvider getProviderFor(ITypedRegion typedRegion) {
		String type = typedRegion.getType();
		LineStyleProvider result = (LineStyleProvider) fTableOfProviders.get(type);
		if (result == null) {
			result = NOOP_PROVIDER;
		}

		return result;
	}

	private YUV_RGBConverter getRGBConverter() {
		if (rgbConverter == null) {
			rgbConverter = new YUV_RGBConverter();
		}
		return rgbConverter;
	}

	private Map getTableOfProviders() {
		if (fTableOfProviders == null) {
			fTableOfProviders = new HashMap();
		}
		return fTableOfProviders;
	}

	/**
	 * Returns the textViewer.
	 * 
	 * @return ITextViewer
	 */
	public ITextViewer getTextViewer() {
		return textViewer;
	}

	/**
	 * @return
	 */
	protected StyledText getTextWidget() {
		return textWidget;
	}

	public void install(ITextViewer newTextViewer) {
		this.textViewer = newTextViewer;

		IPreferenceStore editorStore = SSEUIPlugin.getDefault().getPreferenceStore();
		editorStore.addPropertyChangeListener(fForegroundScaleListener);
		readOnlyForegroundScaleFactor = editorStore.getInt(CommonEditorPreferenceNames.READ_ONLY_FOREGROUND_SCALE);

		if (textWidget != null) {
			textWidget.removeLineStyleListener(this);
		}
		textWidget = newTextViewer.getTextWidget();
		if (textWidget != null) {
			textWidget.addLineStyleListener(this);
		}

		refreshDisplay();
	}

	public StyleRange[] lineGetStyle(int eventLineOffset, int eventLineLength) {
		StyleRange[] eventStyles = EMPTY_STYLE_RANGE;
		try {
			if (getDocument() == null) {

				// during initialization, this is sometimes called before our
				// structured
				// is set, in which case we set styles to be the empty style
				// range
				// (event.styles can not be null)
				eventStyles = EMPTY_STYLE_RANGE;
				return eventStyles;
			}
			int start = eventLineOffset;
			int length = eventLineLength;
			int end = start + length - 1;

			// we sometimes get odd requests from the very last CRLF in the
			// document
			// it has no length, and there is no node for it!
			if (length == 0) {
				eventStyles = EMPTY_STYLE_RANGE;
			} else {
				IRegion vr = null;
				if (getTextViewer() != null) {
					vr = getTextViewer().getVisibleRegion();
				} else {
					vr = new Region(0, getDocument().getLength());
				}
				if (start > vr.getLength()) {
					eventStyles = EMPTY_STYLE_RANGE;
				} else {
					// Determine if we're highlighting a visual portion of the
					// model not
					// starting at zero. If so, adjust the location from which
					// we retrieve
					// the style information
					if (vr.getOffset() > 0) {
						start += vr.getOffset();
						end += vr.getOffset();
					}
					//					// ================
					//					if (start == fSavedOffset && length == fSavedLength &&
					// fSavedRanges != null) {
					//						eventStyles = (StyleRange[]) fSavedRanges;
					//					} else {

					ITypedRegion[] partitions = getDocument().getDocumentPartitioner().computePartitioning(start, length);
					eventStyles = prepareStyleRangesArray(partitions, start, length);

					// If there is a subtext offset, the style ranges must be
					// adjusted to the expected
					// offsets
					if (vr.getOffset() > 0)
						adjust(eventStyles, -vr.getOffset());

					//						fSavedOffset = start;
					//						fSavedLength = length;
					//						fSavedRanges = (StyleRange[]) eventStyles;

					// for debugging only
					if (DEBUG) {
						if (!valid(eventStyles, eventLineOffset, eventLineLength)) {
							Logger.log(Logger.WARNING, "Highlighter::lineGetStyle found invalid styles at offset " + eventLineOffset); //$NON-NLS-1$
						}
					}
					//					}
				}
			}
		} catch (Exception e) {
			// if ANY exception occurs during highlighting,
			// just return "no highlighting"
			eventStyles = EMPTY_STYLE_RANGE;
			if (Debug.syntaxHighlighting) {
				System.out.println("Exception during highlighting!"); //$NON-NLS-1$
			}
		}
		return eventStyles;
	}

	/**
	 * A passthrough method that extracts relevant data from the
	 * LineStyleEvent and passes it along. This method was separated for
	 * performance testing purposes.
	 * 
	 * @see org.eclipse.swt.custom.LineStyleListener#lineGetStyle(LineStyleEvent)
	 */
	public void lineGetStyle(LineStyleEvent event) {
		int offset = event.lineOffset;
		int length = event.lineText.length();

		//		// for some reason, we are sometimes asked for the same style range
		// over and
		//		// over again. This was found to happen during 'revert' of a file
		// with one
		//		// line in it that is 40K long! So, while I don't know root cause,
		// caching
		//		// the styled ranges in case the exact same request is made
		// multiple times
		//		// seems like cheap insurance.
		if (offset == fSavedOffset && length == fSavedLength && fSavedRanges != null) {
			event.styles = fSavedRanges;
		} else {
			// need to assign this array here, or else the field won't get
			// updated
			event.styles = lineGetStyle(offset, length);
			// now saved "cached data" for repeated requests which are exaclty
			// same
			fSavedOffset = offset;
			fSavedLength = length;
			fSavedRanges = event.styles;
		}
	}

	/**
	 * Note: its very important this method never return null, which is why
	 * the final null check is in a finally clause
	 */

	protected StyleRange[] prepareStyleRangesArray(ITypedRegion[] partitions, int start, int length) {

		StyleRange[] result = EMPTY_STYLE_RANGE;

		if (holdStyleResults == null) {
			holdStyleResults = new ArrayList(20);
		} else {
			holdStyleResults.clear();
		}

		// to do: make some of these instance variables to prevent creation on
		// stack
		LineStyleProvider attributeProvider = null;
		boolean handled = false;
		for (int i = 0; i < partitions.length; i++) {
			ITypedRegion typedRegion = partitions[i];
			attributeProvider = getProviderFor(typedRegion);

			// //REMINDER: eventually need to remove this one, and use only
			// structuredDocument
			//attributeProvider.init(getModel(), this);
			attributeProvider.init(getDocument(), this);

			//				handled = attributeProvider.prepareRegions(typedRegion, start,
			// length, holdStyleResults);
			handled = attributeProvider.prepareRegions(typedRegion, typedRegion.getOffset(), typedRegion.getLength(), holdStyleResults);
			if (Debug.syntaxHighlighting && !handled) {
				System.out.println("Did not handle highlighting in Highlighter inner while"); //$NON-NLS-1$
			}
		}

		int resultSize = holdStyleResults.size();
		if (resultSize == 0) {
			result = EMPTY_STYLE_RANGE;
		} else {
			result = new StyleRange[resultSize];
			holdStyleResults.trimToSize();
			System.arraycopy(holdStyleResults.toArray(), 0, result, 0, resultSize);
		}
		result = convertReadOnlyRegions(result, start, length);
		return result;
	}

	public void refreshDisplay() {
		if (textWidget != null && !textWidget.isDisposed())
			textWidget.redraw();
	}

	/**
	 */
	public void refreshDisplay(int start, int length) {
		if (textWidget != null && !textWidget.isDisposed())
			textWidget.redrawRange(start, length, true);
	}

	public void removeProvider(String partitionType) {
		getTableOfProviders().remove(partitionType);
	}

	public void setDocument(IStructuredDocument structuredDocument) {
		fStructuredDocument = structuredDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.style.IHighlighter#uninstall()
	 */
	public void uninstall() {
		if (textWidget != null && !textWidget.isDisposed()) {
			textWidget.removeLineStyleListener(this);
		}
		textWidget = null;

		Collection providers = getTableOfProviders().values();
		Iterator iterator = providers.iterator();
		while (iterator.hasNext()) {
			LineStyleProvider lineStyleProvider = (LineStyleProvider) iterator.next();
			lineStyleProvider.release();
			// this remove probably isn't strictly needed, since
			// typically highlighter instance as a whole will go
			// away ... but in case that ever changes, this seems like
			// a better style.
			iterator.remove();
		}

		IPreferenceStore editorStore = SSEUIPlugin.getDefault().getPreferenceStore();
		editorStore.removePropertyChangeListener(fForegroundScaleListener);
		disposeColorTable();

		// clear out cached variables (d282894)
		fSavedOffset = -1;
		fSavedLength = -1;
		fSavedRanges = null;
	}

	/**
	 * Purely a debugging aide.
	 */
	private boolean valid(StyleRange[] eventStyles, int startOffset, int lineLength) {
		boolean result = false;
		if (eventStyles != null) {
			if (eventStyles.length > 0) {
				StyleRange first = eventStyles[0];
				StyleRange last = eventStyles[eventStyles.length - 1];
				if (startOffset > first.start) {
					result = false;
				} else {
					int lineEndOffset = startOffset + lineLength;
					int lastOffset = last.start + last.length;
					if (lastOffset > lineEndOffset) {
						result = false;
					} else {
						result = true;
					}
				}
			} else {
				// a zero length array is ok
				result = true;
			}
		}
		return result;
	}
}
