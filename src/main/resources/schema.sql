CREATE TABLE IF NOT EXISTS customers (
                                         customer_id TEXT PRIMARY KEY,
                                         category TEXT NOT NULL,
                                         first_name TEXT NOT NULL,
                                         surname TEXT NOT NULL,
                                         id_or_reg_number TEXT,
                                         tax_id TEXT,
                                         address TEXT,
                                         phone TEXT,
                                         email TEXT
);

CREATE TABLE IF NOT EXISTS bank_staff (
                                          staff_id TEXT PRIMARY KEY,
                                          first_name TEXT NOT NULL,
                                          surname TEXT NOT NULL,
                                          role TEXT NOT NULL,
                                          branch TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
                                        account_number TEXT PRIMARY KEY,
                                        customer_id TEXT NOT NULL,
                                        account_type TEXT NOT NULL,
                                        balance TEXT NOT NULL,
                                        branch TEXT NOT NULL,
                                        status TEXT NOT NULL,
                                        employer_name TEXT,
                                        employer_address TEXT,
                                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id TEXT PRIMARY KEY,
                                            account_number TEXT NOT NULL,
                                            transaction_type TEXT NOT NULL,
                                            amount TEXT NOT NULL,
                                            balance_after TEXT NOT NULL,
                                            transaction_date TEXT NOT NULL,
                                            FOREIGN KEY (account_number) REFERENCES accounts(account_number)
    );
