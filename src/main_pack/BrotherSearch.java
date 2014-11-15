package main_pack;

public class BrotherSearch {
	
	private int proof;
	private Boolean definitiveResponse;
	private HSNode responseNode;
	
	public BrotherSearch(int proof){
		this.proof=proof;
		this.definitiveResponse=false;
	}

	protected HSNode getResponseNode() {
		return responseNode;
	}

	protected void setResponseNode(HSNode responseNode) {
		this.responseNode = responseNode;
	}

	
	protected int getProof() {
		return proof;
	}

	protected void setProof(int proof) {
		this.proof = proof;
	}

	protected Boolean getDefinitiveResponse() {
		return definitiveResponse;
	}

	protected void setDefinitiveResponse(Boolean definitiveResponse) {
		this.definitiveResponse = definitiveResponse;
	}
	
	protected void decreaseProof(){
		if (this.proof>0)
			--this.proof;
	}

}
