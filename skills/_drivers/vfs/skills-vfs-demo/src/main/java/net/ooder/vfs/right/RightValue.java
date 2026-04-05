package net.ooder.vfs.right;

import java.util.ArrayList;
import java.util.List;

public enum RightValue {
	none(0,"禁止访问"),
	open(0,new int[]{1,2,3,4,5,6,7,8,9,10,11,13,14,15,16,17,18},new int[]{13,19},"只读"),
	createFolder(1,new int[]{0},new int[]{},-1,"新建文件夹"),
	upload(2,new int[]{0},new int[]{},-1,"上传"),
	download(3,new int[]{0},new int[]{},-1,"下载"),
	edit(4,new int[]{0},new int[]{6,7,8,9,10,11,12,24,27},-1,"编辑"),
	remove(5,new int[]{0},new int[]{},-1,"删除"),
	rename(6,new int[]{0},new int[]{},-1,"重命名"),
	moveto(7,new int[]{0},new int[]{},-1,"移动到"),
	copyto(8,new int[]{0},new int[]{},-1,"复制到"),
	lock(9,new int[]{0},new int[]{},10,"锁定"),
	unLock(10,new int[]{0},new int[]{},9,"解锁"),
	subscribe(11,new int[]{0},new int[]{},12,"订阅"),
	unSubscribe(12,new int[]{0},new int[]{},11,"取消订阅"),
	property(13,new int[]{0},new int[]{},-1,"属性"),
	authright(14,new int[]{0},new int[]{},-1,"权限设置"),
	tag(15,new int[]{0},new int[]{},-1,"标签"),
	send(16,new int[]{0},new int[]{},-1,"发送"),
	
	addComment(19,new int[]{0},new int[]{},-1,"评论"),
	admin(17,new int[]{0},new int[]{14},-1,"管理员"),
	fullcontrol(18,new int[]{0},new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,19,20,21,22,23,24,25,26,27},-1,"完全控制"),
	share(20,new int[]{0},new int[]{},-1,"分享"),
	print(21,new int[]{0},new int[]{},-1,"打印"),
	cooperate(22,new int[]{0},new int[]{},-1,"协作"),
	overCooperate(23,new int[]{0},new int[]{},-1,"完成协作"),
	restore(24,new int[]{0},new int[]{},-1,"还原"),
	cooperateUpload(25,new int[]{0},new int[]{},-1,"更新"),
	shiftDelete(26,new int[]{0},new int[]{},-1,"彻底删除"),
	restoreFileVersion(27,new int[]{0},new int[]{},-1,"还原文件版本");
	
	
	private int rightV;
	
	private String text;
	
	private int[] depends;
	
	private int[] dependx;
	
	private int[] includes;
	
	private int mutex;
	
	RightValue(int rightV,String text){
		this.rightV = rightV;
		this.text = text;
		this.depends = new int[]{};
		this.dependx = new int[]{};
		this.includes = new int[]{};
		this.mutex = -1;
	}
	
	
	RightValue(int rightV,int[] dependx,int[] includes,String text){
		this.rightV = ir(rightV);
		this.dependx = ir(dependx);
		this.includes = ir(includes);
		this.text = text;
		this.mutex = -1;
	}
	
	
	
	RightValue(int rightV,int[] depends,int[] includes,int mutex,String text){
		this.rightV = ir(rightV);
		this.depends = ir(depends);
		this.includes = ir(includes);
		this.mutex = ir(mutex);
		this.text =  text;
	}
	
	
	public int getRightV(){
		return this.rightV;
	}
	
	
	public int[] getDepends() {
		return depends;
	}
	
	public int[] getDependx() {
		return dependx;
	}
	
	public int[] getIncludes() {
		return includes;
	}
	
	public int getMutex() {
		return mutex;
	}
	
	public String getText() {
		return text;
	}
	
	
	
	public static List<RightValue> getRightListByRightValue(int rightValue){
		List<RightValue> rights = new ArrayList<RightValue>();
		if(rightValue==0){
			rights.add(RightValue.none);
			return rights;
		}
		RightValue[] thisRights = values();
		for(RightValue right:thisRights){
			if(right==RightValue.none){
				continue;
			}
			if((right.getRightV()&rightValue)==right.getRightV()){
				rights.add(right);
			}
		}
		return rights;
	}
	
	public static RightValue getRightValueByWeight(int weight){
		for(RightValue value:values()){
			if(value.getRightV()==weight){
				return value;
			}
		}
		return null;
	}
	
	public static RightValue getRightValueByName(String name){
		for(RightValue value:values()){
			if(value.name().equalsIgnoreCase(name)){
				return value;
			}
		}
		return null;
	}
	
	public static int[] getIntArrayByNameArray(String[] nameArray){
		RightValue right = null;
		int rightValue[] = new int[nameArray.length];
		for (int i = 0; i < nameArray.length; i++) {
			right = RightValue.getRightValueByName(nameArray[i]);
			rightValue[i] = right.getRightV();
		}
		return rightValue;
	}
	
	public static int addRight(int oldTotalRightV,int[] addRightV){
		for(int v : addRightV){
			RightValue value = getRightValueByWeight(v);
			if(value!=null){
				if(value==RightValue.none){
					oldTotalRightV &= value.getRightV();
					return oldTotalRightV;
				}
				oldTotalRightV |= value.getRightV();
				int[] depends = value.getDepends();
				if(depends!=null){
					for(int dependV : depends){
						oldTotalRightV |= dependV;
					}
				}
				
				int[] includes = value.getIncludes();
				if(includes!=null){
					for(int include : includes){
						oldTotalRightV |= include;
					}
				}
				
				
				int metux = value.getMutex();
				if(metux!=-1){
					oldTotalRightV = oldTotalRightV & (~metux);
				}
				
			}
			
		}
		return oldTotalRightV;
	}
	
	public static int deleteRight(int oldTotalRightV,int[] addRightV){
		for(int v : addRightV){
			RightValue value = getRightValueByWeight(v);
			if(value!=null){
				oldTotalRightV = oldTotalRightV & (~value.getRightV());
				
				int[] dependx = value.getDependx();
				if(dependx!=null){
					for(int dependX : dependx){
						oldTotalRightV = oldTotalRightV & (~dependX);
					}
				}
				
				
				for(int includes : value.getIncludes()){
					oldTotalRightV = oldTotalRightV & (~includes);
				}
				
				int metux = value.getMutex();
				if(metux!=-1){
					oldTotalRightV |= metux;
				}
				
			}
			
		}
		return oldTotalRightV;
	}
	
	
	public int removeRight(){
		return -1;
	}
	
	private static int ir(int num){
		int a = ((Double) Math.pow(2, num)).intValue();
		return a;
	}
	
	private static int[] ir(int num[]){
		int a[] = new int[num.length];
		for(int i=0;i<num.length;i++){
			a[i] = (((Double) Math.pow(2, num[i])).intValue());
		}
		return a;
	}
	
}
