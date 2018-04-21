package Network;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public Wallet(PublicKey publicKey, PrivateKey privateKey) {
		this.setPublicKey(publicKey);
		this.setPrivateKey(privateKey);
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
}
