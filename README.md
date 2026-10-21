# STUDYFLOW — Native Android project (Phase 1)

STUDYFLOW is being developed as a **native Android app using Kotlin + Jetpack Compose**, not as an HTML website.

## Current starter screens
- Home dashboard
- Weekly timetable screen with add/remove class interactions
- Subject library screen and Android document picker entry point
- Assignment/task list with add/complete interactions
- AI assistant screen placeholder
- Navy / off-white / sage visual theme

## Build an APK using GitHub Actions (tablet-friendly route)
This repository includes `.github/workflows/android-apk.yml`. After uploading the project to a GitHub repository:

1. Open the repository in GitHub on your tablet.
2. Open the **Actions** tab and select **Build STUDYFLOW APK**.
3. Tap **Run workflow** (or push/commit to the `main` branch to trigger it).
4. Wait for the workflow to finish successfully.
5. Open the completed workflow run and download the **STUDYFLOW-debug-apk** artifact.
6. Extract the downloaded artifact ZIP and install `app-debug.apk` on your Android tablet. Android may ask you to allow installs from that browser/files app; only enable this for the source you trust.

The workflow builds a **debug APK for testing**, not a signed public release. This project has not yet been compiled or device-tested here, so the first cloud build is also a validation step. If it fails, use the Actions log to identify and fix the error before relying on the app.

## Current limitations — not a production release
- No persistent database or date-by-date attendance history yet.
- The 8:00 PM reminder flow is not complete; Android 13+ notification permission and reliable scheduling still need implementation and device testing.
- The document picker currently adds file references only for the active session; persistent library and an embedded PDF reader are still needed.
- No secure sign-in or cloud backup/sync yet.
- NSMU Moodle integration depends on supported APIs and the user's authorization; no credentials should be placed in source code.
- The AI assistant is a placeholder. Any OpenAI integration needs a secure backend; never embed an API key in the APK. API usage is billed separately from ChatGPT subscriptions.
- App icon, accessibility, tablet testing, crash testing, and release signing remain to be done.

## Product requirements to preserve
- Editable weekly timetable; schedule changes must not erase attendance history.
- Absence-first attendance: user marks missed classes, but unreviewed days must not silently count as full attendance.
- Daily 8:00 PM local reminder to review/mark absences.
- Subject folders and offline study documents.
- Assignments, deadlines, completion status and grades.
- Optional Moodle sync only through authorized, supported methods.
- AI study help through a secure backend.
- Local-first use with optional authenticated cloud sync.

## Demo data warning
Sample classes and subjects are placeholders. Confirm them against the current NSMU timetable before relying on attendance tracking.

## Technical details
- Kotlin + Jetpack Compose + Material 3
- Minimum Android version: Android 8.0 (API 26)
- Application ID: `com.studyflow.app`

## UI polish update
- Refreshed the dashboard with a richer blue-to-teal gradient hero card and colorful stats.
- Added subtle crossfade transitions when switching tabs and content-size animation on selected cards.
- Improved spacing, card elevation, navigation contrast, and subject/class accents.
- This is still a starter build; it has not been compiled or tested in this environment.
