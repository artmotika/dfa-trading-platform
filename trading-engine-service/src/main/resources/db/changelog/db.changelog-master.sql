CREATE TABLE users (
   id VARCHAR(255) PRIMARY KEY,
   wallet_address VARCHAR(255) NOT NULL UNIQUE,
   kyc_status VARCHAR(50) NOT NULL,
   aml_risk_score INT DEFAULT 0
);

CREATE TABLE assets (
    id VARCHAR(255) PRIMARY KEY,
    solana_mint_address VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    total_supply BIGINT NOT NULL
);

CREATE TABLE orders (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id),
    asset_id VARCHAR(255) NOT NULL REFERENCES assets(id),
    type VARCHAR(10) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE trades_ledger (
   id VARCHAR(255) PRIMARY KEY,
   order_id VARCHAR(255) NOT NULL REFERENCES orders(id),
   transaction_hash VARCHAR(255),
   execution_price DECIMAL(19, 4) NOT NULL,
   timestamp TIMESTAMP NOT NULL
);