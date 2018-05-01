package BlockchainComponents;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
		
		this.transactionsData = new ArrayList<Transaction>();
		for (int i = 0; i < transactionsData.size(); i++) {
			this.transactionsData.add(transactionsData.get(i));
		}
		
		if(index == 0) {
			this.myHash = "00";
		}else {
			this.myHash = calculateHash();
		}
	}
	
	private String calculateHash() {
		System.out.println();
		//Find hash using hash of previous hash + transactions + nonce value,
		int nonce = 0;
		boolean hashFound = false;
		byte[] hash = null;
		
		while(!hashFound){
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				String data = previousHash + transactionsData.toString() + nonce;
				hash = digest.digest(data.getBytes());
					
				hashFound = checkWithDifficulty(hash);
				nonce++;
			
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Exception at calculateHash in Block: " + e.getLocalizedMessage());
			}
		}
		
		System.out.println("Found Hash for block " + index);
		StringBuffer sb = new StringBuffer();
		
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}
		
		String hashString = sb.toString();
		return hashString;
	}
	
	private boolean checkWithDifficulty(byte[] hash){
		int difficulty = MainNetwork.difficulty;		
		StringBuffer sb = new StringBuffer();
		
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}
		
		String hashString = sb.toString();
//		System.out.println("Hash " + hashString);
		
		String substringToBeChecked = hashString.substring(0, difficulty);
//		System.out.println("Substrings: " + substringToBeChecked + " , " + new String(new char[difficulty]).replace("\0", "0"));
		
		int comparing =  substringToBeChecked.compareTo(String.join("",new String(new char[difficulty]).replace("\0", "0")));
		
		if(comparing == 0)
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

	public void printBlock() {
		System.out.println("Block " + this.index);
		System.out.println("Previous Hash:  " + this.previousHash);
		System.out.println("My Hash:  " + this.myHash);
		System.out.println("Transactions: ");
		
		for(int i = 0; i < transactionsData.size(); i++) {
			System.out.println(transactionsData.get(i).getTransactionId());
		}
		System.out.println("------------------------------");
	}
	
}
