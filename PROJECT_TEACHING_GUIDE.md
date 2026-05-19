# Task Manager with Priority Matrix - Complete Project Teaching Guide

This guide explains your Android Kotlin project from beginner level to viva level. It is written so you can explain the project in a demo, code walkthrough, internal evaluation, or semester-end presentation.

## 1. Project Overview

### Simple Explanation

This app is a task manager. A user can sign up, log in, add tasks, choose a due date and time, assign a priority category, mark tasks completed, search/filter tasks, view profile details, use dark mode, and receive reminder notifications.

The main idea is the Eisenhower Matrix:

1. Important and Urgent
2. Important but Not Urgent
3. Urgent but Not Important
4. Neither Urgent nor Important

This helps users decide which tasks need immediate attention and which can be scheduled, delegated, or treated as low priority.

### Technical Explanation

The app is a native Android project written in Kotlin. It uses:

- Activities for screens.
- XML layouts for UI.
- SQLite for task storage.
- SharedPreferences for small user/session/settings data.
- RecyclerView for task lists.
- Intents for navigation and data transfer.
- AlarmManager, BroadcastReceiver, PendingIntent, and NotificationCompat for local reminder notifications.
- Material Design components for modern UI.

### Real-World Analogy

Think of the app like a personal office desk:

- SQLite is the notebook where tasks are permanently written.
- SharedPreferences is a small sticky note for settings like "user logged in" or "dark mode on".
- Activities are different rooms: login room, dashboard room, profile room, task details room.
- Intents are doors between rooms.
- RecyclerView is a smart notice board that shows many task cards efficiently.
- AlarmManager is an alarm clock.
- BroadcastReceiver is the person who hears the alarm.
- Notification is the message shown to the user.

## 2. Complete App Flow

### Opening the App

1. Android launches `SplashActivity` because it is marked as the launcher activity in `AndroidManifest.xml`.
2. `SplashActivity` applies saved dark/light theme using `SharedPrefManager`.
3. It waits for 2.2 seconds using `Handler`.
4. Then it decides where to go:
   - If onboarding is not seen, open `IntroActivity`.
   - Else if user is logged in, open `DashboardActivity`.
   - Else open `LoginActivity`.

### Onboarding Flow

1. `IntroActivity` shows three beginner slides.
2. User can tap Next or Skip.
3. When intro finishes, `saveOnboardingSeen()` stores that intro has been viewed.
4. App opens `LoginActivity`.

### Signup/Login Flow

1. In `SignupActivity`, user enters name, email, password, and confirm password.
2. The app validates the inputs.
3. User details are saved in SharedPreferences.
4. In `LoginActivity`, entered email and password are compared with saved details.
5. If valid, `createSession()` stores `is_logged_in = true`.
6. App opens `DashboardActivity`.

### Dashboard Flow

`DashboardActivity` is the main screen. It:

- Shows greeting based on time.
- Shows total/completed/pending task counts.
- Shows priority matrix counts.
- Shows recent active tasks.
- Supports search and priority filter.
- Lets user add a task using FloatingActionButton.
- Lets user click matrix cards to open `PriorityTasksActivity`.
- Uses bottom navigation for Dashboard, Completed Tasks, and Profile.

### Add Task Flow

1. User taps FloatingActionButton.
2. `AddTaskActivity` opens.
3. User enters title, description, due date, due time, priority, status, reminder date, and reminder time.
4. Due date and due time are combined into one string and stored in the existing `dueDate` database column.
5. Task is inserted into SQLite.
6. Reminder date and time are sent to `NotificationHelper`.
7. `NotificationHelper` schedules a local alarm.
8. App returns to Dashboard.

### Task Details Flow

1. User taps any task item.
2. `TaskAdapter` calls the click lambda.
3. Activity opens `TaskDetailsActivity` using an Intent with `task_id`.
4. `TaskDetailsActivity` loads that task from SQLite.
5. User can edit, mark completed, or delete.

### Completed Tasks Flow

1. User taps Completed bottom navigation.
2. `CompletedTasksActivity` opens.
3. It loads only tasks whose status is `Completed`.
4. If none exist, empty state UI is shown.

### Priority Matrix Flow

1. User taps a matrix card in Dashboard.
2. Dashboard sends selected priority using `putExtra("priority_type", priorityType)`.
3. `PriorityTasksActivity` reads that extra.
4. It calls `databaseHelper.getTasksByPriority(priorityType)`.
5. Only matching tasks are shown.

### Logout Flow

