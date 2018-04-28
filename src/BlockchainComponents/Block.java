package BlockchainComponents;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import Network.MainNetwork;

public class Block {

	private int index;
	private Timestamp timestamp;
	private String previousHash;
	private ArrayList<Transaction> transactionsData;
	private String myHash;
	
	public Block(int index, Timestamp timestamp, String previousHash, ArrayList<Transaction> transactionsData) {
		this.index = index;
		this.timestamp = timestamp;
		this.previousHash = previousHash;
		this.transactionsData = transactionsData;
		System.out.println("Hashing......");
		this.myHash = calculateHash();
	}
	
	private String calculateHash() {
		//find hash using hash of previous hash + transactions + nonce value,
		int nonce = 0;
		boolean hashFound = false;
		while(!hashFound){
			try {
				System.out.println("Finding hash......");
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				String data = previousHash + 
						transactionsData.toString() + 
						nonce;
				byte[] hash = digest.digest(data.getBytes());
					
				hashFound = checkWithDifficulty(hash);
				nonce++;
			
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Exception at calculateHash in Block: " + e.getLocalizedMessage());
			}
			
			
		}
		return "";
	}
	
	private boolean checkWithDifficulty(byte[] hash){
		System.out.println("Checking hash......");
		int difficulty = MainNetwork.difficulty;
//		String hashString = Base64.getEncoder().encode(hash).toString();
		
		StringBuffer sb = new StringBuffer();
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}
		String hashString = sb.toString();
		System.out.println("Hash " + hashString);
		
		String substringToBeChecked = hashString.substring(0, difficulty);
		System.out.println("Substrings: " + substringToBeChecked + " , " + Collections.nCopies(difficulty, "0"));
		int comparing =  substringToBeChecked.compareTo(String.join("",Collections.nCopies(difficulty, "0")));
		System.out.println("Checking hash: " + comparing);
		if(comparing==0)
			return true;
		else
			return false;
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
