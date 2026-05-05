# 🎓 Student Record Management System (SRMS)

A **professional-grade JavaFX desktop application** for managing student records with a modern GUI, dashboard analytics, and advanced CRUD operations.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

---

## ✨ Features

### 🖥️ Premium GUI
- Modern JavaFX interface with **sidebar navigation**
- **Dark / Light mode** toggle
- Glassmorphism design with gradient accents
- Animated toast notifications (slide-in/out)
- Smooth page transitions

### 📊 Dashboard
- **Total Students**, **Average Marks**, **Top Performer**, **Total Courses** stat cards
- **Pie Chart** — Student distribution by course
- **Bar Chart** — Average marks by course
- **Recent Activity Feed** — Live action log

### 👨‍🎓 Student Management
- **Add / Edit / Delete** students with full details (name, age, course, marks, email, phone)
- **Search** by name, ID, or email (real-time)
- **Filter** by course
- **Sort** by any column (ascending / descending)
- **Pagination** (10 records per page)
- **Color-coded marks** — Green (80+), Yellow (50–79), Red (below 50)
- **Real-time input validation** with visual error indicators

### 🔐 Authentication
- Admin login system with **SHA-256 password hashing**
- Activity logging for all user actions

### 📁 Export & Import
- **Export to CSV** — Spreadsheet-compatible format
- **Export to PDF** — Professionally styled report with iTextPDF
- **Import from CSV** — Bulk student upload
- **Database Backup & Restore** — Using mysqldump

---

## 🏗️ Architecture (MVC Pattern)

```
src/com/srms/
├── App.java                    # Entry point
├── config/DatabaseConfig.java  # Singleton DB connection + migrations
├── model/                      # POJOs (Student, User, ActivityLog)
├── dao/                        # Data Access Objects (SQL queries)
├── service/                    # Business logic + activity logging
├── controller/                 # Event handlers
├── view/                       # JavaFX UI components
└── util/                       # Validator, ThemeManager, NotificationManager
```

---

## 🚀 Prerequisites

| Requirement | Version |
|---|---|
| **Java JDK** | 17 or higher |
| **MySQL Server** | 8.0+ |
| **JavaFX SDK** | 21.0.2 (included in `lib/`) |

---

## ⚙️ Setup & Run

### 1. Clone the repository
```bash
git clone https://github.com/Vasu-Nandan20/Student-Record-Management-System.git
cd Student-Record-Management-System
```

### 2. Download JavaFX SDK
Download the [JavaFX 21.0.2 SDK for Windows](https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-sdk.zip) and extract it into the `lib/` folder so you have `lib/javafx-sdk-21.0.2/`.

### 3. Configure MySQL
- Make sure MySQL Server is running on `localhost:3306`
- Create the database:
```sql
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    course VARCHAR(50),
    marks DOUBLE
);
```
- Update credentials in `src/com/srms/config/DatabaseConfig.java` if needed (default: `root` / `Password`)

### 4. Compile
```bash
compile.bat
```
Or manually:
```bash
javac -d out --module-path lib\javafx-sdk-21.0.2\lib --add-modules javafx.controls,javafx.graphics -cp "lib\mysql-connector-j-9.6.0.jar;lib\itextpdf-5.5.13.4.jar" -sourcepath src src\com\srms\App.java
```

### 5. Run
```bash
run.bat
```
Or manually:
```bash
java --module-path lib\javafx-sdk-21.0.2\lib --add-modules javafx.controls,javafx.graphics -cp "out;lib\mysql-connector-j-9.6.0.jar;lib\itextpdf-5.5.13.4.jar" com.srms.App
```

### 6. Login
- **Username:** `admin`
- **Password:** `admin123`

---

## 📂 Project Structure

```
SRMS/
├── lib/
│   ├── mysql-connector-j-9.6.0.jar       # MySQL JDBC Driver
│   ├── itextpdf-5.5.13.4.jar             # PDF generation
│   └── javafx-sdk-21.0.2/                # JavaFX runtime
├── sql/schema.sql                         # Database schema
├── resources/css/
│   ├── light-theme.css                    # Light mode theme
│   └── dark-theme.css                     # Dark mode theme
├── src/com/srms/                          # Source code (MVC)
├── compile.bat                            # Compile script
├── run.bat                                # Launch script
└── README.md
```

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **UI Framework:** JavaFX 21
- **Database:** MySQL 8.0
- **PDF Generation:** iTextPDF 5.5.13
- **Architecture:** MVC (Model-View-Controller)
- **Auth:** SHA-256 password hashing

---

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**Vasu Nandan**
- GitHub: [@Vasu-Nandan20](https://github.com/Vasu-Nandan20)
