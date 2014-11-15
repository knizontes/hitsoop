package main_pack;

public class AncestorFlag {

	private int flag;
	private int depth;
	private int adepth;
	private int bdepth;
	private int diff;
	
	protected int getAdepth() {
		return adepth;
	}

	protected void setAdepth(int adepth) {
		this.adepth = adepth;
	}

	protected int getBdepth() {
		return bdepth;
	}

	protected void setBdepth(int bdepth) {
		this.bdepth = bdepth;
	}

	protected void setFlag(int flag){
		this.flag=flag;
	}
	
	protected int getFlag(){
		return this.flag;
	}

	protected int getDepth() {
		return depth;
	}

	protected void setDepth(int depth) {
		this.depth = depth;
	}

	protected int getDiff() {
		return diff;
	}

	protected void setDiff(int diff) {
		this.diff = diff;
	}
	
	
	
	
	
}