1. User opens Profile.
2. User taps Logout.
3. `clearSession()` stores `is_logged_in = false`.
4. `finishAffinity()` clears current activity stack.
5. Login screen opens.

## 3. Android Studio Project Structure

### `app/src/main/java`

Contains Kotlin source code. Your package is:

```text
com.example.made_project
```

Important subfolders:

- `activities`: all screen classes.
- `adapters`: RecyclerView adapter.
- `database`: SQLite helper.
- `models`: data class for task.
- `notifications`: notification helper and receiver.
- `utils`: reusable helper classes.

### `app/src/main/res`

Contains resources used by the app.

- `layout`: XML screen designs and item layouts.
- `drawable`: icons, backgrounds, shapes.
- `values`: colors, strings, arrays, themes.
- `menu`: bottom navigation menu.
- `anim`: list/item animations.
- `xml`: backup/data extraction rules.

### `AndroidManifest.xml`

The manifest is the app's registration file. It declares:

- Activities.
- Launcher activity.
- BroadcastReceiver.
- Permissions like notifications and exact alarms.
- App icon, label, theme.

### Gradle

Gradle builds the app. `app/build.gradle.kts` declares:

- Application ID.
- Compile SDK.
- Min SDK.
- Dependencies like AppCompat, Material, RecyclerView, CardView.

Viva answer:

> Android Studio organizes code and resources separately. Kotlin files define behavior, XML files define UI, Gradle builds the project, and AndroidManifest declares the app components.

## 4. Kotlin Concepts Used

### `val` and `var`

Simple:

- `val` means fixed reference.
- `var` means changeable variable.

Example:

```kotlin
val title = task.title
var currentIndex = 0
```

In your project:

- `val task = taskList[position]` because that task reference does not change inside binding.
- `var currentIndex = 0` in `IntroActivity` because slide index changes.

Viva point:

> Kotlin encourages using `val` when possible to reduce accidental changes.

### Data Types

Common types in project:

- `String`: title, email, dueDate.
- `Int`: task id, counts, spinner index.
- `Boolean`: logged in, dark mode, onboarding seen.
- `List<TaskModel>`: group of tasks.

### Functions

Functions group reusable logic.

Example:

```kotlin
private fun loadDashboardData()
```

Why used:

- Keeps `onCreate()` smaller.
- Makes viva explanation easier.
- Avoids repeated code.

### Classes

A class is a blueprint.

Examples:

- `DashboardActivity`
- `DatabaseHelper`
- `SharedPrefManager`
- `TaskAdapter`

### Data Class

`TaskModel` is:

```kotlin
data class TaskModel(
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String,
    val priorityType: String,
    val status: String
)
```

Simple:

It represents one task.

Why data class:

- Kotlin automatically gives `copy()`, `toString()`, `equals()`.
- `copy()` is used in `TaskDetailsActivity` to mark a task completed:

```kotlin
val updatedTask = task.copy(status = "Completed")
```

### Nullable Types

Kotlin uses `?` for values that may be null.

Example:

```kotlin
private var taskModel: TaskModel? = null
```

Why:

Task details may not exist if id is invalid.

### Safe Call `?.`

Example:

```kotlin
emailInput.text?.toString()
```

It prevents crash if `text` is null.

### Elvis Operator `?:`

Example:

```kotlin
preferences.getString(KEY_NAME, "Student") ?: "Student"
```

If left side is null, use right side.

### `lateinit`

Example:

```kotlin
private lateinit var databaseHelper: DatabaseHelper
```

Meaning:

The variable will be initialized later, usually inside `onCreate()`.

Common mistake:

Using a `lateinit` variable before initialization causes crash.

### `when`

Kotlin replacement for switch/if ladder.

Used in:

- Greeting logic.
- Status colors.
- Priority colors.
- Bottom navigation.
- Form validation.

Viva:

> `when` improves readability when one value has multiple possible cases.

### Lists, `filter`, and `count`

Example:

```kotlin
activeTasks = allTasks.filter { it.status != "Completed" }
val completedCount = allTasks.count { it.status == "Completed" }
```

Simple:

- `filter` returns matching items.
- `count` returns number of matching items.

### Lambda

Example from `TaskAdapter`:

```kotlin
TaskAdapter(emptyList()) { task ->
    val intent = Intent(this, TaskDetailsActivity::class.java)
    intent.putExtra("task_id", task.id)
    startActivity(intent)
}
```

The `{ task -> ... }` block is a lambda. It defines what happens when a task is clicked.

