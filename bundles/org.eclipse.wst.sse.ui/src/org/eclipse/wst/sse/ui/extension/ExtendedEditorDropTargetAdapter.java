/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.extension;



import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Caret;

/**
 * ExtendedEditorDropTargetAdapter
 */
public class ExtendedEditorDropTargetAdapter extends DropTargetAdapter {

	private Transfer[] transfers = null;
	private String[] editorIds;
	private IExtendedSimpleEditor targetEditor = null;
	private ITextViewer textViewer = null;
	private int orgOffset = 0;

	private Point caret = null;

	public ExtendedEditorDropTargetAdapter() {
		super();
	}

	/**
	 */
	public void setTargetEditor(IExtendedSimpleEditor targetEditor) {
		this.targetEditor = targetEditor;
	}

	public void setTargetIDs(String[] ids) {
		editorIds = ids;
	}

	public IExtendedSimpleEditor getTargetEditor() {
		return targetEditor;
	}

	public void setTextViewer(ITextViewer textViewer) {
		this.textViewer = textViewer;
	}

	public ITextViewer getTextViewer() {
		return textViewer;
	}

	/**
	 * @return org.eclipse.swt.dnd.Transfer[]
	 */
	public Transfer[] getTransfers() {
		if (transfers == null) {
			TransferBuilder tb = new TransferBuilder();
			if (editorIds == null || editorIds.length == 0)
				transfers = tb.getDropTargetTransfers(getTargetEditor().getClass().getName());
			else
				transfers = tb.getDropTargetTransfers(editorIds);
		}
		return transfers;
	}

	/**
	 */
	public void dragEnter(DropTargetEvent event) {
		caret = null;
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

	private Point toControl(Point point) {
		return (textViewer != null ? textViewer.getTextWidget().toControl(point) : point);
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

			// draw insertion point
			int offset = getDropOffset(st, pt);
			if (offset != st.getCaretOffset()) {
				st.setCaretOffset(offset);
				st.setSelection(offset);
			}

			Point newCaret = st.getLocationAtOffset(offset);
			if (newCaret.equals(caret))
				return;

			Caret ct = st.getCaret();
			Point size = ct.getSize();

			GC gc = new GC(st);
			gc.setXORMode(true);
			gc.setLineWidth(size.x);

			// erase old caret
			if (caret != null) {
				Color originalForeground = gc.getForeground();
				gc.setForeground(st.getBackground());
				gc.drawLine(caret.x, caret.y, caret.x, caret.y + size.y);
				gc.setForeground(originalForeground);
			}

			st.redraw();
			st.update();

			// draw new caret
			caret = newCaret;
			if (ct.getImage() != null)
				gc.drawImage(ct.getImage(), caret.x, caret.y);
			else
				gc.drawLine(caret.x, caret.y, caret.x, caret.y + size.y);

			gc.dispose();
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
			// This is normal case if mouse cursor is on outside of valid text.
			boolean found = false;
			Point p = new Point((pt.x > 0 ? pt.x : 0), pt.y);
			// search nearest character
			for (; p.x > -1; p.x--) {
				try {
					offset = st.getOffsetAtLocation(p) + 1; // + 1 to place cursor at an end of line
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

			ISelectionProvider sp = textViewer.getSelectionProvider();
			ISelection sel = new TextSelection(offset, 0);
			sp.setSelection(sel);
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

	/**
	 */
	protected boolean doDrop(Transfer transfer, DropTargetEvent event) {
		TransferBuilder tb = new TransferBuilder();

		IDropAction[] as = null;
		if (editorIds != null && editorIds.length > 0)
			as = tb.getDropActions(editorIds, transfer.getClass().getName());
		else
			as = tb.getDropActions(getTargetEditor().getClass().getName(), transfer.getClass().getName());

		for (int i = 0; i < as.length; ++i) {
			IDropAction da = as[i];
			if (transfer instanceof FileTransfer) {
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
}
