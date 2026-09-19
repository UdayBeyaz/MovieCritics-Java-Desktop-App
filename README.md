# 🎬 MovieCritics - Family Movie Tracking Application

MovieCritics is a Java Swing-based desktop application developed as part of the SE 2232 Software System Analysis course at Yaşar University. The application allows family members to maintain, track, rate, and discover movies based on role-based permissions (Parents & Children).

## 🌟 Key Features

### 🔐 Role-Based Access Control
* **Parents (Admin Level):**
  * Full CRUD operations for movie catalog management.
  * User management (create accounts, assign roles, password resets).
  * Content moderation (ratings, reviews, parental control restrictions).
  * Viewing analytics and family statistics.
* **Children (Member Level):**
  * Log watched movies and create personal watchlists.
  * Rate movies (1-5 stars) and submit reviews/comments.
  * Track watching progress vs. siblings.
  * Filtered movie browsing respecting parental restriction settings.

### 🔍 Shared Capabilities
* Search and filter movies by genre, director, release year, or title.
* View aggregate family ratings and reviews.

## 🛠️ Tech Stack & Tools
* **Programming Language:** Java (Swing GUI Framework)
* **Database:** SQLite / MySQL
* **Architectural & System Modeling:** UML Diagrams (Class, Use Case, Sequence) via Visual Paradigm
* **IDE:** IntelliJ IDEA / NetBeans

## 📁 Database Architecture
The relational database structure consists of three main entities:
* `Movie` (Attributes: Title, Director, Genre, Rating, Parental Restriction, Comments, etc.)
* `User` (Attributes: Username, Password, Role, Email)
* `Person` (Attributes: First Name, Last Name, DOB, Nationality)

---
*Developed by Uday Beyaz - Yaşar University, Software Engineering*