### Inheritance and Override

Example:

```kotlin
class DashboardActivity : BaseActivity()
override fun onCreate(savedInstanceState: Bundle?)
```

`DashboardActivity` inherits from `BaseActivity`, and Android calls overridden lifecycle methods.

## 5. Android Concepts

### Activity

Simple:

An Activity is one screen.

In your app:

- `SplashActivity`: first screen.
- `LoginActivity`: login screen.
- `DashboardActivity`: main screen.
- `AddTaskActivity`: add/edit task screen.

Viva:

> Activity represents a UI screen and handles user interaction for that screen.

### Activity Lifecycle

Important methods:

- `onCreate()`: screen is created. Initialize UI and variables.
- `onResume()`: screen becomes active again. Refresh data.

Why `onResume()` is used:

Dashboard reloads tasks when returning from Add/Edit/Delete screens.

### Intent

Simple:

Intent is a message to move from one screen to another.

Example:

```kotlin
startActivity(Intent(this, AddTaskActivity::class.java))
```

With data:

```kotlin
intent.putExtra("task_id", task.id)
```

Viva:

> Explicit Intent is used because we know exactly which activity to open.

### RecyclerView

Simple:

RecyclerView displays a scrollable list efficiently.

In your app:

- Dashboard recent tasks.
- Completed tasks.
- Priority tasks.

Internal working:

RecyclerView reuses item views instead of creating a new layout for every task. This improves performance.

### Adapter

The adapter connects data to RecyclerView UI.

`TaskAdapter`:

- Receives `List<TaskModel>`.
- Inflates `item_task.xml`.
- Binds task title, description, due date, priority, status.
- Handles item clicks.

### ViewHolder

The ViewHolder stores references to item views.

Why:

`findViewById()` is expensive if repeated for every scroll. ViewHolder keeps references ready.

### SQLite

SQLite is a local database built into Android.

Why used:

Tasks are structured data with columns like title, dueDate, priority, status. SQLite is better than SharedPreferences for lists of records.

### SharedPreferences

Used for small key-value data:

- User name.
- User email.
- Password.
- Login session.
- Onboarding seen.
- Dark mode.

Not used for tasks because tasks are multiple records, not simple settings.

### Toast and Snackbar

- Toast: small temporary message.
- Snackbar: message shown at bottom, Material style.

Used for:

- Login success/failure.
- Task saved.
- Task deleted.

### SearchView

Lets user type a query.

Dashboard uses:

```kotlin
task.title.contains(query, ignoreCase = true)
```

### Spinner

Dropdown selection.

Used for:

- Priority category.
- Task status.
- Dashboard priority filter.

### DatePickerDialog and TimePickerDialog

DatePickerDialog selects date.
TimePickerDialog selects hour/minute.

Your app combines:

```text
24/04/2026 + 06:30 PM = 24/04/2026 06:30 PM
```

This combined value is saved in the existing `dueDate` column.

### Notification System

Components:

- `AlarmManager`: schedules future time.
- `PendingIntent`: action Android can run later.
- `BroadcastReceiver`: receives alarm event.
- `NotificationChannel`: required for Android 8+.
- `NotificationCompat.Builder`: builds notification UI.

Flow:

1. User selects reminder date and time.
2. `AddTaskActivity` saves task.
3. `NotificationHelper.scheduleTaskReminder()` parses reminder time.
4. AlarmManager schedules alarm.
5. At reminder time, Android calls `ReminderReceiver`.
6. Receiver builds and shows notification.
7. Tapping notification opens Dashboard.

## 6. File-by-File Explanation

### `SplashActivity.kt`

Purpose:

First screen. Decides where user should go next.

Important variables:

- `sharedPrefManager`: reads onboarding/session/theme state.

Important logic:

- `applySavedTheme()` applies dark mode before UI appears.
- `Handler(...).postDelayed` waits 2.2 seconds.
- `when` decides destination activity.

Viva:

> Splash screen is used for branding and routing based on saved user state.

### `IntroActivity.kt`

Purpose:

Shows onboarding slides.

Important variables:

- `currentIndex`: current slide number.
- `slides`: list of title-description pairs.

Important functions:

- `bindSlide()`: updates UI with current slide.
- `finishIntro()`: saves onboarding seen and opens Login.

### `SignupActivity.kt`

Purpose:

Registers user locally.

Important logic:

- Validates name, email, password.
- Uses `Patterns.EMAIL_ADDRESS`.
- Saves data with `SharedPrefManager.saveSignupDetails()`.

