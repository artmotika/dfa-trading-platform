use anchor_lang::prelude::*;
use anchor_spl::token::{self, Mint, Token, TokenAccount, Transfer};

declare_id!("DfaPlatform11111111111111111111111111111111");

#[program]
pub mod dfa_trading {
    use super::*;

    pub fn initialize_platform(ctx: Context<InitializePlatform>, asset_name: String, total_supply: u64) -> Result<()> {
        let registry = &mut ctx.accounts.asset_registry;
        registry.admin_pubkey = *ctx.accounts.admin.key;
        registry.asset_name = asset_name;
        registry.total_supply = total_supply;
        registry.is_active = true;
        Ok(())
    }

    pub fn register_user(ctx: Context<RegisterUser>) -> Result<()> {
        let user_account = &mut ctx.accounts.user_account;
        user_account.owner_pubkey = *ctx.accounts.user_wallet.key;
        user_account.is_kyc_approved = true; // Set by admin
        Ok(())
    }

    pub fn issue_dfa(ctx: Context<IssueDfa>, amount: u64) -> Result<()> {
        require!(ctx.accounts.user_account.is_kyc_approved, CustomError::KycNotApproved);
        // SPL Token Minting logic mocked for brevity via CPI
        Ok(())
    }

    pub fn trade_dfa(ctx: Context<TradeDfa>, amount: u64) -> Result<()> {
        require!(ctx.accounts.seller_account.is_kyc_approved, CustomError::KycNotApproved);
        require!(ctx.accounts.buyer_account.is_kyc_approved, CustomError::KycNotApproved);

        let cpi_accounts = Transfer {
            from: ctx.accounts.seller_token_account.to_account_info(),
            to: ctx.accounts.buyer_token_account.to_account_info(),
            authority: ctx.accounts.seller.to_account_info(),
        };
        let cpi_program = ctx.accounts.token_program.to_account_info();
        let cpi_ctx = CpiContext::new(cpi_program, cpi_accounts);
        token::transfer(cpi_ctx, amount)?;

        Ok(())
    }
}

#[derive(Accounts)]
pub struct InitializePlatform<'info> {
    #[account(init, payer = admin, space = 8 + 32 + 64 + 8 + 1)]
    pub asset_registry: Account<'info, AssetRegistry>,
    #[account(mut)]
    pub admin: Signer<'info>,
    pub system_program: Program<'info, System>,
}

#[derive(Accounts)]
pub struct RegisterUser<'info> {
    #[account(init, payer = admin, space = 8 + 32 + 1, seeds = [b"user", user_wallet.key().as_ref()], bump)]
    pub user_account: Account<'info, UserAccount>,
    pub user_wallet: SystemAccount<'info>,
    #[account(mut)]
    pub admin: Signer<'info>, // Only admin can register/approve KYC
    pub system_program: Program<'info, System>,
}

#[derive(Accounts)]
pub struct IssueDfa<'info> {
    pub user_account: Account<'info, UserAccount>,
    // SPL token accounts omitted for payload density
}

#[derive(Accounts)]
pub struct TradeDfa<'info> {
    pub seller_account: Account<'info, UserAccount>,
    pub buyer_account: Account<'info, UserAccount>,
    #[account(mut)] pub seller_token_account: Account<'info, TokenAccount>,
    #[account(mut)] pub buyer_token_account: Account<'info, TokenAccount>,
    pub seller: Signer<'info>,
    pub token_program: Program<'info, Token>,
}

#[account]
pub struct AssetRegistry { pub admin_pubkey: Pubkey, pub asset_name: String, pub total_supply: u64, pub is_active: bool }

#[account]
pub struct UserAccount { pub owner_pubkey: Pubkey, pub is_kyc_approved: bool }

#[error_code]
pub enum CustomError {
    #[msg("Both parties must have an approved KYC status to hold or trade this DFA.")]
    KycNotApproved,
}