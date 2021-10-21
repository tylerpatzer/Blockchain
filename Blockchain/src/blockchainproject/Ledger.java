package blockchainproject;

import java.security.Security;

import java.util.ArrayList;
import java.util.HashMap;






public class Ledger {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public static int difficulty = 3; 
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;
	
	public static void main(String[] args) {
	
		/*
		
		blockchain.add(new Block("Hi im the first block", "0"));
		//Block genesisBlock = new Block("Hi im the first block", "0");
		//System.out.println("Hash for block 1 : " + genesisBlock.hash);
		System.out.println("Mining block one!");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new Block("Second Block", blockchain.get(blockchain.size() - 1).hash));
		//Block secondBlock = new Block("Yo, i'm the second block", genesisBlock.hash);
		//System.out.println("Hash for block 2: " + secondBlock.hash);
		System.out.println("Mining block 2!"); 
		blockchain.get(1).mineBlock(difficulty);
		
		blockchain.add(new Block("Third Block", blockchain.get(blockchain.size() - 1).hash));
		//Block thirdBlock = new Block("Block 3", secondBlock.hash);
		System.out.println("Mining block 3");
		blockchain.get(2).mineBlock(difficulty);
		
		//System.out.println("Hash for block 3: " + thirdBlock.hash);
		System.out.println("\nBlockchain is Valid: " + isValid());
		//Setup Bouncey castle as a Security Provider
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
				//Create the new wallets
				walletA = new Wallet();
				walletB = new Wallet();
				//Test public and private keys
				System.out.println("Private and public keys:");
				System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
				System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
				//Create a test transaction from WalletA to walletB 
				Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
				transaction.generateSignature(walletA.privateKey);
				//Verify the signature works and verify it from the public key
				System.out.println("Is signature verified:");
				System.out.println(transaction.verifySignature());
		
		
		
		//String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		//System.out.println("\nThe block chain");
		//System.out.println(blockchainJson);
		  
		 
		 */
		
		//add our blocks to the blockchain ArrayList:
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
				
				//Create wallets:
				walletA = new Wallet();
				walletB = new Wallet();		
				Wallet coinbase = new Wallet();
				
				//create genesis transaction, which sends 100 NoobCoin to walletA: 
				genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
				genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
				genesisTransaction.transactionId = "0"; //manually set the transaction id
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
				
				System.out.println("Creating and Mining Genesis block... ");
				Block genesis = new Block("0");
				genesis.addTransaction(genesisTransaction);
				addBlock(genesis);
				
				//testing
				Block block1 = new Block(genesis.hash);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
				block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
				addBlock(block1);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				Block block2 = new Block(block1.hash);
				System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
				block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
				addBlock(block2);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				Block block3 = new Block(block2.hash);
				System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
				block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				isValid();
		
		
	} // main
	
	public static boolean isValid() {
		Block presentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loops through the blockchain
		for (int i = 1; i < blockchain.size(); i++) {
			presentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			
			//compares present hashes
			if (!presentBlock.hash.equals(presentBlock.calculateHash())) {
				System.out.println("The present hashes are not equal");
				return false;
			} // if
			
			//compares previous hashes
			if (!previousBlock.hash.contentEquals(previousBlock.calculateHash())) {
				System.out.println("Previous hashes are not equal");
				return false;
			} // if
			
			// checks if solved
			if (!presentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#Previous Hashes not equal");
				return false;
			} // if
			
			TransactionOutput tempOutput;
			
			for (int t = 0; t < presentBlock.transactions.size(); t++) {
				Transaction currentTransaction = presentBlock.transactions.get(t);
				
				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on transaction(" + t + ") is Invalid");
					return false;
				} // if
				
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are not equal to Outputs on Transaction(" + t + ")");
					return false;
				} // if
				
				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if (tempOutput == null) {
						System.out.println("#Referenced input on transaction(" + t + ") is Missing.");
						return false;
					} // if
					
					if (input.UTXO.value != tempOutput.value) {					
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid.");
						return false;
					} // if
					
					tempUTXOs.remove(input.transactionOutputId);					
				} // for
				
				for (TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				} // for
				
				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				} // if
				
				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				} // if
			} // inside for
		} // outside for
		
		System.out.println("Blockchain is valid");
		return true;
	} // isValid
	
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	} // addBlock
} // Ledger
