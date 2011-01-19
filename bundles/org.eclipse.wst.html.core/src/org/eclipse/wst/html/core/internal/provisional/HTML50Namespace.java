/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.provisional;

public interface HTML50Namespace extends HTML40Namespace {

	public static interface ElementName extends HTML40Namespace.ElementName {
		String ARTICLE = "article"; //$NON-NLS-1$
		String ASIDE = "aside"; //$NON-NLS-1$
		String AUDIO = "audio";
		String CANVAS = "canvas";
		String COMMAND = "command";
		String DATALIST = "datalist";
		String DETAILS = "details";
		String FIGURE = "figure"; //$NON-NLS-1$
		String FIGCAPTION = "figcaption"; //$NON-NLS-1$
		String FOOTER = "footer"; //$NON-NLS-1$
		String HEADER = "header";
		String HGROUP = "hgroup";
		String KEYGEN = "keygen";
		String MARK = "mark";
		String MATH = "math";
		String METER = "meter";
		String NAV = "nav";
		String OUTPUT = "output";
		String PROGRESS = "progress";
		String RP = "rp";
		String RT = "rt";
		String RUBY = "ruby";
		String SECTION = "section"; //$NON-NLS-1$
		String SOURCE = "source";
		String SUMMARY = "summary";
		String SVG = "svg";
		String TIME = "time";
		String VIDEO = "video";
	}

	String HTML50_URI = "http://www.w3.org/TR/html50/";
	String HTML50_TAG_PREFIX = "";

	// global attribute names
	String ATTR_NAME_CONTENT_EDITABLE = "contenteditable"; // %coreattrs; //$NON-NLS-1$
	String ATTR_NAME_CONTEXT_MENU = "contextmenu"; // %coreattrs; //$NON-NLS-1$
	String ATTR_NAME_DRAGGABLE = "draggable"; // %coreattrs; //$NON-NLS-1$
	String ATTR_NAME_ROLE = "role"; // %coreattrs; //$NON_NLS-1$
	String ATTR_NAME_SPELLCHECK = "spellcheck"; // %coreattrs; //$NON-NLS-1$
	String ATTR_NAME_ONABORT = "onabort"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONCAN_PLAY = "oncanplay"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONCAN_PLAY_THROUGH = "oncanplaythrough"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONCONTEXT_MENU = "oncontextmenu"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDRAG = "ondrag"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDRAG_END = "ondragend"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDRAG_OVER = "ondragover"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDRAG_ENTER = "ondragenter"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDRAG_LEAVE = "ondragleave"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDRAG_START = "ondragstart"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDROP = "ondrop"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONDURATION_CHANGE = "ondurationchange"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONEMPTIED = "onemptied"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONENDED = "onended"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONERROR = "onerror"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONFORM_CHANGE = "onformchange"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONFORM_INPUT = "onforminput"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONINPUT = "oninput"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONINVALID = "oninvalid"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONLOADED_DATA = "onloadeddata"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONLOADED_METADATA = "onloadedmetadeta"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONLOAD_START = "onloadstart"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONMOUSE_WHEEL = "onmousewheel"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONPAUSE = "onpause"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONPLAY = "onplay"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONPLAYING = "onplaying"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONPROGRESS = "onprogress"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONRATE_CHANGE = "onratechange"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONREADY_STATE_CHANGE = "onreadystatechange"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONSCROLL = "onscroll"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONSEEKED = "onseeked"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONSEEKING = "onseeking"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONSHOW = "onshow"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONSTALLED = "onstalled"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONSUSPEND = "onsuspend"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONTIME_UPDATE = "ontimeupdate"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONVOLUME_UPDATE = "onvolumeupdate"; // %event; //$NON-NLS-1$
	String ATTR_NAME_ONWAITING = "onwaiting"; // %event; //$NON-NLS-1$

