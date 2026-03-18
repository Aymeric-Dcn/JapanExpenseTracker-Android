# 🇯🇵 Japan Expense Tracker – Android

A mobile app to track daily expenses while traveling in Japan.

This project is built using **Kotlin + Jetpack Compose** and focuses on clean architecture, local data persistence, and intuitive expense tracking.

---

## ✨ Features

* Add expenses (amount, category, payment method, note)
* Track cash withdrawals
* Real-time cash remaining calculation
* Filter expenses:

  * Day / Week / Month / Total
  * Category
  * Payment method (Cash / Card)
* Currency toggle (Yen / Euro)
* Statistics dashboard:

  * Total spent
  * Cash vs Card breakdown
  * Category distribution (pie chart)
* Dark mode support

---

## 🏗️ Architecture

The project follows a **clean architecture** approach:

```
data/
domain/
presentation/
```

* **Room** for local database
* **MVVM (ViewModel + StateFlow)**
* **Jetpack Compose UI**

---

## 📱 Screens

* Dashboard
* Add Expense
* Add Withdrawal
* Expenses (filters + bar chart)
* Statistics (analysis + pie chart)

---

## 🚀 Tech Stack

* Kotlin
* Jetpack Compose
* Room Database
* MVVM architecture

---

## 🔗 Related Project

👉 iOS version (SwiftUI):
https://github.com/TON-REPO-IOS-ICI


---

## 📌 Future Improvements

* Edit / delete expenses
* CSV export
* Custom date selection (past weeks/months)
* Improved charts & UI polish

---

## 👤 Author

Aymeric Duchêne