Viva:

> Signup stores simple user details in SharedPreferences because this is a local beginner-level app.

### `LoginActivity.kt`

Purpose:

Validates user credentials and starts session.

Important logic:

- Reads email/password fields.
- Validates input.
- Calls `sharedPrefManager.validateLogin()`.
- Calls `createSession()` on success.

### `BaseActivity.kt`

Purpose:

Avoids repeating bottom navigation code in Dashboard, Completed, and Profile screens.

Important function:

- `setupBottomNavigation()`

Why useful:

Common navigation behavior is written once and reused.

### `DashboardActivity.kt`

Purpose:

Main screen.

Important variables:

- `databaseHelper`: loads tasks.
- `sharedPrefManager`: loads username.
- `taskAdapter`: connects tasks to RecyclerView.
- `allTasks`: all database tasks.
- `activeTasks`: tasks not completed.

Important functions:

- `loadDashboardData()`: loads counts and task list.
- `applyFilters()`: search + priority filtering.
- `countByPriority()`: matrix counts.
- `setupPriorityMatrixClicks()`: opens priority-specific screen.
- `getGreetingMessage()`: time-based greeting.

Viva:

> Dashboard is the central controller of task overview. It reads tasks from SQLite, calculates counts, and updates UI.

### `AddTaskActivity.kt`

Purpose:

Adds or edits tasks.

Important variables:

- `editTaskId`: `-1` means add mode, otherwise edit mode.
- `selectedDueCalendar`: stores selected due date/time.
- `selectedReminderCalendar`: stores reminder date/time.
- `dateFormatter`, `timeFormatter`, `dateTimeFormatter`: convert dates between `Calendar` and String.

Important functions:

- `showDatePicker()`: opens calendar date picker.
- `showTimePicker()`: opens time picker.
- `populateTaskDetails()`: fills fields in edit mode.
- `saveTask()`: inserts or updates SQLite and schedules reminder.
- `getCalendarForInput()`: decides whether input belongs to due date/time or reminder date/time.

Viva:

> The database structure is unchanged. Due date and time are saved as one formatted string in the existing dueDate column.

### `TaskDetailsActivity.kt`

Purpose:

Shows complete details of one task.

Important logic:

- Gets `task_id` from Intent.
- Calls `getTaskById()`.
- Displays title, description, due date/time, priority, and status.
- Edit button opens `AddTaskActivity` with task id.
- Mark Completed uses `task.copy(status = "Completed")`.
- Delete uses AlertDialog confirmation.

### `CompletedTasksActivity.kt`

Purpose:

Shows only completed tasks.

Important logic:

- Calls `databaseHelper.getCompletedTasks()`.
- Uses same `TaskAdapter`.
- Shows empty state if list is empty.

### `PriorityTasksActivity.kt`

Purpose:

Shows tasks from one priority category.

Important logic:

- Reads `priority_type` from Intent.
- Calls `getTasksByPriority(priorityType)`.
- Reuses `TaskAdapter`.
- Task click opens `TaskDetailsActivity`.

### `ProfileActivity.kt`

Purpose:

Shows user profile, dark mode toggle, logout.

Important logic:

- Reads name/email from SharedPreferences.
- Dark mode switch saves preference and changes theme.
- Logout clears session and opens Login.

### `TaskAdapter.kt`

Purpose:

Connects task data to `item_task.xml`.

Important parts:

- `TaskViewHolder`: holds TextViews and CardView.
- `onCreateViewHolder()`: inflates item layout.
- `onBindViewHolder()`: sets task data into UI.
- `updateTasks()`: replaces list and refreshes RecyclerView.
- `getPriorityColor()`: maps priority to badge color.
- `showDueDateWarning()`: shows Due Today or Overdue.

Viva:

> Adapter is the bridge between data source and RecyclerView item UI.

### `TaskModel.kt`

Purpose:

Represents one task record.

Fields:

- `id`
- `title`
- `description`
- `dueDate`
- `priorityType`
- `status`

### `DatabaseHelper.kt`

Purpose:

Handles SQLite database.

Extends:

```kotlin
SQLiteOpenHelper
```

Important functions:

- `onCreate()`: creates Tasks table.
- `insertTask()`: inserts a row.
- `updateTask()`: updates a row using id.
- `deleteTask()`: deletes a row using id.
- `getAllTasks()`: selects all tasks.
- `getTaskById()`: selects one task.
- `getCompletedTasks()`: returns completed tasks.
- `getTasksByPriority()`: returns tasks with selected priority.
- `getTasksByStatus()`: generic status filter.

