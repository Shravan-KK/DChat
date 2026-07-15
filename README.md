# DChat - Real-time Messaging Platform

DChat is a unified, real-time messaging application designed for campus projects. It allows students to discover registered users and initiate instant one-on-one conversations.

## 🚀 Features

- **User Authentication:** Secure Signup and Login using JWT (JSON Web Tokens).
- **User Discovery:** A dynamic home screen showing all registered users, sorted by the latest interaction.
- **Real-time Messaging:** Instant message delivery using WebSockets (Socket.io).
- **Persistent Chat History:** Full conversation history stored and retrieved from a PostgreSQL database.
- **Unread Indicators:** Visual blue dot notifications for unseen messages.

## 🛠️ Tech Stack

### Frontend (Mobile)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Networking:** Retrofit & OkHttp
- **Real-time:** Socket.io-client
- **DI:** Hilt (Dagger)
- **Local Storage:** Jetpack DataStore

### Backend (Server)
- **Framework:** Express.js (Node.js)
- **Database:** PostgreSQL
- **Real-time:** Socket.io
- **Auth:** JWT & Bcrypt

## ⚙️ Setup Instructions

### 1. Database Setup
1. Ensure **PostgreSQL** is installed and running.
2. Create a new database named `dchat`.
3. Run the SQL queries provided in `schema.sql` to create the required tables.

### 2. Backend Setup
1. Navigate to the `backend` folder.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Create a `.env` file in the `backend` folder and add your credentials:
   ```env
   PORT=3000
   DATABASE_URL=postgresql://your_user:your_password@localhost:5432/dchat
   JWT_SECRET=your_secret_key
   ```
4. Start the server:
   ```bash
   npm run dev
   ```

### 3. Frontend Setup
1. Open the project in **Android Studio**.
2. If testing on an emulator, ensure the `BASE_URL` in `NetworkModule.kt` is set to `http://10.0.2.2:3000/`.
3. If using **Localtunnel** for external testing:
   - Run `npx localtunnel --port 3000`
   - Copy the URL and update `BASE_URL` in `NetworkModule.kt`.
4. Build and Run the app on your emulator or physical device.

## 📱 How to Use
1. **Signup:** Create a new account with a username and email.
2. **Discover:** See other users on the Home screen.
3. **Chat:** Tap on a user to start a real-time conversation.
4. **Persist:** Log out and log back in to see your chat history preserved!

---
*Developed with ❤️ for campus collaboration.*
