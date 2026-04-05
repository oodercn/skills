package net.ooder.vfs.right;

public class RightProperty {
	private String authbBy;
	private long authtime;
	private int authType;
	private int rightValue;
	
	private int disable;
	
	public String getAuthbBy() {
		return authbBy;
	}
	public void setAuthbBy(String authbBy) {
		this.authbBy = authbBy;
	}
	public long getAuthtime() {
		return authtime;
	}
	public void setAuthtime(long authtime) {
		this.authtime = authtime;
	}
	public int getAuthType() {
		return authType;
	}
	public void setAuthType(int authType) {
		this.authType = authType;
	}
	public int getRightValue() {
		return rightValue;
	}
	public void setRightValue(int rightValue) {
		this.rightValue = rightValue;
	}
	public int getDisable() {
		return disable;
	}
	public void setDisable(int disable) {
		this.disable = disable;
	}
}