### `SharedPrefManager.kt`

Purpose:

Central helper for SharedPreferences.

Stores:

- Signup details.
- Login session.
- Onboarding state.
- Dark mode state.

### `NotificationHelper.kt`

Purpose:

Creates notification channel and schedules reminder alarm.

Important functions:

- `createNotificationChannel()`
- `scheduleTaskReminder()`
- `parseReminderTimeMillis()`

### `ReminderReceiver.kt`

Purpose:

Receives alarm event and shows notification.

Important logic:

- Reads title, due date, priority from Intent extras.
- Builds notification.
- Uses PendingIntent to open Dashboard on click.

### `UiAnimator.kt`

Purpose:

Reusable animations.

Functions:

- `animateSequence()`
- `pop()`
- `pulse()`
- `animateProgress()`

## 7. XML Layout Guide

### What XML Layouts Are

XML files define the visual structure of screens. Kotlin controls behavior. Connection happens through IDs:

```xml
android:id="@+id/textWelcomeUser"
```

Kotlin:

```kotlin
findViewById<TextView>(R.id.textWelcomeUser)
```

### Important XML Terms

- `layout_width`: width of view.
- `layout_height`: height of view.
- `match_parent`: take full available size.
- `wrap_content`: take only required size.
- `padding`: space inside a view.
- `margin`: space outside a view.
- `orientation`: vertical or horizontal arrangement.
- `gravity`: alignment of content inside view.
- `visibility`: visible/gone.

### Your Layout Files

- `activity_splash.xml`: logo, title, tagline.
- `activity_intro.xml`: onboarding logo, title, description, buttons.
- `activity_login.xml`: email/password login card.
- `activity_signup.xml`: signup form.
- `activity_dashboard.xml`: dashboard counts, matrix, search, filter, RecyclerView, empty state, bottom nav, FAB.
- `activity_add_task.xml`: add/edit task form.
- `activity_task_details.xml`: task information and action buttons.
- `activity_completed_tasks.xml`: completed RecyclerView and empty state.
- `activity_profile.xml`: user info, dark mode switch, logout.
- `activity_priority_tasks.xml`: toolbar, RecyclerView, empty state.
- `item_task.xml`: one task card design.
- `view_matrix_card_*.xml`: four matrix cards.

## 8. SQLite Complete Guide

### Why SQLite

Tasks are structured and multiple. Each task has fields. SQLite supports tables, rows, queries, filtering, updating, and deleting.

### Why Not SharedPreferences for Tasks

SharedPreferences is for small key-value data. It is not good for many records. Searching, filtering, updating, and deleting task rows is easier in SQLite.

### Table

```sql
CREATE TABLE Tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    dueDate TEXT NOT NULL,
    priorityType TEXT NOT NULL,
    status TEXT NOT NULL
)
```

### Insert

Uses `ContentValues`, then:

```kotlin
db.insert(TABLE_TASKS, null, values)
```

### Update

Uses id:

```kotlin
db.update(TABLE_TASKS, values, "$COLUMN_ID=?", arrayOf(task.id.toString()))
```

### Delete

```kotlin
db.delete(TABLE_TASKS, "$COLUMN_ID=?", arrayOf(taskId.toString()))
```

### Select

```sql
SELECT * FROM Tasks ORDER BY dueDate ASC
```

### WHERE Condition

Used to filter:

```sql
SELECT * FROM Tasks WHERE priorityType=?
```

`?` prevents SQL injection and safely inserts parameter values.

### Cursor

Cursor is like a pointer over result rows. `moveToNext()` moves row by row. `cursor.use {}` closes it automatically.

## 9. RecyclerView Complete Guide

### Why RecyclerView Exists

If there are 100 tasks, Android should not create 100 full UI objects at once. RecyclerView creates only visible items and reuses them while scrolling.

### Adapter Flow

1. Activity creates adapter.
2. RecyclerView asks adapter for item count.
3. Adapter inflates `item_task.xml`.
4. ViewHolder stores view references.
5. Adapter binds task data.
6. User scrolls, views are reused.

### `updateTasks()`

```kotlin
fun updateTasks(updatedList: List<TaskModel>) {
    taskList = updatedList
    notifyDataSetChanged()
}
```

It replaces old data and tells RecyclerView to redraw.

## 10. Notification System

### Full Flow

1. User selects reminder date/time in Add Task.
2. `AddTaskActivity` calls:

