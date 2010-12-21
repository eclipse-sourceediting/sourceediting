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



import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * 
 */
public abstract class PropCMNode {

	public final static short VAL_IDENTIFIER = 0;
	public final static short VAL_PROPERTY = 1;
	public final static short VAL_NUMBER = 2;
	public final static short VAL_STRING = 3;
	public final static short VAL_FUNC = 4;
	public final static short VAL_CONTAINER = 5;
	public final static short VAL_SUBPROPERTY = 6;
	public final static short VAL_FONTPROPERTY = 7;
	public final static short VAL_UNICODE_RANGE = 8;
	protected java.lang.String name = null;
	private static short LOADING = 0; // 0 : not initialized, 1 : under
										// initializing, 2 : initialize-done
										// and fix DB

	/**
	 * 
	 */
	public PropCMNode(String name) {
		super();
		this.name = name;
	}

	/**
	 * 
	 */
	public Set getIdentifiers() {
		HashSet ids = new HashSet();
		getIdentifiers(ids);
		return ids;
	}

	/**
	 * 
	 */
	abstract void getIdentifiers(Set indents);

	/**
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 */
	public abstract short getType();

	/**
	 * 
	 */
	public Collection getValues() {
		Vector vals = new Vector();
		getValues(vals);
		return vals;
	}

	/**
	 * 
	 */
	abstract void getValues(Collection values);

