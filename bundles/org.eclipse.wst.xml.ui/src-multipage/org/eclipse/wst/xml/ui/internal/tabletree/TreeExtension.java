/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/


package org.eclipse.wst.xml.ui.internal.tabletree;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;


public class TreeExtension implements PaintListener {

	protected Tree tree;
	protected EditManager editManager;
	protected String[] columnProperties;
	protected ICellModifier cellModifier;
	protected int columnPosition = 300;
	protected int columnHitWidth = 5;
	protected Color tableLineColor;
	protected int controlWidth;
	protected DelayedDrawTimer delayedDrawTimer;

	public TreeExtension(Tree tree) {
		this.tree = tree;
		InternalMouseListener listener = new InternalMouseListener();
		tree.addMouseMoveListener(listener);
		tree.addMouseListener(listener);
		tree.addPaintListener(this);
		editManager = new EditManager(tree);
		delayedDrawTimer = new DelayedDrawTimer(tree);

		tableLineColor = tree.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
	}

	public void dispose() {
		tableLineColor.dispose();
	}

	public void setCellModifier(ICellModifier modifier) {
		cellModifier = modifier;
	}

	public void resetCachedData() {
	}

	public ICellModifier getCellModifier() {
		return cellModifier;
	}

	public List getItemList() {
		List list = new Vector();
		getItemListHelper(tree.getItems(), list);
		return list;
	}

	protected void getItemListHelper(TreeItem[] items, List list) {
		for (int i = 0; i < items.length; i++) {
			TreeItem item = items[i];
			list.add(item);
			getItemListHelper(item.getItems(), list);
		}
	}

	protected TreeItem getTreeItemOnRow(int px, int py) {
		TreeItem result = null;
		List list = getItemList();
		for (Iterator i = list.iterator(); i.hasNext();) {
			TreeItem item = (TreeItem) i.next();
			Rectangle r = item.getBounds();
			if (r != null && px >= r.x && py >= r.y && py <= r.y + r.height) {
				result = item;
			}
		}
		return result;
	}

	protected class InternalMouseListener extends MouseAdapter implements MouseMoveListener {
		protected int columnDragged = -1;
		protected boolean isDown = false;
		protected int prevX = -1;
		protected Cursor cursor = null;

		public void mouseMove(MouseEvent e) {
			if (e.x > columnPosition - columnHitWidth && e.x < columnPosition + columnHitWidth) {
				if (cursor == null) {
					cursor = new Cursor(tree.getDisplay(), SWT.CURSOR_SIZEWE);
					tree.setCursor(cursor);
				}
			} else {
				if (cursor != null) {
					tree.setCursor(null);
					cursor.dispose();
					cursor = null;
				}
			}

			if (columnDragged != -1) {
				// using the delay timer will make redraws less flickery
				if (e.x > 20) {
					columnPosition = e.x;
					delayedDrawTimer.reset(20);
				}
			}
		}

		public void mouseDown(MouseEvent e) {
			// here we handle the column resizing by detect if the user has
			// click on a column separator
			//
			columnDragged = -1;
			editManager.deactivateCellEditor();

			if (e.x > columnPosition - columnHitWidth && e.x < columnPosition + columnHitWidth) {
				columnDragged = 0;
			}

			// here we handle selecting tree items when any thing on the 'row'
			// is clicked
			//
			TreeItem item = tree.getItem(new Point(e.x, e.y));
			if (item == null) {
				item = getTreeItemOnRow(e.x, e.y);
				if (item != null) {
					TreeItem[] items = new TreeItem[1];
					items[0] = item;
					tree.setSelection(items);
				}
			}
		}

		public void mouseUp(MouseEvent e) {
			columnDragged = -1;
		}
	}

	public String[] getColumnProperties() {
		return columnProperties;
	}

	public void setColumnProperties(String[] columnProperties) {
		this.columnProperties = columnProperties;
	}

	public void paintControl(PaintEvent event) {
		GC gc = event.gc;
		Rectangle treeBounds = tree.getBounds();

		controlWidth = treeBounds.width;
		Color bg = tree.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		Color bg2 = tree.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);

		gc.setBackground(bg2);