```kotlin
NotificationHelper.scheduleTaskReminder(this, task, reminderDateTime)
```

3. `NotificationHelper` parses date/time into milliseconds.
4. It creates Intent for `ReminderReceiver`.
5. It wraps Intent in PendingIntent.
6. AlarmManager schedules that PendingIntent.
7. At selected time, Android sends broadcast.
8. `ReminderReceiver.onReceive()` runs.
9. Receiver builds notification.
10. User taps notification.
11. PendingIntent opens `DashboardActivity`.

### Notification Channel

Android 8+ requires notification channels. Channel defines notification category and importance.

### PendingIntent

Normal Intent runs now. PendingIntent runs later by Android.

Viva:

> PendingIntent gives another Android component permission to execute our intended action in the future.

## 11. Project Defense Answers

### Why Kotlin?

Kotlin is official for Android, concise, null-safe, and modern. It reduces boilerplate compared to Java.

### Why SQLite?

SQLite is local, offline, structured, and suitable for task records. It does not require internet or Firebase setup.

### Why not Firebase?

This project is a local task manager. Firebase is useful for cloud sync and multi-device login, but SQLite is simpler, offline, and enough for this scope.

### Why RecyclerView?

RecyclerView efficiently displays dynamic lists and reuses item views, improving performance.

### Why SharedPreferences?

It is perfect for small key-value data like login session, dark mode, and onboarding state.

### Why Intents?

Activities need a standard Android way to open each other and pass data like `task_id` or `priority_type`.

### Why BaseActivity?

It keeps common bottom navigation code in one place, reducing repeated code.

## 12. Advanced Understanding

### Why the App Avoids Crashes

- Uses Kotlin null safety.
- Uses `orEmpty()` for nullable text.
- Uses safe fallback values in SharedPreferences.
- Checks task existence in TaskDetails.
- Uses try/catch for date parsing and exact alarm scheduling.
- Uses `cursor.use {}` to close database cursors.

### Lifecycle Handling

Data is loaded in `onResume()` for screens where data may change:

- Dashboard refreshes after returning from Add/Edit.
- TaskDetails refreshes after edit.
- CompletedTasks refreshes after status changes.
- PriorityTasks refreshes after task changes.

### Performance

- RecyclerView reuses views.
- ViewHolder avoids repeated lookups.
- SQLite queries filter in database for priority/status.
- Shared code in `TaskAdapter` avoids duplicate adapters.

### Modular Structure

- Activities handle screens.
- Adapter handles list UI.
- DatabaseHelper handles database.
- SharedPrefManager handles preferences.
- NotificationHelper handles scheduling.
- ReminderReceiver handles notification display.
- TaskModel represents data.

## 13. Viva Questions and Answers

1. What is the project?
   Answer: It is an Android Kotlin task manager based on the Eisenhower Priority Matrix.

2. What is the Eisenhower Matrix?
   Answer: It categorizes tasks by importance and urgency into four categories.

3. Why did you use SQLite?
   Answer: Tasks are structured records and SQLite supports insert, update, delete, select, and filtering offline.

4. Why not SharedPreferences for tasks?
   Answer: SharedPreferences is for small key-value data, not lists of structured records.

5. What is SharedPreferences used for?
   Answer: User details, login session, onboarding state, and dark mode.

6. What is an Activity?
   Answer: One screen of the app.

7. What is `onCreate()`?
   Answer: Lifecycle method called when Activity is first created.

8. What is `onResume()`?
   Answer: Called when screen becomes active; used to refresh data.

9. What is Intent?
   Answer: Android message used to open another activity or pass data.

10. What is `putExtra()`?
    Answer: It attaches small data to an Intent.

11. How do you open TaskDetails?
    Answer: By sending `task_id` through Intent.

12. What is RecyclerView?
    Answer: Efficient scrollable list component.

13. What is Adapter?
    Answer: Bridge between data and RecyclerView UI.

14. What is ViewHolder?
    Answer: Holds item view references for efficient reuse.

15. What is `TaskModel`?
    Answer: Data class representing one task.

16. What is a data class?
    Answer: Kotlin class designed to store data and auto-generate helper methods.

17. What is `lateinit`?
    Answer: Promise that a variable will be initialized before use.

18. What is null safety?
    Answer: Kotlin feature that prevents null pointer crashes.

19. What is `?.`?
    Answer: Safe call operator.

20. What is `?:`?
    Answer: Elvis operator, gives fallback value.

