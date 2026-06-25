## ClevCaleb v1.0.5

Stability, performance, and finance-calculator fixes since v1.0.4.

### Calculator & UX
- Live result preview is debounced (120ms) so typing feels smoother on long expressions
- Haptic feedback restored on all keypad keys
- Keep-screen-on preference now follows lifecycle correctly (no stale window flags)

### Finance calculators
- Loan calculator no longer crashes on zero-year / zero-month terms
- Savings growth uses closed-form math instead of a month-by-month loop (faster, same results)
- Loan payment formula uses a shared power factor for cleaner numeric stability

### Currency converter
- Exchange rates are cached for 1 hour with mutex-protected fetches (no duplicate network calls)
- Failed fetches back off for 5 minutes and fall back to last good rates when offline

### Under the hood
- Fixed a history-restore race on the main calculator screen
- Trimmed MathEngine overhead for snappier evaluation

### Install
Download **ClevCaleb-v1.0.5.apk** below and sideload on Android 8+.
