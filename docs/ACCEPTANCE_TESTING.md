# Acceptance Testing Guide: Pellu DFA Platform (Solana Devnet)

This guide provides step-by-step instructions to verify the full functional cycle of the Pellu platform using the REST API and Solana Devnet.

## Prerequisites
1.  **Solana CLI**: Installed and configured to `devnet`.
2.  **Environment**: Services (`api-gateway`, `solana-connector`, `trading-engine`, `auth-service`) are running.
3.  **Kafka & Postgres**: Up and running.
4.  **Admin Key**: The private key in `solana-connector` must have some Devnet SOL for transaction fees.

---

## Phase 1: User Onboarding & Compliance

Before any trading, users must be registered on-chain and pass KYC.

### 1.1 Register Users
Register two users (User A and User B) to simulate trading.
```bash
# Register User A
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{"wallet": "USER_A_SOLANA_WALLET_ADDRESS", "password": "password123"}'

# Register User B
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{"wallet": "USER_B_SOLANA_WALLET_ADDRESS", "password": "password123"}'
```
**Expected**: `solana-connector` logs "Registering User on Solana". A `UserAccount` PDA is created on-chain.

### 1.2 Approve KYC
Users cannot trade without approved KYC.
```bash
# Approve User A
curl -X POST http://localhost:8080/api/v1/admin/kyc \
-H "Content-Type: application/json" \
-d '{"userId": "USER_A_UUID", "approved": true}'
```
**Expected**: Solana state for User A's PDA updates `is_kyc_approved = true`.

---

## Phase 2: Asset Lifecycle (IPO)

### 2.1 Create New Asset (IPO_PLANNED)
Create a Digital Financial Asset (e.g., Equity in a Real Estate project).
```bash
curl -X POST http://localhost:8080/api/v1/admin/assets \
-H "Content-Type: application/json" \
-d '{
  "name": "Pellu HQ Equity",
  "totalSupply": 10000,
  "type": "EQUITY",
  "ipoPrice": 100.00,
  "legalDocHash": "QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6uco",
  "tradeUnlockTimestamp": 1713175200
}'
```
**Expected**:
1.  Database stores asset with `IPO_PLANNED` status.
2.  Solana creates an `AssetRegistry` PDA linked to the provided `solanaMintAddress`.

### 2.2 Start IPO (IPO_ACTIVE)
Activate the primary offering.
```bash
curl -X POST "http://localhost:8080/api/v1/admin/ipo/start?assetId=ASSET_UUID"
```
**Expected**: `AssetRegistry.is_ipo_active` becomes `true` on Solana. Secondary trading is still locked.

### 2.3 Finalize IPO (TRADING)
Enable secondary market trading.
```bash
curl -X POST "http://localhost:8080/api/v1/admin/ipo/finalize?assetId=ASSET_UUID"
```
**Expected**: `AssetStatus` becomes `TRADING`. On-chain `is_ipo_active` might be toggled off, and the asset is ready for Peer-to-Peer trades.

---

## Phase 3: Secondary Trading & AML

### 3.1 Execute Trade
Submit an order for User A. The Trading Engine and API Gateway will validate KYC and account status, resolving the wallet addresses and forwarding to the Solana Connector for execution.
```bash
# Submit an order
curl -X POST http://localhost:8080/api/v1/orders \
-H "Content-Type: application/json" \
-d '{
  "userId": "USER_A_UUID",
  "assetId": "ASSET_UUID",
  "type": "SELL",
  "amount": 50,
  "price": 100.00
}'
```
**Expected**:
1.  API Gateway resolves User A's wallet.
2.  Trading Engine matches or executes the order against the platform counterparty.
3.  Solana Connector automatically derives ATA (Associated Token Accounts) if not provided.
4.  Solana Program verifies KYC and freeze status for both wallets and executes the transfer via admin/platform authorization.

### 3.2 AML Freeze
Freeze User B's account due to suspicious activity.
```bash
curl -X POST http://localhost:8080/api/v1/admin/freeze \
-H "Content-Type: application/json" \
-d '{"userId": "USER_B_UUID", "freeze": true}'
```
**Expected**: On-chain `UserAccount.is_frozen` becomes `true`. Subsequent trade attempts will fail with `AccountFrozen`.

---

## Phase 4: Corporate Actions

### 4.1 Initiate Voting
Start a shareholder vote on a new proposal.
```bash
curl -X POST http://localhost:8080/api/v1/admin/vote \
-H "Content-Type: application/json" \
-d '{
  "assetId": "ASSET_UUID",
  "title": "Should we expand to Europe?",
  "options": ["Yes", "No", "Abstain"]
}'
```
**Expected**:
1.  `Voting` PDA created on Solana.
2.  Users can now call `cast_vote` on-chain (weight = token balance). The contract uses a `UserVote` PDA to securely prevent double-voting.

### 4.2 Distribute Dividends
Payout profits to asset holders.
```bash
curl -X POST http://localhost:8080/api/v1/admin/dividends/distribute \
-H "Content-Type: application/json" \
-d '{
  "assetId": "ASSET_UUID",
  "totalAmount": 5000,
  "sourceTokenAccount": "ADMIN_RESERVE_ATA"
}'
```
**Expected**: Corporate action triggers dividend payout messages via Kafka. On-chain `distribute_dividend` is called under strict admin authorization. Holders receive tokens proportional to their share.

---

## Phase 5: Administrative Recovery (Clawback)

Recover assets from a lost key or by court order.
```bash
curl -X POST http://localhost:8080/api/v1/admin/clawback \
-H "Content-Type: application/json" \
-d '{
  "assetId": "ASSET_UUID",
  "targetTokenAccount": "USER_B_ATA",
  "destinationTokenAccount": "ADMIN_RECOVERY_ATA",
  "amount": 50,
  "reason": "Court Order #123"
}'
```
**Expected**: Tokens are moved from User B to Admin even without User B's signature, using the `platform_auth` PDA authority.
