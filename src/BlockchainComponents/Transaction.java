package BlockchainComponents;

public class Transaction {

	private String transactionId;
	
	public Transaction() {
		setTransactionId(this.toString());
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}
