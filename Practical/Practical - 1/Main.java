import java.util.List;

public class Main {
    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("STARTING BANKING APPLICATION VERIFICATION TESTS");
        System.out.println("==================================================");

        testMoneyValidation();
        testMoneyArithmetic();
        testAccountStatus();
        testAccountDeposit();
        testAccountWithdrawal();
        testOverdraftRestrictions();
        testFreezingAndUnfreezing();
        testAccountClosure();
        testLedgerImmutabilityAndTracking();
        testFundTransferSuccess();
        testFundTransferFailureAndRollback();
        testCurrencyMismatchRejection();

        System.out.println("==================================================");
        System.out.println(String.format("TEST RESULTS: %d/%d tests passed successfully.", testsPassed, testsTotal));
        System.out.println("==================================================");

        if (testsPassed != testsTotal) {
            System.exit(1);
        }
    }

    private static void assertTrue(String message, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + message);
        } else {
            System.err.println("[FAIL] " + message);
        }
    }

    private static void testMoneyValidation() {
        System.out.println("\n--- Testing Money Validation ---");
        try {
            new Money(100, null);
            assertTrue("Money should reject null currency", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Money rejects null currency", true);
        }

        try {
            new Money(100, "   ");
            assertTrue("Money should reject blank currency", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Money rejects blank currency", true);
        }

        Money m = new Money(5000, "INR");
        assertTrue("Money amount matches (5000 paise)", m.getAmountInPaise() == 5000);
        assertTrue("Money currency matches (INR)", "INR".equals(m.getCurrency()));
        assertTrue("Money isPositive() returns true for positive", m.isPositive());
        assertTrue("Money isNegative() returns false for positive", !m.isNegative());

        Money zero = new Money(0, "INR");
        assertTrue("Money 0 is not positive", !zero.isPositive());
        assertTrue("Money 0 is not negative", !zero.isNegative());

        Money neg = new Money(-100, "INR");
        assertTrue("Money negative isNegative() returns true", neg.isNegative());
        assertTrue("Money negative isPositive() returns false", !neg.isPositive());
    }

    private static void testMoneyArithmetic() {
        System.out.println("\n--- Testing Money Arithmetic & Currency Safety ---");
        Money m1 = new Money(1000, "INR");
        Money m2 = new Money(500, "INR");
        Money m3 = new Money(500, "USD");

        Money sum = m1.add(m2);
        assertTrue("Money add() gives 1500 paise", sum.getAmountInPaise() == 1500);

        Money diff = m1.subtract(m2);
        assertTrue("Money subtract() gives 500 paise", diff.getAmountInPaise() == 500);

        try {
            m1.add(m3);
            assertTrue("Money add should reject currency mismatch", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Money add rejects currency mismatch (INR vs USD)", true);
        }

        try {
            m1.subtract(m3);
            assertTrue("Money subtract should reject currency mismatch", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Money subtract rejects currency mismatch (INR vs USD)", true);
        }
    }

    private static void testAccountStatus() {
        System.out.println("\n--- Testing AccountStatus Enum ---");
        assertTrue("AccountStatus contains ACTIVE", AccountStatus.ACTIVE != null);
        assertTrue("AccountStatus contains FROZEN", AccountStatus.FROZEN != null);
        assertTrue("AccountStatus contains CLOSED", AccountStatus.CLOSED != null);
        assertTrue("AccountStatus has exactly 3 values", AccountStatus.values().length == 3);
    }

    private static void testAccountDeposit() {
        System.out.println("\n--- Testing Account Deposit ---");
        BankAccount acc = new BankAccount("ACC001", new Money(10000, "INR"), new Money(5000, "INR"));
        acc.deposit(new Money(5000, "INR"));
        assertTrue("Deposit increases balance to 15000", acc.getBalance().getAmountInPaise() == 15000);

        try {
            acc.deposit(new Money(0, "INR"));
            assertTrue("Deposit should reject zero amount", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Deposit rejects zero amount", true);
        }

        try {
            acc.deposit(new Money(-100, "INR"));
            assertTrue("Deposit should reject negative amount", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Deposit rejects negative amount", true);
        }

        try {
            acc.deposit(new Money(100, "USD"));
            assertTrue("Deposit should reject currency mismatch", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Deposit rejects currency mismatch", true);
        }
    }

    private static void testAccountWithdrawal() {
        System.out.println("\n--- Testing Account Withdrawal ---");
        BankAccount acc = new BankAccount("ACC002", new Money(10000, "INR"), new Money(5000, "INR"));
        acc.withdraw(new Money(4000, "INR"));
        assertTrue("Withdrawal decreases balance to 6000", acc.getBalance().getAmountInPaise() == 6000);

        try {
            acc.withdraw(new Money(0, "INR"));
            assertTrue("Withdrawal should reject zero amount", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Withdrawal rejects zero amount", true);
        }

        try {
            acc.withdraw(new Money(-500, "INR"));
            assertTrue("Withdrawal should reject negative amount", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Withdrawal rejects negative amount", true);
        }

        try {
            acc.withdraw(new Money(1000, "EUR"));
            assertTrue("Withdrawal should reject currency mismatch", false);
        } catch (IllegalArgumentException e) {
            assertTrue("Withdrawal rejects currency mismatch", true);
        }
    }

    private static void testOverdraftRestrictions() {
        System.out.println("\n--- Testing Overdraft Restrictions ---");
        // Balance 1000, overdraft 2000 => maximum allowed withdrawal is 3000
        BankAccount acc = new BankAccount("ACC003", new Money(1000, "INR"), new Money(2000, "INR"));

        // Withdraw within overdraft
        acc.withdraw(new Money(2500, "INR"));
        assertTrue("Account can utilize overdraft (balance is -1500)", acc.getBalance().getAmountInPaise() == -1500);

        // Attempt to exceed overdraft (remaining overdraft capacity is 500, try to withdraw 501)
        try {
            acc.withdraw(new Money(501, "INR"));
            assertTrue("Withdrawal exceeding overdraft must fail", false);
        } catch (IllegalStateException e) {
            assertTrue("Withdrawal exceeding overdraft is blocked", true);
        }
        assertTrue("Balance remains -1500 after rejected withdrawal", acc.getBalance().getAmountInPaise() == -1500);
    }

    private static void testFreezingAndUnfreezing() {
        System.out.println("\n--- Testing Freezing and Unfreezing ---");
        BankAccount acc = new BankAccount("ACC004", new Money(5000, "INR"), new Money(1000, "INR"));
        acc.freeze();
        assertTrue("Account status is FROZEN", acc.getStatus() == AccountStatus.FROZEN);

        try {
            acc.withdraw(new Money(1000, "INR"));
            assertTrue("Frozen account must not allow withdrawal", false);
        } catch (IllegalStateException e) {
            assertTrue("Frozen account rejects withdrawal", true);
        }

        try {
            acc.deposit(new Money(1000, "INR"));
            assertTrue("Frozen account must not allow deposit", false);
        } catch (IllegalStateException e) {
            assertTrue("Frozen account rejects deposit", true);
        }

        acc.unfreeze();
        assertTrue("Account status is ACTIVE after unfreeze", acc.getStatus() == AccountStatus.ACTIVE);
        acc.withdraw(new Money(1000, "INR"));
        assertTrue("Account allows withdrawal after unfreeze (balance 4000)", acc.getBalance().getAmountInPaise() == 4000);
    }

    private static void testAccountClosure() {
        System.out.println("\n--- Testing Account Closure ---");
        BankAccount acc = new BankAccount("ACC005", new Money(2000, "INR"), new Money(1000, "INR"));
        acc.close();
        assertTrue("Account is CLOSED", acc.getStatus() == AccountStatus.CLOSED);

        try {
            acc.withdraw(new Money(500, "INR"));
            assertTrue("Closed account cannot withdraw", false);
        } catch (IllegalStateException e) {
            assertTrue("Closed account rejects withdrawal", true);
        }

        try {
            acc.freeze();
            assertTrue("Closed account cannot be frozen", false);
        } catch (IllegalStateException e) {
            assertTrue("Closed account cannot be frozen", true);
        }

        // Account with outstanding overdraft cannot close
        BankAccount overdrawnAcc = new BankAccount("ACC006", new Money(500, "INR"), new Money(2000, "INR"));
        overdrawnAcc.withdraw(new Money(1000, "INR")); // balance -500
        try {
            overdrawnAcc.close();
            assertTrue("Cannot close account with negative balance", false);
        } catch (IllegalStateException e) {
            assertTrue("Account with negative balance cannot be closed until cleared", true);
        }
    }

    private static void testLedgerImmutabilityAndTracking() {
        System.out.println("\n--- Testing Ledger Records & Immutability ---");
        BankAccount acc = new BankAccount("ACC007", new Money(10000, "INR"), new Money(0, "INR"));
        acc.deposit(new Money(2000, "INR"));
        acc.withdraw(new Money(3000, "INR"));

        List<LedgerEntry> ledger = acc.getLedger();
        assertTrue("Ledger has 2 entries", ledger.size() == 2);
        assertTrue("Entry 1 is DEPOSIT", "DEPOSIT".equals(ledger.get(0).getTransactionType()));
        assertTrue("Entry 1 amount is 2000", ledger.get(0).getAmount().getAmountInPaise() == 2000);
        assertTrue("Entry 1 has non-null transactionId", ledger.get(0).getTransactionId() != null);
        assertTrue("Entry 1 has non-null timestamp", ledger.get(0).getTimestamp() != null);

        assertTrue("Entry 2 is WITHDRAWAL", "WITHDRAWAL".equals(ledger.get(1).getTransactionType()));
        assertTrue("Entry 2 amount is 3000", ledger.get(1).getAmount().getAmountInPaise() == 3000);

        // Test immutability of ledger list returned to external callers
        try {
            ledger.add(new LedgerEntry("FRAUD", new Money(9999, "INR")));
            assertTrue("External ledger modification must fail", false);
        } catch (UnsupportedOperationException e) {
            assertTrue("Ledger cannot be modified externally (UnsupportedOperationException)", true);
        }

        // Test LedgerEntry validation
        try {
            new LedgerEntry(null, new Money(100, "INR"));
            assertTrue("LedgerEntry rejects null type", false);
        } catch (IllegalArgumentException e) {
            assertTrue("LedgerEntry rejects null transaction type", true);
        }

        try {
            new LedgerEntry("DEPOSIT", new Money(0, "INR"));
            assertTrue("LedgerEntry rejects non-positive amount", false);
        } catch (IllegalArgumentException e) {
            assertTrue("LedgerEntry rejects non-positive amount", true);
        }
    }

    private static void testFundTransferSuccess() {
        System.out.println("\n--- Testing FundTransfer Success ---");
        BankAccount src = new BankAccount("SRC001", new Money(10000, "INR"), new Money(0, "INR"));
        BankAccount dest = new BankAccount("DEST001", new Money(5000, "INR"), new Money(0, "INR"));

        FundTransfer ft = new FundTransfer(src, dest, new Money(4000, "INR"));
        ft.transfer();

        assertTrue("Source balance deducted to 6000", src.getBalance().getAmountInPaise() == 6000);
        assertTrue("Destination balance credited to 9000", dest.getBalance().getAmountInPaise() == 9000);
        assertTrue("Source ledger recorded WITHDRAWAL", src.getLedger().size() == 1);
        assertTrue("Destination ledger recorded DEPOSIT", dest.getLedger().size() == 1);
        assertTrue("TransferId is not null", ft.getTransferId() != null && !ft.getTransferId().isBlank());
    }

    private static void testFundTransferFailureAndRollback() {
        System.out.println("\n--- Testing FundTransfer Failure & Rollback ---");
        BankAccount src = new BankAccount("SRC002", new Money(10000, "INR"), new Money(0, "INR"));
        BankAccount dest = new BankAccount("DEST002", new Money(5000, "INR"), new Money(0, "INR"));

        // Freeze destination so destination.deposit() throws IllegalStateException
        dest.freeze();

        FundTransfer ft = new FundTransfer(src, dest, new Money(3000, "INR"));
        try {
            ft.transfer();
            assertTrue("Transfer to frozen destination should fail", false);
        } catch (IllegalStateException e) {
            assertTrue("Transfer failed and caught exception", true);
        }

        // Verify full rollback: source balance must be untouched and no partial ledger record
        assertTrue("Source balance rolled back to 10000", src.getBalance().getAmountInPaise() == 10000);
        assertTrue("Destination balance remained 5000", dest.getBalance().getAmountInPaise() == 5000);
        assertTrue("Source ledger contains no uncommitted entries", src.getLedger().isEmpty());
        assertTrue("Destination ledger contains no uncommitted entries", dest.getLedger().isEmpty());

        // Test transfer exceeding source overdraft
        BankAccount brokeSrc = new BankAccount("SRC003", new Money(1000, "INR"), new Money(500, "INR"));
        BankAccount goodDest = new BankAccount("DEST003", new Money(2000, "INR"), new Money(0, "INR"));
        FundTransfer overTransfer = new FundTransfer(brokeSrc, goodDest, new Money(2000, "INR"));
        try {
            overTransfer.transfer();
            assertTrue("Transfer exceeding source overdraft must fail", false);
        } catch (IllegalStateException e) {
            assertTrue("Transfer exceeding overdraft was blocked at withdrawal", true);
        }
        assertTrue("Source balance unchanged after overdraft failure", brokeSrc.getBalance().getAmountInPaise() == 1000);
        assertTrue("Destination balance unchanged after overdraft failure", goodDest.getBalance().getAmountInPaise() == 2000);
    }

    private static void testCurrencyMismatchRejection() {
        System.out.println("\n--- Testing Currency Mismatch Rejection ---");
        BankAccount inrAcc = new BankAccount("INR01", new Money(10000, "INR"), new Money(0, "INR"));
        BankAccount usdAcc = new BankAccount("USD01", new Money(10000, "USD"), new Money(0, "USD"));

        try {
            new FundTransfer(inrAcc, usdAcc, new Money(100, "INR"));
            assertTrue("FundTransfer must reject accounts with different currencies", false);
        } catch (IllegalArgumentException e) {
            assertTrue("FundTransfer rejects currency mismatch between accounts", true);
        }

        BankAccount inrAcc2 = new BankAccount("INR02", new Money(5000, "INR"), new Money(0, "INR"));
        try {
            new FundTransfer(inrAcc, inrAcc2, new Money(100, "USD"));
            assertTrue("FundTransfer must reject transfer amount currency mismatch", false);
        } catch (IllegalArgumentException e) {
            assertTrue("FundTransfer rejects transfer amount currency mismatch", true);
        }

        try {
            new FundTransfer(inrAcc, inrAcc, new Money(100, "INR"));
            assertTrue("FundTransfer must reject transfer to same account", false);
        } catch (IllegalArgumentException e) {
            assertTrue("FundTransfer rejects transfer to same account", true);
        }
    }
}
