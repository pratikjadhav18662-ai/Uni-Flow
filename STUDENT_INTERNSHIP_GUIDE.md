# UniFlow: Android Internship & Play Store Project Guide

Welcome to **UniFlow** — a premium, production-grade Android application developed with **Kotlin**, **Jetpack Compose (Material 3)**, and **Room SQLite Database**.

This comprehensive documentation is tailored specifically for **beginners**, internship submissions, academic viva examinations, and Google Play Store publication.

---

## 📱 1. Project Overview & Identity

- **App Name:** UniFlow
- **Tagline:** Smart University Timetable, Attendance Predictor & Academic Workflow Hub
- **Target Audience:** College and University students, academic advisors, and interns
- **Package / Namespace:** `com.example`
- **Application ID:** `com.aistudio.uniflow.qrvpxw`

### 🎓 Logo & Brand Concept
The UniFlow emblem merges the traditional **Academic Mortarboard (Graduation Cap)** with a continuous, glowing **Flow Stream Ribbon**:
- **Mortarboard Cap:** Symbolizes academic achievement, discipline, and course mastery.
- **Dynamic Flow Ribbon:** Represents frictionless workflow management — avoiding burnout, balancing coursework, and maintaining exam eligibility.
- **Color Identity:**
  - **Midnight Indigo (`#1E1B4B`):** Deep academic intellect, focus, and stability.
  - **Electric Indigo / Violet (`#4F46E5`, `#6366F1`):** Energy, clarity, and modern digital productivity.
  - **Cyber Cyan (`#0EA5E9`):** Agility, real-time timetable flow, and fresh modern design.
  - **Emerald Success (`#10B981`):** Attendance safety threshold (>75%) and completed goals.
  - **Amber Alert (`#F59E0B`):** Upcoming exam countdowns and due date warnings.

---

## 🏛️ 2. Architectural Pattern: MVVM (Model-View-ViewModel)

UniFlow follows Google's recommended **Clean Modern Architecture**:

```
[ Room Database (SQLite) ]
           ▲
           │ Flow<List<T>> / Coroutines
[ UniFlowRepository ]
           ▲
           │ Flow / State updates
[ UniFlowViewModel ] (StateFlow<UniFlowUiState>)
           ▲
           │ Composable state observation
[ Jetpack Compose UI Screens ]
   ├── AuthScreen (Login, Registration & Forgot Password)
   ├── DashboardScreen (KPIs, Next Class Hero, Deadlines)
   ├── TimetableScreen (Weekly Day Tabs, Slots, Type Badges)
   ├── TasksScreen (Assignments, Progress, Priority Filter)
   ├── AttendanceScreen (Bunk/Skip Predictor & Class Logger)
   └── ToolsScreen (Pomodoro Study Timer & GPA Calculator)
```

### Key Architectural Advantages:
1. **Unidirectional Data Flow (UDF):** The UI only emits user events (`markPresent`, `toggleTask`, `addSlot`) to the ViewModel. The ViewModel updates the Room database via the Repository. Room outputs reactive Kotlin `Flow` streams that automatically update the UI.
2. **Configuration Change Resilient:** When the phone rotates or switches to dark mode, state stored in the ViewModel survives without data loss.
3. **No Boilerplate XML / Adapters:** Declarative Jetpack Compose replaces outdated RecyclerViews and XML layouts.

---

## 🗄️ 3. Database Schema (Room SQLite)

The SQLite database (`uniflow_academic_db`) consists of 5 normalized tables:

### Table 1: `courses`
| Column | Type | Description |
|---|---|---|
| `id` | `Long` (Primary Key, AutoIncrement) | Unique course identifier |
| `name` | `String` | Full course title (e.g. *Data Structures & Algorithms*) |
| `code` | `String` | Course code (e.g. *CS201*) |
| `instructor` | `String` | Professor name (e.g. *Dr. Aris Thorne*) |
| `location` | `String` | Default classroom or lab room |
| `colorHex` | `String` | Hex color code for subject badges |
| `targetAttendancePercent` | `Int` | Minimum required attendance (default: 75%) |
| `attendedClasses` | `Int` | Number of classes attended |
| `totalClasses` | `Int` | Total number of classes conducted |
| `credits` | `Int` | Course credit hours (e.g. 3 or 4) |
| `targetGrade` | `String` | Target letter grade (*A*, *B+*, *O*) |
| `currentScore` | `Double` | Current test score / percentage |

