
import java.time.LocalDateTime;
import java.util.UUID;

public class LedgerEntry {

    private final String transactionId;
    private final String transactionType;
    private final Money amount;
    private final String timestamp;

    public LedgerEntry(String transactionType, Money amount) {

        if (transactionType == null || transactionType.isBlank()
                || amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Invalid ledger entry");
        }

        this.transactionId = UUID.randomUUID().toString();
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public Money getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return transactionId + " | " + transactionType
                + " | " + amount + " | " + timestamp;
    }
}
