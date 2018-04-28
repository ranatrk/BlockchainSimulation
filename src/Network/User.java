package Network;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import BlockchainComponents.Block;
import BlockchainComponents.Blockchain;
import BlockchainComponents.TransactionLog;
import BlockchainComponents.Transaction;
public class User {

	private int userId;
	private PublicKey publicKeyID;
	private ArrayList<User> peers;
	private Wallet wallet;
	private TransactionLog transactionLog;
	private Blockchain blockchain;
	
	public User(int id) {
		this.userId = id;
		this.wallet = createWallet();
		this.publicKeyID = wallet.getPublicKey();
		this.transactionLog = new TransactionLog();
		this.blockchain = new Blockchain();
		this.peers = new ArrayList<User>();
	}
	
	private Wallet createWallet() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA","SUN");
			keyGen.initialize(1024);
			
			KeyPair keyPair = keyGen.genKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
		    return new Wallet(publicKey, privateKey);

		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.err.println("Exception at createWallet in User: " + e.getLocalizedMessage());
		}
		return null;
	}
	
	//Add neighboring peers to user
	public void addPeers(ArrayList<User> peers) {
		this.peers.addAll(peers);
		System.out.println("Added peers for user " + this.userId + ": " + this.peers.toString());
	}
	
	//Generate random number of peers to announce transaction to
	private int getNumOfPeers() {
		Random rand = new Random();
		return rand.nextInt(peers.size()) + (peers.size()/2);
	}
	
	//Get selected users from peers to announce transaction to
	private ArrayList<User> getRandomPeers(){
	
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<User> randomPeers = new ArrayList<User>();
		
		int numOfPeers = getNumOfPeers();
//		System.out.println("Random peers num for user " + this.userId + " : " + numOfPeers);
		Random rand = new Random();
		
		for (int i = 0; i < numOfPeers; i++) {
			int randomIndex = rand.nextInt(peers.size());
			
			while(indices.contains(new Integer(randomIndex)) && indices.size() < peers.size()) {
				randomIndex = rand.nextInt(peers.size());
			}
				
			indices.add(randomIndex);
//			System.out.println("Adding random peer for user " +  this.userId + " : " + this.peers.get(randomIndex).userId);
			randomPeers.add(this.peers.get(randomIndex));
		}
		return randomPeers;
	}
	
	public int getUserId() {
		return userId;
	}

	//Create transaction to announce
	public Transaction createTransaction() {
		Transaction transaction = new Transaction();
		this.signTransaction(transaction);
		return transaction;
	}
	
	//Sign transaction using private key
	private void signTransaction(Transaction transaction) {
		try {
			Signature sig = Signature.getInstance("SHA1withDSA");
			byte[] data = transaction.getTransactionId().getBytes();
//			PublicKey publicKey = wallet.getPublicKey();
			PrivateKey privateKey = wallet.getPrivateKey();
			
			// initialize the signing of the transaction 
			sig.initSign(privateKey);
			sig.update(data); 
			byte[] signature = sig.sign();
			
			// verify the signature of the transaction
			// sig.initVerify(publicKey);
			// sig.update(data);
			
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Exception at signTransaction in User: " + e.getLocalizedMessage());
		} catch (InvalidKeyException e) {
			System.err.println("Exception at signTransaction in User: " + e.getLocalizedMessage());
		} catch (SignatureException e) {
			System.err.println("Exception at signTransaction in User: " + e.getLocalizedMessage());
		} 
	}
	
	//Announce transaction to random subset of peers
	public boolean announceTransaction(Transaction transaction) {
		
		//Add announced transaction to ledger
		if(!this.transactionLog.containsTransaction(transaction)) {
			System.out.println("transaction not in log");
			this.transactionLog.addTransaction(transaction);
			
			ArrayList<User> peersToAnnounceTo =  getRandomPeers();		
			for(int i = 0; i < peersToAnnounceTo.size(); i++) {
				User peer = peersToAnnounceTo.get(i);
				peer.announceTransaction(transaction);
			}
			
			//At n transactions announce ledger as new block TODO - set n
			if(this.transactionLog.getReceivedTransactions().size() >= 5)
				announceBlock(this.transactionLog);
			
			
			System.out.println("User " + this.userId + " announcing transaction to peers: " + peersToAnnounceTo.toString());
			return true;
		}
		else {
			System.out.println("User " + this.userId + " stopping propagation");
		}
		return false;
	}
	

	public void announceBlock(TransactionLog transactionLog) {

		Block blockToAnnounce = new Block(this.blockchain.getNewIndex(), new Timestamp(System.currentTimeMillis())
				, this.blockchain.getLastHash() ,transactionLog.getReceivedTransactions());
		
		blockchain.addBlock(blockToAnnounce);
		//TODO Add block created to my current blockchain.
		for(int i = 0; i < this.peers.size(); i++) {
			User peer = this.peers.get(i);
			peer.verifyBlock(blockToAnnounce);
		}
	
		//TODO
	}
	
	
	public boolean verifyBlock(Block receivedBlock) {
		
		//TODO - send block to peers to reach all network
		for(int i = 0; i < this.peers.size(); i++) {
			User peer = this.peers.get(i);
//			peer.verifyBlock(receivedBlock);
		}
		
		boolean correctBlock = true;
		
		//TODO - block verification mechanism
		
		return correctBlock;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public TransactionLog getLedger() {
		return transactionLog;
	}
	
	public Blockchain getBlockchain() {
		return blockchain;
	}
	
	@Override
	public String toString() {
		return this.userId + "";
	}
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		
	}

}
