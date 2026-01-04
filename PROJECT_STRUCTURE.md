# Project Structure Documentation

## Overview

This Android project follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns. The project is organized into three main layers: **Data**, **Domain**, and **UI**.

---

## 📁 Package Structure

```
com.example.projectmanagement/
├── data/                    # Data Layer (Local & Remote)
├── domain/                  # Domain Layer (Business Logic)
├── ui/                      # UI Layer (Presentation)
├── MainActivity.kt          # Main Activity
└── ProjectApplication.kt   # Application Class
```

---

## 📦 Package Responsibilities

### 1. `data/` - Data Layer

**Purpose:** Handles all data operations - local storage (Room) and remote API (Supabase).

#### `data/database/` - Room Database (Local Storage)
- **`entity/`** - Room database entities
  - `ProjectEntity.kt` - Project table entity with `isSynced` flag
  - `TaskEntity.kt` - Task table entity with `isSynced` flag
  - `UserEntity.kt` - User table entity with `isSynced` flag
- **`dao/`** - Data Access Objects (Room queries)
  - `ProjectDao.kt` - Project database operations
  - `TaskDao.kt` - Task database operations
  - `UserDao.kt` - User database operations
- **`ProjectDatabase.kt`** - Room database configuration (Version 2)

#### `data/model/` - Supabase Data Models
- **`user/AppUser.kt`** - Supabase user model (String IDs, @Serializable)
- **`project/Project.kt`** - Supabase project model
- **`project/ProjectMember.kt`** - Project member model
- **`task/Task.kt`** - Supabase task model
- **`task/TaskCategory.kt`** - Task category model
- **`task/TaskDependency.kt`** - Task dependency model
- **`meeting/`** - Meeting-related models

**Note:** These models use String IDs (UUIDs) and are designed for Supabase API communication.

#### `data/repository/` - Data Repositories
- **`user/`**
  - `AuthRepository.kt` - Supabase authentication operations
  - `UserRepository.kt` - User profile operations (Supabase)
- **`project/`**
  - `ProjectRepository.kt` - Project operations (Supabase)
  - `ProjectMemberRepository.kt` - Project member operations
- **`task/`**
  - `TaskRepository.kt` - Task operations (Supabase)
  - `TaskDependencyRepository.kt` - Task dependency operations
  - `TaskCategoryRepository.kt` - Task category operations
- **`meeting/`** - Meeting repositories

**Note:** These repositories interact directly with Supabase and return suspend functions.

#### `data/repository/` (Root) - Room Repositories
- **`ProjectRepository.kt`** - Room-based project repository (LiveData)
- **`AuthRepository.kt`** - Room-based auth repository
- **`SupabaseSyncRepository.kt`** - Unified repository that syncs Room ↔ Supabase

#### `data/core/` - Core Infrastructure
- **`SupabaseClient.kt`** - Supabase client wrapper
- **`config/`**
  - `SupabaseConfig.kt` - Supabase configuration (URL, keys)
  - `DateTimeConfig.kt` - Date/time utilities
  - `JsonConfig.kt` - JSON serialization config

---

### 2. `domain/` - Domain Layer

**Purpose:** Contains business logic, use cases, and domain models. This layer is independent of data sources and UI.

#### `domain/model/` - Domain Models
- **`Project.kt`** - Domain project model (Int IDs, used by UI)
- **`Task.kt`** - Domain task model (Int IDs, used by UI)
- **`User.kt`** - Domain user model (Int IDs, used by UI)
- **`TaskStatus.kt`** - Task status enum
- **`TaskPriority.kt`** - Task priority enum

**Note:** These models use Int IDs and are used by ViewModels and UI components.

#### `domain/usecase/` - Use Cases (Business Logic)
- **`user/`**
  - `UserAuthUseCases.kt` - SignUp, SignIn, SignOut use cases
  - `GetUserUseCase.kt` - Get user by ID
  - `AuthResponse.kt` - Authentication response model
  - `exception/UserAuthFailure.kt` - Authentication error types
- **`project/`**
  - `CreateProjectUseCase.kt` - Create project
  - `ViewProjectsUseCase.kt` - Get all projects
  - `UpdateProjectUseCase.kt` - Update project
  - `JoinProjectUseCase.kt` - Join project
  - `AssignLeaderUseCase.kt` - Assign project leader
  - `RemoveMemberUseCase.kt` - Remove project member
  - `exception/ProjectFailure.kt` - Project error types
