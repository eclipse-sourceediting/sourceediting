package test432978;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


public class AtBeginTag extends BodyTagSupport
{
    private int extra;
    
    public int getExtra() {
        return extra;
    }

    
    public void setExtra( int extra ) {
        this.extra = extra;
    }

    @Override
    public int doStartTag() throws JspException
    {
        pageContext.setAttribute("extra", 10);
        return EVAL_BODY_INCLUDE;
    }
}
