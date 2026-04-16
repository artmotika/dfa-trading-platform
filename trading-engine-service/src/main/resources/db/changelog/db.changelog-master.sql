--liquibase formatted sql

--changeset initial:2
CREATE TABLE IF NOT EXISTS users (
   id VARCHAR(255) PRIMARY KEY,
   wallet_address VARCHAR(255) NOT NULL UNIQUE,
   kyc_status VARCHAR(50) NOT NULL,
   aml_risk_score INT DEFAULT 0,
   password VARCHAR(255),
   is_qualified BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS assets (
    id VARCHAR(255) PRIMARY KEY,
    solana_mint_address VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    total_supply BIGINT NOT NULL,
    type VARCHAR(50),
    status VARCHAR(50),
    ipo_price DECIMAL(19, 4),
    legal_doc_hash VARCHAR(255),
    trade_unlock_timestamp BIGINT
);

CREATE TABLE IF NOT EXISTS investor_limits (
    user_id VARCHAR(255) PRIMARY KEY REFERENCES users(id),
    annual_investment DECIMAL(19, 4) NOT NULL,
    last_reset TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id),
    asset_id VARCHAR(255) NOT NULL REFERENCES assets(id),
    type VARCHAR(10) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS tax_ledger (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) REFERENCES users(id),
    order_id VARCHAR(255) REFERENCES orders(id),
    tax_amount DECIMAL(19, 4) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS corporate_actions (
    id VARCHAR(255) PRIMARY KEY,
    asset_id VARCHAR(255) REFERENCES assets(id),
    type VARCHAR(50) NOT NULL, -- DIVIDEND, COUPON
    amount_per_share DECIMAL(19, 4) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, COMPLETED
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS trades_ledger (
   id VARCHAR(255) PRIMARY KEY,
   order_id VARCHAR(255) NOT NULL REFERENCES orders(id),
   transaction_hash VARCHAR(255),
   execution_price DECIMAL(19, 4) NOT NULL,
   timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_balances (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) REFERENCES users(id),
    asset_id VARCHAR(255) REFERENCES assets(id),
    amount DECIMAL(19, 4) NOT NULL,
    weighted_average_cost DECIMAL(19, 4),
    last_update TIMESTAMP NOT NULL
);
