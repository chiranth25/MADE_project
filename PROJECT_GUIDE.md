# Task Manager with Priority Matrix

## Project Structure

```
app/src/main/java/com/example/made_project/
├── activities/
│   ├── BaseActivity.kt
│   ├── SplashActivity.kt
│   ├── IntroActivity.kt
│   ├── LoginActivity.kt
│   ├── SignupActivity.kt
│   ├── DashboardActivity.kt
│   ├── AddTaskActivity.kt
│   ├── TaskDetailsActivity.kt
│   ├── CompletedTasksActivity.kt
│   └── ProfileActivity.kt
├── adapters/
│   └── TaskAdapter.kt
├── database/
│   └── DatabaseHelper.kt
├── models/
│   └── TaskModel.kt
└── utils/
    └── SharedPrefManager.kt
```

## Project Flow

1. `SplashActivity` opens first and waits for 2.2 seconds.
2. It checks `SharedPrefManager`:
   - If onboarding is not completed, it opens `IntroActivity`.
   - If onboarding is completed and session exists, it opens `DashboardActivity`.
   - Otherwise it opens `LoginActivity`.
3. `IntroActivity` shows three simple intro slides and stores first-time status in `SharedPreferences`.
4. `SignupActivity` stores name, email, and password in `SharedPreferences`.
5. `LoginActivity` validates email and password, checks stored signup data, and saves login session.
6. `DashboardActivity` reads tasks from SQLite and shows:
   - total, completed, and pending counts
   - four Eisenhower matrix category counts
   - recent tasks list using `RecyclerView`
   - search and filter support
7. `AddTaskActivity` inserts or updates a task in SQLite.
8. `TaskDetailsActivity` shows full task data and allows edit, delete, and complete actions.
9. `CompletedTasksActivity` shows only completed tasks.
10. `ProfileActivity` shows user details, dark mode toggle, app info, and logout.

## SharedPreferences Data

- User name
- User email
- User password
- Login session
- Intro seen status
- Dark mode setting

## SQLite Data

Database: `TaskManagerDB`

Table: `Tasks`

Columns:
- `id`
- `title`
- `description`
- `dueDate`
- `priorityType`
- `status`

## Viva Questions and Answers

1. What is the main purpose of this project?
   - This app helps users manage daily tasks using the Eisenhower Priority Matrix and store them locally.

2. Why did you use SharedPreferences?
   - SharedPreferences is used for lightweight key-value data such as login session, onboarding status, user details, and dark mode.

3. Why did you use SQLite?
   - SQLite is used for permanent structured task storage because tasks need insert, update, delete, and filter operations.

4. What is the role of RecyclerView?
   - RecyclerView efficiently displays a list of tasks and reuses item views to improve performance.

5. What is the role of TaskAdapter?
   - It connects the task data with the RecyclerView item layout and handles click events.

6. Which activities are used in this project?
   - Splash, Intro, Login, Signup, Dashboard, Add Task, Task Details, Completed Tasks, and Profile.

7. How does login work?
   - Signup stores email and password in SharedPreferences. Login compares input values with stored values, then saves a session boolean.

8. How is task data categorized?
   - Each task stores a `priorityType` string based on one of the four Eisenhower matrix categories.

9. How is delete handled safely?
   - A confirmation `AlertDialog` is shown before deleting a task from SQLite.

10. How is dark mode implemented?
   - A boolean flag is saved in SharedPreferences and applied using `AppCompatDelegate`.