- **`task/`**
  - `CreateTaskUseCase.kt` - Create task
  - `ViewTasksUseCase.kt` - Get tasks
  - `UpdateTaskUseCase.kt` - Update task
  - `DeleteTaskUseCase.kt` - Delete task
  - `AssignTaskUseCase.kt` - Assign task to user
  - `ChangeTaskStatusUseCase.kt` - Change task status
  - `AddTaskDependencyUseCase.kt` - Add task dependency
  - `ViewFilteredTasksUseCase.kt` - Get filtered tasks
  - `exception/TaskFailure.kt` - Task error types
  - `exception/DependencyFailure.kt` - Dependency error types
- **`meeting/`**
  - `ScheduleMeetingUseCase.kt` - Schedule meeting
  - `ViewMeetingsUseCase.kt` - Get meetings
  - `EditMeetingNoteUseCase.kt` - Edit meeting notes
  - `AddAttachmentUseCase.kt` - Add attachment
  - `exception/MeetingFailure.kt` - Meeting error types
  - `exception/AttachmentFailure.kt` - Attachment error types

#### `domain/entity/` - Domain Entities (Business Logic)
- **`user/AppUserEntity.kt`** - Domain user entity with validation
- **`project/ProjectEntity.kt`** - Domain project entity with validation
- **`project/ProjectMemberEntity.kt`** - Project member entity
- **`project/ProjectRole.kt`** - Project role enum
- **`task/TaskEntity.kt`** - Domain task entity with validation
- **`task/TaskStatus.kt`** - Task status enum (domain)
- **`task/TaskDependencyEntity.kt`** - Task dependency entity
- **`task/TaskDependencyType.kt`** - Dependency type enum
- **`task/TaskCategoryEntity.kt`** - Task category entity
- **`meeting/`** - Meeting domain entities

**Note:** Domain entities contain business logic and validation rules, separate from Room entities.

#### `domain/utilities/` - Domain Utilities
- **`FailureMapping.kt`** - Error mapping utilities
- **`Updatable.kt`** - Interface for entities that track modifications

---

### 3. `ui/` - UI Layer

**Purpose:** Contains all UI components (Activities, Fragments, ViewModels, Adapters).

#### `ui/viewmodel/` - ViewModels
- **`RegisterViewModel.kt`** - User registration logic
- **`LoginViewModel.kt`** - User login logic
- **`ForgotPasswordViewModel.kt`** - Password reset logic
- **`HomeViewModel.kt`** - Home screen logic
- **`CreateProjectViewModel.kt`** - Create project logic
- **`ProjectDetailViewModel.kt`** - Project details logic
- **`CreateEditTaskViewModel.kt`** - Create/edit task logic
- **`TaskDetailViewModel.kt`** - Task details logic
- **`MeetingsViewModel.kt`** - Meetings screen logic
- **`ProfileViewModel.kt`** - User profile logic

**Note:** ViewModels use repositories or use cases to fetch data and expose LiveData/StateFlow to UI.

#### `ui/auth/` - Authentication Screens
- **`RegisterFragment.kt`** - Registration screen
- **`LoginFragment.kt`** - Login screen
- **`ForgotPasswordFragment.kt`** - Password reset screen

#### `ui/home/` - Home Screen
- **`HomeFragment.kt`** - Main dashboard
- **`ProjectsAdapter.kt`** - Projects RecyclerView adapter
- **`TasksAdapter.kt`** - Tasks RecyclerView adapter

#### `ui/createproject/` - Create Project
- **`CreateProjectFragment.kt`** - Create project screen

#### `ui/createedittask/` - Create/Edit Task
- **`CreateEditTaskFragment.kt`** - Create/edit task screen

#### `ui/projectdetail/` - Project Details
- **`ProjectDetailFragment.kt`** - Project details screen
- **`TasksAdapter.kt`** - Tasks adapter for project

#### `ui/taskdetail/` - Task Details
- **`TaskDetailFragment.kt`** - Task details screen

#### `ui/meetings/` - Meetings
- **`MeetingsFragment.kt`** - Meetings list screen
- **`MeetingsAdapter.kt`** - Meetings RecyclerView adapter

#### `ui/profile/` - User Profile
- **`ProfileFragment.kt`** - User profile screen

#### `ui/common/` - Common UI Components
- **`UiState.kt`** - UI state wrapper (Loading, Success, Error)

---

## 🔄 Data Flow

### Registration Flow Example:
```
UI (RegisterFragment)
  ↓
ViewModel (RegisterViewModel)
  ↓
UseCase (SignUpUserUseCase)
  ↓
Repository (AuthRepository + UserRepository)
  ↓
Supabase API
  ↓
Response flows back up
```

