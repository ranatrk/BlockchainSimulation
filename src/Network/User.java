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
		Random rand = new Random();

		for (int i = 0; i < numOfPeers; i++) {
			int randomIndex = rand.nextInt(peers.size());

			while(indices.contains(new Integer(randomIndex)) && indices.size() < peers.size()) {
				randomIndex = rand.nextInt(peers.size());
			}

			indices.add(randomIndex);
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
		//At n transactions announce ledger as new block
//		System.out.println("Size of  Transaction Log: "+ transactionLog.getReceivedTransactions().size());
		if(this.transactionLog.getReceivedTransactions().size() == 5) {
			announceBlock(this.transactionLog);
			this.transactionLog.clearTransactionLog();
		} 
		
		//Add announced transaction to ledger
		if(!this.transactionLog.containsTransaction(transaction)) {
			System.out.println("transaction not in log");
			this.transactionLog.addTransaction(transaction);
			ArrayList<User> peersToAnnounceTo =  getRandomPeers();		
			for(int i = 0; i < peersToAnnounceTo.size(); i++) {
				User peer = peersToAnnounceTo.get(i);
				peer.announceTransaction(transaction);
			}
			System.out.println("User " + this.userId + " announcing transaction to peers: " + peersToAnnounceTo.toString());
			return true;
		}
		else {
			System.out.println("User " + this.userId + " stopping propagation");
		}
		return false;
	}


	public void announceBlock(TransactionLog transactionLog) {
		System.out.println("User " + userId + " announcing block...");
		Block blockToAnnounce = new Block(this.blockchain.getNewIndex(), new Timestamp(System.currentTimeMillis())
				, this.blockchain.getLastHash() ,transactionLog.getReceivedTransactions());

		//Adding mined block to my blockchain
		boolean blockAdded = blockchain.addBlock(blockToAnnounce);
		transactionLog.clearTransactionLog();

		//Announce block to peers
		for(int i = 0; i < this.peers.size(); i++) {
			User peer = this.peers.get(i);
			peer.receiveBlock(blockToAnnounce);
		}
	}

	//Forwards block to peers
	public void receiveBlock(Block receivedBlock) {
		
		if(blockExists(receivedBlock))
			return;
		System.out.println("User " + userId + " recieved block: ");
//		printBlock(receivedBlock);
		boolean blockAdded = blockchain.addBlock(receivedBlock);

		//Removing duplicate transactions in received block from transaction log 
		if(blockAdded) {
			for (int i = 0; i < receivedBlock.getTransactions().size(); i++) {
				//Found duplicate transaction
				if(transactionLog.containsTransaction(receivedBlock.getTransactions().get(i))) {
//					System.out.println("Transactionlog size when finding duplicate: " + receivedBlock.getTransactions().size());
					transactionLog.removeTransaction(receivedBlock.getTransactions().get(i));
				}
			}
		}

		for(int i = 0; i < this.peers.size(); i++) {
			User peer = this.peers.get(i);
			peer.receiveBlock(receivedBlock);
		}
	}


	public boolean blockExists(Block block) {
		//Check in blockchain
		for(int i = 0; i < blockchain.getBlockchain().size(); i++) {
			if(block.getMyHash().equals(blockchain.getBlockchain().get(i).getMyHash()))
				return true;
		}

		//Check in cache
		for(int i = 0; i < blockchain.getCache().size(); i++) {
			if(block.getMyHash().equals(blockchain.getCache().get(i).getMyHash()))
				return true;
		}
		
		return false;
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
	
	public void printBlock(Block block) {
		System.out.println("Block " + block.getIndex());
		System.out.println("Previous Hash:  " + block.getPreviousHash());
		System.out.println("My Hash:  " + block.getMyHash());
		System.out.println("Transactions: ");
		
		for(int i = 0; i < block.getTransactions().size(); i++) {
			System.out.println(block.getTransactions().get(i).getTransactionId());
		}
		System.out.println("------------------------------");
	}

}
