package test432978;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;


public class AtBeginTei extends TagExtraInfo
{
    @Override
    public VariableInfo[] getVariableInfo( TagData data )
    {
        return new VariableInfo[] {
            new VariableInfo(
                "extra", Integer.class.getName(), true, VariableInfo.AT_BEGIN)
        };
    }
}