	String ATTR_NAME_PING = "ping";
	String ATTR_NAME_AUTOFOCUS = "autofocus";
	String ATTR_NAME_CHALLENGE = "challenge";
	String ATTR_NAME_FORM = "form";
	String ATTR_NAME_KEYTYPE = "keytype";
	String ATTR_NAME_REQUIRED = "required";
	String ATTR_NAME_AUTOCOMPLETE = "autocomplete"; // input
	String ATTR_NAME_MIN = "min"; // input
	String ATTR_NAME_MAX = "max"; // input
	String ATTR_NAME_PATTERN = "pattern"; // input
	String ATTR_NAME_STEP = "step"; // input
	String ATTR_NAME_NOVALIDATE = "novalidate"; // form
	String ATTR_NAME_FORMACTION = "formaction"; // input|button
	String ATTR_NAME_FORMENCTYPE = "formenctype"; // input|button
	String ATTR_NAME_FORMMETHOD = "formmethod"; // input|button
	String ATTR_NAME_FORMNOVALIDATE = "formnovalidate"; // input|button
	String ATTR_NAME_FORMTARGET = "formtarget"; // input|button
	String ATTR_NAME_SCOPED = "scoped"; // style
	String ATTR_NAME_ASYNC = "async"; // script
	String ATTR_NAME_MANIFEST = "manifest"; // html
	String ATTR_NAME_SIZES = "sizes"; // link
	String ATTR_NAME_REVERSED = "reversed"; // ol
	String ATTR_NAME_SANDBOX = "sandbox"; // iframe
	String ATTR_NAME_SEAMLESS = "seamless"; // iframe
	String ATTR_NAME_SRCDOC = "srcdoc"; // iframe
	String ATTR_NAME_PRELOAD = "preload"; // %mediaElement; //$NON-NLS-1$
	String ATTR_NAME_AUTOPLAY = "autoplay"; // %mediaElement; //$NON-NLS-1$
	String ATTR_NAME_LOOP = "loop"; // %mediaElement; //$NON-NLS-1$
	String ATTR_NAME_CONTROLS = "controls"; // %mediaElement; //$NON-NLS-1$
	String ATTR_NAME_POSTER = "poster"; // %video; //$NON-NLS-1$
	String ATTR_NAME_OPEN = "open"; // details //$NON-NLS-1$
	String ATTR_NAME_PUBDATE = "pubdate"; //time //$NON-NLS-1$
	String ATTR_NAME_LOW = "low"; //meter //$NON-NLS-1$
	String ATTR_NAME_HIGH = "high"; //meter //$NON-NLS-1$
	String ATTR_NAME_OPTIMUM = "optimum"; //meter //$NON-NLS-1$
	String ATTR_NAME_ICON = "icon"; //command //$NON-NLS-1$
	String ATTR_NAME_RADIOGROUP = "radiogroup"; //command //$NON-NLS-1$
	String ATTR_NAME_LIST = "list"; //input //$NON-NLS-1$
	String ATTR_NAME_PLACEHOLDER = "placeholder"; //input //$NON-NLS-1$
	String ATTR_NAME_WRAP = "wrap"; //textarea //$NON-NLS-1$
	String ATTR_NAME_XMLNS = "xmlns"; //html //$NON-NLS-1$
	
	// Global attributes properties

	// for contenteditable (EMPTY|TRUE|FALSE|INHERIT)
	String ATTR_VALUE_EMPTY = ""; // contenteditable //$NON-NLS-1$
	String ATTR_VALUE_INHERIT = "inherit"; // contenteditable //$NON-NLS-1$
	
	// for MediaElement (Audio/Video) 
	String ATTR_VALUE_METADATA = "metadata"; // mediaelement //$NON-NLS-1$
	
	// for Command
	String ATTR_VALUE_COMMAND = "command"; //command //$NON-NLS-1$
	
	//Input type
	String ATTR_VALUE_SEARCH = "search"; //input type //$NON-NLS-1$
	String ATTR_VALUE_TEL = "tel"; //input type //$NON-NLS-1$
	String ATTR_VALUE_URL = "url"; //input type //$NON-NLS-1$
	String ATTR_VALUE_EMAIL = "email"; //input type //$NON-NLS-1$
	String ATTR_VALUE_DATE = "date"; //input type //$NON-NLS-1$
	String ATTR_VALUE_DATETIME = "datetime"; //input type //$NON-NLS-1$
	String ATTR_VALUE_MONTH = "month"; //input type //$NON-NLS-1$
	String ATTR_VALUE_WEEK = "week"; //input type //$NON-NLS-1$
	String ATTR_VALUE_TIME = "time"; //input type //$NON-NLS-1$
	String ATTR_VALUE_DATETIME_LOCAL = "datetime-local"; //input type //$NON-NLS-1$
	String ATTR_VALUE_RANGE = "range"; //input type //$NON-NLS-1$
	String ATTR_VALUE_COLOR = "color"; //input type //$NON-NLS-1$
	String ATTR_VALUE_NUMBER_STRING = "number"; //input type //$NON-NLS-1$

	String ATTR_VALUE_ON = "on"; //input autocomplete //$NON-NLS-1$
	String ATTR_VALUE_OFF = "off"; //input autocomplete //$NON-NLS-1$
	
	String ATTR_VALUE_PUT = "PUT"; //input formmethod //$NON-NLS-1$
	String ATTR_VALUE_DELETE = "DELETE"; //input formmethod //$NON-NLS-1$
	
	String ATTR_VALUE_FORM_DATA = "multipart/form-data"; //input formencType //$NON-NLS-1$
	String ATTR_VALUE_PLAIN = "text/plain"; //input formencType //$NON-NLS-1$
	
	String ATTR_VALUE_SOFT = "soft"; //textarea wrap //$NON-NLS-1$
	String ATTR_VALUE_HARD = "hard"; //textarea wrap //$NON-NLS-1$
	
	
	String ATTR_VALUE_CONTEXT = "context"; //menu type //$NON-NLS-1$
	String ATTR_VALUE_TOOLBAR = "toolbar"; //menu type //$NON-NLS-1$
	String ATTR_VALUE_LIST = "list"; //menu type //$NON-NLS-1$
		
}