### Table 2: `schedule_slots`
| Column | Type | Description |
|---|---|---|
| `id` | `Long` (Primary Key, AutoIncrement) | Unique timetable slot ID |
| `courseId` | `Long` (Foreign Key) | Reference to `courses.id` |
| `courseName` | `String` | Cached course name |
| `dayOfWeek` | `Int` | `1` = Monday, `2` = Tuesday ... `7` = Sunday |
| `startTime` | `String` | Start time (e.g. `09:00 AM`) |
| `endTime` | `String` | End time (e.g. `10:30 AM`) |
| `type` | `String` | `Lecture`, `Lab`, `Tutorial`, `Seminar` |
| `room` | `String` | Specific classroom for this slot |

### Table 3: `tasks`
| Column | Type | Description |
|---|---|---|
| `id` | `Long` (Primary Key, AutoIncrement) | Unique task/assignment ID |
| `title` | `String` | Task title |
| `description` | `String` | Extended notes or requirements |
| `courseName` | `String` | Associated course name |
| `dueDateMillis` | `Long` | Timestamp of deadline |
| `priority` | `String` | `HIGH`, `MEDIUM`, `LOW` |
| `type` | `String` | `Assignment`, `Project`, `Lab Report`, `Quiz` |
| `isCompleted` | `Boolean` | Completion status checkbox |
| `progress` | `Int` | Completion percentage (0 - 100%) |

### Table 4: `exams`
| Column | Type | Description |
|---|---|---|
| `id` | `Long` (Primary Key, AutoIncrement) | Unique exam ID |
| `title` | `String` | Exam title (e.g. *Midterm Examination*) |
| `courseName` | `String` | Associated course |
| `examDateMillis` | `Long` | Timestamp of scheduled exam |
| `time` | `String` | Time of exam |
| `venue` | `String` | Exam hall or building |
| `weightagePercent` | `Int` | Contribution to final grade (e.g. 30%) |

### Table 5: `attendance_logs`
| Column | Type | Description |
|---|---|---|
| `id` | `Long` (Primary Key, AutoIncrement) | Unique log ID |
| `courseId` | `Long` | Associated course ID |
| `status` | `String` | `PRESENT`, `ABSENT`, `CANCELLED` |
| `dateMillis` | `Long` | Timestamp of the logged class |

---

## 🧮 4. The Smart Attendance & Bunk Predictor Algorithm

One of the standout features of UniFlow is the mathematical Bunk Predictor:

### A. When Attendance is Above the Target ($\ge 75\%$):
How many future classes can a student skip without dropping below the target threshold?
$$\frac{\text{attendedClasses}}{\text{totalClasses} + x} \ge \text{target}$$
$$\text{attendedClasses} \ge \text{target} \times (\text{totalClasses} + x)$$
$$x \le \left\lfloor \frac{\text{attendedClasses}}{\text{target}} \right\rfloor - \text{totalClasses}$$
*UniFlow calculates and displays: "You can safely miss $x$ more classes without falling below 75%!"*

### B. When Attendance is Below the Target ($< 75\%$):
How many consecutive classes must the student attend to recover back to the target threshold?
$$\frac{\text{attendedClasses} + y}{\text{totalClasses} + y} \ge \text{target}$$
$$y \times (1 - \text{target}) \ge (\text{target} \times \text{totalClasses}) - \text{attendedClasses}$$
$$y = \left\lceil \frac{(\text{target} \times \text{totalClasses}) - \text{attendedClasses}}{1 - \text{target}} \right\rceil$$
*UniFlow calculates and displays: "Attendance Critical! You must attend the next $y$ classes in a row to reach 75%."*

---

## 🛠️ 5. Step-by-Step Android Studio Setup (Beginner Friendly)

Follow these clear, step-by-step instructions to open, build, run, and export your project on your local computer:

