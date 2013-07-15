/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.tests;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.web.core.internal.PathUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * <p>Unit tests for the {@link PathUtils} class.</p>
 *
 */
public class PathUtilsTests extends TestCase {

	/**
	 * <p>Default constructor</p>
	 */
	public PathUtilsTests() {
		super("Path Utils Tests");
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_SegmentCountTest_0() {
		runCountPatternSegmentsThatMatchParentTest("foo", "foo", 1);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_SegmentCountTest_1() {
		runCountPatternSegmentsThatMatchParentTest("/foo", "foo", 1);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_SegmentCountTest_2() {
		runCountPatternSegmentsThatMatchParentTest("/foo", "foo", 1);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_SegmentCountTest_3() {
		runCountPatternSegmentsThatMatchParentTest("foo", "/foo", 1);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_SegmentCountTest_4() {
		runCountPatternSegmentsThatMatchParentTest("/foo", "/foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_0() {
		runCountPatternSegmentsThatMatchParentTest("foo/bar", "foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_1() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar", "foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_2() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar", "foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_3() {
		runCountPatternSegmentsThatMatchParentTest("foo/bar", "/foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_4() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar", "/foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_5() {
		runCountPatternSegmentsThatMatchParentTest("foo/bar/", "foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_6() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar/", "foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_7() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar/", "foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_8() {
		runCountPatternSegmentsThatMatchParentTest("foo/bar/", "/foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_9() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar/", "/foo", 1);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_SegmentCountTest_10() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar/", "/boo", 0);
	}
	
	public void testTwoSegmentParentWithThreeSegmentPattern_SegmentCountTest_0() {
		runCountPatternSegmentsThatMatchParentTest("/foo/bar/blarg", "/foo/bar", 2);
	}
	
	public void testStar_SegmentCountTest_0() {
		runCountPatternSegmentsThatMatchParentTest("/foo/*/blarg", "foo/bar", 2);
	}
	
	public void testStar_SegmentCountTest_1() {
		runCountPatternSegmentsThatMatchParentTest("/foo/*", "foo", 1);
	}
	
	public void testStar_SegmentCountTest_2() {
		runCountPatternSegmentsThatMatchParentTest("/foo/*", "foo/blarg", 2);
	}
	
	public void testStar_SegmentCountTest_3() {
		runCountPatternSegmentsThatMatchParentTest("/foo/*", "foo", 1);
	}
	
	public void testStarStar_SegmentCountTest_0() {
		runCountPatternSegmentsThatMatchParentTest("/foo/**", "foo", 1);
	}
	
	public void testStarStar_SegmentCountTest_1() {
		runCountPatternSegmentsThatMatchParentTest("/foo/**", "foo/bar", 2);
	}
	
	public void testStarStar_SegmentCountTest_2() {
		runCountPatternSegmentsThatMatchParentTest("/foo/**", "foo/bar/blarg", 3);
	}
	
	public void testStarStar_SegmentCountTest_3() {
		runCountPatternSegmentsThatMatchParentTest("/foo/**/blarg", "foo/bar/blarg", 3);
	}
	
	public void testStarStar_SegmentCountTest_4() {
		runCountPatternSegmentsThatMatchParentTest("/foo/**/blarg", "foo/bar/boo/blarg", 3);
	}
	
	public void testStarStar_SegmentCountTest_5() {
		runCountPatternSegmentsThatMatchParentTest("/foo/**/blarg/nerg", "foo/bar/boo/blarg", 3);
	}
	
	public void testQuestionMark_SegmentCountTest_0() {
		runCountPatternSegmentsThatMatchParentTest("foo?bar", "/fooZbar", 1);
	}
	
	public void testQuestionMark_SegmentCountTest_1() {
		runCountPatternSegmentsThatMatchParentTest("foo?bar", "/foobar", 0);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_TransformPatternTest_0() {
		runMakePatternRelativeToParentTest("foo", "foo", null);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_TransformPatternTest_1() {
		runMakePatternRelativeToParentTest("/foo", "foo", null);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_TransformPatternTest_2() {
		runMakePatternRelativeToParentTest("/foo", "foo", null);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_TransformPatternTest_3() {
		runMakePatternRelativeToParentTest("foo", "foo", null);
	}
	
	public void testOneSegmentParentWithOneSegmentPattern_TransformPatternTest_4() {
		runMakePatternRelativeToParentTest("/foo", "foo", null);
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_0() {
		runMakePatternRelativeToParentTest("foo/bar", "foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_1() {
		runMakePatternRelativeToParentTest("/foo/bar", "foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_2() {
		runMakePatternRelativeToParentTest("/foo/bar", "foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_3() {
		runMakePatternRelativeToParentTest("foo/bar", "/foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_4() {
		runMakePatternRelativeToParentTest("/foo/bar", "/foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_5() {
		runMakePatternRelativeToParentTest("foo/bar/", "foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_6() {
		runMakePatternRelativeToParentTest("/foo/bar/", "foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_7() {
		runMakePatternRelativeToParentTest("/foo/bar/", "foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_8() {
		runMakePatternRelativeToParentTest("foo/bar/", "/foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_9() {
		runMakePatternRelativeToParentTest("/foo/bar/", "/foo", "bar/");
	}
	
	public void testOneSegmentParentWithTwoSegmentPattern_TransformPatternTest_10() {
		runMakePatternRelativeToParentTest("/foo/bar/", "/boo", null);
	}
	
	public void testTwoSegmentParentWithThreeSegmentPattern_TransformPatternTest_0() {
		runMakePatternRelativeToParentTest("/foo/bar/blarg", "/foo/bar", "blarg/");
	}
	
	public void testStar_TransformPatternTest_0() {
		runMakePatternRelativeToParentTest("/foo/*/blarg", "foo/bar", "blarg/");
	}
	
	public void testStar_TransformPatternTest_1() {
		runMakePatternRelativeToParentTest("/foo/*", "foo", "*/");
	}
	
	public void testStar_TransformPatternTest_2() {
		runMakePatternRelativeToParentTest("/foo/*", "foo/blarg/", null);
	}
	
	public void testStar_TransformPatternTest_3() {
		runMakePatternRelativeToParentTest("/foo/*", "foo", "*/");
	}
	
	public void testStarStar_TransformPatternTest_0() {
		runMakePatternRelativeToParentTest("/foo/**", "foo", "**/");
	}
	
	public void testStarStar_TransformPatternTest_1() {
		runMakePatternRelativeToParentTest("/foo/**", "foo/bar/", null);
	}
	
	public void testStarStar_TransformPatternTest_2() {
		runMakePatternRelativeToParentTest("/foo/**", "foo/bar/blarg/", null);
	}
	
	public void testStarStar_TransformPatternTest_3() {
		runMakePatternRelativeToParentTest("/foo/**/blarg", "foo/bar/blarg/", null);
	}
	
	public void testStarStar_TransformPatternTest_4() {
		runMakePatternRelativeToParentTest("/foo/**/blarg", "foo/bar/boo/blarg/", null);
	}
	
	public void testStarStar_TransformPatternTest_5() {
		runMakePatternRelativeToParentTest("/foo/**/blarg/nerg", "foo/bar/boo/blarg", "nerg/");
	}
	
	public void testQuestionMark_TransformPatternTest_0() {
		runMakePatternRelativeToParentTest("foo?bar", "/fooZbar", null);
	}
	
	public void testQuestionMark_TransformPatternTest_1() {
		runMakePatternRelativeToParentTest("foo?bar", "/foobar", null);
	}
	
	public void testQuestionMark_TransformPatternTest_2() {
		runMakePatternRelativeToParentTest("foo?bar/awesome", "/fooZbar", "awesome/");
	}
	
	public void testQuestionMark_TransformPatternTest_3() {
		runMakePatternRelativeToParentTest("foo?bar/awesome", "/foobar", null);
	}
	
	/**
	 * <p>Runs a test on {@link PathUtils#countPatternSegmentsThatMatchParent(IPath, IPath)}</p>
	 *
	 * @param pattern the pattern path to test
	 * @param parentPath the parent path to test
	 * @param expectedMatchedSegments the expected number of segments in the pattern that match the parent
	 */
	private static void runCountPatternSegmentsThatMatchParentTest(String pattern, String parentPath,
			int expectedMatchedSegments) {
		int matchedSegments = PathUtils.countPatternSegmentsThatMatchParent(
				new Path(pattern), new Path(parentPath));
		
		Assert.assertEquals("Number of matched path segments does not equal expected.", //$NON-NLS-1$
				expectedMatchedSegments, matchedSegments);
	}
	
	/**
	 * 
	 * <p>Runs a test on {@link PathUtils#makePatternRelativeToParent(IPath, IPath)}</p>
	 *
	 * @param pattern the pattern path to test
	 * @param parent the parent path to test
	 * @param expected the expected pattern path made relative to the parent path
	 */
	private static void runMakePatternRelativeToParentTest(String pattern, String parent, String expected) {
		IPath transformedPattern = PathUtils.makePatternRelativeToParent(
				new Path(pattern), new Path(parent));
		
		IPath expectedPath = null;
		if(expected != null) {
			expectedPath = new Path(expected);
		}
		
		Assert.assertEquals("Transformed pattern does not match expected.",
				expectedPath, transformedPattern);
	}
}