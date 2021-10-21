package blockchainproject;

import java.security.PublicKey;

public class TransactionOutput {
	
	public String id;
	public PublicKey reciepient; // new owner of the coins
	public float value; // the amount of coins that they own
	public String parentTransactionId; // the id of the transaction this output was created in
	
	
	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.reciepient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
	} // TransactionOutput Constructor 
	
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	} // isMine
	
} // TransactionOutput
