package BlockchainComponents;

import java.util.ArrayList;

public class TransactionLog {
	
	private ArrayList<Transaction> receivedTransactions = new ArrayList<Transaction>();
	
	public TransactionLog() {
		
	}

	public ArrayList<Transaction> getReceivedTransactions() {
		return receivedTransactions;
	}
	
	public void clearLedger() {
		this.receivedTransactions.clear();
	}
	
	public void addTransaction(Transaction transaction) {
		this.receivedTransactions.add(transaction);
	}
	
	public void removeTransaction(Transaction transaction) {
		this.receivedTransactions.remove(transaction);
	}
	
	public boolean containsTransaction(Transaction transaction) {
		
		boolean foundTransaction = false;
		for(int i = 0; i < this.receivedTransactions.size(); i++) {
			Transaction mTransaction = this.receivedTransactions.get(i);
			
			if(mTransaction.getTransactionId().equals(transaction.getTransactionId()))
				foundTransaction = true;	
		}
		return foundTransaction;
	}
	
	public void clearTransactionLog() {
		this.receivedTransactions.clear();
	}


}
