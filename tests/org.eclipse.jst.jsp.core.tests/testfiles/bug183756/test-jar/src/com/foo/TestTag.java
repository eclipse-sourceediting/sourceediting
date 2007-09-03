package com.foo;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class TestTag extends TagSupport {

	public int doStartTag() throws JspException {
		try {
		pageContext.getOut().write("TAG WORKED");
		} catch (Exception e) {
			
		}
		return super.doStartTag();
	}

}
