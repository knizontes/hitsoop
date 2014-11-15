package main_pack;


public class HSNode{
	
//	private static int id=0;
	/*
	 * The identifier of the node
	 */
	private int node_id;
	
	/* Here the data of a node in AFL graph */
	
	private HSNode father;			//The father of the node in the forest.
//	private AFLNode cc;				//The representative element of the connected component (if any).
//	private AFLNode bcc;			//The representative element of the biconnected component (if any).
	private HSNode left_brother;	//The left brother of the node in the biconnected component (if any).
	private HSNode right_brother;	//The right brother of the node in the biconnected component (if any).
//	private Integer bcc_size;			//The size of the biconnected component.
//	private Integer cc_size;			//The size of the connected component.
	private HSNode strongLeftBrother;
	private HSNode strongRightBrother;
	private Boolean controlled;
	private Boolean inBcc;
	
	/**
	 *Debug global variables controlling function execution time. 	
	 */
	private final static int VARNUM=8;
	
	private final static int GETFATHER=0;
	private final static int HASLEFTBROTHER=1;
	private final static int HASRIGHTBROTHER=2;
	private final static int GETSTRONGLEFTBROTHER=3;
	private final static int GETSTRONGRIGHTBROTHER=4;
	private final static int GETLEFTESTBROTHER=5;
	private final static int GETLEFTESTSETRIGHTEST=6;
	private final static int GETRIGHTESTBROTHER=7;
	
	
	private static long [] funcTimes= new long [VARNUM];

	
	
	
	public HSNode (){

		father=this;
		left_brother=this;
		right_brother=this;
		strongLeftBrother=this;
		strongRightBrother=this;
		controlled=false;
		inBcc=false;
	}
	
	public HSNode (int id){
		this.node_id=id;
		father=this;
		left_brother=this;
		right_brother=this;
		strongLeftBrother=this;
		strongRightBrother=this;
		controlled=false;
		inBcc=false;
	}
	
	public HSNode (HSNode nd){
		this.father=nd.getFather();
		this.left_brother=nd.getLeft_brother();
		this.right_brother=nd.getRight_brother();
	}

	
	protected int getNode_id() {
		return node_id;
	}

	protected void setNode_id(int node_id) {
		this.node_id = node_id;
	}

	/*recursive */
	protected HSNode getFather() {
//		long t= System.nanoTime();
		if (this.father!=null){
			++funcTimes[GETFATHER];
			return this.father;
		}
		else if (!this.strongLeftBrother.equals(this)){
			++funcTimes[GETFATHER];
			return this.strongLeftBrother.getFather();
		}
		else if (!this.left_brother.equals(this)){
			++funcTimes[GETFATHER];
			return this.left_brother.getFather();
		}
		System.out.println("Something wrong searching the father of node: "+this.node_id);
		return null;
	}
	
	protected HSNode getIstanceFather(){
		return this.father;
	}
	
	
	/*iterative*/
	protected Boolean hasLeftBrother(HSNode target_node){
//		long t = System.nanoTime();
		++funcTimes[HASLEFTBROTHER];
		if (!this.getLeftestBrother().equals(target_node.getLeftestBrother())){
			return false;
		}
		HSNode nd=this;
		while (true){
			if (!nd.getLeft_brother().equals(nd)){
				if (nd.getLeft_brother().equals(target_node)) {
//					++funcTimes[HASLEFTBROTHER];
					return true;
				}
				nd=nd.getLeft_brother();
		}
			else break;
		}
//		++funcTimes[HASLEFTBROTHER];
		return false;
	}
	
	protected Boolean hasRightBrother(HSNode target_node){
//		long t=System.nanoTime();
		++funcTimes[HASRIGHTBROTHER];
		if (this.equals(target_node)){
//			funcTimes[HASRIGHTBROTHER]+= (System.nanoTime()) - t;
			return true;
		}
		if (this.right_brother!=this){
			if (this.right_brother.equals(target_node)){ 
//				funcTimes[HASRIGHTBROTHER]+= (System.nanoTime()) - t;
				return true;
			}
//			funcTimes[HASRIGHTBROTHER]+= (System.nanoTime()) - t;
			return this.right_brother.hasRightBrother(target_node);
		}
//		funcTimes[HASRIGHTBROTHER]+= (System.nanoTime()) - t;
		return false;
	}

	protected void setFather(HSNode father) {
		this.father = father;
	}
	
	protected void resetFather(){
		this.father=null;
	}

	protected HSNode getCc(){
		HSNode nd=this;
		while (true){
			if (nd.father!=null && nd.getIstanceFather().equals(nd))
				break;
			nd=nd.getFather();
//			System.out.println("nd:"+nd.getNode_id()+"fth:"+nd.getIstanceFather());
		}
		return nd;
	}
	

	protected HSNode getBcc() {
		 return this.getLeftestBrother();
	}

	
	protected void setupBcc(HSNode commonFather, HSNode fromNode){
			
	}

	protected HSNode getLeft_brother() {
		return left_brother;
	}

	protected void setLeft_brother(HSNode left_brother) {
		this.left_brother = left_brother;
	}

	protected HSNode getRight_brother() {
		return right_brother;
	}

	protected void setRight_brother(HSNode right_brother) {
		this.right_brother = right_brother;
	}
	



	
	/*recursive */
	protected HSNode getStrongLeftBrother() {
//		long t=System.nanoTime();
		++funcTimes[GETSTRONGLEFTBROTHER];
		if (this.strongLeftBrother.equals(this)){
//			funcTimes[GETSTRONGLEFTBROTHER]+= (System.nanoTime()) - t;
			return this;
		}
//		funcTimes[GETSTRONGLEFTBROTHER]+= (System.nanoTime()) - t;
		this.strongLeftBrother=this.strongLeftBrother.getStrongLeftBrother();
		return this.strongLeftBrother;
	}
	
