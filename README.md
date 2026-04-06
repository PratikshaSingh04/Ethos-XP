# Ethos XP is a high-engagement desktop application designed to transform sustainable habits into an immersive RPG experience. Built with JavaFX and MySQL, it aligns with the United Nations Sustainable Development Goal 13 (Climate Action) by rewarding real-world environmental missions with Experience Points (XP) and Ranks.

(Replace this with a real screenshot of your forest dashboard!)

# 🌟 Key Features
Quest-Based Tracking: Add, manage, and complete sustainability "Missions" (Water conservation, Energy efficiency, Waste reduction).

RPG Progression System: Earn XP for every mission. Level up from a Beginner to an Eco Warrior ⚔️.

Living UI: Features a dynamic particle system (atmospheric spores/bubbles) and floating animations for a premium game feel.

Persistent Data: Powered by MySQL and JDBC to save your progress, streaks, and total impact.

Visual Achievements: Custom "Level Up" banners triggered by specific XP milestones.

# 🛠️ Technical Stack
Language: Java 17+

Frontend: JavaFX (CSS Styling, Animation API)

Backend: Java (OOP, Lambdas, Streams)

Database: MySQL 8.0 (JDBC)

Design Assets: Custom PNG textures, 'Luckiest Guy' typography.

# 🌍 SDG Alignment: Goal 13 (Climate Action)
Ethos XP is more than a tracker; it’s a tool for behavioral change. By gamifying "Small Habits," we aim to create a "Big Impact" on global climate awareness, directly supporting UN Target 13.3: Improving education and human capacity on climate change mitigation.

# 🚀 Getting Started
Prerequisites
JDK 17 or higher.

MySQL Server running locally.

JavaFX SDK configured in your IDE (Eclipse/IntelliJ).

Database Setup
Create a database named ecohabit_db.

Run the following SQL to set up your tables:

SQL
CREATE TABLE habits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    habit_name VARCHAR(255),
    date DATE,
    status VARCHAR(50),
    category VARCHAR(50)
);

CREATE TABLE users (
    id INT PRIMARY KEY,
    points INT DEFAULT 0
);
INSERT INTO users (id, points) VALUES (1, 0);
Installation
Clone the repository:

Bash
git clone https://github.com/yourusername/EthosXP.git
Import the project into your IDE.

Update DBConnection.java with your MySQL credentials.

Run MainApp.java.

# 🎨 UI/UX Design Notes
Atmospheric Particles: Implemented using a JavaFX Timeline and TranslateTransition.

Modern Java: Utilizes Lambdas for event handling and Streams for asset management.

Contrast: "Glassmorphism" panels ensure text readability against complex background art.

# 🛡️ License
Distributed under the MIT License. See LICENSE for more information.
