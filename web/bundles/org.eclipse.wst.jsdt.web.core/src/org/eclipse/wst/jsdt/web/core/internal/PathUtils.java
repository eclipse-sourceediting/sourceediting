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
package org.eclipse.wst.jsdt.web.core.internal;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.jsdt.core.compiler.CharOperation;

/**
 * <p>Utility class dealing with {@link IPath}s.</p>
 */
public class PathUtils {
	/** represents the * pattern in a path pattern */
	private static final String STAR = "*"; //$NON-NLS-1$
	
	/** represents the ** pattern in a path pattern */
	private static final String STAR_STAR = "**"; //$NON-NLS-1$
	
	/**
	 * <p>Counts the number of segments in a given pattern that match segments in a given parent path.
	 * This counting takes place from the beginning of both the pattern and parent and stops when
	 * they no longer match.  The pattern can contain **, * and ? wild cards.</p>
	 *
	 * @param pattern count the number of segments of this pattern that match the given <code>parent</code>
	 * @param parent count the number of segments in the given <code>pattern</code> that match this path
	 * @return the number of segments from the beginning of the given <code>pattern</code> {@link IPath}
	 * that match the beginning segments of the given <code>parent</code> {@link IPath}
	 */
	public static int countPatternSegmentsThatMatchParent(IPath pattern, IPath parent) {
		int matchingSegments = 0;
		
		//ignore a pattern that is just ** or *
		if(!(pattern.segmentCount() == 1 &&
				(pattern.segment(0).equals(STAR_STAR) || pattern.segment(0).equals(STAR)))) {
			
			int patternSegmentIndex = 0;
			int parentSegmentIndex = 0;
			boolean starStarMode = false;
			while(patternSegmentIndex < pattern.segmentCount() &&
					parentSegmentIndex < parent.segmentCount()) {
				
				String patternSegment = pattern.segment(patternSegmentIndex);
				String parentSegment = parent.segment(parentSegmentIndex);
				
				/* if matching on wild
				 * else if wild match on multiple path segments
				 * else if wild match on one path segment or path segments are equal
				 * else not equal so stop comparing
				 */
				if(starStarMode) {
					/* if parent segment equals first pattern segment after a ** stop matching on it
					 * else still matching on **
					 */
					if(pathSegmentMatchesPattern(patternSegment, parentSegment)) {
						starStarMode = false;
						matchingSegments++;
						patternSegmentIndex++;
						parentSegmentIndex++;
					} else {
						parentSegmentIndex++;
					}
				
				} else if(patternSegment.equals(STAR_STAR)) { //$NON-NLS-1$
					starStarMode = true;
					
					//find the first pattern segment after the ** that is not another ** or *
					matchingSegments++;
					parentSegmentIndex++;
					
					for(int i = patternSegmentIndex+1; i < pattern.segmentCount(); ++i) {
						
						if(!(pattern.segment(i).equals(STAR_STAR) || //$NON-NLS-1$
								pattern.segment(i).equals(STAR))) { //$NON-NLS-1$
							
							patternSegmentIndex = i;
							break;
						}
					}
				} else if(patternSegment.equals("*") || //$NON-NLS-1$
						pathSegmentMatchesPattern(patternSegment, parentSegment)){
					
					matchingSegments++;
					patternSegmentIndex++;
					parentSegmentIndex++;
				} else {
					break;
				}
			}
		}
		
		return matchingSegments;
	}
	
	/**
	 * <p>Given a pattern path and a parent path attempts to truncate the given pattern path such
	 * that it is relative to the given parent path.</p>
	 *
	 * @param pattern attempt to truncate this {@link IPath} such that it is relative to the given
	 * <code>parent</code> {@link IPath}
	 * @param parent attempt to truncate the given <code>pattern</code> {@link IPath} such that it
	 * is relative to this {@link IPath}
	 * @return either a truncated version of the given <code>pattern</code> {@link IPath} that is
	 * relative to the given <code>parent</code> {@link IPath}, or <code>null</code> if the given
	 * <code>pattern</code> {@link IPath} could not be truncated to be relative to the given
	 * <code>parent</code> {@link IPath}
	 */
	public static IPath makePatternRelativeToParent(IPath pattern, IPath parent) {
		int matchedSegments = countPatternSegmentsThatMatchParent(pattern, parent);
		
		IPath relativePattern = null;
		if(matchedSegments != 0) {
			relativePattern = pattern.removeFirstSegments(matchedSegments);
			
			if(relativePattern.segmentCount() == 0) {
				relativePattern = null;
			} else {
				relativePattern.makeRelative();
			}
		}
		
		return relativePattern;
	}
	
	/**
	 * <p>A convenience method for checking the matching of one segment from a pattern path with
	 * one segment from a path.</p>
	 *Bug 334922 - CharOperation#match does not work as expected when isCaseSensitive is passed as false
	 * @param patternSegment check if this pattern segment is a match with the given path segment.
	 * @param segment check if this path segment matches with the given pattern segment
	 * @return <code>true</code> if the segments match, <code>false</code> otherwise
	 */
	private static boolean pathSegmentMatchesPattern(String patternSegment, String segment) {
		return CharOperation.pathMatch(patternSegment.toCharArray(), segment.toCharArray(),
				false, File.separatorChar);
	}
}
