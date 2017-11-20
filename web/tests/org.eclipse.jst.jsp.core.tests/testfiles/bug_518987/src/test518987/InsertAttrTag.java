package test518987;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class InsertAttrTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String insert;

	@Override
	public int doStartTag() throws JspException {
		pageContext.setAttribute("insert", 10);
		try {
			pageContext.getOut().append(getInsert());
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getInsert() {
		return insert;
	}

	public void setInsert(String insert) {
		this.insert = insert;
	}
}