21. What is `when`?
    Answer: Kotlin conditional expression like switch.

22. What is `filter`?
    Answer: Returns list elements matching a condition.

23. What is `count`?
    Answer: Counts elements matching a condition.

24. What is a lambda?
    Answer: A function passed as a value.

25. Why use BaseActivity?
    Answer: To reuse bottom navigation logic.

26. How does login work?
    Answer: Email/password are compared with saved SharedPreferences data.

27. How is session maintained?
    Answer: Boolean `is_logged_in` is stored in SharedPreferences.

28. How is logout done?
    Answer: Session is cleared and Login screen opens.

29. How does dark mode persist?
    Answer: Dark mode Boolean is saved in SharedPreferences.

30. How are tasks saved?
    Answer: `DatabaseHelper.insertTask()` inserts a row into SQLite.

31. How are tasks updated?
    Answer: `updateTask()` updates row by id.

32. How are tasks deleted?
    Answer: `deleteTask()` deletes row by id.

33. How are completed tasks loaded?
    Answer: `getCompletedTasks()` calls `getTasksByStatus("Completed")`.

34. How are priority tasks loaded?
    Answer: `getTasksByPriority(priorityType)` uses WHERE condition.

35. What is Cursor?
    Answer: Object used to read SQLite query results row by row.

36. Why use `cursor.use`?
    Answer: It closes cursor automatically.

37. What is ContentValues?
    Answer: Key-value object used to insert/update SQLite rows.

38. What is DatePickerDialog?
    Answer: Dialog for selecting date.

39. What is TimePickerDialog?
    Answer: Dialog for selecting time.

40. How is due date/time stored?
    Answer: Combined string in existing `dueDate` column.

41. Why no database change for due time?
    Answer: The existing dueDate text field can store formatted date and time together.

42. What is AlarmManager?
    Answer: Android service for scheduling future operations.

43. What is BroadcastReceiver?
    Answer: Component that receives broadcasts like alarm triggers.

44. What is PendingIntent?
    Answer: Intent that Android can execute later.

45. What is Notification Channel?
    Answer: Required category for notifications on Android 8+.

46. What is NotificationCompat?
    Answer: Compatibility helper for creating notifications.

47. What happens when notification is tapped?
    Answer: DashboardActivity opens.

48. What is Material Design?
    Answer: Google's design system for modern Android UI.

49. What is CardView?
    Answer: UI container with card-style background/elevation.

50. What is FloatingActionButton?
    Answer: Circular primary action button; used for Add Task.

51. What is SearchView?
    Answer: UI component for text search.

52. What is Spinner?
    Answer: Dropdown selector.

53. How does search work?
    Answer: Filters active tasks where title contains query.

54. How does priority filter work?
    Answer: Matches selected priority from Spinner.

55. How does empty state work?
    Answer: Shows a message when task list is empty.

56. How does priority color tag work?
    Answer: Adapter maps priority text to background color.

57. How does due warning work?
    Answer: Adapter compares task date with today's date.

58. Why use `Calendar`?
    Answer: It handles date/time and converts to milliseconds.

59. Why use SimpleDateFormat?
    Answer: It converts Date/Calendar to readable string and parses strings.

60. What is `finish()`?
    Answer: Closes current Activity.

61. What is `finishAffinity()`?
    Answer: Closes current activity stack.

62. What is AppCompatActivity?
    Answer: Activity class with backward compatibility features.

63. What is AppCompatDelegate?
    Answer: Used to control app-wide dark/light theme.

64. Why use local notifications?
    Answer: Reminder feature works offline without Firebase.

65. Why not WorkManager?
    Answer: AlarmManager is simpler for exact time reminders.

66. What is `R.id`?
    Answer: Generated reference to XML view IDs.

67. What is `R.layout`?
    Answer: Generated reference to XML layout files.

68. What is `setContentView()`?
    Answer: Connects an Activity to an XML layout.

69. What is `findViewById()`?
    Answer: Finds a UI view by its XML id.

70. What is `notifyDataSetChanged()`?
    Answer: Tells RecyclerView data changed and redraw list.

71. What is `LinearLayoutManager`?
    Answer: Lays RecyclerView items vertically or horizontally.

72. What is `ifBlank`?
    Answer: Uses fallback if string is blank.

73. Why use `orEmpty()`?
    Answer: Converts nullable String to non-null empty string.

74. What is `private`?
    Answer: Restricts access to inside the class/file.

75. What is `companion object`?
    Answer: Holds class-level constants/functions.

