<div align="center">

# ✦ Advanced Object-Oriented Programming ✦

<a href="https://github.com/JITHINDSK1/AOOP-PROJECT">
  <img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=24&duration=3000&pause=900&color=00D9FF&center=true&vCenter=true&width=650&lines=Building+safe+banking+logic+in+Java;Objects.+Rules.+Transactions.+Confidence.;Welcome+to+the+AOOP+Project." alt="Animated project introduction" />
</a>

<p>
  <img src="https://img.shields.io/badge/Language-Java_17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/Focus-OOP_Design-6C63FF?style=for-the-badge" alt="OOP Design" />
  <img src="https://img.shields.io/badge/Status-Tests_Passing-00C853?style=for-the-badge&logo=checkmarx&logoColor=white" alt="Tests passing" />
</p>

[![Repository](https://img.shields.io/badge/Explore_the_code-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/JITHINDSK1/AOOP-PROJECT)
[![Practical%201](https://img.shields.io/badge/Open-Practical%20%7C%201-00D9FF?style=flat-square&logo=java&logoColor=white)](./Practical/Practical%20-%201)

</div>

---

## ⚡ Project pulse

This repository collects practical work for **Advanced Object-Oriented Programming**. The first practical is a compact banking domain that favors clear business rules, defensive validation, and transaction safety.

```text
        ┌───────────────┐       transfer        ┌───────────────┐
        │ BankAccount A │ ────────────────────► │ BankAccount B │
        └───────┬───────┘                       └───────┬───────┘
                │                                       │
          validates status                        records ledger
          respects overdraft                      protects balance
                └───────────────┬───────────────────────┘
                                ▼
                        Atomic result ✓
```

## 🗂️ What's inside

| Area | What it demonstrates |
| :-- | :-- |
| [`Practical - 1`](./Practical/Practical%20-%201) | A banking model with accounts, money, ledger records, and fund transfers |
| `Money` | Currency-aware arithmetic using paise as the smallest unit |
| `BankAccount` | Deposits, withdrawals, overdraft limits, freeze/unfreeze, and closing rules |
| `LedgerEntry` | Immutable transaction history exposed through an unmodifiable list |
| `FundTransfer` | Validated account-to-account transfers with rollback on failure |
| `Main` | A zero-dependency verification suite covering the core behaviors |

## ✨ Practical 1 — Banking domain

<details open>
<summary><b>Key rules implemented</b></summary>
<br />

- 💰 Positive, currency-compatible values are required for transactions.
- 🛡️ Withdrawals cannot exceed the account's configured overdraft limit.
- ❄️ Frozen and closed accounts reject transactions.
- 📒 Every successful deposit and withdrawal is recorded in the ledger.
- 🔁 Transfers are atomic: a failed destination credit restores the source account.
- 🚫 Invalid scenarios—currency mismatch, self-transfer, bad amounts—are rejected early.

</details>

## ▶ Run it

From the practical directory:

```bash
javac *.java
java Main
```

The program runs its verification checks and reports a final `passed/total` summary.

## 🧭 Repository map

```text
AOOP-PROJECT/
├── Practical/
│   └── Practical - 1/       # Banking domain + verification runner
└── Project/                 # Reserved for the course project
```

<div align="center">

### Keep learning. Keep building. Keep shipping. 🚀

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:6C63FF,50:00D9FF,100:00C853&height=100&section=footer" width="100%" alt="Animated gradient footer" />

</div>
