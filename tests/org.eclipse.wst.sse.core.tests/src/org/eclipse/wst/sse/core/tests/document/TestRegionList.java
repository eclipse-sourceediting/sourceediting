/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.document;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;
import org.eclipse.wst.sse.core.tests.util.Accessor;



public class TestRegionList extends TestCase {


	private static final String REGION_TYPE = TestRegionList.class.getName();


	public TestRegionList() {
		super();
	}


	public TestRegionList(String name) {
		super(name);
	}

	public void test_add() {
		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
	}

	public void test_add2() {
		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
	}

	public void test_addAllToPosition() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);

		TextRegionListImpl impl2 = new TextRegionListImpl();
		added = impl2.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		added = impl2.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);

		added = impl.addAll(0, impl2);
		assertTrue("regions not added", added);

		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		assertEquals("object was wrong", regions[1], ((ITextRegion[]) new Accessor(impl, TextRegionListImpl.class).get("fRegions"))[0]);
		assertEquals("object was wrong", regions[2], ((ITextRegion[]) new Accessor(impl, TextRegionListImpl.class).get("fRegions"))[1]);
		assertEquals("object was wrong", regions[0], ((ITextRegion[]) new Accessor(impl, TextRegionListImpl.class).get("fRegions"))[2]);
	}

	public void test_clear() {
		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		impl.clear();
		assertEquals("count was wrong", 0, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
	}

	public void test_get() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		assertEquals("wrong object", regions[0], impl.get(0));
		assertEquals("wrong object", regions[1], impl.get(1));
		assertEquals("wrong object", regions[2], impl.get(2));
	}

	public void test_indexOf() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		assertEquals("wrong object", 0, impl.indexOf(regions[0]));
		assertEquals("wrong object", 1, impl.indexOf(regions[1]));
		assertEquals("wrong object", 2, impl.indexOf(regions[2]));
	}

	public void test_isEmpty() {
		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		impl.clear();

		assertEquals("count was wrong", true, impl.isEmpty());
	}

	public void test_iterator0() {
		TextRegionListImpl impl = new TextRegionListImpl();
		Iterator it = null;
		assertNotNull("no iterator returned", it = (Iterator) new Accessor(impl, TextRegionListImpl.class).invoke("iterator", new Object[0]));
		assertFalse(it.hasNext());
	}

	public void test_iterator1() {
		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, impl.size());
		added = impl.add(new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, impl.size());

		Iterator it = null;
		assertNotNull("no iterator returned", it = (Iterator) new Accessor(impl, TextRegionListImpl.class).invoke("iterator", new Object[0]));
		assertTrue(it.hasNext());
	}

	public void test_iterator2() {
		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		Iterator it = null;
		assertNotNull("no iterator returned", it = (Iterator) new Accessor(impl, TextRegionListImpl.class).invoke("iterator", new Object[0]));
		assertTrue(it.hasNext());

		assertNotNull(it.next());
		assertNotNull(it.next());
		assertNotNull(it.next());

		assertFalse(it.hasNext());
	}

	public void test_removeByPosition() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		assertEquals("wrong object", 0, impl.indexOf(regions[0]));
		assertEquals("wrong object", 1, impl.indexOf(regions[1]));
		assertEquals("wrong object", 2, impl.indexOf(regions[2]));

		impl.remove(1);

		Iterator it = null;
		assertNotNull("no iterator returned", it = (Iterator) new Accessor(impl, TextRegionListImpl.class).invoke("iterator", new Object[0]));
		assertTrue(it.hasNext());

		assertEquals(regions[0], it.next());
		assertEquals(regions[2], it.next());

		assertFalse(it.hasNext());
	}

	public void test_removeByObject() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		assertEquals("wrong object", 0, impl.indexOf(regions[0]));
		assertEquals("wrong object", 1, impl.indexOf(regions[1]));
		assertEquals("wrong object", 2, impl.indexOf(regions[2]));

		impl.remove(regions[1]);

		Iterator it = null;
		assertNotNull("no iterator returned", it = (Iterator) new Accessor(impl, TextRegionListImpl.class).invoke("iterator", new Object[0]));
		assertTrue(it.hasNext());

		assertEquals(regions[0], it.next());
		assertEquals(regions[2], it.next());

		assertFalse(it.hasNext());
	}

	public void test_removeAll() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));

		assertEquals("wrong object", 0, impl.indexOf(regions[0]));
		assertEquals("wrong object", 1, impl.indexOf(regions[1]));
		assertEquals("wrong object", 2, impl.indexOf(regions[2]));

		TextRegionListImpl impl2 = new TextRegionListImpl();
		impl2.add(regions[0]);
		impl2.add(regions[1]);
		impl2.add(regions[2]);
		impl.removeAll(impl2);

		assertEquals("count was wrong", 0, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
	}

	public void test_size() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, impl.size());
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, impl.size());
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, impl.size());

		assertEquals("wrong object", 0, impl.indexOf(regions[0]));
		assertEquals("wrong object", 1, impl.indexOf(regions[1]));
		assertEquals("wrong object", 2, impl.indexOf(regions[2]));

		assertEquals("wrong count", 3, impl.size());
		impl.remove(regions[1]);
		assertEquals("wrong count", 2, impl.size());
	}

	public void test_toArray() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, impl.size());
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, impl.size());
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, impl.size());

		assertEquals("wrong object", 0, impl.indexOf(regions[0]));
		assertEquals("wrong object", 1, impl.indexOf(regions[1]));
		assertEquals("wrong object", 2, impl.indexOf(regions[2]));

		assertTrue("wrong array", Arrays.equals(regions, impl.toArray()));
		impl.remove(regions[1]);
		assertTrue("wrong array", Arrays.equals(new ITextRegion[]{regions[0], regions[2]}, impl.toArray()));
	}

	public void test_trimToSize0() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, impl.size());
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, impl.size());
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, impl.size());

		impl.clear();
		assertEquals("count was wrong", 0, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		impl.trimToSize();
		assertEquals("count was wrong", 0, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		assertEquals("not trimmed", 0, ((Object[])new Accessor(impl, TextRegionListImpl.class).get("fRegions")).length);
	}
	
	public void test_trimToSize1() {
		ITextRegion[] regions = new ITextRegion[3];

		TextRegionListImpl impl = new TextRegionListImpl();
		boolean added = impl.add(regions[0] = new ContextRegion(REGION_TYPE, 0, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 1, impl.size());
		added = impl.add(regions[1] = new ContextRegion(REGION_TYPE, 1, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 2, impl.size());
		added = impl.add(regions[2] = new ContextRegion(REGION_TYPE, 2, 1, 1));
		assertTrue("region not added", added);
		assertEquals("count was wrong", 3, impl.size());

		impl.remove(0);
		impl.remove(0);
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		impl.trimToSize();
		assertEquals("count was wrong", 1, new Accessor(impl, TextRegionListImpl.class).getInt("fRegionsCount"));
		assertEquals("not trimmed", 1, ((Object[])new Accessor(impl, TextRegionListImpl.class).get("fRegions")).length);
	}
}