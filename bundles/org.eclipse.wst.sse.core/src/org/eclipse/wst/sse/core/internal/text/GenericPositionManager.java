/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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

package org.eclipse.wst.sse.core.internal.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.util.Assert;

/**
 * Based on the Position management methods from
 * org.eclipse.jface.text.AbstractDocument
 */

public class GenericPositionManager {
	private CharSequence fCharSequence;



	private Map fPositions;
	/** All registered document position updaters */
	private List fPositionUpdaters;

	/**
	 * don't allow instantiation with out document pointer
	 * 
	 */
	private GenericPositionManager() {
		super();
	}

	/**
	 * 
	 */
	public GenericPositionManager(CharSequence charSequence) {
		this();
		// we only use charSequence for "length", to
		// made more generic than "document" even "text store"
		fCharSequence = charSequence;
		completeInitialization();
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#addPosition(org.eclipse.jface.text.Position)
	 */
	public void addPosition(Position position) throws BadLocationException {
		try {
			addPosition(IDocument.DEFAULT_CATEGORY, position);
		}
		catch (BadPositionCategoryException e) {
		}
	}



	/*
	 * @see org.eclipse.jface.text.IDocument#addPosition(java.lang.String,
	 *      org.eclipse.jface.text.Position)
	 */
	public synchronized void addPosition(String category, Position position) throws BadLocationException, BadPositionCategoryException {

		if ((0 > position.offset) || (0 > position.length) || (position.offset + position.length > getDocumentLength()))
			throw new BadLocationException();

		if (category == null)
			throw new BadPositionCategoryException();

		List list = (List) fPositions.get(category);
		if (list == null)
			throw new BadPositionCategoryException();

		list.add(computeIndexInPositionList(list, position.offset), position);
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#addPositionCategory(java.lang.String)
	 */
	public void addPositionCategory(String category) {

		if (category == null)
			return;

		if (!containsPositionCategory(category))
			fPositions.put(category, new ArrayList());
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#addPositionUpdater(org.eclipse.jface.text.IPositionUpdater)
	 */
	public void addPositionUpdater(IPositionUpdater updater) {
		insertPositionUpdater(updater, fPositionUpdaters.size());
	}


	/**
	 * Initializes document listeners, positions, and position updaters. Must
	 * be called inside the constructor after the implementation plug-ins have
	 * been set.
	 */
	protected void completeInitialization() {

		fPositions = new HashMap();
		fPositionUpdaters = new ArrayList();

		addPositionCategory(IDocument.DEFAULT_CATEGORY);
		addPositionUpdater(new DefaultPositionUpdater(IDocument.DEFAULT_CATEGORY));
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#computeIndexInCategory(java.lang.String,
	 *      int)
	 */
	public int computeIndexInCategory(String category, int offset) throws BadLocationException, BadPositionCategoryException {

		if (0 > offset || offset > getDocumentLength())
			throw new BadLocationException();

		List c = (List) fPositions.get(category);
		if (c == null)
			throw new BadPositionCategoryException();

		return computeIndexInPositionList(c, offset);
	}


	/**
	 * Computes the index in the list of positions at which a position with
	 * the given offset would be inserted. The position is supposed to become
	 * the first in this list of all positions with the same offset.
	 * 
	 * @param positions
	 *            the list in which the index is computed
	 * @param offset
	 *            the offset for which the index is computed
	 * @return the computed index
	 * 
	 * @see IDocument#computeIndexInCategory(String, int)
	 */
	protected synchronized int computeIndexInPositionList(List positions, int offset) {

		if (positions.size() == 0)
			return 0;

		int left = 0;
		int right = positions.size() - 1;
		int mid = 0;
		Position p = null;

		while (left < right) {

			mid = (left + right) / 2;

			p = (Position) positions.get(mid);
			if (offset < p.getOffset()) {
				if (left == mid)
					right = left;
				else
					right = mid - 1;
			}
			else if (offset > p.getOffset()) {
				if (right == mid)
					left = right;
				else
					left = mid + 1;
			}
			else if (offset == p.getOffset()) {
				left = right = mid;
			}

		}

		int pos = left;
		p = (Position) positions.get(pos);
		if (offset > p.getOffset()) {
			// append to the end
			pos++;
		}
		else {
			// entry will became the first of all entries with the same
			// offset
			do {
				--pos;
				if (pos < 0)
					break;
				p = (Position) positions.get(pos);
			}
			while (offset == p.getOffset());
			++pos;
		}

		Assert.isTrue(0 <= pos && pos <= positions.size());

		return pos;
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#containsPosition(java.lang.String,
	 *      int, int)
	 */
	public boolean containsPosition(String category, int offset, int length) {

		if (category == null)
			return false;

		List list = (List) fPositions.get(category);
		if (list == null)
			return false;

		int size = list.size();
		if (size == 0)
			return false;

		int index = computeIndexInPositionList(list, offset);
		if (index < size) {
			Position p = (Position) list.get(index);
			while (p != null && p.offset == offset) {
				if (p.length == length)
					return true;
				++index;
				p = (index < size) ? (Position) list.get(index) : null;
			}
		}

		return false;
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#containsPositionCategory(java.lang.String)
	 */
	public boolean containsPositionCategory(String category) {
		if (category != null)
			return fPositions.containsKey(category);
		return false;
	}



	public int getDocumentLength() {
		return fCharSequence.length();
	}

	/**
	 * Returns all positions managed by the document grouped by category.
	 * 
	 * @return the document's positions
	 */
	protected Map getDocumentManagedPositions() {
		return fPositions;
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#getPositionCategories()
	 */
	public String[] getPositionCategories() {
		String[] categories = new String[fPositions.size()];
		Iterator keys = fPositions.keySet().iterator();
		for (int i = 0; i < categories.length; i++)
			categories[i] = (String) keys.next();
		return categories;
	}


	public Position[] getPositions(String category) throws BadPositionCategoryException {

		if (category == null)
			throw new BadPositionCategoryException();

		List c = (List) fPositions.get(category);
		if (c == null)
			throw new BadPositionCategoryException();

		Position[] positions = new Position[c.size()];
		c.toArray(positions);
		return positions;
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#getPositionUpdaters()
	 */
	public IPositionUpdater[] getPositionUpdaters() {
		IPositionUpdater[] updaters = new IPositionUpdater[fPositionUpdaters.size()];
		fPositionUpdaters.toArray(updaters);
		return updaters;
	}



	/*
	 * @see org.eclipse.jface.text.IDocument#insertPositionUpdater(org.eclipse.jface.text.IPositionUpdater,
	 *      int)
	 */
	public synchronized void insertPositionUpdater(IPositionUpdater updater, int index) {

		for (int i = fPositionUpdaters.size() - 1; i >= 0; i--) {
			if (fPositionUpdaters.get(i) == updater)
				return;
		}

		if (index == fPositionUpdaters.size())
			fPositionUpdaters.add(updater);
		else
			fPositionUpdaters.add(index, updater);
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#removePosition(org.eclipse.jface.text.Position)
	 */
	public void removePosition(Position position) {
		try {
			removePosition(IDocument.DEFAULT_CATEGORY, position);
		}
		catch (BadPositionCategoryException e) {
		}
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#removePosition(java.lang.String,
	 *      org.eclipse.jface.text.Position)
	 */
	public synchronized void removePosition(String category, Position position) throws BadPositionCategoryException {

		if (position == null)
			return;

		if (category == null)
			throw new BadPositionCategoryException();

		List c = (List) fPositions.get(category);
		if (c == null)
			throw new BadPositionCategoryException();

		// remove based on identity not equality
		int size = c.size();
		for (int i = 0; i < size; i++) {
			if (position == c.get(i)) {
				c.remove(i);
				return;
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#removePositionCategory(java.lang.String)
	 */
	public void removePositionCategory(String category) throws BadPositionCategoryException {

		if (category == null)
			return;

		if (!containsPositionCategory(category))
			throw new BadPositionCategoryException();

		fPositions.remove(category);
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#removePositionUpdater(org.eclipse.jface.text.IPositionUpdater)
	 */
	public synchronized void removePositionUpdater(IPositionUpdater updater) {
		for (int i = fPositionUpdaters.size() - 1; i >= 0; i--) {
			if (fPositionUpdaters.get(i) == updater) {
				fPositionUpdaters.remove(i);
				return;
			}
		}
	}


	/**
	 * Updates all positions of all categories to the change described by the
	 * document event. All registered document updaters are called in the
	 * sequence they have been arranged. Uses a robust iterator.
	 * 
	 * @param event
	 *            the document event describing the change to which to adapt
	 *            the positions
	 */
	protected synchronized void updatePositions(DocumentEvent event) {
		List list = new ArrayList(fPositionUpdaters);
		Iterator e = list.iterator();
		while (e.hasNext()) {
			IPositionUpdater u = (IPositionUpdater) e.next();
			u.update(event);
		}
	}


}
