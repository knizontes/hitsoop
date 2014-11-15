package main_pack;

/**
 * The class AtFirstLook holds all the data structures and the methods useful
 * to represent the At First Look Navigational Scketch (NS) of a generic input graph.
 * The input graph is given both one edge after the other and as lists of
 * nodes which are already known to be in the same biconnected component (bcc).
 * 
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

public class HitSoop {

	/**
	 * The nodes in the forest are stored into the
	 * hash table 'forest' which size is 'hashSize'.
	 * The search of an already existing node and
	 * the insertion of a new one takes a constant time.
	 */
	private HSNode [] forest;
	private int hashSize;
	
	/**
	 *Debug global variables controlling function execution time. 	
	 */
	private final static int VARNUM=11;
	
	private final static int SAMEBCC=0;
	private final static int ADDEDGE=1;
	private final static int BCCSETUP=2;
	private final static int COMMONANCESTORSEARCH=3;
	private final static int TREEUNIONCCSETUP=4;
	private final static int ADDBROTHER=5;
	private final static int PARSENUM=6;
	private final static int GETARGNUM=7;
	private final static int ADDBROTHERLIST=8;
	
	private final static int BCCSETUPWHILE1=9;
	private final static int BCCSETUPWHILE2=10;
	
	private long [] funcTimes= new long [VARNUM];
