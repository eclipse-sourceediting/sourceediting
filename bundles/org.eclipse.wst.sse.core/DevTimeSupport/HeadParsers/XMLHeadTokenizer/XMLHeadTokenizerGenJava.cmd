@echo on

rem The following variables need to be set/specified for each "development machine"
set PATH=%PATH%;D:\JDKs\j2sdk1.4.2_03\bin
set WORKSPACE_LOCATION=D:\builds\Workspaces\WSWBM8
set JFLEX_LIB_LOCATION=D:\DevTimeSupport\JFlex\lib

rem The following variables differ from project to project, but should be otherwise constant
set MAIN_NAME=XMLHeadTokenizer

rem set PROJECT_SRC=\org.eclipse.wst.sse.core.xml\src\
rem set PACKAGE_DIR=com\ibm\sse\model\xml\encoding\

set PROJECT_SRC=\org.eclipse.wst.common.encoding.contentspecific\src\
set PACKAGE_DIR=com\ibm\encoding\resource\contentspecific\xml\


rem Given the above "framework" and the command themselves, these variables should never need to be modified
set JAVA_FILE=%MAIN_NAME%.java
set JFLEX_RULES=%MAIN_NAME%.jflex
set SKELETON_FILE=%MAIN_NAME%.skeleton

IF EXIST %JAVA_FILE% del %JAVA_FILE%
rem java -Xmx470000000 -cp %JFLEX_LIB_LOCATION%\sed-jflex.jar;. JFlex.Main %JFLEX_RULES% -skel %SKELETON_FILE% 1>jflexout.txt 2>jflexerr.txt
java -Xmx470000000 -cp %JFLEX_LIB_LOCATION%\sed-jflex.jar;. JFlex.Main %JFLEX_RULES%  1>jflexout.txt 2>jflexerr.txt
IF EXIST %JAVA_FILE% copy %JAVA_FILE% %WORKSPACE_LOCATION%%PROJECT_SRC%%PACKAGE_DIR%%JAVA_FILE%

pause
