
import java.util.UUID;

public class FundTransfer {

    private final String transferId;
    private final BankAccount source;
    private final BankAccount destination;
    private final Money amount;

    public FundTransfer(BankAccount source,
                        BankAccount destination,
                        Money amount) {

        if (source == null || destination == null || amount == null) {
            throw new IllegalArgumentException("Invalid transfer details");
        }

        if (source == destination ||
                source.getAccountNumber().equals(destination.getAccountNumber())) {
            throw new IllegalArgumentException(
                "Source and destination must be different accounts"
            );
        }

        if (!amount.isPositive()) {
            throw new IllegalArgumentException(
                "Transfer amount must be positive"
            );
        }

        if (!source.getBalance().getCurrency()
                .equals(destination.getBalance().getCurrency())
                || !amount.getCurrency().equals(
                    source.getBalance().getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        this.transferId = UUID.randomUUID().toString();
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    public void transfer() {

        Money originalSourceBalance = source.getBalance();
        Money originalDestinationBalance = destination.getBalance();

        source.withdraw(amount);

        try {
            destination.deposit(amount);

        } catch (RuntimeException e) {

            // Compensating rollback for this in-memory implementation.
            source.restoreBalance(originalSourceBalance);
            source.removeLastLedgerEntry();

            destination.restoreBalance(originalDestinationBalance);
            destination.removeLastLedgerEntry();

            throw new IllegalStateException(
                "Transfer failed. Balances restored.", e
            );
        }
    }

    public BankAccount getSource() {
        return source;
    }

    public BankAccount getDestination() {
        return destination;
    }

    public Money getAmount() {
        return amount;
    }

    public String getTransferId() {
        return transferId;
    }
}
