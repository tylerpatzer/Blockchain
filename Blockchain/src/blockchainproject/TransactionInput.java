package blockchainproject;

public class TransactionInput {

	public String transactionOutputId; // reference to TransactionOutputs -> transactionId
	public TransactionOutput UTXO; // Contains the Unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	} // TransactionInput Constructor
	
} // TransactionInput
