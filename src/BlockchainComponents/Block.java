package BlockchainComponents;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Block {

	private int index;
	private Timestamp timestamp;
	private String myHash;
	private String previousHash;
	private ArrayList<Transaction> transactionsData;
	
	public Block(int index, Timestamp timestamp, String previousHash, ArrayList<Transaction> transactionsData) {
		this.index = index;
		this.timestamp = timestamp;
		this.myHash = calculateHash();
		this.previousHash = previousHash;
		this.transactionsData = transactionsData;
		
		//TODO - find nonce
	}
	
	private String calculateHash() {
		//TODO
		return "";
	}

	public int getIndex() {
		return index;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getMyHash() {
		return myHash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactionsData;
	}

}
