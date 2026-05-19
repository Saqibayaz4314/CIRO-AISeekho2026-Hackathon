# 🚀 CIRO Backend - DigitalOcean Deployment Guide

> **یہ guide ہے!** صرف commands copy-paste کریں - کچھ اور نہ کریں!

---

## **STEP 1: LOCAL SETUP (اپنے Computer پر)**

### 1.1 Virtual Environment بنائیں
```powershell
python -m venv venv
```

### 1.2 Activate کریں
```powershell
venv\Scripts\activate
```
**✓ چیک کریں:** اگر `(venv)` prompt سے پہلے آئے تو ✅ ہو گیا

### 1.3 Dependencies Install کریں
```powershell
pip install -r requirements.txt
```
**بیٹھ جائے (2-3 منٹ لگے گا)**

### 1.4 Local میں Test کریں
```powershell
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

**Browser میں کھولیں:**
```
http://localhost:8000/docs
http://localhost:8000/health
```

✅ کام کر رہا ہے؟ آگے بڑھیں!

---

## **STEP 2: GitHub Prepare (Repository Setup)**

### 2.1 app.yaml فائل بنائیں

**Backend folder کی root میں یہ فائل create کریں:**

File name: `app.yaml`

Content (یہ پورا copy-paste کریں):

```yaml
name: ciro-backend
services:
- name: api
  github:
    branch: main
    repo: YOUR_GITHUB_USERNAME/CIRO-AISeekho2026-Hackathon-main
    deploy_on_push: true
  source_dir: backend
  dockerfile_path: Dockerfile
  http_port: 8000
  health_check:
    http_path: /health
    initial_delay_seconds: 30
    period_seconds: 10
    timeout_seconds: 5
    success_threshold: 2
    failure_threshold: 3
  envs:
  - key: GEMINI_API_KEY
    scope: RUN_TIME
    value: ${GEMINI_API_KEY}
  - key: FIREBASE_PROJECT_ID
    scope: RUN_TIME
    value: antigravity-af708
  - key: FIREBASE_CREDENTIALS_JSON
    scope: RUN_TIME
    value: ${FIREBASE_CREDENTIALS_JSON}
  - key: PORT
    scope: RUN_TIME
    value: "8000"
  - key: HOST
    scope: RUN_TIME
    value: "0.0.0.0"
  - key: PRODUCTION_MODE
    scope: RUN_TIME
    value: "false"
