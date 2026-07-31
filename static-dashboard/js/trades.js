// TICKET-ADV106 — Sortable, resizable, frozen header trades data table.
class TradesTableManager {
  constructor(tableId, tbodyId) {
    this.table = document.getElementById(tableId);
    this.tbody = document.getElementById(tbodyId);
    this.rows = [];

    if (this.table && this.tbody) {
      this.initSort();
      this.initResize();
      this.loadData();
    }
  }

  initSort() {
    this.table.querySelectorAll('thead th').forEach(th => {
      th.addEventListener('click', (e) => {
        if (e.target.classList.contains('resize-handle')) return; // Ignore resize handle clicks

        const col = th.dataset.col;
        const type = th.dataset.type || 'string';
        const currentSort = th.getAttribute('aria-sort');
        const nextDir = currentSort === 'ascending' ? 'descending' : 'ascending';

        // Clear sort on all other headers
        this.table.querySelectorAll('thead th').forEach(other => other.removeAttribute('aria-sort'));
        th.setAttribute('aria-sort', nextDir);

        const mult = nextDir === 'ascending' ? 1 : -1;
        this.rows.sort((a, b) => {
          const av = a[col] != null ? a[col] : (a.qty != null && col === 'quantity' ? a.qty : '');
          const bv = b[col] != null ? b[col] : (b.qty != null && col === 'quantity' ? b.qty : '');
          if (type === 'number') {
            return (Number(av) - Number(bv)) * mult;
          }
          return String(av).localeCompare(String(bv)) * mult;
        });

        this.render();
      });
    });
  }

  initResize() {
    this.table.querySelectorAll('.resize-handle').forEach(handle => {
      handle.addEventListener('mousedown', (e) => {
        e.preventDefault();
        const th = handle.closest('th');
        const startX = e.clientX;
        const startWidth = th.offsetWidth;

        const onMove = (ev) => {
          th.style.width = Math.max(50, startWidth + ev.clientX - startX) + 'px';
        };

        const onUp = () => {
          document.removeEventListener('mousemove', onMove);
          document.removeEventListener('mouseup', onUp);
        };

        document.addEventListener('mousemove', onMove);
        document.addEventListener('mouseup', onUp);
      });
    });
  }

  render() {
    const qtyFormatter = new Intl.NumberFormat('en-US');
    const priceFormatter = new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 4 });

    this.tbody.innerHTML = this.rows.map(r => {
      const qtyVal = r.quantity != null ? r.quantity : r.qty;
      const statusStr = (r.status || '').toUpperCase();
      let badgeClass = 'badge--connecting';
      if (statusStr === 'MATCHED') badgeClass = 'badge--live';
      else if (statusStr === 'BREAK' || statusStr === 'UNMATCHED') badgeClass = 'badge--reconnecting';

      return `
        <tr>
          <td><strong>${r.tradeRef || ''}</strong></td>
          <td>${r.symbol || ''}</td>
          <td>${qtyVal != null ? qtyFormatter.format(qtyVal) : '0'}</td>
          <td>${r.price != null ? priceFormatter.format(r.price) : '0.00'}</td>
          <td><span class="badge ${badgeClass}">${statusStr}</span></td>
        </tr>
      `;
    }).join('');
  }

  loadData() {
    fetch('/api/v1/trades?size=200')
      .then(r => r.json())
      .then(data => {
        const list = data.content || (Array.isArray(data) ? data : []);
        if (list && list.length > 0) {
          this.rows = list;
          this.render();
        } else {
          this.loadFallbackData();
        }
      })
      .catch(() => {
        this.loadFallbackData();
      });
  }

  loadFallbackData() {
    this.rows = [
      { tradeRef: 'EQU-20260603-0001', symbol: 'SAP.DE',  quantity: 1000, price: 125.50, status: 'MATCHED' },
      { tradeRef: 'FX-20260603-0001',  symbol: 'EUR/USD', quantity: 1000000, price: 1.0852, status: 'PENDING' },
      { tradeRef: 'EQU-20260603-0002', symbol: 'AAPL',    quantity: 500,  price: 178.20, status: 'BREAK' },
      { tradeRef: 'BOND-20260603-0003', symbol: 'US10Y',   quantity: 250000, price: 98.45, status: 'MATCHED' },
      { tradeRef: 'EQU-20260603-0004', symbol: 'MSFT',    quantity: 1200, price: 415.10, status: 'MATCHED' },
      { tradeRef: 'DERIV-20260603-0005', symbol: 'SPX-OPT', quantity: 50, price: 5120.00, status: 'BREAK' }
    ];
    this.render();
  }
}

document.addEventListener('DOMContentLoaded', () => {
  new TradesTableManager('trades-table', 'trades-tbody');
});
