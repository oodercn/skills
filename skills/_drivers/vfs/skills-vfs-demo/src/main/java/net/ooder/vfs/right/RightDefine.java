package net.ooder.vfs.right;

import java.util.LinkedHashSet;
import java.util.Set;

public class RightDefine {
	public static Set<RightValue> commonSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> folderSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> fileSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> privateFolderSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> privateFileSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> privateMulti = new LinkedHashSet<RightValue>();
	public static Set<RightValue> multi = new LinkedHashSet<RightValue>();
	public static Set<RightValue> authFileRightList = new LinkedHashSet<RightValue>();
	public static Set<RightValue> authFolderRightList = new LinkedHashSet<RightValue>();

	public static Set<RightValue> cooperateFolderSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> cooperateFileSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> cooperateMulti = new LinkedHashSet<RightValue>();

	public static Set<RightValue> deletedFolderSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> deletedFileSet = new LinkedHashSet<RightValue>();
	public static Set<RightValue> deletedMulti = new LinkedHashSet<RightValue>();
	static {
		commonSet.add(RightValue.none);
		commonSet.add(RightValue.open);
		commonSet.add(RightValue.createFolder);
		commonSet.add(RightValue.remove);
		commonSet.add(RightValue.moveto);
		commonSet.add(RightValue.copyto);
		commonSet.add(RightValue.rename);
		commonSet.add(RightValue.remove);
		commonSet.add(RightValue.lock);
		commonSet.add(RightValue.unLock);
		commonSet.add(RightValue.subscribe);
		commonSet.add(RightValue.unSubscribe);
		commonSet.add(RightValue.send);
		commonSet.add(RightValue.tag);
		commonSet.add(RightValue.authright);
		commonSet.add(RightValue.property);

		folderSet.addAll(commonSet);
		folderSet.add(RightValue.upload);

		fileSet.addAll(commonSet);
		fileSet.add(RightValue.edit);
		fileSet.add(RightValue.download);
		fileSet.add(RightValue.addComment);
		fileSet.add(RightValue.send);
		fileSet.add(RightValue.cooperate);
		fileSet.add(RightValue.restoreFileVersion);

		privateFolderSet.add(RightValue.none);
		privateFolderSet.add(RightValue.open);
		privateFolderSet.add(RightValue.createFolder);
		privateFolderSet.add(RightValue.moveto);
		privateFolderSet.add(RightValue.copyto);
		privateFolderSet.add(RightValue.upload);
		privateFolderSet.add(RightValue.rename);
		privateFolderSet.add(RightValue.remove);
		privateFolderSet.add(RightValue.property);

		privateFileSet.add(RightValue.none);
		privateFileSet.add(RightValue.open);
		privateFileSet.add(RightValue.download);
		privateFileSet.add(RightValue.send);
		privateFileSet.add(RightValue.share);
		privateFileSet.add(RightValue.moveto);
		privateFileSet.add(RightValue.copyto);
		privateFileSet.add(RightValue.rename);
		privateFileSet.add(RightValue.remove);
		privateFileSet.add(RightValue.property);

		privateMulti.add(RightValue.send);
		privateMulti.add(RightValue.moveto);
		privateMulti.add(RightValue.copyto);
		privateMulti.add(RightValue.remove);
		privateMulti.add(RightValue.tag);

		multi.addAll(privateMulti);
		multi.add(RightValue.lock);
		multi.add(RightValue.unLock);
		multi.add(RightValue.subscribe);
		multi.add(RightValue.unSubscribe);

		authFileRightList.add(RightValue.fullcontrol);
		authFileRightList.add(RightValue.none);
		authFileRightList.add(RightValue.cooperate);
		authFileRightList.add(RightValue.edit);
		authFileRightList.add(RightValue.remove);
		authFileRightList.add(RightValue.download);
		authFileRightList.add(RightValue.open);

		authFolderRightList.add(RightValue.fullcontrol);
		authFolderRightList.add(RightValue.none);
		authFolderRightList.add(RightValue.edit);
		authFolderRightList.add(RightValue.remove);
		authFolderRightList.add(RightValue.createFolder);
		authFolderRightList.add(RightValue.upload);
		authFolderRightList.add(RightValue.open);
		
		cooperateFileSet.add(RightValue.open);
		cooperateFileSet.add(RightValue.edit);
		cooperateFileSet.add(RightValue.download);
		cooperateFileSet.add(RightValue.cooperateUpload);
		
		cooperateMulti.add(RightValue.overCooperate);
		
		deletedFolderSet.add(RightValue.restore);
		deletedFolderSet.add(RightValue.shiftDelete);
		
		deletedFileSet.add(RightValue.restore);
		deletedFileSet.add(RightValue.shiftDelete);
		
		deletedMulti.add(RightValue.restore);
		deletedMulti.add(RightValue.shiftDelete);
	}
}
