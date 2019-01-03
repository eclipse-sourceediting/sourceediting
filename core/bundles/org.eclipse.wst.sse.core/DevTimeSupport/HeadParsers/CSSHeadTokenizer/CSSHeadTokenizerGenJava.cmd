@rem ***************************************************************************
@rem Copyright (c) 2004, 2018 IBM Corporation and others.
@rem This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License 2.0
@rem which accompanies this distribution, and is available at
@rem https://www.eclipse.org/legal/epl-2.0/
@rem
@rem SPDX-License-Identifier: EPL-2.0
@rem
@rem Contributors:
@rem     IBM Corporation - initial API and implementation
@rem ***************************************************************************
@echo off

rem The following variables need to be set/specified for each "development machine"
set PATH=%PATH%;d:jdks\j2sdk1.4.1_02\bin
set WORKSPACE_LOCATION=D:\builds\Workspaces\PureHeadWorkspace
set JFLEX_LIB_LOCATION=D:\DevTimeSupport\JFlex\lib

rem The following variables differ from project to project, but should be otherwise constant
set MAIN_NAME=CSSHeadTokenizer
set PROJECT_SRC=\org.eclipse.wst.common.encoding.contentspecific\src\
set PACKAGE_DIR=com\ibm\encoding\resource\contentspecific\css\

rem Given the above "framework" and the command themselves, these variables should never need to be modified
set JAVA_FILE=%MAIN_NAME%.java
set JFLEX_RULES=%MAIN_NAME%.jflex
set SKELETON_FILE=%MAIN_NAME%.skeleton

IF EXIST %JAVA_FILE% del %JAVA_FILE%
rem java -Xmx470000000 -cp %JFLEX_LIB_LOCATION%\sed-jflex.jar;. JFlex.Main %JFLEX_RULES% -skel %SKELETON_FILE% 1>jflexout.txt 2>jflexerr.txt
java -Xmx470000000 -cp %JFLEX_LIB_LOCATION%\sed-jflex.jar;. JFlex.Main %JFLEX_RULES%  1>jflexout.txt 2>jflexerr.txt
IF EXIST %JAVA_FILE% copy %JAVA_FILE% %WORKSPACE_LOCATION%%PROJECT_SRC%%PACKAGE_DIR%%JAVA_FILE%

rem pause
