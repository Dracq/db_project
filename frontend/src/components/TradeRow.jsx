import React from 'react';

function TradeRowImpl({ trade, onClick }) {
  return (
    <div className="trade-row-content" onClick={() => onClick && onClick(trade.id)} style={{ display: 'contents' }}>
      <span className="data-table__cell">{trade.tradeRef}</span>
      <span className="data-table__cell">{trade.symbol}</span>
      <span className="data-table__cell">{trade.qty}</span>
      <span className="data-table__cell">{trade.price}</span>
      <span className="data-table__cell">
        <span className={`status-pill ${trade.status.toLowerCase()}`}>{trade.status}</span>
      </span>
    </div>
  );
}

// Custom equality — only the fields we actually render
function areEqual(prev, next) {
  return prev.trade.id      === next.trade.id
      && prev.trade.status  === next.trade.status
      && prev.trade.price   === next.trade.price
      && prev.onClick       === next.onClick;
}

export const TradeRow = React.memo(TradeRowImpl, areEqual);