### Project Creation Flow:
```
UI (CreateProjectFragment)
  ↓
ViewModel (CreateProjectViewModel)
  ↓
Repository (SupabaseSyncRepository)
  ↓
Room (Local) + Supabase (Remote)
```

---

## 📊 Model Types

### 1. Domain Models (`domain/model/`)
- **Purpose:** Used by UI and ViewModels
- **IDs:** Int (for local Room database)
- **Location:** `domain/model/`
- **Examples:** `Project`, `Task`, `User`

### 2. Supabase Models (`data/model/`)
- **Purpose:** API communication with Supabase
- **IDs:** String (UUIDs from Supabase)
- **Location:** `data/model/`
- **Examples:** `AppUser`, `Project` (Supabase), `Task` (Supabase)

### 3. Room Entities (`data/database/entity/`)
- **Purpose:** Local database persistence
- **IDs:** Int (auto-generated)
- **Location:** `data/database/entity/`
- **Examples:** `ProjectEntity`, `TaskEntity`, `UserEntity`
- **Features:** Include `isSynced: Boolean` flag for sync tracking

### 4. Domain Entities (`domain/entity/`)
- **Purpose:** Business logic and validation
- **Location:** `domain/entity/`
- **Examples:** `ProjectEntity`, `TaskEntity`, `AppUserEntity`
- **Features:** Validation rules, business logic

---

## 🔐 Security & RLS

### Row Level Security (RLS)
All Supabase database operations use Row Level Security policies:
- **`app_users` table:** Users can only insert/read/update their own profile
- **Policies:** Defined in Supabase dashboard (see `FIX_RLS_POLICY.sql`)

### Authentication Flow
1. User signs up → Supabase Auth creates account
2. User profile created in `app_users` table
3. RLS policies ensure users can only access their own data

---

## 🗄️ Database Architecture

### Room Database (Local)
- **Version:** 2
- **Migration:** Uses `.fallbackToDestructiveMigration()` for development
- **Purpose:** Offline-first architecture, local caching
- **Entities:** `UserEntity`, `ProjectEntity`, `TaskEntity`

### Supabase (Remote)
- **Purpose:** Cloud database, real-time sync
- **Tables:** `app_users`, `projects`, `tasks`, etc.
- **Sync:** Handled by `SupabaseSyncRepository`

---

## 🎯 MVVM Pattern

### Model
- **Domain Models:** `domain/model/`
- **Data Models:** `data/model/`
- **Entities:** `data/database/entity/` (Room), `domain/entity/` (Business logic)

### View
- **Fragments:** `ui/*/`
- **Activities:** `MainActivity.kt`
- **Adapters:** `ui/*/Adapters`

### ViewModel
- **ViewModels:** `ui/viewmodel/`
- **Responsibilities:**
  - Expose data to UI via LiveData/StateFlow
  - Handle user interactions
  - Call use cases or repositories
  - Manage UI state

---

## 📝 Key Files

### Application
- **`ProjectApplication.kt`** - Application class, dependency injection setup

### Main Activity
- **`MainActivity.kt`** - Main activity, navigation setup

### Configuration
- **`data/core/config/SupabaseConfig.kt`** - Supabase configuration
- **`data/database/ProjectDatabase.kt`** - Room database configuration

### Dependency Injection
- **`data/di/AppModule.kt`** - Dependency injection module (currently unused, dependencies in ProjectApplication)

---

## 🔄 Sync Architecture

### SupabaseSyncRepository
- **Purpose:** Unified repository that syncs between Room and Supabase
- **Flow:**
  1. Save to Room first (offline support)
  2. Sync to Supabase in background
  3. Update `isSynced` flag when successful

---

## ✅ Best Practices

1. **Separation of Concerns:** Each layer has clear responsibilities
2. **Single Source of Truth:** ViewModels expose data, UI observes
3. **Error Handling:** Use `UiState` wrapper for loading/success/error states
4. **Offline-First:** Data saved locally first, synced remotely
5. **Type Safety:** Use sealed classes for error types
6. **Logging:** Comprehensive logging for debugging

---

## 🚀 Future Improvements

1. **Proper Migrations:** Replace `.fallbackToDestructiveMigration()` with real migrations
2. **Repository Unification:** Further consolidate Room and Supabase repositories
3. **Use Case Integration:** Use cases currently not fully integrated with all ViewModels
4. **Dependency Injection:** Consider using Hilt or Koin for better DI
5. **Testing:** Add unit tests for ViewModels and use cases

