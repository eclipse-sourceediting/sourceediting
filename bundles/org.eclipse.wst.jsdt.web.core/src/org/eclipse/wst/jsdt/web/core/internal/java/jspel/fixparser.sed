#!/usr/bin/sed

#
# The SSE generally uses compiler setting to turn  most warnings into errors.  
# This is a little problematic for JavaCC generated files.  We don't want 
# to distribute a customized version of JavaCC nor is there any "template" 
# mechanism.  So, this simple sed script goes through the generated token
# manager and fixes a few things.  If JavaCC changes the generated code, 
# it's likely that this script will no longer do the right thing.  Ditto with
# any version of JavaCC besides 3.2. Also, there's no guarantee that this 
# script will even work with an arbitrary JavaCC grammar.  It's only been tested
# with the current JSP EL grammar.
#
# Author: Ted A. Carroll (tcarroll@bea.com)
#

s/static private final class LookaheadSuccess extends java.lang.Error { }/static private final class LookaheadSuccess extends java.lang.Error { \n    private static final long serialVersionUID = 1L; \n  }/g

