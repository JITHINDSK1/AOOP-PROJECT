
public class Money {

    private final long amountInPaise;
    private final String currency;

    public Money(long amountInPaise, String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Invalid currency");
        }

        this.amountInPaise = amountInPaise;
        this.currency = currency;
    }

    public Money add(Money other) {
        checkCurrency(other);
        return new Money(
            Math.addExact(this.amountInPaise, other.amountInPaise),
            currency
        );
    }

    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(
            Math.subtractExact(this.amountInPaise, other.amountInPaise),
            currency
        );
    }

    public boolean isPositive() {
        return amountInPaise > 0;
    }

    public boolean isNegative() {
        return amountInPaise < 0;
    }

    public long getAmountInPaise() {
        return amountInPaise;
    }

    public String getCurrency() {
        return currency;
    }

    private void checkCurrency(Money other) {
        if (other == null || !currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }

    @Override
    public String toString() {
        return currency + " " + (amountInPaise / 100.0);
    }
}
