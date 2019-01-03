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
set JAVA_HOME=d:\jdk6_03
set JFLEX_HOME=D:\JFlex\jflex-1.4.2

%JAVA_HOME%\bin\java -Xmx470M -jar %JFLEX_HOME%\lib\JFlex.jar CSSTokenizer.jflex
move CSSTokenizer.java ..\..\..\..\..\org.eclipse.wst.css.core\src\org\eclipse\wst\css\core\internal\parser\ && del CSSTokenizer.java*
