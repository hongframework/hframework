package com.hframework.web.config.bean.dataset;

import com.hframework.common.util.StringUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("helper-datas")
public class HelperDatas {

	@XStreamImplicit
    @XStreamAlias("helper-data")
	private List<HelperData> helperDatas;
    public HelperDatas() {}

	public List<HelperData> getHelperDatas() {
		return helperDatas;
	}

	public void setHelperDatas(List<HelperData> helperDatas) {
		this.helperDatas = helperDatas;
	}
}