//	private Date dt = new Date();
	
	
	/**
	 * AtFirstLook constructors with and without the hash table 
	 * dimension parameter.
	 * If no parameter is specified, a table of 1 million 
	 * AFLNode is allocated.
	 */
	public HitSoop (int size){
		hashSize=size;
		forest= new HSNode [hashSize];
		for (int i=0; i<VARNUM; ++i)
			funcTimes[i]= 0;
	}
	
	public HitSoop (){
		hashSize=1000000;
		forest= new HSNode [hashSize];
		for (int i=0; i<VARNUM; ++i)
			funcTimes[i]= 0;
	}
	
	/** 
	 * search() finds the id node in the forest if any
	 * in constant time.
	 * @param	id	the identifier of a node
	 * @return	the node identified by id if any, else null
	 */
	public HSNode search(int id){
		if (forest[id]==null) return null;
		return forest[id];
	}
	
	
	/**
	 * add_node() insert the new node id in the 
	 * NavigationalScketch forest. If the node is already present
	 * in the forest the function does nothing (idempotence), 
	 * else the new node 'id' is added.
	 * If the new node id exceed the dimension of the forest hash
	 * table the function does nothing and returns -1
	 * @param 	id
	 * @return	1 if the node is already present in the forest
	 *			0 if the node has correctly inserted into the forest
	 *			-1 if node id exceed the hash table dimension
	 */
	public int add_node(int id){
		if (id>hashSize)
			return -1;
		else if (forest[id]!=null)
			return 1;						
		forest[id] = new HSNode();
		forest[id].setNode_id(id);
		return 0;							
	}

	
	/*------------------------------Beginning the AFL algorithm section--------------------------------*/	
	
	/**
	 * sameBcc() takes two nodes and says whether or not they are in the same Bcc.
	 * @param	and	the first node we want to check
	 * @param	bnd the second node we want to check
	 * @return	true if the nodes are in the same bcc, false otherwise.
	 */
	public Boolean sameBcc(HSNode and, HSNode bnd){
		long t=System.nanoTime();
		if (and.getLeftestBrother().equals(bnd.getLeftestBrother())){
			funcTimes[SAMEBCC]+= (System.nanoTime()) - t;
			return true;
		}
		if(and.getIstanceFather()==null && and.getLeftestBrother().getIstanceFather().equals(bnd)){
			funcTimes[SAMEBCC]+= (System.nanoTime()) - t;
			return true;
		}
		if(bnd.getIstanceFather()==null && bnd.getLeftestBrother().getIstanceFather().equals(and)){
			funcTimes[SAMEBCC]+= (System.nanoTime()) - t;
			return true;
		}
		funcTimes[SAMEBCC]+= (System.nanoTime()) - t;
		return false;
	}
	
	/** 
	 * add_edge() takes as argument a string like "<int> <int>", which represents an edge in the graph
	 * we want to analyze between the nodes identified by the two integers.
	 * Two cases may appear:
	 * 		case 1. node a and node b belongs to different trees, so they are in
	 * 				different connected components (CCs):
	 * 					- treeUnionCCSetup() procedure is applied to the second node of the edge;
	 * 					- the first node is set as the father of the second one.
	 * 		case 2. node a and node b are in the same tree (and connected component):
	 * 			case 2.1. the nodes are already in the same biconnected component (BCC):
	 * 					- ignore the edge and return.
	 * 			case 2.2. the nodes are in the same CC but not in the same BCC:
	 * 					- bbcSetUp() procedure is called upon the two nodes.
	 * 					- the function returns. 
	 * @param	edge	a String "<node_id> <node_id>" which represents an edge in the input graph
	 * 				
	 */
	public void add_edge(String edge){
		long t= System.nanoTime();
		int a,b;
		HSNode and,bnd;
		a = parseNum(edge, 0);
		b = parseNum(edge, 1);
		add_node(a);
		add_node(b);
		/*reflexive edges does not make any change*/
		if (a==b){
			funcTimes[ADDEDGE]+= (System.nanoTime()) - t;
			return;
		}
		
		and = search(a);
		bnd = search(b);
		
		/*case 1: node a and node b are not in the same tree*/
		if (and.getCc().getNode_id()!=bnd.getCc().getNode_id()){
			ccUnionSetup(bnd);
//			bnd.resetCc();	
			bnd.setFather(and);
			funcTimes[ADDEDGE]+= (System.nanoTime()) - t;
			return;
		}
		/*case 2: node a and node b are in the same tree*/
		else{
			/*case 2.1: the nodes are already in the same biconnected component
			 * 	- behavior: ignoring the edge.
			 */
			if(sameBcc(and, bnd)){
				funcTimes[ADDEDGE]+= (System.nanoTime()) - t;
				return;
			}
			
			/*case 2.2: the nodes are in the same CC but not in the same BCC
			 * 	-behavior: union of BCCs.
			 */
			
			bccUnionSetup(and, bnd);
			funcTimes[ADDEDGE]+= (System.nanoTime()) - t;
			return;
		}
		
	}
	

	
	/** 
	 * bccSetUp() sets up the afl tree in which are the two nodes, structuring the brother relationship between them.
	 * It first computes the common ancestor between the nodes and the flag describing the position of the ancestor.
	 * 	case 1. a node is the ancestor of the other one:
	 * 		- the grandson node chain of brother is "merged" with the father one, the resulting chain is 
	 * 			joined to the one of the grandfather and so on;
	 * 		- when the father of the chain is the common ancestor, returns.
	 * case 2. the common ancestor is the father of two ancestors respectively of the first and the second node:
	 * 		 - the intermediate ancestors from both nodes to the ancestor and their possibly empty bcc's are joined to
	 * 			the same bcc.
	 * Notes: the function is iterative.
	 * 
	 * @param and:AFLNode
	 * @param bnd:AFLNode
	 */
	
	private void bccUnionSetup(HSNode and,HSNode bnd){
		long t= System.nanoTime();
		long tw1,tw2;

		HSNode nd1,nd2,lnd,rnd,nd,commonAncestor,tempnd;
		AncestorFlag bfl;
		Boolean b=true,lastnd1=false,lastnd2=false;
		nd1=and;
		nd2=bnd;
		bfl = new AncestorFlag();
		commonAncestor=commonAncestorSearch(and, bnd,bfl);
		// case 1: one of the nodes is an ancestor of the other one
		if (and.equals(commonAncestor)){
			/*setting up lnd and rnd*/
			lnd=bnd;
			rnd = bnd.getRightestBrother(); 
			if (bfl.getFlag()==0){
				while(true){
					if (lnd.getFather().equals(commonAncestor)){
						lnd=lnd.getLeftestBrother();
						break;
					}
						
					lnd=lnd.getFather();
				}
			}
			else if (bfl.getFlag()==1){
				for (int i=0;i<bfl.getDiff();++i)
					lnd=lnd.getFather();
				lnd=lnd.getLeftestBrother();
				
			}
			/*now lnd contains the node which will be the leftest brother of the brothers chain 
			 * rnd the rightest one*/
			
			while (true){
//				nd2.resetBcc();
				if (nd2.equals(and)||nd2.getFather().equals(and)){
					funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
					return;
				}
				if (nd2.getLeft_brother().equals(nd2)){
					nd2.setLeft_brother(nd2.getFather().getRightestBrother());
					nd2.getFather().getRightestBrother().setRight_brother(nd2);
					nd2.resetFather();
				}
				nd2.setStrongLeftBrother(lnd);
				nd2.setStrongRightBrother(rnd);
				nd2=nd2.getLeft_brother();

			}
		}
		
		if (bnd.equals(commonAncestor)){
			/*setting up lnd and rnd*/
			lnd=and;
			rnd = and.getRightestBrother();
			if (bfl.getFlag()==0){
				while(true){
					if (lnd.getFather().equals(commonAncestor)){
						lnd=lnd.getLeftestBrother();
						break;
					}
						
					lnd=lnd.getFather();
				}
			}
			else if (bfl.getFlag()==2){
				for (int i=0;i<bfl.getDiff();++i)
					lnd=lnd.getFather();
				lnd=lnd.getLeftestBrother();
				
			}
			/*now lnd contains the node which will be the leftest brother of the brothers chain 
			 * rnd the rightest one*/
			
			while (b){
//				nd1.resetBcc();
				if (nd1.equals(bnd)||nd1.getFather().equals(bnd)){
					funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
					return;
				}
				if (nd1.getLeft_brother().equals(nd1)){
					nd1.setLeft_brother(nd1.getFather().getRightestBrother());
					nd1.getFather().getRightestBrother().setRight_brother(nd1);
					nd1.resetFather();
				}
				nd1.setStrongLeftBrother(lnd);
				nd1.setStrongRightBrother(rnd);
				nd1=nd1.getLeft_brother();
			}
		}
		/* setting lnd and rnd as respectively the leftest and rightest left brother*/
		//case 2: the nodes have no ancestor which are respectively brother each other
		if (bfl.getFlag()==0){
			rnd = nd2.getRightestBrother();
			lnd= nd1;
			while (true){
				if (lnd.getFather().equals(commonAncestor)){
					lnd=lnd.getLeftestBrother();
					break;
				}
				lnd=lnd.getFather();
			}
			nd=nd1.getRightestBrother();
		}
		//case 3: the first node a has an ancestor which is a left-brother of an ancestor of the second one
		else if (bfl.getFlag()==1){
			rnd = nd1.getRightestBrother();
			lnd=commonAncestor.getLeftestBrother();
			/*nd holds the rightest node in the nd2 chain, 
			 * the one will be added the chain of 
			 * brothers from nd1 to the common ancestor excepted.*/
			nd=nd2.getRightestBrother();
			
		}
		//case 4: the second node a has an ancestor which is a left-brother of an ancestor of the first one
		else {
			rnd=nd2.getRightestBrother();
			lnd=commonAncestor.getLeftestBrother();
			/*nd holds the rightest node in the nd1 chain, 
			 * the one will be added the chain of 
			 * brothers from nd2 to the common ancestor excepted.*/
			nd=nd1.getRightestBrother();
		}
		
		tw1= System.nanoTime();
		tw2= System.nanoTime();
		while (b){
			if (!lastnd2){
				
				
				if ((bfl.getFlag()==0|| bfl.getFlag()==2) && nd2.getIstanceFather()!=null && nd2.getIstanceFather().equals(commonAncestor)){
					lastnd2=true;
					funcTimes[BCCSETUPWHILE1]+= (System.nanoTime()) - tw1;
					tw2= System.nanoTime();
					continue;
				}				
				else if (bfl.getFlag()==1 && nd2.equals(commonAncestor)){
					lastnd2=true;
					funcTimes[BCCSETUPWHILE1]+= (System.nanoTime()) - tw1;
					tw2= System.nanoTime();
					continue;
				}				
				
				if (nd2.getLeft_brother().equals(nd2)){
					tempnd=nd2.getFather().getRightestBrother();
					nd2.setLeft_brother(tempnd);
					tempnd.setRight_brother(nd2);
				}
//				nd2.resetBcc();
				nd2.resetFather();

				tempnd=nd2;
				if (bfl.getFlag()==1) 
					nd2=nd2.getLeft_brother();
				else nd2=nd2.getLeftestBrother();
				tempnd.setStrongLeftBrother(lnd);
				tempnd.setStrongRightBrother(rnd);
			}
			
			else if (!lastnd1){
				if (bfl.getFlag()==2 && ((nd1.getIstanceFather()!=null && nd1.getIstanceFather().equals(commonAncestor))||nd1.equals(commonAncestor))){
					/*In this case we have an inconstincence in lnd and rnd*/					
//					nd2.resetBcc();
					nd.setRight_brother(nd2);
					nd2.setLeft_brother(nd);
					nd2.resetFather();
					nd1.setStrongLeftBrother(lnd);
					nd1.setStrongRightBrother(rnd);
					nd2.setStrongLeftBrother(lnd);
					nd2.setStrongRightBrother(rnd);
					funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
					funcTimes[BCCSETUPWHILE2]+= (System.nanoTime()) - tw2;
					return;
				}
				
				
				if (bfl.getFlag()==0 && nd1.getIstanceFather()!=null && nd1.getIstanceFather().equals(commonAncestor)){
//					nd1.resetBcc();
//					nd2.resetBcc();
					nd2.resetFather();
					nd.setRight_brother(nd2);
					nd2.setLeft_brother(nd);
					nd.setStrongLeftBrother(lnd);
					nd1.setStrongLeftBrother(nd1);
					nd2.setStrongLeftBrother(lnd);
					nd1.setStrongRightBrother(rnd);
					nd2.setStrongRightBrother(rnd);
					nd.setStrongRightBrother(rnd);			
					funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
					funcTimes[BCCSETUPWHILE2]+= (System.nanoTime()) - tw2;
					return;
				}

				if (bfl.getFlag()==1 && nd1.getIstanceFather()!=null && nd1.getIstanceFather().equals(commonAncestor)){
//					nd1.resetBcc();
//					nd2.resetBcc();
					nd.setRight_brother(nd1);
					nd1.setLeft_brother(nd);
					nd1.resetFather();
					nd1.setStrongLeftBrother(lnd);
					nd1.setStrongRightBrother(rnd);
					nd.setStrongLeftBrother(lnd);
					nd.setStrongRightBrother(rnd);
					funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
					funcTimes[BCCSETUPWHILE2]+= (System.nanoTime()) - tw2;
					return;
					
				}
				
				
				if (nd1.getLeft_brother().equals(nd1)){
					tempnd=nd1.getFather().getRightestBrother();
					nd1.setLeft_brother(tempnd);
					tempnd.setRight_brother(nd1);
					nd1.resetFather();
				}

//				nd1.resetBcc();
				tempnd=nd1;
				if (bfl.getFlag()==2)nd1=nd1.getLeft_brother();
				else nd1=nd1.getLeftestBrother();
				tempnd.setStrongLeftBrother(lnd);
				tempnd.setStrongRightBrother(rnd);
			}
			else {
				funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
				funcTimes[BCCSETUPWHILE2]+= (System.nanoTime()) - tw2;
				return;
			}
		}
		funcTimes[BCCSETUP]+= (System.nanoTime()) - t;
	}
	
	
	
	/**
	 * commonAncestorSearch() search the common ancestor between nodes 'and' and 'bnd'.
	 * Notes: the function is iterative.
	 * @param and	the first node we want to check
	 * @param bnd	the second node we want to check
	 * @param brotherFlag	a flag used to distinguish the intermediate nodes ancestors relationship.
	 * 						it may hold the values:
	 * 						 	- 0	if the nodes have not ancestors which are brother respectively;
	 *							- 1	if the first node has an ancestor which is a 
	 *								left brother of the ancestor of the second node;
	 *							- 2 if the second node has an ancestor which is a 
	 *								left brother of an ancestor of the first node.
	 * @return	the AFLNode representing the 'and' and 'bnd' common ancestor
	 */
	
	private HSNode commonAncestorSearch(HSNode and, HSNode bnd, AncestorFlag brotherFlag){
		long t = System.nanoTime();
		int diff, depth, adepth, bdepth;
		HSNode nd1, nd2;
		adepth=and.getDepth();
		bdepth=bnd.getDepth();
		diff= adepth-bdepth;
		nd1=and;
		nd2=bnd;
		if (diff<0){
			diff=-diff;
			brotherFlag.setDiff(diff);
			depth=adepth;
			for (int i=0; i<diff;++i)
				nd2=nd2.getFather();
			if (nd2.equals(and)){
				funcTimes[COMMONANCESTORSEARCH]+= (System.nanoTime()) - t;
				return and;
			}
		}
		else if (diff>0){
			depth=bdepth;
			brotherFlag.setDiff(diff);

			for (int i=0; i<diff;++i)
				nd1=nd1.getFather();
			if (nd1.equals(bnd)){
				funcTimes[COMMONANCESTORSEARCH]+= (System.nanoTime()) - t;
				return bnd;
			}
		}
		else depth=bdepth;
		brotherFlag.setDiff(diff);
		brotherFlag.setAdepth(adepth);
		brotherFlag.setBdepth(bdepth);
		
		while (true){
			/*brotherFlag may hold the value:
			 * 	- 0	if the nodes have not ancestors which are brother respectively;
			 *	- 1	if the first node has an ancestor which is a left brother of the ancestor of the second node;
			 *	- 2 if the second node has an ancestor which is a left brother of an ancestor of the first node.*/
			
			if (nd1.getFather().equals(nd2.getFather())){
				if (nd1.equals(nd2)){
					brotherFlag.setFlag(0);
					brotherFlag.setDepth(depth);
					funcTimes[COMMONANCESTORSEARCH]+= (System.nanoTime()) - t;
					return nd1;
				}			
				if (nd1.hasLeftBrother(nd2)){
					brotherFlag.setFlag(2);
					brotherFlag.setDepth(depth);
					funcTimes[COMMONANCESTORSEARCH]+= (System.nanoTime()) - t;
					return nd2;
				}
				if (nd2.hasLeftBrother(nd1)){
					brotherFlag.setFlag(1);
					brotherFlag.setDepth(depth);
					funcTimes[COMMONANCESTORSEARCH]+= (System.nanoTime()) - t;
					return nd1;
				}
			}
			
			nd1=nd1.getFather();
			nd2=nd2.getFather();
			--depth;
		}
		
	}
	
	/**
	 * treeUnionCCSetup() rotate the nd tree (representing the CC of nd)
	 * making nd the root of the tree.
	 * Notes: recursive.
	 * @param nd	the node we want to make root of the tree
	 */
	private void ccUnionSetup(HSNode nd){
		long t = System.nanoTime();
		HSNode temp, tempnd1, tempnd2, fathernd;
		if (nd.equals(nd.getFather())){
			funcTimes[TREEUNIONCCSETUP]+= (System.nanoTime()) - t;
			return;
		}
		fathernd=nd.getFather();
		if (nd.getLeft_brother().equals(nd)){
			/*in this case the node nd has no left nor right brothers*/
			if (nd.getRight_brother().equals(nd)){
				temp=nd;
				while(fathernd.getLeft_brother().equals(fathernd)&&fathernd.getRight_brother().equals(fathernd)){
					if (nd.equals(nd.getFather())){
						funcTimes[TREEUNIONCCSETUP]+= (System.nanoTime()) - t;
						return;
					}
					tempnd1=nd;
					nd= fathernd;
					fathernd=fathernd.getFather();
					nd.setFather(tempnd1);
				}
				ccUnionSetup(fathernd);
				fathernd.setFather(nd);
				temp.resetFather();
				funcTimes[TREEUNIONCCSETUP]+= (System.nanoTime()) - t;
				return;
			}
			/*in this case the node nd is the leftest brother of a chain of brothers*/
			else {
				ccUnionSetup(fathernd);
				tempnd1=nd.getRight_brother();
				fathernd.setRight_brother(tempnd1);
				tempnd1.setLeft_brother(fathernd);
				nd.setRight_brother(nd);
				nd.setStrongRightBrotherUnitary(nd);
				tempnd1.resetFather();
				fathernd.setStrongRightBrother(tempnd1.getStrongRightBrother());
				tempnd1=fathernd;
				while (true){
					tempnd1.setStrongLeftBrotherUnitary(fathernd);
					if (tempnd1.getRight_brother().equals(tempnd1))
						break;
					tempnd1=tempnd1.getRight_brother();
				}
				nd.resetFather();
				fathernd.setFather(nd);
				funcTimes[TREEUNIONCCSETUP]+= (System.nanoTime()) - t;
				return;
			}
		}
		/*in this case the node nd is the rightest of a chain of brothers*/
		else if (nd.getRight_brother().equals(nd)){
			ccUnionSetup(fathernd);
			tempnd2=nd.getLeft_brother();
			tempnd2.setRight_brother(tempnd2);
			nd.setLeft_brother(nd);
			nd.setRight_brother(nd);
			nd.setStrongLeftBrotherUnitary(nd);
			nd.setStrongRightBrotherUnitary(nd);
			tempnd1=tempnd2.getLeftestBrother();
			tempnd1.setLeft_brother(fathernd);
			fathernd.setRight_brother(tempnd1);
			tempnd1.resetFather();
			tempnd1=fathernd;
			while (true){
				tempnd1.setStrongLeftBrotherUnitary(fathernd);
				tempnd1.setStrongRightBrotherUnitary(tempnd2);
				if (tempnd1.getRight_brother().equals(tempnd1))
					break;
				tempnd1=tempnd1.getRight_brother();
			}
			fathernd.setFather(nd);
			nd.resetFather();
			funcTimes[TREEUNIONCCSETUP]+= (System.nanoTime()) - t;
			return;
		}
		else{
			ccUnionSetup(fathernd);
			tempnd1=nd.getLeftestBrother();
			tempnd2=nd.getRightestBrother();
			nd.getLeft_brother().setRight_brother(nd.getRight_brother());
			nd.getRight_brother().setLeft_brother(nd.getLeft_brother());
			nd.setLeft_brother(nd);
			nd.setRight_brother(nd);
			nd.setStrongLeftBrotherUnitary(nd);
			nd.setStrongRightBrotherUnitary(nd);
			tempnd1.setLeft_brother(fathernd);
			fathernd.setRight_brother(tempnd1);
			tempnd1.resetFather();
			tempnd1=fathernd;
			while (true){
				tempnd1.setStrongLeftBrotherUnitary(fathernd);
				tempnd1.setStrongRightBrotherUnitary(tempnd2);
				if (tempnd1.getRight_brother().equals(tempnd1))
					break;
				tempnd1=tempnd1.getRight_brother();
			}
			fathernd.setFather(nd);
			nd.resetFather();
			funcTimes[TREEUNIONCCSETUP]+= (System.nanoTime()) - t;
			return;
		}
	}
	
	/**
	 * addBrother() adds the node brotherNd to the BCC of nd, linking brotherNd
	 * as nd right brother.
	 * @param nd	the node we want to add a brother node.
	 * @param brotherNd	the node we want to add as brother of nd.
	 */
	private void addBrother(HSNode nd, HSNode brotherNd){
		long t = System.nanoTime();
		nd.setRight_brother(brotherNd);
		brotherNd.setLeft_brother(nd);
		brotherNd.resetFather();
		funcTimes[ADDBROTHER]+= (System.nanoTime()) - t;
	}
	
	
	/**
	 * parseNum() returns the 'num' parameter (an integer from the 'num' substring) in str.
	 * @param str	the string to parse
	 * @param num	the position of the parameter we want
	 * @return the integer in 'num' position in str
	 */
	private int parseNum(String str,int num){
		long t= System.nanoTime();
		int cur=0,pos,pnum=0;
    	
    	Boolean b=true;
    	String s = new String ();
    	
    	if (str.charAt(0)==' ')
    		cur=1;
    	else cur=0;
    	
    	for (;cur<str.length();++cur){
    		if (pnum==num)
    			break;
    		if (str.charAt(cur)==' ')++pnum;
    	}
    	
    	pos=cur;
		while (b){
			
			if (pos>=str.length()||str.charAt(pos)==' ') 
				break;
			++pos;
		}
		
		s=str.substring(cur, pos);
		if (s.equals("")){
			System.out.println("Errore nel processare l'"+num+"-esimo numero di:"+str);
			funcTimes[PARSENUM]+= (System.nanoTime()) - t;
			return -1;
		}
    	cur=(int) Integer.parseInt(s);
    	funcTimes[PARSENUM]+= (System.nanoTime()) - t;
    	return cur;
    }
	
	/**
	 * printNavigationalScketch() prints the navigational scketch to the standard output.
	 */
	public void printNavigationalScketch(){
		for (int i=0; i<hashSize;++i){
			if (forest[i]!=null){
				System.out.print(i+" node: "+forest[i].getNode_id());
				System.out.print("		|father:"+forest[i].getFather().getNode_id());
				System.out.print("		|bcc:"+forest[i].getBcc().getNode_id());
				System.out.print("		|lb:"+forest[i].getLeft_brother().getNode_id());
				System.out.print("		|rb:"+forest[i].getRight_brother().getNode_id());
				System.out.print("		|CC:"+forest[i].getCc().getNode_id());
				System.out.print("		|strongLB:"+forest[i].getStrongLeftBrotherRaw().getNode_id());
				System.out.println("		|strongRB:"+forest[i].getStrongRightBrotherRaw().getNode_id());
			}
		}
	}
	
	/**
	 * printNavigationalScketch() prints the navigational scketch from
	 * begindex node to endindex node to the standard output.
	 * @param begindex	the node from which we want to print the NS
	 * @param endindex	the node to which we want to print the NS
	 */
	
	public void printNavigationalScketch(int begindex, int endindex){
		for (int i=begindex; i<hashSize && i<endindex;++i){
			if (forest[i]!=null){
				System.out.print(i+" node: "+this.forest[i].getNode_id());
				System.out.print("		|father:"+this.forest[i].getFather().getNode_id());
				System.out.print("		|bcc:"+this.forest[i].getBcc().getNode_id());
				System.out.print("		|lb:"+this.forest[i].getLeft_brother().getNode_id());
				System.out.print("		|rb:"+this.forest[i].getRight_brother().getNode_id());
				System.out.print("		|CC:"+this.forest[i].getCc().getNode_id());
				System.out.print("		|strongLB:"+this.forest[i].getStrongLeftBrotherRaw().getNode_id());
				System.out.println("		|strongRB:"+this.forest[i].getStrongRightBrotherRaw().getNode_id());
			}
		}
	}
	
	/**
	 * printFileNavigationalScketch() prints the navigational scketch to the PrintStream out.
	 * @param out	the PrintStream we want to write the NS in.
	 */
	public void printFileNavigationalScketch(PrintStream out){
		for (int i=0; i<hashSize;++i){
			if (forest[i]!=null){
				out.print(i+" nd: "+this.forest[i].getNode_id());
				out.print("		|fth:"+this.forest[i].getFather().getNode_id());
				out.print("		|bcc:"+this.forest[i].getBcc().getNode_id());
				out.print("		|lb:"+this.forest[i].getLeft_brother().getNode_id());
				out.print("		|rb:"+this.forest[i].getRight_brother().getNode_id());
				out.print("		|CC:"+this.forest[i].getCc().getNode_id());
				out.print("		|sLB:"+this.forest[i].getStrongLeftBrotherRaw().getNode_id());
				out.println("		|sRB:"+this.forest[i].getStrongRightBrotherRaw().getNode_id());

			}
		}
	}
	
	
	/**
	 * getArgNum() returns the number of arguments in the String s.
	 * @param s: s is the string which arguments we want to count.  
	 * @return the number of arguments in s. 
	 * 
	 * Sample: a string like s= "<arg0> <arg1> <arg2>" will give 
	 * as output 3.
	 */
	public int getArgNum(String s){
		long t=System.nanoTime();
		int argnum;
		if (s.length()==0)
			argnum=0;
		else argnum=1;
		for (int i=0;i<s.length();++i){
			if (s.charAt(i)==' ')
				++argnum;
		}
		funcTimes[GETARGNUM]+= (System.nanoTime()) - t;
		return argnum;
	}
	
	/**
	 * add_brother_list() takes a string in input with two or more arguments:
	 *		>if there are two arguments, the input holds an edge in the navigational scketch;
	 *		>if there are more arguments, the input string holds a list of node in the same biconnected component.
	 * Notes: lines in s beginning with '#' are comments, so they are ignored.
	 * @param brotherList	holds an edge or a bcc we want to add to the NS.
	 */
	public void add_brother_list (String brotherList){
		long t=System.nanoTime();
		if (brotherList.charAt(0)=='#'||getArgNum(brotherList)<2){
			funcTimes[ADDBROTHERLIST]+= (System.nanoTime()) - t;
			return;
		}
		int bnum;
		int unknownBrotherLst [], brotherLstCursor, knownLstCursor,unionTreeLstCursor, tmp;
		HSNode knownBrotherLst[],unionTreeLst[];
		bnum=getArgNum(brotherList);
		add_edge(brotherList);
		funcTimes[ADDBROTHERLIST]+= (System.nanoTime()) - t;			
	}
	
	
	/**
	 * navigationalScketchToString() returns a string which holds a line for each
	 * bcc in the navigational sketch tree and a line for each remaining
	 * father-son relationship between two nodes.
	 * 
	 * Notes: the return string is the mapper output.
	 * @return	a string representing the NS
	 */
	public StringBuilder navigationalScketchToString(){
		int fatherIndex;
		HSNode nd;
		StringBuilder bcc= new StringBuilder(100000000);
		
		for (int i=0; i<hashSize;++i){
			if ((forest[i]!=null) && !(forest[i].controlled()) && (forest[i].isInBcc())){
				nd=forest[i].getLeftestBrother();
				fatherIndex=nd.getIstanceFather().getNode_id();
				bcc.append(fatherIndex+ " "+ nd.addIdToString()+"\n");
				
				while(!(nd.getRight_brother().equals(nd))){
					nd.setControlled();
					bcc.append(nd.addIdToString()+ " "+ nd.getRight_brother().addIdToString()+"\n");
					nd.setBccRead();
					nd=nd.getRight_brother();
				}	
				
				nd.setControlled();
				bcc.append(fatherIndex+" "+nd.addIdToString()+"\n");
				nd.setBccRead();
			}
		}
		for (int i=0; i<hashSize;++i){
			if (forest[i]!=null && !(forest[i].getIstanceFather()==null)  && !((forest[i].isInBcc())&&(forest[i].getLeft_brother().equals(forest[i]))) &&(!(forest[i].getIstanceFather().equals(forest[i]))))
				bcc.append(forest[i].getFather().getNode_id()+" "+forest[i].getNode_id()+"\n");
		}
		return (bcc);
		
	}

	
	
	/**
	 * navigationalScketchToStringVerbose() returns a string which holds a line for each
	 * bcc in the navigational sketch tree, a line for each remaining
	 * father-son relationship between two nodes, and some comments with the id of
	 * the father node of the bcc.
	 * Notes: the return string is the mapper output.
	 * @return	a string representing the NS
	 */
	public StringBuilder navigationalScketchToStringVerbose(){
		int fatherIndex;
		HSNode nd;
		StringBuilder bcc= new StringBuilder(100000000);
		
		for (int i=0; i<hashSize;++i){
			if ((forest[i]!=null) && !(forest[i].controlled()) && (forest[i].isInBcc())){
				nd=forest[i].getLeftestBrother();
				fatherIndex=nd.getIstanceFather().getNode_id();
				bcc.append("#"+i+":\n"+fatherIndex);
				
				while(!(nd.getRight_brother().equals(nd))){
					nd.setControlled();
					bcc.append(nd.addIdToString()+"\n"+nd.addIdToString());
					nd.setBccRead();
					nd=nd.getRight_brother();
				}	
				
				nd.setControlled();
				bcc.append(nd.addIdToString()+"\n");
				nd.setBccRead();
			}
		}
		for (int i=0; i<hashSize;++i){
			if (forest[i]!=null && !(forest[i].getIstanceFather()==null)  && !((forest[i].isInBcc())&&(forest[i].getLeft_brother().equals(forest[i]))) &&(!(forest[i].getIstanceFather().equals(forest[i]))))
				bcc.append(forest[i].getFather().getNode_id()+" "+forest[i].getNode_id()+"\n");
		}
		return (bcc);
		
	}
	
	public StringBuilder navigationalScketchToStringComplete(){
		int fatherIndex;
		HSNode nd;
		StringBuilder bcc= new StringBuilder(100000000);
		
		for (int i=0; i<hashSize;++i){
			if ((forest[i]!=null) && !(forest[i].controlled()) && (forest[i].isInBcc())){
				nd=forest[i].getLeftestBrother();
				fatherIndex=nd.getIstanceFather().getNode_id();
				bcc.append("#"+i+":\n"+fatherIndex);
				
				while(!(nd.getRight_brother().equals(nd))){
					nd.setControlled();
					bcc.append(nd.addIdToString());
					nd.setBccRead();
					nd=nd.getRight_brother();
				}	
				
				nd.setControlled();
				bcc.append(nd.addIdToString()+"\n");
				nd.setBccRead();
			}
		}
		for (int i=0; i<hashSize;++i){
			if (forest[i]!=null && !(forest[i].getIstanceFather()==null)  && !((forest[i].isInBcc())&&(forest[i].getLeft_brother().equals(forest[i]))) &&(!(forest[i].getIstanceFather().equals(forest[i]))))
				bcc.append(forest[i].getFather().getNode_id()+" "+forest[i].getNode_id()+"\n");
		}
		return (bcc);
		
	}
	
	public void printTimes(){
		System.out.println("\n\n=============================================\n");
		System.out.println("I tempi di esecuzione delle funzioni sono:");
		for (int i=0; i<VARNUM; ++i)
			System.out.println(i+": "+funcTimes[i]);
		System.out.println("funzioni di HSNode:");
		HSNode.printTimes();
		System.out.println("=============================================\n");
	}
	
	
	

	

	public static void maina(String [] args){
		HitSoop afl = new HitSoop();
		Boolean b=true;
		String s;

		while (b){
			s = JOptionPane.showInputDialog("Inserisci un edge o \"end\" per terminare");
			if (s.equals("end")) break;
			System.out.println("Adding "+s+" edge...");
			afl.add_edge(s);

			System.out.println("---------------Navigational scketch---------------");
			afl.printNavigationalScketch();
			System.out.println("----------------------------------------");
		}

		return;
	}

	public static void main(String [] args){
		HitSoop afl ;
		String s,o,outS;
		int prnt=0;
		long cur=0;
		s=new String("/home/knizontes/Projects/hadoop_proj/HitSoop/grafo_Eu/grafo_eu-2005.txt");
//		s=new String("/home/knizontes/debug1");
		o= new String("/home/knizontes/navigationalSketch");

		try {
			BufferedReader in = new BufferedReader( new FileReader(s));
			PrintStream out = new PrintStream(new FileOutputStream(o));

//			s=in.readLine();
			afl= new HitSoop(10000000);
			HSNode debugNode,debudNode2;
//			s=in.readLine();
			String start= new String(DateUtils.now("hh:mm:ss z"));
			while (true){
				prnt= (prnt+1)%100000;
				if (( s=in.readLine())== null)
					break;
				if (s.equals(""))
					continue;
//				System.out.println(s);
//				debugNode=afl.search(4566);
//				if (debugNode!=null)		
//					debudNode2=debugNode.getFather();
//				debugNode=afl.search(599);
//				if (debugNode!=null)		
//					debudNode2=debugNode.getFather();
//				
//				if (s.equals("236 3845")){
////					afl.printFileNavigationalScketch(out);
//					out.println(afl.navigationalScketchToStringVerbose());
//				}
				afl.add_brother_list(s);
				if (prnt==0)
					System.out.println("Adding "+((++cur)*100000)+" edge...");
//				afl.printNavigationalScketch();
//				System.out.println();
				
			}
			System.out.println(start);
			outS=new String();
			System.out.println("---------------Navigational scketch---------------");
			out.println(afl.navigationalScketchToStringVerbose());
//			afl.printFileNavigationalScketch(out);
			System.out.println("--------------------------------------------------");
			System.out.println(DateUtils.now("hh:mm:ss z"));
			afl.printTimes();
//			afl.printNavigationalScketch();
		} catch (IOException e){}

		return;
	}

	
	
}