```

**⚠️ ضروری:** 
- `YOUR_GITHUB_USERNAME` کی جگہ اپنا GitHub username لکھو (مثال: `saqibdev24`)

### 2.2 GitHub پر Push کریں

```powershell
git add app.yaml
git commit -m "Add DigitalOcean deployment config"
git push origin main
```

---

## **STEP 3: DigitalOcean Setup**

### 3.1 DigitalOcean پر جاؤ

**Website:** https://www.digitalocean.com

**Login کریں** → اپنا account کھولیں

### 3.2 App Platform میں جاؤ

1. Left sidebar میں **Apps** کو click کریں
2. **Create App** button دیکھو
3. Click کریں

### 3.3 GitHub Connect کریں

1. **GitHub** option select کریں
2. Authorize کریں (اگر کہے تو)
3. Apنا repository select کریں:
   ```
   CIRO-AISeekho2026-Hackathon-main
   ```
4. **Next** دیں

### 3.4 Build Settings

**یہ values set کریں:**

| Field | Value |
|-------|-------|
| Source Repo | CIRO-AISeekho2026-Hackathon-main |
| Branch | `main` |
| Dockerfile Path | `backend/Dockerfile` |
| Output Directory | (خالی رکھیں) |

**Next** دیں

### 3.5 Environment Variables Set کریں

**Settings → Environment Variables**

یہ variables add کریں (Click "Add Variable" ہر بار):

#### Variable 1:
```
Name: GEMINI_API_KEY
Value: YOUR_OPENROUTER_API_KEY_HERE
```

#### Variable 2:
```
Name: FIREBASE_PROJECT_ID
Value: YOUR_FIREBASE_PROJECT_ID_HERE
```

#### Variable 3:
```
Name: FIREBASE_CREDENTIALS_JSON
Value: (نیچے copy-paste کریں - پوری JSON)
```

**FIREBASE_CREDENTIALS_JSON کی value:**
```json
{YOUR_FIREBASE_SERVICE_ACCOUNT_JSON_HERE}
```

⚠️ **Security Note**: Replace the above placeholders with your actual credentials:
- Get `GEMINI_API_KEY` from OpenRouter API dashboard
- Get `FIREBASE_PROJECT_ID` from Firebase Console
- Download `YOUR_FIREBASE_SERVICE_ACCOUNT_JSON_HERE` from Firebase Console → Service Accounts → Generate Private Key

**Add کریں** → **Next**

### 3.6 Plan Select کریں

- **Basic Plan** ($12/month) - ✅ بس کافی ہے
- **Click Next**

### 3.7 Deploy کریں

**"Create Resources"** button دیں

**⏳ بیٹھ جائیں (5-10 منٹ)**

---

## **STEP 4: Deployment Status Check**

### 4.1 Building ہو رہی ہے؟

Dashboard میں **Builds** دیکھو:
- 🟡 In Progress = بنایا جا رہا ہے
- ✅ Success = تیار ہو گیا
- ❌ Failed = کوئی مسئلہ ہے

### 4.2 App Running ہے؟

Left panel میں **api** service دیکھو:
- 🟢 Active = Live ہے
- 🟡 Starting = شروع ہو رہی ہے
- 🔴 Failed = Error ہے

### 4.3 URL ملا؟

DigitalOcean dashboard میں **Live App** link ملے گا:
```
https://coral-app-r3jy3.ondigitalocean.app
```

---

## **STEP 5: Test کریں (ہمیشہ کریں!)**

Browser میں یہ URLs کھولیں:

### Health Check:
```
https://coral-app-r3jy3.ondigitalocean.app/health
```
**Expected:** `{"status":"healthy",...}`

### System Info:
```
https://coral-app-r3jy3.ondigitalocean.app/system-info
```
**Expected:** Firebase connection info

### API Docs:
```
https://coral-app-r3jy3.ondigitalocean.app/docs
```
**Expected:** Swagger UI کھل جائے

### Mock Weather:
```
https://coral-app-r3jy3.ondigitalocean.app/mock/weather
```
**Expected:** JSON weather data

---

## **TROUBLESHOOTING**

### ❌ App Failed ہو رہی ہے؟

1. **Logs دیکھیں:**
   - Dashboard → api → Runtime Logs
   - Error message پڑھیں

2. **عام مسائل:**
   
   | Error | Fix |
   |-------|-----|
   | `GEMINI_API_KEY not set` | Env variable add کریں |
   | `Firebase not connected` | FIREBASE_CREDENTIALS_JSON check کریں |
   | `Port already in use` | Dockerfile میں PORT 8000 ہے - ٹھیک ہے |
   | `Connection refused` | Health check wait time بڑھائیں |

3. **Logs میں error دیکھو:**
   ```
   [FirebaseClient] ERROR...
   [GeminiClient] ERROR...
   ```

### ⏳ Deployment 30+ منٹ لگ رہے ہیں?

- ✅ Rebuild دے دیں
- Dashboard → Click **Trigger Deploy** (دوبارہ)

### 🔄 Code Update کرنا ہے?

```powershell
# Local میں code change کریں
git add .
git commit -m "Update description"
git push origin main

# DigitalOcean خود rebuild کرے گا!
```

---

## **QUICK REFERENCE**

### Commands (Local Machine)
```powershell
# Setup (پہلی بار)
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt

# Run (development)
uvicorn main:app --reload --host 0.0.0.0 --port 8000

# Run (production-like)
uvicorn main:app --host 0.0.0.0 --port 8000

# Test
python test_fb.py
```

### Important Settings
| Setting | Value |
|---------|-------|
| **Port** | 8000 |
| **Host** | 0.0.0.0 |
| **Health Check Path** | /health |
| **Source Dir** | backend |
| **Dockerfile** | backend/Dockerfile |

### Important Files
| File | Purpose |
|------|---------|
| **Dockerfile** | Container image بنانے کے لیے |
| **requirements.txt** | Python packages |
| **app.yaml** | DigitalOcean config |
| **.env** | Local environment variables |
| **main.py** | FastAPI app entry point |

---

## **FINAL CHECKLIST**

- ✅ GitHub repo میں push کیا
- ✅ app.yaml فائل بنائی
- ✅ GEMINI_API_KEY env variable set کیا
- ✅ FIREBASE_CREDENTIALS_JSON set کیا
- ✅ DigitalOcean میں deploy کیا
- ✅ /health endpoint test کیا
- ✅ API docs (`/docs`) دیکھا

---

## **SUCCESS! 🎉**

اگر یہاں تک پہنچے ہو:
- ✅ Backend live ہے
- ✅ Database connected ہے
- ✅ API working ہے
- ✅ Ready for frontend!

---

**کوئی سوال؟** Logs میں error دیکھو یا support سے رابطہ کریں!
