package Network;

import java.util.ArrayList;
import java.util.Random;

public class MainNetwork {
	
	public static void main(String[] args) {
		
		User user1 = new User(1);
		User user2 = new User(2);
		User user3 = new User(3);
		User user4 = new User(4);
		User user5 = new User(5);
		User user6 = new User(6);
		User user7 = new User(7);

		ArrayList<User> peers = new ArrayList<User>();
		peers.add(user2);
		peers.add(user4);
		peers.add(user5);
		user1.addPeers(peers);
		
		peers.clear();
		peers.add(user1);
		peers.add(user3);
		peers.add(user4);
		peers.add(user6);
		peers.add(user7);
		user2.addPeers(peers);
		
		peers.clear();
		peers.add(user2);
		peers.add(user6);
		user3.addPeers(peers);
		
		peers.clear();
		peers.add(user2);
		peers.add(user1);
		peers.add(user7);
		user4.addPeers(peers);
		
		peers.clear();
		peers.add(user1);
		peers.add(user6);
		user5.addPeers(peers);
		
		peers.clear();
		peers.add(user2);
		peers.add(user3);
		peers.add(user5);
		user6.addPeers(peers);
		
		peers.clear();
		peers.add(user2);
		peers.add(user4);
		user7.addPeers(peers);
		
		ArrayList<User> networkUsers = new ArrayList<User>();
		networkUsers.add(user1);
		networkUsers.add(user2);
		networkUsers.add(user3);
		networkUsers.add(user4);
		networkUsers.add(user5);
		networkUsers.add(user6);
		networkUsers.add(user7);	

		//Add user randomly to network every 30 seconds
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	addUserRandomly(networkUsers);
		            }
		        }, 
		        20000,30000 
		);
		
		//create transaction randomly to network every 1 seconds
		new java.util.Timer().schedule( 
				new java.util.TimerTask() {
					@Override
					public void run() {
						createTransactionRandomly(networkUsers);
					}
				}, 
				1000, 10000 
		);
	}
	
	
	public static void createTransactionRandomly(ArrayList<User> networkUsers) {
		Random rand = new Random();
		int userIndex = rand.nextInt(networkUsers.size());
		System.out.println("User " + networkUsers.get(userIndex).getUserId() + " announcing a transaction...");
		boolean announcement = networkUsers.get(userIndex).announceTransaction(networkUsers.get(userIndex).createTransaction());
		System.out.println("Announcement from user " + networkUsers.get(userIndex).getUserId() + " : " + announcement);
	}
	
	
	public static void addUserRandomly(ArrayList<User> networkUsers) {
		User user = new User(networkUsers.size()+1);
		System.out.println("Adding random user " + user.getUserId() + " to network...");
		user.addPeers(getRandomPeers(networkUsers));
		networkUsers.add(user);
	}
	
	
	public static ArrayList<User> getRandomPeers(ArrayList<User> networkUsers){
		Random rand = new Random();
		int numOfPeers = rand.nextInt(networkUsers.size()) + 2;
		ArrayList<User> peers = new ArrayList<User>();
		
		for(int i = 0; i < numOfPeers; i++) {
			int peerIndex = rand.nextInt(networkUsers.size());
			User peerUser = networkUsers.get(peerIndex);
			if(!peers.contains(peerUser))
				peers.add(peerUser);
		}
		return peers;
	}
	
	
	

}
