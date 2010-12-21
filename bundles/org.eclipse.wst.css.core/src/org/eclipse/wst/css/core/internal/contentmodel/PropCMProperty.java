/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.contentmodel;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class PropCMProperty extends PropCMContainer {

	// static fields
	private static java.util.Hashtable instances = null;
	private static java.util.Hashtable cachedIdMap = null;
	public final static String P_AZIMUTH = "azimuth";//$NON-NLS-1$
	public final static String P_BG = "background";//$NON-NLS-1$
	public final static String P_BG_ATTACHMENT = "background-attachment";//$NON-NLS-1$
	public final static String P_BG_CLIP = "background-clip"; //$NON-NLS-1$
	public final static String P_BG_COLOR = "background-color";//$NON-NLS-1$
	public final static String P_BG_IMAGE = "background-image";//$NON-NLS-1$
	public final static String P_BG_ORIGIN = "background-origin"; //$NON-NLS-1$
	public final static String P_BG_POSITION = "background-position";//$NON-NLS-1$
	public final static String P_BG_REPEAT = "background-repeat";//$NON-NLS-1$
	public final static String P_BG_SIZE = "background-size"; //$NON-NLS-1$
	public final static String P_BORDER = "border";//$NON-NLS-1$
	public final static String P_BORDER_COLLAPSE = "border-collapse";//$NON-NLS-1$
	public final static String P_BORDER_COLOR = "border-color";//$NON-NLS-1$
	public final static String P_BORDER_SPACING = "border-spacing";//$NON-NLS-1$
	public final static String P_BORDER_STYLE = "border-style";//$NON-NLS-1$
	public final static String P_BORDER_TOP = "border-top";//$NON-NLS-1$
	public final static String P_BORDER_RIGHT = "border-right";//$NON-NLS-1$
	public final static String P_BORDER_BOTTOM = "border-bottom";//$NON-NLS-1$
	public final static String P_BORDER_LEFT = "border-left";//$NON-NLS-1$
	public final static String P_BORDER_TOP_COLOR = "border-top-color";//$NON-NLS-1$
	public final static String P_BORDER_RIGHT_COLOR = "border-right-color";//$NON-NLS-1$
	public final static String P_BORDER_BOTTOM_COLOR = "border-bottom-color";//$NON-NLS-1$
	public final static String P_BORDER_LEFT_COLOR = "border-left-color";//$NON-NLS-1$
	public final static String P_BORDER_TOP_STYLE = "border-top-style";//$NON-NLS-1$
	public final static String P_BORDER_RIGHT_STYLE = "border-right-style";//$NON-NLS-1$
	public final static String P_BORDER_BOTTOM_STYLE = "border-bottom-style";//$NON-NLS-1$
	public final static String P_BORDER_LEFT_STYLE = "border-left-style";//$NON-NLS-1$
	public final static String P_BORDER_TOP_WIDTH = "border-top-width";//$NON-NLS-1$
	public final static String P_BORDER_RIGHT_WIDTH = "border-right-width";//$NON-NLS-1$
	public final static String P_BORDER_BOTTOM_WIDTH = "border-bottom-width";//$NON-NLS-1$
	public final static String P_BORDER_LEFT_WIDTH = "border-left-width";//$NON-NLS-1$
	public final static String P_BORDER_WIDTH = "border-width";//$NON-NLS-1$
	public final static String P_BORDER_RADIUS = "border-radius"; //$NON-NLS-1$
	public final static String P_BORDER_TOP_LEFT_RADIUS = "border-top-left-radius"; //$NON-NLS-1$
	public final static String P_BORDER_TOP_RIGHT_RADIUS = "border-top-right-radius"; //$NON-NLS-1$
	public final static String P_BORDER_BOTTOM_LEFT_RADIUS = "border-bottom-left-radius"; //$NON-NLS-1$
	public final static String P_BORDER_BOTTOM_RIGHT_RADIUS = "border-bottom-right-radius"; //$NON-NLS-1$
	public final static String P_BOTTOM = "bottom";//$NON-NLS-1$
	public final static String P_BOX_SHADOW = "box-shadow"; //$NON-NLS-1$
	public final static String P_BOX_SIZING = "box-sizing"; //$NON-NLS-1$
	public final static String P_CAPTION_SIDE = "caption-side";//$NON-NLS-1$
	public final static String P_CLEAR = "clear";//$NON-NLS-1$
	public final static String P_CLIP = "clip";//$NON-NLS-1$
	public final static String P_COLOR = "color";//$NON-NLS-1$
	public final static String P_COLUMNS = "columns";//$NON-NLS-1$
	public final static String P_COLUMN_COUNT = "column-count";//$NON-NLS-1$
	public final static String P_COLUMN_FILL = "column-fill";//$NON-NLS-1$
	public final static String P_COLUMN_GAP = "column-gap";//$NON-NLS-1$
	public final static String P_COLUMN_RULE = "column-rule";//$NON-NLS-1$
	public final static String P_COLUMN_RULE_COLOR = "column-rule-color";//$NON-NLS-1$
	public final static String P_COLUMN_RULE_STYLE = "column-rule-styler";//$NON-NLS-1$
	public final static String P_COLUMN_RULE_WIDTH = "column-rule-width";//$NON-NLS-1$
	public final static String P_COLUMN_SPAN = "column-span";//$NON-NLS-1$
	public final static String P_COLUMN_WIDTH = "column-width";//$NON-NLS-1$
	public final static String P_CONTENT = "content";//$NON-NLS-1$
	public final static String P_COUNTER_INCREMENT = "counter-increment";//$NON-NLS-1$
	public final static String P_COUNTER_RESET = "counter-reset";//$NON-NLS-1$
	public final static String P_CUE = "cue";//$NON-NLS-1$
	public final static String P_CUE_AFTER = "cue-after";//$NON-NLS-1$
	public final static String P_CUE_BEFORE = "cue-before";//$NON-NLS-1$
	public final static String P_CURSOR = "cursor";//$NON-NLS-1$
	public final static String P_DIRECTION = "direction";//$NON-NLS-1$
	public final static String P_DISPLAY = "display";//$NON-NLS-1$
	public final static String P_ELEVATION = "elevation";//$NON-NLS-1$
	public final static String P_EMPTY_CELLS = "empty-cells";//$NON-NLS-1$
	public final static String P_FLOAT = "float";//$NON-NLS-1$
	public final static String P_FONT = "font";//$NON-NLS-1$
	public final static String P_FONT_FAMILY = "font-family";//$NON-NLS-1$
	public final static String P_FONT_SIZE = "font-size";//$NON-NLS-1$
	public final static String P_FONT_SIZE_ADJUST = "font-size-adjust";//$NON-NLS-1$
	public final static String P_FONT_STRETCH = "font-stretch";//$NON-NLS-1$
	public final static String P_FONT_STYLE = "font-style";//$NON-NLS-1$
	public final static String P_FONT_VARIANT = "font-variant";//$NON-NLS-1$
	public final static String P_FONT_WEIGHT = "font-weight";//$NON-NLS-1$
	public final static String P_HEIGHT = "height";//$NON-NLS-1$
	public final static String P_LEFT = "left";//$NON-NLS-1$
	public final static String P_LETTER_SPACING = "letter-spacing";//$NON-NLS-1$
	public final static String P_LINE_HEIGHT = "line-height";//$NON-NLS-1$
	public final static String P_LIST_STYLE = "list-style";//$NON-NLS-1$
	public final static String P_LIST_STYLE_IMAGE = "list-style-image";//$NON-NLS-1$
	public final static String P_LIST_STYLE_POSITION = "list-style-position";//$NON-NLS-1$
	public final static String P_LIST_STYLE_TYPE = "list-style-type";//$NON-NLS-1$
	public final static String P_MARGIN = "margin";//$NON-NLS-1$
	public final static String P_MARGIN_TOP = "margin-top";//$NON-NLS-1$
	public final static String P_MARGIN_RIGHT = "margin-right";//$NON-NLS-1$
	public final static String P_MARGIN_BOTTOM = "margin-bottom";//$NON-NLS-1$
	public final static String P_MARGIN_LEFT = "margin-left";//$NON-NLS-1$
	public final static String P_MARKER_OFFSET = "marker-offset";//$NON-NLS-1$
	public final static String P_MARKS = "marks";//$NON-NLS-1$
	public final static String P_MAX_HEIGHT = "max-height";//$NON-NLS-1$
	public final static String P_MAX_WIDTH = "max-width";//$NON-NLS-1$
	public final static String P_MIN_HEIGHT = "min-height";//$NON-NLS-1$
	public final static String P_MIN_WIDTH = "min-width";//$NON-NLS-1$
	public final static String P_OPACITY = "opacity"; //$NON-NLS-1$
	public final static String P_ORPHANS = "orphans";//$NON-NLS-1$
	public final static String P_OUTLINE = "outline";//$NON-NLS-1$
	public final static String P_OUTLINE_COLOR = "outline-color";//$NON-NLS-1$
	public final static String P_OUTLINE_STYLE = "outline-style";//$NON-NLS-1$
	public final static String P_OUTLINE_WIDTH = "outline-width";//$NON-NLS-1$
	public final static String P_OVERFLOW = "overflow";//$NON-NLS-1$
	public final static String P_PADDING = "padding";//$NON-NLS-1$
	public final static String P_PADDING_TOP = "padding-top";//$NON-NLS-1$
	public final static String P_PADDING_RIGHT = "padding-right";//$NON-NLS-1$
	public final static String P_PADDING_BOTTOM = "padding-bottom";//$NON-NLS-1$
	public final static String P_PADDING_LEFT = "padding-left";//$NON-NLS-1$
	public final static String P_PAGE = "page";//$NON-NLS-1$
	public final static String P_PAGE_BREAK_AFTER = "page-break-after";//$NON-NLS-1$
	public final static String P_PAGE_BREAK_BEFORE = "page-break-before";//$NON-NLS-1$
	public final static String P_PAGE_BREAK_INSIDE = "page-break-inside";//$NON-NLS-1$
	public final static String P_PAUSE = "pause";//$NON-NLS-1$
	public final static String P_PAUSE_AFTER = "pause-after";//$NON-NLS-1$
	public final static String P_PAUSE_BEFORE = "pause-before";//$NON-NLS-1$
	public final static String P_PITCH = "pitch";//$NON-NLS-1$
	public final static String P_PITCH_RANGE = "pitch-range";//$NON-NLS-1$
	public final static String P_PLAY_DURING = "play-during";//$NON-NLS-1$
	public final static String P_POSITION = "position";//$NON-NLS-1$
	public final static String P_QUOTES = "quotes";//$NON-NLS-1$
	public final static String P_RESIZE = "resize"; //$NON-NLS-1$
	public final static String P_RICHNESS = "richness";//$NON-NLS-1$
	public final static String P_RIGHT = "right";//$NON-NLS-1$
	public final static String P_SIZE = "size";//$NON-NLS-1$
	public final static String P_SPEAK = "speak";//$NON-NLS-1$
	public final static String P_SPEAK_HEADER = "speak-header";//$NON-NLS-1$
	public final static String P_SPEAK_NUMERAL = "speak-numeral";//$NON-NLS-1$
	public final static String P_SPEAK_PUNCTUATION = "speak-punctuation";//$NON-NLS-1$
	public final static String P_SPEECH_RATE = "speech-rate";//$NON-NLS-1$
	public final static String P_STRESS = "stress";//$NON-NLS-1$
	public final static String P_TABLE_LAYOUT = "table-layout";//$NON-NLS-1$
	public final static String P_TEXT_ALIGN = "text-align";//$NON-NLS-1$
	public final static String P_TEXT_DECORATION = "text-decoration";//$NON-NLS-1$
	public final static String P_TEXT_INDENT = "text-indent";//$NON-NLS-1$
	public final static String P_TEXT_OVERFLOW = "text-overflow"; //$NON-NLS-1$
	public final static String P_TEXT_SHADOW = "text-shadow";//$NON-NLS-1$
	public final static String P_TEXT_TRANSFORM = "text-transform";//$NON-NLS-1$
	public final static String P_TOP = "top";//$NON-NLS-1$
	public final static String P_UNICODE_BIDI = "unicode-bidi";//$NON-NLS-1$
	public final static String P_VERTICAL_ALIGN = "vertical-align";//$NON-NLS-1$
	public final static String P_VISIBILITY = "visibility";//$NON-NLS-1$
	public final static String P_VOICE_FAMILY = "voice-family";//$NON-NLS-1$
	public final static String P_VOLUME = "volume";//$NON-NLS-1$
	public final static String P_WHITE_SPACE = "white-space";//$NON-NLS-1$
	public final static String P_WIDOWS = "widows";//$NON-NLS-1$
	public final static String P_WIDTH = "width";//$NON-NLS-1$
	public final static String P_WORD_SPACING = "word-spacing";//$NON-NLS-1$
	public final static String P_Z_INDEX = "z-index";//$NON-NLS-1$
	// instance fields
	private int fMinValueCount = 1;
	private int fMaxValueCount = 1;
	private Object[] mediaGroups;
	protected List containers;

	/**
	 * 
	 */
	protected PropCMProperty(String name) {
		super(name);
	}

	/**
	 * 
	 */
	Object appendChild(Object node) {
		Object ret = super.appendChild(node);
		if (node instanceof PropCMProperty) {
			// register shorthand container
			PropCMProperty cm = (PropCMProperty) node;
			if (cm.containers == null)
				cm.containers = new Vector();
			if (!cm.containers.contains(this))
				cm.containers.add(this);
		}
		return ret;
	}

	/**
	 * 
	 */
	void appendMediaGroup(Object mg) {
		if (mediaGroups == null)
			mediaGroups = new Object[1];
		else {
			Object[] oldMediaGroups = mediaGroups;
			mediaGroups = new Object[oldMediaGroups.length + 1];
			System.arraycopy(oldMediaGroups, 0, mediaGroups, 0, oldMediaGroups.length);
		}
		mediaGroups[mediaGroups.length - 1] = mg;
	}

	/**
	 * 
	 */
	public static PropCMProperty getInstanceOf(String name) {
		// initialize
		if (instances == null)
			instances = new Hashtable(200);

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// query
		Object node = instances.get(name);
		if (node != null)
			return (PropCMProperty) node;

		// register
		if (PropCMNode.isLoading()) {
			node = new PropCMProperty(name);
			instances.put(name, node);
		}

		return (PropCMProperty) node;
	}

	/**
	 * 
	 */
	public int getMaxValueCount() {
		return fMaxValueCount;
	}

	/**
	 * 
	 */
	public List getMediaGroups() {
		if (mediaGroups != null)
			return Arrays.asList(mediaGroups);
		else
			return new ArrayList();
	}

	/**
	 * 
	 */
	public int getMinValueCount() {
		return fMinValueCount;
	}

	/**
	 * 
	 */
	public static Enumeration getNameEnum() {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		return instances.keys();
	}

	/**
	 * 
	 */
	public static Enumeration getPropertyEnum() {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		return instances.elements();
	}

	/**
	 * 
	 */
	public int getShorthandContainerCount() {
		return (containers == null) ? 0 : containers.size();
	}

	/**
	 * 
	 */
	public short getType() {
		return VAL_PROPERTY;
	}

	/**
	 * 
	 */
	public boolean isShorthand() {
		int nChild = getNumChild();
		for (int i = 0; i < nChild; i++) {
			Object obj = getChildAt(i);
			if (obj instanceof PropCMProperty && !(obj instanceof PropCMSubProperty))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public static Vector names() {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		return new Vector(instances.keySet());
	}

	/**
	 * 
	 */
	public static List names(Object mediaGroup) {
		if (mediaGroup == null)
			return names();

		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		Vector properties = new Vector();

		Iterator it = instances.values().iterator();
		while (it.hasNext()) {
			PropCMProperty prop = (PropCMProperty) it.next();
			if (prop.getMediaGroups().contains(mediaGroup))
				properties.add(prop.getName());
		}

		return properties;
	}

	/**
	 * If identifier is null, get all properties
	 */
	public static Vector propertiesFor(String identifier, boolean shorthands) {
		// Initialize of DB
		if (isNeedInitialize())
			PropCMNode.initPropertyCM();

		// if identifier is null, get all properties
		if (identifier == null)
			return new Vector(instances.values());

		if (cachedIdMap == null) {
			// start cacheing
			cachedIdMap = new Hashtable();
		}
		else {
			// search cached
			Object ret = cachedIdMap.get(identifier + String.valueOf(shorthands));
			if (ret != null)
				return new Vector((Collection) ret);
		}

		// create
		Enumeration propertyEnum = getPropertyEnum();
		HashSet set = new HashSet();
		while (propertyEnum.hasMoreElements()) {
			PropCMProperty prop = (PropCMProperty) propertyEnum.nextElement();
			if (!shorthands && prop.isShorthand())
				continue;
			if (prop.canHave(identifier))
				set.add(prop);
		}

		// cache
		cachedIdMap.put(identifier + String.valueOf(shorthands), set);

		return new Vector(set);
	}

	/**
	 * 
	 */
	protected void setMaxValueCount(int newMaxValueCount) {
		fMaxValueCount = newMaxValueCount;
	}

	/**
	 * 
	 */
	protected void setMinValueCount(int newMinValueCount) {
		fMinValueCount = newMinValueCount;
	}

	/**
	 * 
	 */
	public PropCMProperty shorthandContainerAt(int i) {
		if (containers == null)
			return null;
		if (i < 0 || containers.size() <= i)
			return null;
		return (PropCMProperty) containers.get(i);
	}

	/**
	 * 
	 */
	public Iterator shorthandContainerIterator() {
		if (containers == null) {
			return new Iterator() {
				public boolean hasNext() {
					return false;
				}

				public Object next() {
					return null;
				}

				public void remove() {
				}
			};
		}
		else
			return containers.iterator();
	}
}