### Step 1: Install Android Studio
1. Download **Android Studio** (Ladybug, Hedgehog, or newest stable release) from [developer.android.com/studio](https://developer.android.com/studio).
2. During installation, ensure the **Android SDK**, **Android SDK Command-line Tools**, and **Android Virtual Device (AVD)** components are checked.

### Step 2: Open UniFlow in Android Studio
1. Launch Android Studio.
2. In the Welcome screen, click **Open** (or go to `File` -> `Open`).
3. Select the folder where your UniFlow project files reside.
4. Click **OK**.

### Step 3: Configure JDK & Gradle Sync
1. In Android Studio, go to `File` -> `Settings` (on macOS: `Android Studio` -> `Settings` or `Preferences`).
2. Search for **Gradle** under `Build, Execution, Deployment` -> `Build Tools` -> `Gradle`.
3. Set **Gradle JDK** to **Embedded JDK 17** (or **JDK 21**).
4. Click **Apply** and **OK**.
5. Click the **Sync Project with Gradle Files** icon (the small elephant icon in the top toolbar).
6. Android Studio will download dependencies and compile Room KSP code. You will see `BUILD SUCCESSFUL` in the bottom Build tab.

### Step 4: Run the App on an Emulator or Real Phone
- **Option A (Emulator):**
  1. Click the **Device Manager** icon on the right sidebar.
  2. Click **Create Virtual Device**. Select **Pixel 8** or **Pixel 7**, choose system image **API 34 or 35**, and click **Finish**.
  3. Select your virtual device in the top device dropdown and click the green **Run (▶)** button (or press `Shift + F10`).
- **Option B (Physical Phone):**
  1. On your phone, go to `Settings` -> `About Phone` -> tap `Build Number` 7 times to enable Developer Options.
  2. Go to `Settings` -> `Developer Options` -> enable **USB Debugging**.
  3. Connect your phone via USB cable and allow USB debugging when prompted.
  4. Select your phone in Android Studio's device dropdown and click **Run (▶)**.

### Step 5: Build a Standalone APK for Submission
1. In the top menu bar, click:
   `Build` ➔ `Build Bundle(s) / APK(s)` ➔ `Build APK(s)`
2. Once the build completes, a pop-up appears in the bottom right corner with a **locate** link.
3. Your installable APK will be at:
   `app/build/outputs/apk/debug/app-debug.apk`
4. You can transfer this `.apk` to any Android phone or share it with your internship evaluator!

### Step 6: Generate Signed AAB for Google Play Store Release
1. In Android Studio, go to `Build` ➔ `Generate Signed Bundle / APK...`
2. Select **Android App Bundle** and click **Next**.
3. Under *Key store path*, click **Create new...**, choose a password and alias, and save your keystore.
4. Select build variant `release` and click **Create**.
5. Android Studio will generate `app-release.aab`.
6. Open your [Google Play Console](https://play.google.com/console), create a new application, and upload this `.aab` under Production or Internal Testing.

---

## 🎯 6. Internship Viva / Interview Preparation

Here are key questions your evaluator or professor might ask, along with professional answers:

### Q1: What makes UniFlow different from standard student schedule apps?
> **Answer:** UniFlow combines schedule management, task tracking, and academic performance metrics with an automated mathematical attendance predictor. Instead of just recording attendance, it tells students in real-time whether they are eligible for university exams and exactly how many classes they can afford to miss or need to attend consecutively to stay above the university requirement.

### Q2: Why did you use Room Database instead of SharedPreferences or raw SQLite?
> **Answer:** Room provides compile-time verification of SQL queries, eliminates raw SQL boilerplate, handles migrations safely, and integrates natively with Kotlin Coroutines and Flows. Whenever data in the database updates, the UI automatically receives the newest snapshot via asynchronous Flows without having to write manual refresh logic.

### Q3: What is the purpose of KSP in your build configuration?
> **Answer:** KSP (Kotlin Symbol Processing) is an annotation processor developed by Google that is up to 2x faster than KAPT. In our app, Room's compiler uses KSP to generate the database and DAO implementation classes directly from Kotlin metadata at compile time.

### Q4: How is Edge-to-Edge and Navigation Bar padding handled in Jetpack Compose?
> **Answer:** We invoke `enableEdgeToEdge()` in `MainActivity`, and apply `WindowInsets` handling (such as `statusBarsPadding()` in headers and `navigationBarsPadding()` on the bottom navigation bar). This guarantees that the UI never clips underneath the device camera notch or system gesture pill.
