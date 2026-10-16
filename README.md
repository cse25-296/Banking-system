# Banking System

Core banking domain model implemented in Java. The model includes customers,
bank staff, accounts, account types, and transactions. It provides basic
account deposits and withdrawals, account status handling, and monthly
interest calculations. This project does not include a GUI or database.

## Run

From the repository root, compile and run the console demonstration with:

```sh
mkdir -p out
javac -d out .devcontainer/src/banking/*.java
java -cp out banking.Main
```
