package blockchainproject;

import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.ArrayList;



public class Transaction {
	
	public String transactionId; // hash of the transaction
	public PublicKey sender; 
	public PublicKey reciepient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0;
	
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	} // Transaction Constructor
	
	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence);
	} // calulateHash
	
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringUtil.applyECDSAS(privateKey, data);
	} // generateSignature
	
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	} // verifySignature
	
	public boolean processTransaction() {
		
		if (verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		} // if
		
		for(TransactionInput i : inputs) {
			i.UTXO = Ledger.UTXOs.get(i.transactionOutputId);
		} // for
		
		if(getInputsValue() < Ledger.minimumTransaction) {
			System.out.println("#TransactionInputs to small: " + getInputsValue());
			return false;
		} // if
		
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
		
		for (TransactionOutput o : outputs) {
			Ledger.UTXOs.put(o.id, o);
		} // for
		
		for (TransactionInput i : inputs) {
			if (i.UTXO == null) continue;
			Ledger.UTXOs.remove(i.UTXO.id);
		} // for
		
		return true;
	} // processTransaction
	
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		} // for
		
		return total;
			
	} // getInputsValue
	
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		} // for
		
		return total;
	} // getOutputsValue
	
	
} // Transaction
