package com.fstar.cms.modle;


/**
 * 已安装的APP信息描述类
 * 
 * @author Administrator
 * 
 */
public class AppLancherJavaBean {
	private String icon;// 图标
	private String name;// 名字
	private String intent;// 跳转
	private String dataDir;// 路径

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

}
