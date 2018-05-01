package BlockchainComponents;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Blockchain {

	private LinkedHashMap<String, Integer> blockchainLengths= new LinkedHashMap<String, Integer>();
	private ArrayList<Block> blockchain = new ArrayList<Block>();
	private ArrayList<Block> cache = new ArrayList<Block>();
	private Block lastBlock;
	private static Block genesisBlock;

	public Blockchain() {
		initBlockChain();
	}

	private void initBlockChain() {

		if(genesisBlock == null) 
			Blockchain.genesisBlock = new Block(0, new Timestamp(System.currentTimeMillis()), "00", new ArrayList<Transaction>());

		this.blockchain.add(genesisBlock);
		this.blockchainLengths.put(genesisBlock.getMyHash(), 1);
		this.lastBlock = genesisBlock;
	}

	public static Block getGenesisBlock() {
		return genesisBlock;
	}

	public int getNewIndex() {
		return this.blockchain.size();
	}

	public boolean addBlock(Block block) {
		boolean blockAddedToBlockChain, blockAddedToCache = false;
		blockAddedToBlockChain = addBlockToBlockChain(block);
		
		if(!blockAddedToBlockChain) {
			blockAddedToCache = addBlockToCache(block);
		}
		printBlockchain();
		
		return (blockAddedToBlockChain || blockAddedToCache);
	}

	public boolean addBlockToBlockChain(Block block) {
		String prevHash= block.getPreviousHash();

		//Check if previous hash is last block in current blockchain -> Add block to the end of blockchain
		if(lastBlock.getMyHash().equals(prevHash)) {
			blockchain.add(block);
			blockchainLengths.put(block.getMyHash(), blockchain.size() + 1);
			lastBlock = block;
			
			//Updating cache blocks
			updateCache();
			
			return true;
		}
		//Loop over blockchain to check if previous hash is the current hash of any block in the chain
		else {
			for(int i = 0; i < blockchain.size(); i++) {
				Block blockToCheckWith = blockchain.get(i);

				//Found middle block with hash equal to previous hash -> Add block to cache
				if(blockToCheckWith.getMyHash().equals(prevHash)) {
					cache.add(block);
					int lengthOfPrevBlock = blockchainLengths.get(blockToCheckWith.getMyHash());
					blockchainLengths.put(block.getMyHash(), lengthOfPrevBlock + 1);
					return true;
				}
			}
		}
		return false;
	}


	public boolean addBlockToCache(Block block) {
		String prevHash= block.getPreviousHash();

		for(int i = 0; i < cache.size(); i++) {
			Block blockToCheckWith = cache.get(i);

			//Found previous block in cache
			if(blockToCheckWith.getMyHash().equals(prevHash)) {
				int lengthOfPrevBlock = blockchainLengths.get(blockToCheckWith.getMyHash());
				int currentLength = lengthOfPrevBlock + 1;

				//Cache blockchain longer than current blockchain --> replace current block chain
				if(currentLength > blockchain.size()) {
					ArrayList<Block> replacementBlocks = getReplacementBlocks(block);
					
					//Get index of fork point in blockchain
					String hashOfBlockInBlockChain = (replacementBlocks.get(replacementBlocks.size() - 1)).getPreviousHash();
					int forkPosInBlockchain = findPosInBlockchain(hashOfBlockInBlockChain);
					if(forkPosInBlockchain == -1) {
						break;
					}
					//Removing blocks from fork index from blockchain and adding to cache
					ArrayList<Block> blocksToAddInCache = new ArrayList<Block>(blockchain.subList(forkPosInBlockchain + 1, blockchain.size()));
					blockchain.subList(forkPosInBlockchain + 1, blockchain.size()).clear();					
					cache.addAll(blocksToAddInCache);
					
					//Adding replacement blocks to current blockchain
					Collections.reverse(replacementBlocks);
					blockchain.addAll(replacementBlocks);
					lastBlock = blockchain.get(blockchain.size()-1);
					
					//Updating cache blocks
					updateCache();
					return true;
				}
				else {
					//Add block to cache
					cache.add(block);
					blockchainLengths.put(block.getMyHash(), currentLength);
					return true;
				}
			}
		}

		return false;
	}

	//Returns position of block in blockchain
	public int findPosInBlockchain(String hash) {
		int index = -1;
		for (int i = 0; i < blockchain.size(); i++) {
			String currHash = blockchain.get(i).getMyHash();
			if(currHash.equals(hash)) {
				index = i;
			}
		}
		return index;
	}

	//Returns a list of blocks to replace part of the current blockchain
	public ArrayList<Block> getReplacementBlocks(Block newBlock){
		
		boolean allBlocksfoundInCache = false;
		ArrayList<Block> replacementBlocks = new ArrayList<Block>();
		
		String prevHash = newBlock.getPreviousHash();
		replacementBlocks.add(newBlock);
		
		while(!allBlocksfoundInCache) {
			
			boolean blockfoundInCache = false;
			for (int i = 0; i < cache.size(); i++) {
				Block blockToCheckWith = cache.get(i);
				
				//Add block to list
				if(prevHash.equals(blockToCheckWith.getMyHash())) {
					replacementBlocks.add(blockToCheckWith);
					cache.remove(blockToCheckWith);
					prevHash = blockToCheckWith.getPreviousHash();
					blockfoundInCache = true;
					break;
				}
			}
			
			if(!blockfoundInCache)
				allBlocksfoundInCache = true;
		}
		return replacementBlocks;
	}
	

	//Removes blocks that have a distance difference bigger than 3 with the current blockchain length
	public void updateCache() {
		
		for (int i = 0; i < cache.size(); i++) {
			Block currBlock = cache.get(i);
			int currBlockLength = blockchainLengths.get(currBlock.getMyHash());
			
			if(blockchain.size() - currBlockLength > 3) {
				cache.remove(currBlock);
				blockchainLengths.remove(currBlock.getMyHash());
			}
		}	
	}
	

	public String getLastHash() {
		return this.lastBlock.getMyHash();
	}

	public ArrayList<Block> getBlockchain() {
		return blockchain;
	}

	public Block getLastBlock() {
		return lastBlock;
	}

	public ArrayList<Block> getCache() {
		return cache;
	}

	public void printBlockchain() {
		for(int i = 0; i < blockchain.size(); i++)
			blockchain.get(i).printBlock();
		System.out.println("************************************************************");
	}
	
	

	

}
