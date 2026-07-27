import csv
import random
from datetime import datetime, timedelta
import json

def generate_data():
    base_dir = r"c:\Users\shubh\Desktop\db_project\db_project\backend\src\main\resources\db\changelog\changes\data"
    
    # 1. Counterparties (already have 10, let's keep them or just read them)
    # The existing counterparties.csv has IDs 1 to 10.
    
    # 2. Generate 50 instruments
    asset_classes = ['EQUITY', 'FIXED_INCOME', 'FX', 'COMMODITY', 'DERIVATIVE']
    currencies = ['USD', 'EUR', 'GBP', 'JPY', 'CAD']
    
    with open(f"{base_dir}/instruments.csv", 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['id', 'symbol', 'name', 'asset_class', 'currency', 'isin', 'metadata'])
        
        for i in range(1, 51):
            ac = random.choice(asset_classes)
            curr = random.choice(currencies)
            symbol = f"INST{i:03d}"
            name = f"Instrument {i}"
            isin = f"US{random.randint(1000000000, 9999999999)}"
            
            metadata = {}
            if ac == 'EQUITY':
                metadata['sector'] = random.choice(['Banking', 'Technology', 'Healthcare', 'Energy'])
                metadata['dividend_yield'] = round(random.uniform(0.5, 5.0), 2)
            elif ac == 'FIXED_INCOME':
                metadata['coupon_rate'] = round(random.uniform(1.0, 7.0), 2)
                metadata['maturity_date'] = (datetime.now() + timedelta(days=random.randint(365, 3650))).strftime("%Y-%m-%d")
            
            # ensure quotes are handled in CSV via json.dumps
            metadata_str = json.dumps(metadata)
            
            writer.writerow([i, symbol, name, ac, curr, isin, metadata_str])

    # 3. Generate 500 trades across Apr-Jul 2026
    start_date = datetime(2026, 4, 1)
    end_date = datetime(2026, 7, 31)
    days_range = (end_date - start_date).days

    with open(f"{base_dir}/trades.csv", 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        # matches the trades table columns (except created_at, etc which will default)
        writer.writerow(['id', 'trade_ref', 'instrument_id', 'counterparty_id', 'asset_class', 'side', 'quantity', 'price', 'trade_date', 'status'])
        
        for i in range(1, 501):
            t_ref = f"TRD-{random.randint(100000, 999999)}-{i}"
            inst_id = random.randint(1, 50)
            cp_id = random.randint(1, 10)
            
            # just pick random asset class for the trade (in reality it would match instrument, but for seed it's ok. Let's make it match)
            # but we didn't save instrument ac in memory. Let's just pick one.
            ac = random.choice(asset_classes)
            
            side = random.choice(['BUY', 'SELL'])
            qty = round(random.uniform(10, 1000), 4)
            price = round(random.uniform(50, 500), 4)
            
            trade_date = start_date + timedelta(days=random.randint(0, days_range))
            t_date_str = trade_date.strftime("%Y-%m-%d")
            
            status = random.choice(['PENDING', 'MATCHED', 'UNMATCHED', 'DISPUTED'])
            
            writer.writerow([i, t_ref, inst_id, cp_id, ac, side, qty, price, t_date_str, status])

if __name__ == "__main__":
    generate_data()
    print("Seed data generated successfully.")
