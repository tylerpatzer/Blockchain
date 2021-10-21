package blockchainproject;


import java.util.Date;
import java.util.ArrayList;


 

public class Block {

	// vars 

	public String hash;
	public String previousHash;
	public String data; 
	public int nonce;
	public long timeStamp;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList <Transaction>();
	
	public Block (String previousHash) {

		
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime(); 
		this.hash = calculateHash(); 

	} // Block Constructor
	
	public String calculateHash() {
		String calculatedHash = StringUtil.applySha256(
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) +
				data);
		return calculatedHash;
	} // calculateHash
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Additional block mined: " + hash);
	} // mineBlock
	
	public boolean addTransaction(Transaction transaction) {
		if (transaction == null) return true;
		if ((previousHash != "0")) {
			if ((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			} // inner if
		} // outer if
		
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to block");
		return true; 
	} // addTransaction

} // Block 