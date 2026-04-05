package net.ooder.vfs.ext;

public enum FileLinkStatus {

	RUN("文件协作中"), END("文件协作结束");

	private FileLinkStatus(String desc) {
		this.desc = desc;
	}

	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