	protected void setStrongLeftBrother(HSNode strongLeftBrother) {
		this.strongLeftBrother = strongLeftBrother.getStrongLeftBrother();
	}
	
	/*recursive */
	protected HSNode getStrongRightBrother() {
//		long t=System.nanoTime();
		++funcTimes[GETSTRONGRIGHTBROTHER];
		if (this.strongRightBrother.equals(this)){
//			funcTimes[GETSTRONGRIGHTBROTHER]+= (System.nanoTime()) - t;
			return this;
		}
//		funcTimes[GETSTRONGRIGHTBROTHER]+= (System.nanoTime()) - t;
		this.strongRightBrother=this.strongRightBrother.getStrongRightBrother();
		return this.strongRightBrother;
	}

	protected void setStrongRightBrother(HSNode strongRightBrother) {
		this.strongRightBrother = strongRightBrother.getStrongRightBrother();
	}


	
	protected HSNode getStrongLeftBrotherRaw(){
		return this.strongLeftBrother;
	}

	protected HSNode getStrongRightBrotherRaw(){
		return this.strongRightBrother;
	}

	
	protected void setStrongLeftBrotherUnitary(HSNode strongLeftBrother){
		this.strongLeftBrother = strongLeftBrother;
	}
	
	protected void setStrongRightBrotherUnitary(HSNode strongRightBrother){
		this.strongRightBrother = strongRightBrother;
	}
	
	protected HSNode getLeftestBrother(){
		HSNode nd = this;
		while (true){
			BrotherSearch brs = new BrotherSearch(100000);
			nd=nd.getLeftestBrother(brs);
			if (brs.getDefinitiveResponse())
				break;
		}
		return nd;	
	}
	
	/*recursive */
	protected HSNode getLeftestBrother(BrotherSearch brs){
		
//		long t =System.nanoTime();
		++funcTimes[GETLEFTESTBROTHER];
		if (this.equals(this.left_brother)){
			brs.setDefinitiveResponse(true);
//			funcTimes[GETLEFTESTBROTHER]+= (System.nanoTime()) - t;
			return this;
		}
		
		if (brs.getProof()<=0){
//			funcTimes[GETLEFTESTBROTHER]+= (System.nanoTime()) - t;
			return this;
		}
		
		brs.decreaseProof();

//		funcTimes[GETLEFTESTBROTHER]+= (System.nanoTime()) - t;
		if (this.strongLeftBrother.equals(this)){
			this.strongLeftBrother= this.left_brother.getLeftestBrother(brs);
			
		}
		else {
			this.strongLeftBrother=this.strongLeftBrother.getLeftestBrother(brs);
		}
		return this.strongLeftBrother;
	}
	
	/*recursive 
	 * La funzione arriva al leftest brother passando di fratello in fratello
	 */
	protected HSNode getLeftestSetRightest(HSNode strongRightBrother){
//		long t= System.nanoTime();
		++funcTimes[GETLEFTESTSETRIGHTEST];
		this.strongRightBrother = strongRightBrother;
		if (this.getLeft_brother().equals(this)){
//			funcTimes[GETLEFTESTSETRIGHTEST]+= (System.nanoTime()) - t;
			return this;
		}
//		funcTimes[GETLEFTESTSETRIGHTEST]+= (System.nanoTime()) - t;
		this.strongLeftBrother = this.left_brother.getLeftestSetRightest(strongRightBrother);
		return this.strongLeftBrother;
	}
	
	/*recursive */
	protected HSNode getRightestBrother(){
//		long t=System.nanoTime();
		++funcTimes[GETRIGHTESTBROTHER];
		if (this.equals(this.right_brother)){
			this.strongRightBrother=this;
//			funcTimes[GETRIGHTESTBROTHER]+= (System.nanoTime()) - t;
		}
		else if (this.strongRightBrother.equals(this)){
//			funcTimes[GETRIGHTESTBROTHER]+= (System.nanoTime()) - t;
			this.strongRightBrother= this.right_brother.getRightestBrother();
		}
		else{ 
//			funcTimes[GETRIGHTESTBROTHER]+= (System.nanoTime()) - t;
			this.strongRightBrother=this.strongRightBrother.getRightestBrother();
		}
		return this.strongRightBrother;
	}
	

	
	private void printnodeid(HSNode nd){
		if (nd!=null) System.out.print(""+nd.getNode_id());
		else System.out.print("null");
		return;
	}
	
	public void setControlled(){
		this.controlled=true;
		return;
	}
	
	private void resetControlled(){
		this.controlled=false;
		return;
	}
	
	public Boolean controlled(){
		return controlled;
	}
	
	public void setBccRead(){
		inBcc=true;
	}
	
	public Boolean bccRead (){
		return inBcc;
	}
	
	private Boolean hasLeftBrothers(){
		return (!(left_brother.equals(this)));
	}
	
	private Boolean hasRightBrothers(){
		return (!(right_brother.equals(this)));
	}
	
	public Boolean isInBcc(){
		return (hasLeftBrothers()||hasRightBrothers());
	}
	
	public String addIdToString(){
		return (" "+node_id);
	}
	
	public static void printTimes(){
		for (int i=0; i<VARNUM; ++i){
			System.out.println(i+": "+funcTimes[i]);
		}
	}
	
	/*iterative*/
	protected int getDepth(){
		int dep=0;
		HSNode nd;
		nd=this;
		while(true){
			if (nd.getIstanceFather()!=null&&nd.getIstanceFather().equals(nd))
				return dep;
			++dep;
			nd=nd.getFather();
		}
		
	}
	
	
	
}