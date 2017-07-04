package test518987;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class InsertAttrTEI extends TagExtraInfo {
	@Override
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[]{new VariableInfo("insert", Integer.class.getName(), true, VariableInfo.AT_BEGIN)};
	}
}