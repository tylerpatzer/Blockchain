package blockchainproject;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Wallet {

	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	
	public Wallet() {
		generateKeyPair();
	} // Wallet Constructor
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// initializing the key generator and generates a KeyPair
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			
			// setting the public and private keys
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} // try - catch
	} // generateKeyPair
	
	
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : Ledger.UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) {
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			} // if
		} // for
		return total;
	} // getBalance
	
	public Transaction sendFunds(PublicKey theRecipient, float value) {
		if (getBalance() < value) {
			System.out.println("#Not enough funds to complete this transaction. Transaction Discared");
			return null;
		} // if
		
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		} // for
		
		Transaction newTransaction = new Transaction(publicKey, theRecipient, value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for (TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		} // for
			
		return newTransaction;		
		
	} // sendFunds
	
	
} // Wallet
