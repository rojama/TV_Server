package com.fstar.cms.modle;

import java.io.Serializable;

public class VideoTypeInfo implements Serializable {

	private static final long serialVersionUID = -7043500525639160582L;
	public String tid;
	public String name;
	public String logo;

	@Override
	public String toString() {
		return "VideoTypeInfo [tid=" + tid + ", name=" + name + ", logo="
				+ logo + "]";
	}

}
