# ClevCaleb

Native **Android** calculator app — **ClevCalc** feature parity, no ads, styled with the **Hermes Nous Blue Dark** theme from [Hermes Desktop](https://github.com/NousResearch/hermes-agent) (`nousTheme.darkColors`).

## ClevCalc parity

- **Navigation drawer** with Favorites + All Calculators (hamburger menu)
- **Main calculator** with ClevCalc-style keypad (`C`, `()`, `%`, `00`, operator column, `=` button)
- **Scientific functions** via `…` row (sin, cos, tan, log, ln, √, x², π, e, DEG/RAD)
- **History** from the top-bar clock icon
- **Overflow menu** for decimal places and settings
- **Settings** screen matching ClevCalc:
  - Calculator list (favorites & visible calculators)
  - Startup calculator
  - Theme (Hermes Nous Blue Dark — fixed, no ads)
  - Number format
  - Button feedback (vibration)
  - Keep screen on
  - Keep calculation record
  - Open calculator list at startup
- **Help** screen (no “Remove Ads” — this build is ad-free)

## Calculators (18)

| Calculator | ClevCalc name |
|---|---|
| Basic + Scientific | Calculator |
| Unit Converter | Unit Converter |
| Currency Converter | Currencies |
| Percentage | Percent |
| Discount | Discount |
| Loan | Loan |
| Date | Date |
| Health (BMI/BMR) | Body Metrics |
| Fuel Cost | Fuel Cost |
| Fuel Efficiency | Fuel Efficiency |
| GPA | Grade Average |
| Tip | Tip |
| Sales Tax | Sales Tax |
| Unit Price | Unit Price |
| World Time | World Time |
| Ovulation | Ovulation |
| Hexadecimal | Hex |
| Savings | Savings |

## Theme

Hermes Nous Blue Dark (`nousTheme.darkColors`):

- Background: `#0D2F86`
- Foreground: `#FFE6CB` (warm cream)
- Card: `#12378F`
- Accent / Nous blue: `#1540B1` / `#0053FD`

## Build

Requirements: Android SDK 34, JDK 17+

```bash
export ANDROID_HOME=~/android-sdk
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Test

```bash
./gradlew test
```

## Tech

- Kotlin + Jetpack Compose + Material 3
- Navigation Compose + DataStore preferences
- exp4j (expression evaluation)
- OkHttp (live currency rates via Frankfurter API)
