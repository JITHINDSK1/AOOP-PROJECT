
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankAccount {

    private final String accountNumber;
    private Money balance;
    private final Money overdraftLimit;
    private AccountStatus status;
    private final List<LedgerEntry> ledger;

    public BankAccount(String accountNumber, Money balance,
                       Money overdraftLimit) {

        if (accountNumber == null || accountNumber.isBlank()
                || balance == null || overdraftLimit == null) {
            throw new IllegalArgumentException("Invalid account details");
        }

        if (overdraftLimit.isNegative()) {
            throw new IllegalArgumentException("Invalid overdraft limit");
        }

        if (!balance.getCurrency().equals(overdraftLimit.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        if (balance.add(overdraftLimit).isNegative()) {
            throw new IllegalArgumentException("Balance exceeds overdraft limit");
        }

        this.accountNumber = accountNumber;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.status = AccountStatus.ACTIVE;
        this.ledger = new ArrayList<>();
    }

    public void deposit(Money amount) {
        validateTransaction(amount);
        checkActive();

        balance = balance.add(amount);
        ledger.add(new LedgerEntry("DEPOSIT", amount));
    }

    public void withdraw(Money amount) {
        validateTransaction(amount);
        checkActive();

        Money newBalance = balance.subtract(amount);

        if (newBalance.add(overdraftLimit).isNegative()) {
            throw new IllegalStateException("Overdraft limit exceeded");
        }

        balance = newBalance;
        ledger.add(new LedgerEntry("WITHDRAWAL", amount));
    }

    public void freeze() {
        checkNotClosed();
        status = AccountStatus.FROZEN;
    }

    public void unfreeze() {
        if (status != AccountStatus.FROZEN) {
            throw new IllegalStateException("Account is not frozen");
        }

        status = AccountStatus.ACTIVE;
    }

    public void close() {
        checkActive();

        if (balance.isNegative()) {
            throw new IllegalStateException(
                "Outstanding overdraft must be cleared before closing"
            );
        }

        status = AccountStatus.CLOSED;
    }

    public Money getBalance() {
        return balance;
    }

    public Money getOverdraftLimit() {
        return overdraftLimit;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public List<LedgerEntry> getLedger() {
        return Collections.unmodifiableList(ledger);
    }

    private void validateTransaction(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException(
                "Transaction amount must be positive"
            );
        }

        if (!amount.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }

    private void checkActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
    }

    private void checkNotClosed() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is closed");
        }
    }

    // Used by FundTransfer for rollback
    void restoreBalance(Money previousBalance) {
        if (previousBalance == null ||
                !previousBalance.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException("Invalid rollback balance");
        }

        balance = previousBalance;
    }

    // Used by FundTransfer to remove uncommitted ledger entry on rollback
    void removeLastLedgerEntry() {
        if (!ledger.isEmpty()) {
            ledger.remove(ledger.size() - 1);
        }
    }
}
