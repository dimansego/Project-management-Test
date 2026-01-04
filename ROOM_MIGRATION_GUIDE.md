# Room Database Migration Guide

## ✅ Issue Fixed: Schema Version Mismatch

### Problem
Room detected a schema change (we added `isSynced` fields to entities) but the version number wasn't updated, causing a crash.

### Solution Applied

1. **Updated Database Version**: Changed from `version = 1` to `version = 2` in `ProjectDatabase.kt`

2. **Added Development Migration**: Added `.fallbackToDestructiveMigration()` in `ProjectApplication.kt`

### What This Means

**`.fallbackToDestructiveMigration()`** - This tells Room to:
- ✅ **Delete the entire database** when schema changes
- ✅ **Recreate it** with the new schema
- ⚠️ **WARNING**: All existing data will be lost!

### For Development (Current Setup)
✅ **This is fine** - Your app will work, but all data is cleared on each schema change.

### For Production (Future)
❌ **Remove `.fallbackToDestructiveMigration()`** and add proper migrations:

```kotlin
val database: ProjectDatabase by lazy {
    Room.databaseBuilder(
        applicationContext,
        ProjectDatabase::class.java,
        ProjectDatabase.DATABASE_NAME
    )
    .addMigrations(
        Migration(1, 2) { database ->
            // Add is_synced columns
            database.execSQL("ALTER TABLE projects ADD COLUMN is_synced INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE tasks ADD COLUMN is_synced INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE users ADD COLUMN is_synced INTEGER NOT NULL DEFAULT 0")
        }
    )
    .build()
}
```

## Current Status

✅ **Fixed** - Database version updated to 2
✅ **Fixed** - Development migration added
⚠️ **Note** - Data will be cleared on app restart (development only)

## Next Steps

1. **Test the app** - It should no longer crash
2. **For production** - Remove `.fallbackToDestructiveMigration()` and add proper migrations
3. **Future schema changes** - Always increment version and add migration