		//		// This next part causes problems on LINUX, so let's not do it
		// there
		//		if (B2BHacks.IS_UNIX == false) {
		//			TreeItem[] selectedItems = tree.getSelection();
		//			if (selectedItems.length > 0) {
		//				for (int i = 0; i < selectedItems.length; i++) {
		//					TreeItem item = selectedItems[i];
		//					Rectangle bounds = item.getBounds();
		//					if (bounds != null) {
		//						gc.fillRectangle(bounds.x + bounds.width, bounds.y, controlWidth,
		// bounds.height);
		//					}
		//				}
		//			}
		//		}
		//
		TreeItem[] items = tree.getItems();
		if (items.length > 0) {
			gc.setForeground(tableLineColor);
			gc.setBackground(bg);

			gc.fillRectangle(columnPosition, treeBounds.x, treeBounds.width, treeBounds.height);

			Rectangle itemBounds = items[0].getBounds();
			int height = computeTreeItemHeight();

			if (itemBounds != null) {
				int startY = itemBounds.y < treeBounds.y ? itemBounds.y : treeBounds.y + ((treeBounds.y - itemBounds.y) % height);

				for (int i = startY; i < treeBounds.height; i += height) {
					if (i >= treeBounds.y) {
						gc.drawLine(0, i, treeBounds.width, i);
					}
				}
			}
			gc.drawLine(columnPosition, 0, columnPosition, treeBounds.height);
		} else {
			addEmptyTreeMessage(gc);
		}

