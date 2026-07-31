// TICKET-ADV100 — theme toggle, persisted to localStorage; first paint reads
// the persisted value to avoid a FOUC flash of the wrong theme.
(function () {
  const stored = localStorage.getItem('reconx-theme') || 'light';
  document.documentElement.setAttribute('data-theme', stored);

  document.addEventListener('DOMContentLoaded', () => {
    const btn = document.getElementById('theme-toggle');
    if (!btn) return;

    btn.setAttribute('aria-pressed', stored === 'dark' ? 'true' : 'false');

    btn.addEventListener('click', () => {
      const current = document.documentElement.getAttribute('data-theme') || 'light';
      const next = current === 'light' ? 'dark' : 'light';
      document.documentElement.setAttribute('data-theme', next);
      localStorage.setItem('reconx-theme', next);
      btn.setAttribute('aria-pressed', next === 'dark' ? 'true' : 'false');
    });
  });
})();
