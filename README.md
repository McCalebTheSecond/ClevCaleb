# ClevCaleb

Native **Android** calculator app for **US English** users — ClevCalc feature parity, no ads, styled with the **Hermes Nous Blue Dark** theme from [Hermes Desktop](https://github.com/NousResearch/hermes-agent).

## US English build

- **Language:** English only (`en` resources)
- **Numbers:** US format (`1,234.56`) and USD currency (`$`)
- **Defaults:** Imperial units (lb, in, mi, gal, °F, MPG)
- **US Time:** Major US cities and time zones (including Bolivar, MO)
- **Currencies:** USD plus common US trading partners (CAD, MXN, EUR, GBP, JPY, AUD, CHF)

## ClevCalc parity

- Navigation drawer with Favorites + All Calculators
- Main calculator with ClevCalc-style keypad, scientific mode, and history
- Settings: calculator list, startup calculator, vibration, keep screen on, calc record
- All 18 calculators

## Theme

Hermes Nous Blue Dark (`nousTheme.darkColors`):

- Background: `#0D2F86`
- Foreground: `#FFE6CB`
- Accent: `#0053FD` / `#1540B1`

## Build

```bash
export ANDROID_HOME=~/android-sdk
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Test

```bash
./gradlew test
```
