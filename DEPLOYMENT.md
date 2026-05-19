# CIRO — DigitalOcean App Platform Deployment Guide

## Prerequisites
- Code pushed to GitHub repo
- DigitalOcean account with $200 credits
- Firebase service account JSON file
- Gmail App Password ready

---

## Step 1 — Push Code to GitHub
```bash
git add .
git commit -m "feat: complete integration - auth, social, dark mode, deployment ready"
git push origin main
```

---

## Step 2 — Create App on DigitalOcean
1. Go to https://cloud.digitalocean.com/apps
2. Click **Create App**
3. Select **GitHub** as source
4. Authorize DigitalOcean to access your repo
5. Select repo: `CIRO-AISeekho2026-Hackathon-main`
6. Branch: `main`
7. Source Directory: **`backend`** ← Important!
8. Autodeploy: ✅ On

---

## Step 3 — Configure Build Settings
- DigitalOcean will detect the `Dockerfile` automatically
- Build command: (leave empty, Dockerfile handles it)
- Run command: (leave empty, Dockerfile CMD handles it)

---

## Step 4 — Add Environment Variables
Click **"Edit"** next to Environment Variables and add ALL of these:

| Key | Value |
|-----|-------|
| `GEMINI_API_KEY` | `sk-or-v1-8bcb21f6...` (your OpenRouter key) |
| `FIREBASE_PROJECT_ID` | `antigravity-af708` |
| `FIREBASE_CREDENTIALS_JSON` | *(paste entire JSON from service account file)* |
| `OPENWEATHER_API_KEY` | `5067bf13ef3c0ef29ab5bca558669978` |
| `SMTP_EMAIL` | `your_gmail@gmail.com` |
| `SMTP_PASSWORD` | `your_16_char_app_password` |
| `JWT_SECRET` | `some-long-random-string-here` |
| `DEMO_MODE` | `false` |

> **FIREBASE_CREDENTIALS_JSON**: Open the file
> `antigravity-af708-firebase-adminsdk-fbsvc-d34ff41900.json`
> with Notepad, select ALL text (Ctrl+A), copy it, and paste it
> as the value of this variable.

---

## Step 5 — Choose Plan
- Select **Basic** plan
- **$5/month** (or free with your $200 credits for 40 months!)

---

## Step 6 — Deploy
1. Click **Create Resources**
2. Wait 3-5 minutes for build to complete
3. You'll get a URL like: `https://ciro-backend-xxxxx.ondigitalocean.app`

---

## Step 7 — Update Mobile App
Once deployed:
1. Open the Android app
2. On the **Login screen**, change the "Backend Server URL" field to:
   ```
   https://ciro-backend-xxxxx.ondigitalocean.app
   ```
3. Tap Login — it should show **Online** status ✅

---

## Step 8 — Build APK

### Debug APK (recommended for hackathon demo)
1. Open Android Studio
2. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. APK location: `mobile_app/build/outputs/apk/debug/mobile_app-debug.apk`

### Signed APK (for distribution)
1. **Build** → **Generate Signed Bundle / APK**
2. Choose **APK**
3. **Create new keystore** (fill in any values, keep the file safe)
4. Build → Release APK location: `mobile_app/build/outputs/apk/release/`

---

## Verify Deployment
Test these URLs in your browser once deployed:
```
GET  https://YOUR_URL/health          → {"status": "healthy"}
GET  https://YOUR_URL/mock/social?area=G-10  → social signals
POST https://YOUR_URL/auth/register   → register test user
```

---

## Share APK for Free
- **Google Drive**: Upload and share link
- **WhatsApp**: Send `.apk` file directly
- **Firebase App Distribution**: Free, up to 500 testers
  - Go to Firebase Console → App Distribution → Upload APK