	/**
	 * 
	 */
	protected static void initPropertyCM() {
		if (!isNeedInitialize())
			return;
		LOADING = 1;

		PropCMNumber.initNumberCMDim();

		// azimuth
		PropCMProperty prop = PropCMProperty.getInstanceOf(PropCMProperty.P_AZIMUTH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_ANGLE));
		prop.appendChild(IValID.V_LEFT_SIDE);
		prop.appendChild(IValID.V_FAR_LEFT);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_CENTER_LEFT);
		prop.appendChild(IValID.V_CENTER);
		prop.appendChild(IValID.V_CENTER_RIGHT);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendChild(IValID.V_FAR_RIGHT);
		prop.appendChild(IValID.V_RIGHT_SIDE);
		prop.appendChild(IValID.V_BEHIND);
		prop.appendChild(IValID.V_LEFTWARDS);
		prop.appendChild(IValID.V_RIGHTWARDS);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);
		prop.setMaxValueCount(2);

		// background
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_COLOR));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_IMAGE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_REPEAT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ATTACHMENT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.setMaxValueCount(6);

		// background-attachment
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ATTACHMENT);
		prop.appendChild(IValID.V_SCROLL);
		prop.appendChild(IValID.V_FIXED);
		prop.appendChild(IValID.V_LOCAL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-clip
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_CLIP);
		prop.appendChild(IValID.V_BORDER_BOX);
		prop.appendChild(IValID.V_CONTENT_BOX);
		prop.appendChild(IValID.V_PADDING_BOX);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendChild(IValID.V_TRANSPARENT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-image
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_IMAGE);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-origin
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ORIGIN);
		prop.appendChild(IValID.V_PADDING_BOX);
		prop.appendChild(IValID.V_BORDER_BOX);
		prop.appendChild(IValID.V_CONTENT_BOX);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-position ---- sub-properties horizontal
		prop = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_CENTER);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-position ---- sub-properties vertical
		prop = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_TOP);
		prop.appendChild(IValID.V_CENTER);
		prop.appendChild(IValID.V_BOTTOM);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-position
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION);
		prop.appendChild(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_X));
		prop.appendChild(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_BG_POSITION_Y));
		prop.setMaxValueCount(2);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-repeat
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_REPEAT);
		prop.appendChild(IValID.V_REPEAT);
		prop.appendChild(IValID.V_REPEAT_X);
		prop.appendChild(IValID.V_REPEAT_Y);
		prop.appendChild(IValID.V_SPACE);
		prop.appendChild(IValID.V_ROUND);
		prop.appendChild(IValID.V_NO_REPEAT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// background-size
		prop = PropCMSubProperty.getInstanceOf(PropCMProperty.P_BG_SIZE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_CONTAIN);
		prop.appendChild(IValID.V_COVER);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE));
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR));
		prop.setMaxValueCount(9);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-collapse
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLLAPSE);
		prop.appendChild(IValID.V_COLLAPSE);
		prop.appendChild(IValID.V_SEPARATE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_COLOR));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_COLOR));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_COLOR));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_COLOR));
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendChild(IValID.V_TRANSPARENT);
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-spacing
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_SPACING);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.setMaxValueCount(2);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_STYLE));
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-top
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_COLOR));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-right
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_COLOR));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-bottom
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_COLOR));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-left
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_COLOR));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-top-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-top-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_STYLE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-top-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_WIDTH);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-right-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-right-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_STYLE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-right-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_WIDTH);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-bottom-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-bottom-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_STYLE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-bottom-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_WIDTH);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-left-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-left-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_STYLE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-left-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_WIDTH);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH);
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RIGHT_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_LEFT_WIDTH));
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-top-left-radius
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_LEFT_RADIUS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-top-right-radius
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_RIGHT_RADIUS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-bottom-left-radius
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_LEFT_RADIUS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// border-bottom-right-radius
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_RIGHT_RADIUS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_RADIUS);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_LEFT_RADIUS));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_TOP_RIGHT_RADIUS));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_LEFT_RADIUS));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_BOTTOM_RIGHT_RADIUS));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.setMaxValueCount(4);

		// bottom
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BOTTOM);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// box-shadow
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BOX_SHADOW);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// box-shadow
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_BOX_SIZING);
		prop.appendChild(IValID.V_BORDER_BOX);
		prop.appendChild(IValID.V_CONTENT_BOX);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// caption-side
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CAPTION_SIDE);
		prop.appendChild(IValID.V_TOP);
		prop.appendChild(IValID.V_BOTTOM);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// clear
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CLEAR);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendChild(IValID.V_BOTH);
		prop.appendChild(IValID.V_STATIC);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// clip ---- sub-properties top
		prop = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_TOP);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		((PropCMSubProperty) prop).setContainer(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// clip ---- sub-properties right
		prop = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_RIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		((PropCMSubProperty) prop).setContainer(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// clip ---- sub-properties bottom
		prop = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_BOTTOM);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		((PropCMSubProperty) prop).setContainer(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// clip ---- sub-properties left
		prop = PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_LEFT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		((PropCMSubProperty) prop).setContainer(PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// clip
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CLIP);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_SHAPE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// columns
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMNS);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_COUNT));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_WIDTH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-count
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_COUNT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-fill
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_FILL);
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_BALANCE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-gap
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_GAP);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_NORMAL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-rule
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE_COLOR));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-rule-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-rule-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE_STYLE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-rule-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_RULE_WIDTH);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// column-span
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COLUMN_SPAN);
		prop.appendChild(IValID.V_1);
		prop.appendChild(IValID.V_ALL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// content
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CONTENT);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_ANY));
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_COUNTER));
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_ATTR));
		prop.appendChild(IValID.V_OPEN_QUOTE);
		prop.appendChild(IValID.V_CLOSE_QUOTE);
		prop.appendChild(IValID.V_NO_OPEN_QUOTE);
		prop.appendChild(IValID.V_NO_CLOSE_QUOTE);
		prop.appendChild(IValID.V_NONE);
		prop.appendChild(IValID.V_NORMAL);
		prop.setMaxValueCount(-1); // infinite
		prop.appendMediaGroup(IMediaGroupID.M_ALL);

		// counter-increment
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COUNTER_INCREMENT);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_COUNTER_ID));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.appendChild(IValID.V_NONE);
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_ALL);

		// counter-reset
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_COUNTER_RESET);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_COUNTER_ID));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.appendChild(IValID.V_NONE);
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_ALL);

		// cue
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CUE);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_BEFORE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_AFTER));
		prop.setMaxValueCount(2);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// cue-after
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_AFTER);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// cue-before
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CUE_BEFORE);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// cursor
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_CURSOR);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_CROSSHAIR);
		prop.appendChild(IValID.V_DEFAULT);
		prop.appendChild(IValID.V_POINTER);
		prop.appendChild(IValID.V_PROGRESS);
		prop.appendChild(IValID.V_MOVE);
		prop.appendChild(IValID.V_E_RESIZE);
		prop.appendChild(IValID.V_NE_RESIZE);
		prop.appendChild(IValID.V_NW_RESIZE);
		prop.appendChild(IValID.V_N_RESIZE);
		prop.appendChild(IValID.V_SE_RESIZE);
		prop.appendChild(IValID.V_SW_RESIZE);
		prop.appendChild(IValID.V_S_RESIZE);
		prop.appendChild(IValID.V_W_RESIZE);
		prop.appendChild(IValID.V_TEXT);
		prop.appendChild(IValID.V_WAIT);
		prop.appendChild(IValID.V_HELP);
		prop.setMaxValueCount(-1); // infinite
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_INTERACTIVE);

		// direction
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_DIRECTION);
		prop.appendChild(IValID.V_LTR);
		prop.appendChild(IValID.V_RTL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// display
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_DISPLAY);
		prop.appendChild(IValID.V_INLINE);
		prop.appendChild(IValID.V_INLINE_BLOCK);
		prop.appendChild(IValID.V_BLOCK);
		prop.appendChild(IValID.V_LIST_ITEM);
		prop.appendChild(IValID.V_RUN_IN);
		prop.appendChild(IValID.V_COMPACT);
		prop.appendChild(IValID.V_MARKER);
		prop.appendChild(IValID.V_TABLE);
		prop.appendChild(IValID.V_INLINE_TABLE);
		prop.appendChild(IValID.V_TABLE_ROW_GROUP);
		prop.appendChild(IValID.V_TABLE_HEADER_GROUP);
		prop.appendChild(IValID.V_TABLE_FOOTER_GROUP);
		prop.appendChild(IValID.V_TABLE_ROW);
		prop.appendChild(IValID.V_TABLE_COLUMN_GROUP);
		prop.appendChild(IValID.V_TABLE_COLUMN);
		prop.appendChild(IValID.V_TABLE_CELL);
		prop.appendChild(IValID.V_TABLE_CAPTION);
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_ALL);

		// elevation
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_ELEVATION);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_ANGLE));
		prop.appendChild(IValID.V_BELOW);
		prop.appendChild(IValID.V_LEVEL);
		prop.appendChild(IValID.V_ABOVE);
		prop.appendChild(IValID.V_HIGHER);
		prop.appendChild(IValID.V_LOWER);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// empty-cells
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_EMPTY_CELLS);
		prop.appendChild(IValID.V_SHOW);
		prop.appendChild(IValID.V_HIDE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// float
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FLOAT);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_VARIANT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_WEIGHT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_LINE_HEIGHT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_FAMILY));
		prop.appendChild(IValID.V_CAPTION);
		prop.appendChild(IValID.V_ICON);
		prop.appendChild(IValID.V_MENU);
		prop.appendChild(IValID.V_MESSAGE_BOX);
		prop.appendChild(IValID.V_SMALL_CAPTION);
		prop.appendChild(IValID.V_STATUS_BAR);
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-family
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_FAMILY);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_FONT));
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_GENERIC_FAMILY));
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-size
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_ABSOLUTE_SIZE));
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_RELATIVE_SIZE));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-size-adjust
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE_ADJUST);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-stretch
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STRETCH);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_WIDER);
		prop.appendChild(IValID.V_NARROWER);
		prop.appendChild(IValID.V_ULTRA_CONDENSED);
		prop.appendChild(IValID.V_EXTRA_CONDENSED);
		prop.appendChild(IValID.V_CONDENSED);
		prop.appendChild(IValID.V_SEMI_CONDENSED);
		prop.appendChild(IValID.V_SEMI_EXPANDED);
		prop.appendChild(IValID.V_EXPANDED);
		prop.appendChild(IValID.V_EXTRA_EXPANDED);
		prop.appendChild(IValID.V_ULTRA_EXPANDED);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STYLE);
		prop.appendChild(IValID.V_ITALIC);
		prop.appendChild(IValID.V_OBLIQUE);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-variant
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_VARIANT);
		prop.appendChild(IValID.V_SMALL_CAPS);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// font-weight
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_WEIGHT);
		prop.appendChild(IValID.V_BOLD);
		prop.appendChild(IValID.V_100);
		prop.appendChild(IValID.V_200);
		prop.appendChild(IValID.V_300);
		prop.appendChild(IValID.V_400);
		prop.appendChild(IValID.V_500);
		prop.appendChild(IValID.V_600);
		prop.appendChild(IValID.V_700);
		prop.appendChild(IValID.V_800);
		prop.appendChild(IValID.V_900);
		prop.appendChild(IValID.V_LIGHTER);
		prop.appendChild(IValID.V_BOLDER);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// height
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_HEIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// leftt
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LEFT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// letter-spacing
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LETTER_SPACING);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// line-height
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LINE_HEIGHT);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// list-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_TYPE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_POSITION));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_IMAGE));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// list-style-image
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_IMAGE);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// list-style-position
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_POSITION);
		prop.appendChild(IValID.V_INSIDE);
		prop.appendChild(IValID.V_OUTSIDE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// list-style-type
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_TYPE);
		prop.appendChild(IValID.V_DISC);
		prop.appendChild(IValID.V_CIRCLE);
		prop.appendChild(IValID.V_SQUARE);
		prop.appendChild(IValID.V_DECIMAL);
		prop.appendChild(IValID.V_DECIMAL_LEADING_ZERO);
		prop.appendChild(IValID.V_LOWER_ROMAN);
		prop.appendChild(IValID.V_UPPER_ROMAN);
		prop.appendChild(IValID.V_LOWER_GREEK);
		prop.appendChild(IValID.V_LOWER_ALPHA);
		prop.appendChild(IValID.V_LOWER_LATIN);
		prop.appendChild(IValID.V_UPPER_ALPHA);
		prop.appendChild(IValID.V_UPPER_LATIN);
		prop.appendChild(IValID.V_HEBREW);
		prop.appendChild(IValID.V_ARMENIAN);
		prop.appendChild(IValID.V_GEORGIAN);
		prop.appendChild(IValID.V_CJK_IDEOGRAPHIC);
		prop.appendChild(IValID.V_HIRAGANA);
		prop.appendChild(IValID.V_KATAKANA);
		prop.appendChild(IValID.V_HIRAGANA_IROHA);
		prop.appendChild(IValID.V_KATAKANA_IROHA);
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// margin
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN);
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_MARGIN_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_TOP));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_RIGHT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_BOTTOM));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_LEFT));
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// margin-top
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_TOP);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_MARGIN_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// margin-right
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_RIGHT);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_MARGIN_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// margin-bottom
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_BOTTOM);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_MARGIN_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// margin-left
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARGIN_LEFT);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_MARGIN_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// marker-offset
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARKER_OFFSET);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// marks
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MARKS);
		prop.appendChild(IValID.V_CROP);
		prop.appendChild(IValID.V_CROSS);
		prop.appendChild(IValID.V_NONE);
		prop.setMaxValueCount(2);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// max-height
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MAX_HEIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// max-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MAX_WIDTH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// min-height
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MIN_HEIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// min-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_MIN_WIDTH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// opacity
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_OPACITY);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// orphans
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_ORPHANS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// outline
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE);
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_COLOR));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_STYLE));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_WIDTH));
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_INTERACTIVE);

		// outline-color
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_COLOR);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendChild(IValID.V_INVERT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_INTERACTIVE);

		// outline-style
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_STYLE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_INTERACTIVE);

		// outline-width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_OUTLINE_WIDTH);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_INTERACTIVE);

		// overflow
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_OVERFLOW);
		prop.appendChild(IValID.V_VISIBLE);
		prop.appendChild(IValID.V_HIDDEN);
		prop.appendChild(IValID.V_SCROLL);
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// padding
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING);
		// prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_PADDING_WIDTH));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_TOP));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_RIGHT));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_BOTTOM));
		prop.appendChild(PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_LEFT));
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// padding-top
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_TOP);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_PADDING_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// padding-right
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_RIGHT);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_PADDING_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// padding-bottom
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_BOTTOM);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_PADDING_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// padding-left
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PADDING_LEFT);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_PADDING_WIDTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// page
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_PAGE_ID));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// page-break-after
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_AFTER);
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_ALWAYS);
		prop.appendChild(IValID.V_AVOID);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// page-break-before
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_BEFORE);
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_ALWAYS);
		prop.appendChild(IValID.V_AVOID);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// page-break-inside
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAGE_BREAK_INSIDE);
		prop.appendChild(IValID.V_AVOID);
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// pause
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_TIME));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.setMaxValueCount(2);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// pause-after
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE_AFTER);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_TIME));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// pause-before
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PAUSE_BEFORE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_TIME));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// pitch
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PITCH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_FREQUENCY));
		prop.appendChild(IValID.V_X_LOW);
		prop.appendChild(IValID.V_LOW);
		prop.appendChild(IValID.V_MEDIUM);
		prop.appendChild(IValID.V_HIGH);
		prop.appendChild(IValID.V_X_HIGH);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// pitch-range
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PITCH_RANGE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// play-during
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_PLAY_DURING);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(IValID.V_MIX);
		prop.appendChild(IValID.V_REPEAT);
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_NONE);
		prop.setMaxValueCount(3);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// position
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_POSITION);
		prop.appendChild(IValID.V_ABSOLUTE);
		prop.appendChild(IValID.V_RELATIVE);
		prop.appendChild(IValID.V_FIXED);
		prop.appendChild(IValID.V_STATIC);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// quotes
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_QUOTES);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_ANY));
		prop.appendChild(IValID.V_NONE);
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// resize
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_RESIZE);
		prop.appendChild(IValID.V_BOTH);
		prop.appendChild(IValID.V_HORIZONTAL);
		prop.appendChild(IValID.V_NONE);
		prop.appendChild(IValID.V_VERTICAL);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// richness
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_RICHNESS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// right
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_RIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// size
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_SIZE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_PORTRAIT);
		prop.appendChild(IValID.V_LANDSCAPE);
		prop.setMaxValueCount(2);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// speak
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_NONE);
		prop.appendChild(IValID.V_SPELL_OUT);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// speak-header
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_HEADER);
		prop.appendChild(IValID.V_ONCE);
		prop.appendChild(IValID.V_ALWAYS);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// speak-numeral
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_NUMERAL);
		prop.appendChild(IValID.V_DIGITS);
		prop.appendChild(IValID.V_CONTINUOUS);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// speak-punctuation
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_SPEAK_PUNCTUATION);
		prop.appendChild(IValID.V_CODE);
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// speech-rate
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_SPEECH_RATE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendChild(IValID.V_X_SLOW);
		prop.appendChild(IValID.V_SLOW);
		prop.appendChild(IValID.V_MEDIUM);
		prop.appendChild(IValID.V_FAST);
		prop.appendChild(IValID.V_X_FAST);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// stress
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_STRESS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// table-layout
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TABLE_LAYOUT);
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(IValID.V_FIXED);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// text-align
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_ALIGN);
		prop.appendChild(IValID.V_LEFT);
		prop.appendChild(IValID.V_CENTER);
		prop.appendChild(IValID.V_RIGHT);
		prop.appendChild(IValID.V_JUSTIFY);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_ANY));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// text-decoration
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_DECORATION);
		prop.appendChild(IValID.V_UNDERLINE);
		prop.appendChild(IValID.V_OVERLINE);
		prop.appendChild(IValID.V_LINE_THROUGH);
		prop.appendChild(IValID.V_BLINK);
		prop.appendChild(IValID.V_NONE);
		prop.setMaxValueCount(4);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// text-indent
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_INDENT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// text-overflow
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_OVERFLOW);
		prop.appendChild(IValID.V_CLIP);
		prop.appendChild(IValID.V_ELLIPSIS);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// text-shadow
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_SHADOW);
		prop.appendChild(IValID.V_NONE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// text-transform
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TEXT_TRANSFORM);
		prop.appendChild(IValID.V_CAPITALIZE);
		prop.appendChild(IValID.V_UPPERCASE);
		prop.appendChild(IValID.V_LOWERCASE);
		prop.appendChild(IValID.V_NONE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// top
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_TOP);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// unicode-bidi
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_UNICODE_BIDI);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_EMBED);
		prop.appendChild(IValID.V_BIDI_OVERRIDE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// vertical-align
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_VERTICAL_ALIGN);
		prop.appendChild(IValID.V_BOTTOM);
		prop.appendChild(IValID.V_TEXT_BOTTOM);
		prop.appendChild(IValID.V_SUB);
		prop.appendChild(IValID.V_BASELINE);
		prop.appendChild(IValID.V_MIDDLE);
		prop.appendChild(IValID.V_SUPER);
		prop.appendChild(IValID.V_TEXT_TOP);
		prop.appendChild(IValID.V_TOP);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// visibility
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_VISIBILITY);
		prop.appendChild(IValID.V_VISIBLE);
		prop.appendChild(IValID.V_HIDDEN);
		prop.appendChild(IValID.V_COLLAPSE);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// voice-family
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_VOICE_FAMILY);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_VOICE));
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_GENERIC_VOICE));
		prop.setMaxValueCount(-1);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// volume
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_VOLUME);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_SILENT);
		prop.appendChild(IValID.V_X_SOFT);
		prop.appendChild(IValID.V_SOFT);
		prop.appendChild(IValID.V_MEDIUM);
		prop.appendChild(IValID.V_LOUD);
		prop.appendChild(IValID.V_X_LOUD);
		prop.appendMediaGroup(IMediaGroupID.M_AURAL);

		// white-space
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_WHITE_SPACE);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_PRE);
		prop.appendChild(IValID.V_PRE_LINE);
		prop.appendChild(IValID.V_PRE_WRAP);
		prop.appendChild(IValID.V_NOWRAP);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// widows
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_WIDOWS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);
		prop.appendMediaGroup(IMediaGroupID.M_PAGED);

		// width
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_WIDTH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_AUTO);
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// word-spacing
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_WORD_SPACING);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		// z-index
		prop = PropCMProperty.getInstanceOf(PropCMProperty.P_Z_INDEX);
		prop.appendChild(IValID.V_AUTO);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.appendMediaGroup(IMediaGroupID.M_VISUAL);

		/** ******** font properties ********* */
		// font-family
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_FONT_FAMILY);
		prop.appendChild(PropCMString.getInstanceOf(PropCMString.VAL_FONT));
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_GENERIC_FAMILY));
		prop.setMaxValueCount(-1);

		// font-style
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_FONT_STYLE);
		prop.appendChild(IValID.V_ITALIC);
		prop.appendChild(IValID.V_OBLIQUE);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_ALL);
		prop.setMaxValueCount(-1);

		// font-variant
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_FONT_VARIANT);
		prop.appendChild(IValID.V_SMALL_CAPS);
		prop.appendChild(IValID.V_NORMAL);
		prop.setMaxValueCount(-1);

		// font-weight
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_FONT_WEIGHT);
		prop.appendChild(IValID.V_BOLD);
		prop.appendChild(IValID.V_100);
		prop.appendChild(IValID.V_200);
		prop.appendChild(IValID.V_300);
		prop.appendChild(IValID.V_400);
		prop.appendChild(IValID.V_500);
		prop.appendChild(IValID.V_600);
		prop.appendChild(IValID.V_700);
		prop.appendChild(IValID.V_800);
		prop.appendChild(IValID.V_900);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_ALL);
		prop.setMaxValueCount(-1);

		// font-stretch
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_FONT_STRETCH);
		prop.appendChild(IValID.V_NORMAL);
		prop.appendChild(IValID.V_ULTRA_CONDENSED);
		prop.appendChild(IValID.V_EXTRA_CONDENSED);
		prop.appendChild(IValID.V_CONDENSED);
		prop.appendChild(IValID.V_SEMI_CONDENSED);
		prop.appendChild(IValID.V_SEMI_EXPANDED);
		prop.appendChild(IValID.V_EXPANDED);
		prop.appendChild(IValID.V_EXTRA_EXPANDED);
		prop.appendChild(IValID.V_ULTRA_EXPANDED);
		prop.appendChild(IValID.V_ALL);
		prop.setMaxValueCount(-1);

		// font-size
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_FONT_SIZE);
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_ABSOLUTE_SIZE));
		prop.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_RELATIVE_SIZE));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		prop.appendChild(IValID.V_ALL);
		prop.setMaxValueCount(-1);

		// unicode-range
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_UNICODE_RANGE);
		prop.appendChild(PropCMURange.getInstanceOf(PropCMURange.VAL_URANGE));
		prop.setMaxValueCount(-1);

		// units-per-em
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_UNITS_PER_EM);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// src
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_SRC);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_FORMAT));
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_LOCAL));
		prop.setMaxValueCount(-1);

		// panose-1
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_PANOSE_1);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_INTEGER));
		prop.setMaxValueCount(10);

		// stemv
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_STEMV);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// stemh
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_STEMH);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// slope
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_SLOPE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// cap-height
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_CAP_HEIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// x-height
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_X_HEIGHT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// ascent
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_ASCENT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// descent
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_DESCENT);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// widths
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_WIDTHS);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.appendChild(PropCMURange.getInstanceOf(PropCMURange.VAL_URANGE));
		prop.setMaxValueCount(-1);

		// bbox
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_BBOX);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));
		prop.setMaxValueCount(4);
		prop.setMaxValueCount(4);

		// definition-src
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_DEFINITION_SRC);
		prop.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_URI));

		// baseline
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_BASELINE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// centerline
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_CENTERLINE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// mathline
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_MATHLINE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		// topline
		prop = PropCMFontProperty.getInstanceOf(PropCMFontProperty.PF_TOPLINE);
		prop.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_NUM));

		/** ******** containers ********* */

		// absolute-size
		PropCMContainer cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_ABSOLUTE_SIZE);
		cont.appendChild(IValID.V_XX_SMALL);
		cont.appendChild(IValID.V_X_SMALL);
		cont.appendChild(IValID.V_SMALL);
		cont.appendChild(IValID.V_MEDIUM);
		cont.appendChild(IValID.V_LARGE);
		cont.appendChild(IValID.V_X_LARGE);
		cont.appendChild(IValID.V_XX_LARGE);

		// border-style
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_STYLE);
		cont.appendChild(IValID.V_SOLID);
		cont.appendChild(IValID.V_DASHED);
		cont.appendChild(IValID.V_DOTTED);
		cont.appendChild(IValID.V_DOUBLE);
		cont.appendChild(IValID.V_GROOVE);
		cont.appendChild(IValID.V_RIDGE);
		cont.appendChild(IValID.V_INSET);
		cont.appendChild(IValID.V_OUTSET);
		cont.appendChild(IValID.V_HIDDEN);
		cont.appendChild(IValID.V_NONE);

		// border-width
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_BORDER_WIDTH);
		cont.appendChild(IValID.V_THIN);
		cont.appendChild(IValID.V_MEDIUM);
		cont.appendChild(IValID.V_THICK);
		cont.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));

		// color
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_COLOR);
		cont.appendChild(IValID.V_AQUA);
		cont.appendChild(IValID.V_BLACK);
		cont.appendChild(IValID.V_BLUE);
		cont.appendChild(IValID.V_FUCHSIA);
		cont.appendChild(IValID.V_GRAY);
		cont.appendChild(IValID.V_GREEN);
		cont.appendChild(IValID.V_LIME);
		cont.appendChild(IValID.V_MAROON);
		cont.appendChild(IValID.V_NAVY);
		cont.appendChild(IValID.V_OLIVE);
		cont.appendChild(IValID.V_ORANGE);
		cont.appendChild(IValID.V_PURPLE);
		cont.appendChild(IValID.V_RED);
		cont.appendChild(IValID.V_SILVER);
		cont.appendChild(IValID.V_TEAL);
		cont.appendChild(IValID.V_WHITE);
		cont.appendChild(IValID.V_YELLOW);
		cont.appendChild(PropCMContainer.getContInstanceOf(PropCMContainer.VAL_SYSTEM_COLOR));
		cont.appendChild(PropCMFunction.getInstanceOf(PropCMFunction.VAL_RGB));
		cont.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_HASH));

		// system color
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_SYSTEM_COLOR);
		cont.appendChild(IValID.V_ACTIVEBORDER);
		cont.appendChild(IValID.V_ACTIVECAPTION);
		cont.appendChild(IValID.V_APPWORKSPACE);
		cont.appendChild(IValID.V_BACKGROUND);
		cont.appendChild(IValID.V_BUTTONFACE);
		cont.appendChild(IValID.V_BUTTONHIGHLIGHT);
		cont.appendChild(IValID.V_BUTTONSHADOW);
		cont.appendChild(IValID.V_BUTTONTEXT);
		cont.appendChild(IValID.V_CAPTIONTEXT);
		cont.appendChild(IValID.V_GRAYTEXT);
		cont.appendChild(IValID.V_HIGHLIGHT);
		cont.appendChild(IValID.V_HIGHLIGHTTEXT);
		cont.appendChild(IValID.V_INACTIVEBORDER);
		cont.appendChild(IValID.V_INACTIVECAPTION);
		cont.appendChild(IValID.V_INACTIVECAPTIONTEXT);
		cont.appendChild(IValID.V_INFOBACKGROUND);
		cont.appendChild(IValID.V_INFOTEXT);
		cont.appendChild(IValID.V_MENU);
		cont.appendChild(IValID.V_MENUTEXT);
		cont.appendChild(IValID.V_SCROLLBAR);
		cont.appendChild(IValID.V_THREEDDARKSHADOW);
		cont.appendChild(IValID.V_THREEDFACE);
		cont.appendChild(IValID.V_THREEDHIGHLIGHT);
		cont.appendChild(IValID.V_THREEDLIGHTSHADOW);
		cont.appendChild(IValID.V_THREEDSHADOW);
		cont.appendChild(IValID.V_WINDOW);
		cont.appendChild(IValID.V_WINDOWFRAME);
		cont.appendChild(IValID.V_WINDOWTEXT);

		// generic-family
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_GENERIC_FAMILY);
		cont.appendChild(IValID.V_SERIF);
		cont.appendChild(IValID.V_SANS_SERIF);
		cont.appendChild(IValID.V_CURSIVE);
		cont.appendChild(IValID.V_FANTASY);
		cont.appendChild(IValID.V_MONOSPACE);

		// generic-voice
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_GENERIC_VOICE);
		cont.appendChild(IValID.V_MALE);
		cont.appendChild(IValID.V_FEMALE);
		cont.appendChild(IValID.V_CHILD);

		// margin-width
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_MARGIN_WIDTH);
		cont.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		cont.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));
		cont.appendChild(IValID.V_AUTO);

		// padding-width
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_PADDING_WIDTH);
		cont.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_LENGTH));
		cont.appendChild(PropCMNumber.getInstanceOf(PropCMNumber.VAL_PERCENTAGE));

		// relative-size
		cont = PropCMContainer.getContInstanceOf(PropCMContainer.VAL_RELATIVE_SIZE);
		cont.appendChild(IValID.V_SMALLER);
		cont.appendChild(IValID.V_LARGER);

		LOADING = 2;
	}

	/**
	 * 
	 */
	protected static boolean isLoading() {
		return LOADING == 1;
	}

	/**
	 * 
	 */
	protected static boolean isNeedInitialize() {
		return LOADING == 0;
	}

	/**
	 * 
	 */
	public String toString() {
		return getName();
	}
}