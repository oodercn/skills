package net.ooder.vfs.ext;


public enum FileBrowserRightType {
	Org, Duty, Group, Position, Role, Person;
	
	public static FileBrowserRightType getByType(int type) {
		for (FileBrowserRightType c : FileBrowserRightType.values()) {
			if (c.ordinal() == type) {
				return c;
			}
		}
		return null;
	}
	
}
