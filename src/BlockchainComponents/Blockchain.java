package BlockchainComponents;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Blockchain {
	
	//TODO we might need to change the datastructure to work with fork
	private LinkedHashMap<String, Block> blockchain= new LinkedHashMap<String, Block>();
	private Block lastBlock;
	private static Block genesisBlock;
	
	public Blockchain() {
		initBlockChain();
	}
	
	private void initBlockChain() {
		
		//TODO create genesis block and set it to last block
		if(genesisBlock == null) 
			this.genesisBlock = new Block(0, new Timestamp(System.currentTimeMillis()), "", new ArrayList<Transaction>());
			
		this.blockchain.put("", genesisBlock);
		this.lastBlock = genesisBlock;
	}
	
	public static Block getGenesisBlock() {
		return genesisBlock;
	}

	public int getNewIndex() {
		return this.blockchain.size();
	}
	
	public void addBlock(Block block) {
		//TODO
	}
	
	public String getLastHash() {
		return this.lastBlock.getMyHash();
	}
	
	public LinkedHashMap<String, Block> getBlockchain() {
		return blockchain;
	}
	
	public Block getLastBlock() {
		return lastBlock;
	}


}