		paintItems(gc, items, treeBounds);
	}

	protected int computeTreeItemHeight() {
		int result = -1;

		// On GTK tree.getItemHeight() seems to lie to us. It reports that the
		// tree item occupies a few pixles less
		// vertical space than it should. This which foils our code that draw
		// the 'row' lines since we assume that
		// lines should be drawn at 'itemHeight' increments. In the case of
		// LINUX we don't trust getItemHeight()
		// to compute the increment... instead we compute the value based on
		// distance between two TreeItems.
		//		if (B2BHacks.IS_UNIX) {
		//			TreeItem[] items = tree.getItems();
		//			Rectangle itemBounds = items[0].getBounds();
		//
		//			if (items[0].getExpanded()) {
		//				TreeItem[] children = items[0].getItems();
		//				if (children.length > 0) {
		//					result = children[0].getBounds().y - itemBounds.y;
		//				}
		//			}
		//			else if (items.length > 1) {
		//				result = items[1].getBounds().y - itemBounds.y;
		//			}
		//		}

		result = result != -1 ? result : tree.getItemHeight();
		return result;
	}

	protected void addEmptyTreeMessage(GC gc) {
	}

	public void paintItems(GC gc, TreeItem[] items, Rectangle treeBounds) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				TreeItem item = items[i];
				if (item != null) {
					Rectangle bounds = item.getBounds();
					if (bounds != null) {
						if (treeBounds.intersects(bounds)) {
							paintItem(gc, item, bounds);
						}
					}

					// defect 241039
					//
					if (item.getExpanded()) {
						paintItems(gc, item.getItems(), treeBounds);
					}
				}
			}
		}
	}

	protected void paintItem(GC gc, TreeItem item, Rectangle bounds) {
	}

	public interface ICellEditorProvider {
		CellEditor getCellEditor(Object o, int col);
	}

	/**
	 * This class is used to improve drawing during a column resize.
	 */
	public class DelayedDrawTimer implements Runnable {
		protected Control control;

		public DelayedDrawTimer(Control control) {
			this.control = control;
		}

		public void reset(int milliseconds) {
			getDisplay().timerExec(milliseconds, this);
		}

		public void run() {
			control.redraw();
		}
	}

	private Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * EditManager
	 */
	public class EditManager {
		protected Tree tree;
		protected Control cellEditorHolder;
		protected CellEditorState cellEditorState;

		public EditManager(Tree tree) {
			this.tree = tree;
			this.cellEditorHolder = new Composite(tree, SWT.NONE);

			final Tree theTree = tree;

			MouseAdapter theMouseAdapter = new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					deactivateCellEditor();

					if (e.x > columnPosition + columnHitWidth) {
						TreeItem[] items = theTree.getSelection();
						// No edit if more than one row is selected.
						if (items.length == 1) {
							Rectangle bounds = items[0].getBounds();
							if (bounds != null && e.y >= bounds.y && e.y <= bounds.y + bounds.height) {
								int columnToEdit = 1;
								activateCellEditor(items[0], columnToEdit);
							}
						}
					}
				}
			};

			SelectionListener selectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					applyCellEditorValue();
				}

				public void widgetSelected(SelectionEvent e) {
					applyCellEditorValue();
				}
			};

			KeyListener keyListener = new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.CR) {
						deactivateCellEditor();
						TreeItem[] items = theTree.getSelection();
						if (items.length == 1) {
							activateCellEditor(items[0], 1);
						}
					}
				}
			};

			tree.addMouseListener(theMouseAdapter);
			tree.addKeyListener(keyListener);
			ScrollBar hBar = tree.getHorizontalBar();
			if (hBar != null)
				hBar.addSelectionListener(selectionListener);
			ScrollBar vBar = tree.getVerticalBar();
			if (vBar != null)
				vBar.addSelectionListener(selectionListener);
		}

		public boolean isCellEditorActive() {
			return cellEditorState != null;
		}

		public void applyCellEditorValue() {
			if (cellEditorState != null && cellModifier != null) {
				TreeItem treeItem = cellEditorState.treeItem;

				// The area below the cell editor needs to be explicity
				// repainted on Linux
				//
				//				Rectangle r = B2BHacks.IS_UNIX ? treeItem.getBounds() :
				// null;

				Object value = cellEditorState.cellEditor.getValue();
				String property = cellEditorState.property;

				deactivateCellEditor();

				cellModifier.modify(treeItem, property, value);

				//				if (r != null) {
				//					tree.redraw(r.x, r.y, tree.getBounds().width, r.height,
				// false);
				//				}
			}
		}

		public void deactivateCellEditor() {
			// Clean up any previous editor control
			if (cellEditorState != null) {
				cellEditorState.deactivate();
				cellEditorState = null;
			}
		}

		public void activateCellEditor(TreeItem treeItem, int column) {
			if (cellModifier instanceof ICellEditorProvider) {
				ICellEditorProvider cellEditorProvider = (ICellEditorProvider) cellModifier;
				Object data = treeItem.getData();
				if (columnProperties.length > column) {
					String property = columnProperties[column];
					if (cellModifier.canModify(data, property)) {
						CellEditor newCellEditor = cellEditorProvider.getCellEditor(data, column);
						if (newCellEditor != null) {
							// The control that will be the editor must be a
							// child of the columnPosition
							Control control = newCellEditor.getControl();
							if (control != null) {
								cellEditorState = new CellEditorState(newCellEditor, control, treeItem, column, property);
								cellEditorState.activate();
							}
						}
					}
				}
			}
		}

		/**
		 * this class holds the state that is need on a per cell editor
		 * invocation basis
		 */
		public class CellEditorState implements ICellEditorListener, FocusListener {
			public CellEditor cellEditor;
			public Control control;
			public TreeItem treeItem;
			public int columnNumber;
			public String property;

			public CellEditorState(CellEditor cellEditor, Control control, TreeItem treeItem, int columnNumber, String property) {
				this.cellEditor = cellEditor;
				this.control = control;
				this.treeItem = treeItem;
				this.columnNumber = columnNumber;
				this.property = property;
			}

			public void activate() {
				Object element = treeItem.getData();
				String value = cellModifier.getValue(element, property).toString();
				if (control instanceof Text) {
					Text text = (Text) control;
					int requiredSize = value.length() + 100;
					if (text.getTextLimit() < requiredSize) {
						text.setTextLimit(requiredSize);
					}
				}
				Rectangle r = treeItem.getBounds();
				if (r != null) {
					control.setBounds(columnPosition + 5, r.y + 1, tree.getClientArea().width - (columnPosition + 5), r.height - 1);
					control.setVisible(true);
					cellEditor.setValue(value);
					cellEditor.addListener(this);
					cellEditor.setFocus();
					control.addFocusListener(this);
				}
			}

			public void deactivate() {
				cellEditor.removeListener(this);
				control.removeFocusListener(this);
				cellEditor.deactivate();
				tree.forceFocus();
			}

			// ICellEditorListener methods
			//
			public void applyEditorValue() {
				applyCellEditorValue();
			}

			public void cancelEditor() {
				deactivateCellEditor();
			}

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}

			// FocusListener methods
			//
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				applyCellEditorValue();
			}
		}
	}
}