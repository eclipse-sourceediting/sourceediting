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
PATH=%PATH%;d:\jdk6_01\bin
java -Xmx470000000 -cp d:\JFlex\1.2.2\lib\JFlex.jar;. JFlex.Main JSPTokenizer.jflex -skel skeleton.sse && rm -f JSPTokenizer.java~ JSPTokenizer~ && copy JSPTokenizer.java ..\..\..\..\..\org.eclipse.jst.jsp.core\src\org\eclipse\jst\jsp\core\internal\parser\\internal\JSPTokenizer.java
