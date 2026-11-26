## Japan Expense Tracker 2.0.0

The first public release. Install the APK below on an Android phone (Android 7.0 or later);
Android will ask you to allow installs from this source.

### New
- **Suica / Pasmo / ICOCA reading over NFC**: card balance, last 20 transactions, and
  one-tap import of the payments as expenses (top-ups are skipped, rescanning never creates
  duplicates)
- **Live JPY → EUR rate** from the European Central Bank, cached for offline use
- **Edit and delete** expenses
- **CSV export** of the filtered expenses (opens correctly in Excel, Japanese included)
- IC card added as a payment method

### Improved
- Amounts stored as whole yen; existing data is migrated automatically
- Week filter fixed for weeks that span the new year
- Numeric keyboard and validation when entering amounts
- The yen / euro choice is shared by every screen

### Under the hood
- New `felica` module: FeliCa frame building and Suica history decoding in pure Kotlin
- 36 unit tests and GitHub Actions on every push