76. Why constants for database names?
    Answer: Avoid spelling mistakes and make maintenance easier.

77. What is AUTOINCREMENT?
    Answer: SQLite automatically generates unique id.

78. What is `WHERE id=?`?
    Answer: Selects/updates/deletes a specific row safely.

79. What happens if task id is invalid?
    Answer: TaskDetails shows "Task not found" and closes.

80. What is `copy()` in data class?
    Answer: Creates a new object with changed values.

81. Why does Dashboard use `take(6)`?
    Answer: Shows only recent six tasks in dashboard.

82. How are matrix counts calculated?
    Answer: `activeTasks.count { it.priorityType == category }`.

83. Why exclude completed tasks from active dashboard?
    Answer: Dashboard focuses on pending/in-progress tasks.

84. What does `ignoreCase = true` do?
    Answer: Search is case-insensitive.

85. How does PriorityTasksActivity receive category?
    Answer: Through Intent extra `priority_type`.

86. Does PriorityTasksActivity need new adapter?
    Answer: No, TaskAdapter is reusable.

87. What is XML view hierarchy?
    Answer: Parent-child arrangement of views.

88. What is padding?
    Answer: Space inside a view.

89. What is margin?
    Answer: Space outside a view.

90. What is gravity?
    Answer: Alignment of content.

91. What is `wrap_content`?
    Answer: Size only as much as content needs.

92. What is `match_parent`?
    Answer: Fill parent size.

93. What is `layout_weight`?
    Answer: Distributes available space in LinearLayout.

94. What is `visibility = gone`?
    Answer: View is hidden and takes no space.

95. What is `visibility = visible`?
    Answer: View is shown.

96. Why use XML arrays?
    Answer: To store Spinner options separately from Kotlin.

97. What is package name?
    Answer: Unique namespace for app classes.

98. What is minSdk?
    Answer: Oldest Android version supported.

99. What is targetSdk?
    Answer: Android version app is optimized for.

100. What is dependency?
     Answer: External library used by the project.

101. What is Material dependency used for?
     Answer: Material buttons, toolbar, cards, bottom navigation.

102. What is RecyclerView dependency used for?
     Answer: Displaying task lists.

103. What is CardView dependency used for?
     Answer: Card-like task items.

104. What is a common mistake with RecyclerView?
     Answer: Forgetting to set LayoutManager or adapter.

105. What is a common SQLite mistake?
     Answer: Forgetting to close cursor or mismatching column names.

106. What is a common Intent mistake?
     Answer: Using wrong extra key.

107. What is a common null-safety mistake?
     Answer: Force-unwrapping nullable values with `!!`.

108. What is a common notification mistake?
     Answer: Not creating a notification channel on Android 8+.

109. What is a common lifecycle mistake?
     Answer: Loading changing data only in `onCreate()` instead of `onResume()`.

110. How will you summarize the project?
     Answer: It is an offline Android task manager that uses the Eisenhower Matrix, SQLite persistence, RecyclerView lists, SharedPreferences sessions, and local notifications to help users manage priorities effectively.

## 14. Short Demo Script

Use this in presentation:

> My project is Task Manager with Priority Matrix. It helps users organize tasks using the Eisenhower Matrix: Important and Urgent, Important but Not Urgent, Urgent but Not Important, and Neither Urgent nor Important. The app starts with a splash screen, then onboarding, login/signup, and finally Dashboard. Tasks are stored locally using SQLite. User session, onboarding, and dark mode are stored using SharedPreferences. The Dashboard shows task counts, matrix counts, search, filter, and recent tasks. RecyclerView is used for efficient task lists. Users can add due date/time, custom reminder date/time, and receive local notifications through AlarmManager and BroadcastReceiver. The app is modular because activities handle screens, DatabaseHelper handles SQLite, TaskAdapter handles list display, and SharedPrefManager handles preferences.

## 15. Final Confidence Points

Remember these strong answers:

- SQLite is for structured task records.
- SharedPreferences is for small settings/session data.
- RecyclerView is for efficient dynamic lists.
- Intent is for navigation and passing ids/categories.
- Adapter binds task data to item views.
- ViewHolder improves scrolling performance.
- AlarmManager schedules reminder time.
- BroadcastReceiver receives alarm trigger.
- PendingIntent lets Android perform an action later.
- Notification channel is mandatory for modern Android notifications.
- Kotlin null safety reduces crashes.
- `onResume()` refreshes data when returning to a screen.
- BaseActivity avoids repeated bottom navigation code.
