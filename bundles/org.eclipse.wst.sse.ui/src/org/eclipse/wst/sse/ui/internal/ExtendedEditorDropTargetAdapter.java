/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.internal.TransferBuilder.TransferProxyForDelayLoading;

/**
 * ExtendedEditorDropTargetAdapter
 */
public class ExtendedEditorDropTargetAdapter extends DropTargetAdapter {
	private String[] editorIds;
	private int orgOffset = 0;
	private IEditorPart targetEditor = null;
	private ITextViewer textViewer = null;

	private Transfer[] transfers = null;

	private boolean useProxy;

	/**
	 * @deprecated use ExtendedEditorDropTargetAdapter(boolean useProxy) for
	 *             the performance
	 */
	public ExtendedEditorDropTargetAdapter() {
		this(false);
	}

	public ExtendedEditorDropTargetAdapter(boolean useProxy) {
		super();
		this.useProxy = useProxy;
	}

	protected boolean doDrop(Transfer transfer, DropTargetEvent event) {
		TransferBuilder tb = new TransferBuilder(useProxy);

		IDropAction[] as = null;
		if (editorIds != null && editorIds.length > 0)
			as = tb.getDropActions(editorIds, transfer);
		else
			as = tb.getDropActions(getTargetEditor().getClass().getName(), transfer);

		for (int i = 0; i < as.length; ++i) {
			IDropAction da = as[i];
			Transfer actualTransfer;
			if (transfer instanceof TransferProxyForDelayLoading) {
				actualTransfer = ((TransferProxyForDelayLoading) transfer).getTransferClass();
			}
			else {
				actualTransfer = transfer;
			}
			if (actualTransfer instanceof FileTransfer) {
				if (event.data == null) {
					Logger.log(Logger.ERROR, "No data in DropTargetEvent from " + event.widget); //$NON-NLS-1$
					return false;
				}
				String[] strs = (String[]) event.data;
				boolean[] bs = new boolean[strs.length];
				int c = 0;
				for (int j = 0; j < strs.length; ++j) {
					bs[j] = false;
					if (da.isSupportedData(strs[j])) {
						event.data = new String[]{strs[j]};
						if (!da.run(event, targetEditor)) {
							bs[j] = true;
							c++;
						}
					}
					else {
						bs[j] = true;
						c++;
					}
				}
				if (c == 0) {
					return true;
				}

				int k = 0;
				String[] rests = new String[c];
				for (int j = 0; j < strs.length; ++j) {
					if (bs[j])
						rests[k++] = strs[j];
				}
				event.data = rests;
			}
			else if (da.isSupportedData(event.data)) {
				if (da.run(event, targetEditor)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 */
	public void dragEnter(DropTargetEvent event) {
		TransferData data = null;
		Transfer[] ts = getTransfers();
		for (int i = 0; i < ts.length; i++) {
			for (int j = 0; j < event.dataTypes.length; j++) {
				if (ts[i].isSupportedType(event.dataTypes[j])) {
					data = event.dataTypes[j];
					break;
				}
			}
			if (data != null) {
				event.currentDataType = data;
				break;
			}
		}

		if (textViewer != null) {
			orgOffset = textViewer.getTextWidget().getCaretOffset();
		}
	}

	public void dragLeave(DropTargetEvent event) {
		if (textViewer != null) {
			StyledText st = textViewer.getTextWidget();
			st.setCaretOffset(orgOffset);
			st.redraw();
			st.update();
		}
	}

	/**
	 * Scroll the visible area as needed
	 */
	public void dragOver(DropTargetEvent event) {
		event.operations &= ~DND.DROP_MOVE;
		event.detail = DND.DROP_COPY;

		if (textViewer != null) {
			Point pt = toControl(new Point(event.x, event.y));
			StyledText st = textViewer.getTextWidget();

			// auto scroll
			Rectangle ca = st.getClientArea();
			int margin = st.getLineHeight();

			if (pt.y < margin) { // up
				st.invokeAction(ST.LINE_UP);
			}
			else if (pt.y > ca.height - margin) { // down
				st.invokeAction(ST.LINE_DOWN);
			}

			int offsetUnder = getDropOffset(event);
			if (offsetUnder > 0 && offsetUnder < st.getCharCount()) {
				int currentLine = st.getLineAtOffset(offsetUnder);
				Rectangle rect = st.getTextBounds(offsetUnder, offsetUnder);
				if (pt.x < rect.width && st.getHorizontalPixel() > 0) {
					st.invokeAction(ST.COLUMN_PREVIOUS); // left
					if (offsetUnder != st.getCaretOffset()) {
						st.setCaretOffset(offsetUnder);
						st.setSelection(offsetUnder);
					}
					st.redraw();
				}
				else if (pt.x > st.getClientArea().x && offsetUnder + 2 < st.getCharCount() && currentLine == st.getLineAtOffset(offsetUnder + 2)) { // right
					st.invokeAction(ST.COLUMN_NEXT); // right
					if (offsetUnder != st.getCaretOffset()) {
						st.setCaretOffset(offsetUnder);
						st.setSelection(offsetUnder);
					}
					st.redraw();
				}
			}
		}
	}

	/**
	 */
	public void drop(DropTargetEvent event) {
		if (event.operations == DND.DROP_NONE)
			return;

		if (textViewer != null) {
			Point pt = toControl(new Point(event.x, event.y));
			StyledText st = textViewer.getTextWidget();

			int offset = getDropOffset(st, pt);
			if (offset != st.getCaretOffset()) {
				st.setCaretOffset(offset);
			}

			// ISelectionProvider sp = textViewer.getSelectionProvider();
			// ISelection sel = new TextSelection(offset, 0);
			// sp.setSelection(sel);
			// BUG145392 - need to account for folded regions
			if (textViewer instanceof ITextViewerExtension5) {
				offset = ((ITextViewerExtension5) textViewer).widgetOffset2ModelOffset(offset);
			}
			textViewer.setSelectedRange(offset, 0);
		}

		Transfer[] ts = getTransfers();
		for (int i = 0; i < ts.length; i++) {
			if (ts[i].isSupportedType(event.currentDataType)) {
				if (doDrop(ts[i], event)) {
					break;
				}
			}
		}
	}

	protected int getDropOffset(DropTargetEvent event) {
		Point pt = getTextViewer().getTextWidget().toControl(new Point(event.x, event.y));
		StyledText st = textViewer.getTextWidget();
		return getDropOffset(st, pt);
	}

	private int getDropOffset(StyledText st, Point pt) {
		int offset = st.getCaretOffset();
		try {
			offset = st.getOffsetAtLocation(pt);
		}
		catch (IllegalArgumentException e) {
			// This is normal case if mouse cursor is on outside of valid
			// text.
			boolean found = false;
			Point p = new Point((pt.x > 0 ? pt.x : 0), pt.y);
			// search nearest character
			for (; p.x > -1; p.x--) {
				try {
					offset = st.getOffsetAtLocation(p);

					/*
					 * Now that a valid offset has been found, try to place at
					 * the end of the line
					 */
					/*
					 * partial line folding invalidates any "move to EOL"
					 * action we might take
					 */
					// if (textViewer != null && textViewer.getDocument() !=
					// null) {
					// IRegion lineInfo = null;
					// try {
					// if (textViewer instanceof ITextViewerExtension5) {
					// lineInfo =
					// textViewer.getDocument().getLineInformationOfOffset(((ITextViewerExtension5)textViewer).widgetOffset2ModelOffset(offset));
					// }
					// else {
					// lineInfo =
					// textViewer.getDocument().getLineInformationOfOffset(offset);
					// }
					// } catch (BadLocationException e1) {
					// }
					// if (lineInfo != null)
					// offset = lineInfo.getOffset() + lineInfo.getLength();
					// }
					found = true;
					break;
				}
				catch (IllegalArgumentException ex) {
				}
			}

			if (!found) {
				offset = st.getCharCount();
			}
		}
		return offset;
	}

	public IEditorPart getTargetEditor() {
		return targetEditor;
	}

	public ITextViewer getTextViewer() {
		return textViewer;
	}

	/**
	 * @return org.eclipse.swt.dnd.Transfer[]
	 */
	public Transfer[] getTransfers() {
		if (transfers == null) {
			TransferBuilder tb = new TransferBuilder(useProxy);
			if (editorIds == null || editorIds.length == 0)
				transfers = tb.getDropTargetTransfers(getTargetEditor().getClass().getName());
			else
				transfers = tb.getDropTargetTransfers(editorIds);
		}
		return transfers;
	}

	/**
	 */
	public void setTargetEditor(IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
	}

	public void setTargetIDs(String[] ids) {
		editorIds = ids;
	}

	public void setTextViewer(ITextViewer textViewer) {
		this.textViewer = textViewer;
	}

	private Point toControl(Point point) {
		return (textViewer != null ? textViewer.getTextWidget().toControl(point) : point);
	}
}
