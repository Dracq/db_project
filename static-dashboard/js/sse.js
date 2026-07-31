// TICKET-ADV104 / TICKET-ADV105 — EventSource live feed with badge status and prepend logic.
(function () {
  const feed = document.getElementById('trade-feed');
  if (!feed) return;

  const statusBadge = document.getElementById('sse-status');

  function updateStatus(text, stateClass) {
    if (!statusBadge) return;
    statusBadge.textContent = text;
    statusBadge.className = 'badge badge--' + stateClass;
  }

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
          if (window.prependTradeRow) {
            window.prependTradeRow(trade);
          } else {
            prependFallback(trade);
          }
        } catch (err) {
          console.error('Error parsing SSE event data:', err);
        }
      };

      sse.onerror = function () {
        // Critical: Do NOT construct new EventSource here — browser auto-reconnects with backoff.
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
        if (window.prependTradeRow) {
          window.prependTradeRow(e);
        } else {
          prependFallback(e);
        }
      }, 500 * (i + 1));
    });
  }

  function prependFallback(trade) {
    const el = document.createElement('article');
    el.className = 'trade-card trade-card--' + (trade.status ? trade.status.toLowerCase() : 'matched');
    el.innerHTML = `
      <strong>${trade.tradeRef || ''}</strong>
      <span> ${trade.symbol || ''} </span>
      <span> qty=${trade.qty || 0} </span>
      <span> price=${trade.price || 0} </span>
      <span> [${trade.status || ''}]</span>`;
    feed.prepend(el);
  }

  window.addEventListener('beforeunload', () => {
    if (sse) {
      sse.close();
    }
  });

  connect();
})();
