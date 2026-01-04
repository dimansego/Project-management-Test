# MVVM Restructuring Plan

## Current Structure Issues

1. Files scattered across `datageneral/` subfolders
2. Mixed concerns (data, domain, UI mixed together)
3. Duplicate models in different locations
4. Repositories in multiple locations

## Target MVVM Structure

```
com.example.projectmanagement/
├── data/                          # DATA LAYER
│   ├── local/                     # Room Database
│   │   ├── database/
│   │   │   ├── ProjectDatabase.kt
│   │   │   └── dao/
│   │   │       ├── ProjectDao.kt
│   │   │       ├── TaskDao.kt
│   │   │       └── UserDao.kt
│   │   └── entity/
│   │       ├── ProjectEntity.kt
│   │       ├── TaskEntity.kt
│   │       └── UserEntity.kt
│   ├── remote/                    # Supabase
│   │   ├── model/                 # Supabase models (String IDs)
│   │   │   ├── user/
│   │   │   ├── project/
│   │   │   ├── task/
│   │   │   └── meeting/
│   │   └── repository/            # Supabase repositories
│   │       ├── user/
│   │       ├── project/
│   │       ├── task/
│   │       └── meeting/
│   ├── repository/                 # Unified repositories
│   │   ├── ProjectRepository.kt    # Room-based
│   │   ├── AuthRepository.kt      # Room-based
│   │   └── SupabaseSyncRepository.kt
│   └── core/                       # Core infrastructure
│       ├── SupabaseClient.kt
│       └── config/
│           ├── SupabaseConfig.kt
│           ├── DateTimeConfig.kt
│           └── JsonConfig.kt
│
├── domain/                         # DOMAIN LAYER
│   ├── model/                      # Domain models (Int IDs)
│   │   ├── Project.kt
│   │   ├── Task.kt
│   │   ├── User.kt
│   │   ├── TaskStatus.kt
│   │   └── TaskPriority.kt
│   ├── usecase/                    # Use cases
│   │   ├── user/
│   │   ├── project/
│   │   ├── task/
│   │   └── meeting/
│   └── util/                       # Domain utilities
│       ├── FailureMapping.kt
│       └── Updatable.kt
│
└── ui/                             # UI LAYER (Already correct)
    ├── viewmodel/
    ├── auth/
    ├── home/
    └── ...
```

## Migration Steps

1. Move Room database files to `data/local/`
2. Move Supabase models to `data/remote/model/`
3. Move Supabase repositories to `data/remote/repository/`
4. Move domain models to `domain/model/`
5. Move use cases to `domain/usecase/`
6. Update all imports
7. Update package declarations

## Files to Move

### Data Layer
- `datageneral/database/` → `data/local/database/`
- `datageneral/data/model/` → `data/remote/model/`
- `datageneral/data/repository/` → `data/remote/repository/`
- `datageneral/repository/` → `data/repository/`
- `datageneral/core/` → `data/core/`

### Domain Layer
- `datageneral/model/` → `domain/model/`
- `datageneral/domain/usecase/` → `domain/usecase/`
- `datageneral/domain/utilities/` → `domain/util/`

### Keep As Is
- `ui/` - Already correct
- `MainActivity.kt`
- `ProjectApplication.kt`

## Notes

- Domain entities in `datageneral/domain/entity/` can stay or be moved to `domain/entity/`
- `di/AppModule.kt` can be moved to root or kept in data layer
- All package names need updating after moves

