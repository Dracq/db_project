// TICKET-ADV104 / TICKET-ADV105 — EventSource live feed with prepend & animation hardening.
(function () {
  const feed = document.getElementById('trade-feed');
  if (!feed) return;

  const statusBadge = document.getElementById('sse-status');

  function updateStatus(text, stateClass) {
    if (!statusBadge) return;
    statusBadge.textContent = text;
    statusBadge.className = 'badge badge--' + stateClass;
  }

  function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  const qtyFormatter = new Intl.NumberFormat('en-US');
  const priceFormatter = new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 4,
  });

  function prependTradeRow(trade) {
    if (!trade) return;

    const rawStatus = (trade.status || '').toUpperCase();
    let statusModifier = 'trade-card--pending';
    if (rawStatus === 'MATCHED') {
      statusModifier = 'trade-card--matched';
    } else if (rawStatus === 'UNMATCHED' || rawStatus === 'BREAK') {
      statusModifier = 'trade-card--break';
    }

    const row = document.createElement('article');
    row.className = `trade-card ${statusModifier} trade-card--new`;

    const tradeRef = escapeHtml(trade.tradeRef || 'N/A');
    const symbol = escapeHtml(trade.symbol || trade.instrumentSymbol || 'N/A');
    const qtyVal = trade.qty != null ? trade.qty : trade.quantity;
    const qty = qtyVal != null ? qtyFormatter.format(qtyVal) : '0';
    const price = trade.price != null ? priceFormatter.format(trade.price) : '0.00';
    const status = escapeHtml(rawStatus);

    row.innerHTML = `
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <strong>${tradeRef}</strong>
        <span style="font-size:12px; font-weight:600;">[${status}]</span>
      </div>
      <div style="margin-top:4px; font-size:13px; color:var(--color-text-muted);">
        <span><strong>Symbol:</strong> ${symbol}</span> ·
        <span><strong>Qty:</strong> ${qty}</span> ·
        <span><strong>Price:</strong> ${price}</span>
      </div>
    `;

    feed.prepend(row);

    // Remove --new modifier after entrance completes
    setTimeout(() => {
      row.classList.remove('trade-card--new');
    }, 500);

    // DOM cap: maximum 50 cards
    while (feed.children.length > 50) {
      feed.lastElementChild.remove();
    }
  }

  window.prependTradeRow = prependTradeRow;

  const STREAM_URL = '/api/v1/trades/stream';
  let sse = null;

  function connect() {
    try {
      sse = new EventSource(STREAM_URL);

      sse.onopen = function () {
        updateStatus('Live', 'live');
      };

      sse.onmessage = function (event) {
        try {
          const trade = JSON.parse(event.data);
          prependTradeRow(trade);
        } catch (err) {
          console.error('Error parsing SSE event data:', err);
        }
      };

      sse.onerror = function () {
        // Critical: Do NOT call new EventSource() here — browser auto-reconnects with backoff.
        updateStatus('Reconnecting...', 'reconnecting');
      };
    } catch (e) {
      console.warn('EventSource failed to initialize:', e);
      runDemoStream();
    }
  }

  function runDemoStream() {
    updateStatus('Live', 'live');
    const demoEvents = [
      { tradeRef: 'EQU-20260603-0001', symbol: 'SAP.DE',  qty: 1000, price: 125.50, status: 'MATCHED' },
      { tradeRef: 'FX-20260603-0001',  symbol: 'EUR/USD', qty: 1000000, price: 1.0852, status: 'PENDING' },
      { tradeRef: 'EQU-20260603-0002', symbol: 'AAPL',    qty: 500,  price: 178.20, status: 'BREAK' },
    ];

    demoEvents.forEach((e, i) => {
      setTimeout(() => {
        prependTradeRow(e);
      }, 500 * (i + 1));
    });
  }

  window.addEventListener('beforeunload', () => {
    if (sse) {
      sse.close();
    }
  });

  connect();
})();
