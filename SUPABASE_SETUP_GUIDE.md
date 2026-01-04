# Supabase Setup Guide

## ⚠️ Network Error Fix

If you're seeing "Network error" when registering, it's likely because Supabase is not configured correctly.

## Step 1: Get Your Supabase Credentials

1. Go to [https://supabase.com](https://supabase.com)
2. Sign in or create an account
3. Create a new project (or use existing)
4. Go to **Settings** → **API**
5. Copy:
   - **Project URL** (e.g., `https://xxxxx.supabase.co`)
   - **anon/public key** (starts with `eyJ...`)

## Step 2: Configure in Android Project

1. Open `local.properties` file in the root of your project
2. Add these lines (replace with your actual values):

```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
```

**Example:**
```properties
SUPABASE_URL=https://abcdefghijklmnop.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFiY2RlZmdoaWprbG1ub3AiLCJyb2xlIjoiYW5vbiIsImlhdCI6MTYzODk2NzI5MCwiZXhwIjoxOTU0NTQzMjkwfQ.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## Step 3: Create the app_users Table in Supabase

1. Go to **SQL Editor** in Supabase dashboard
2. Run this SQL:

```sql
-- Create app_users table
CREATE TABLE IF NOT EXISTS app_users (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  auth_id UUID NOT NULL UNIQUE,
  email TEXT NOT NULL,
  name TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Enable Row Level Security (RLS)
ALTER TABLE app_users ENABLE ROW LEVEL SECURITY;

-- ⚠️ IMPORTANT: Drop existing policies first if they exist
DROP POLICY IF EXISTS "Users can read own data" ON app_users;
DROP POLICY IF EXISTS "Users can insert own data" ON app_users;
DROP POLICY IF EXISTS "Users can update own data" ON app_users;
DROP POLICY IF EXISTS "Users can read own profile" ON app_users;
DROP POLICY IF EXISTS "Users can insert own profile" ON app_users;
DROP POLICY IF EXISTS "Users can update own profile" ON app_users;

-- Create policy to allow users to INSERT their own profile
-- This is CRITICAL for registration to work!
CREATE POLICY "Users can insert own profile" ON app_users
  FOR INSERT
  WITH CHECK (auth.uid() = auth_id);

-- Create policy to allow users to read their own data
CREATE POLICY "Users can read own profile" ON app_users
  FOR SELECT
  USING (auth.uid() = auth_id);

-- Create policy to allow users to update their own data
CREATE POLICY "Users can update own profile" ON app_users
  FOR UPDATE
  USING (auth.uid() = auth_id)
  WITH CHECK (auth.uid() = auth_id);
```

## Step 4: Rebuild the Project

1. **Sync Project** in Android Studio (File → Sync Project with Gradle Files)
2. **Clean Project** (Build → Clean Project)
3. **Rebuild Project** (Build → Rebuild Project)
4. Run the app again

## Step 5: Verify Configuration

Check Logcat for these messages:
- ✅ If you see warnings about Supabase URL/KEY, the configuration is missing
- ✅ Check for any error messages during signup

## Common Issues

### Issue 1: "Network error" message
**Cause:** Supabase URL or KEY not configured correctly
**Fix:** 
- Check `local.properties` has correct values
- Rebuild the project
- Check Logcat for configuration warnings

### Issue 2: "Could not create user profile"
**Cause:** `app_users` table doesn't exist or RLS policies are blocking
**Fix:**
- Create the `app_users` table (see Step 3)
- Check RLS policies allow inserts

### Issue 3: Still getting errors after setup
**Fix:**
1. Check Logcat for detailed error messages
2. Verify Supabase project is active
3. Check internet connection
4. Verify API keys are correct

## Testing

After setup:
1. Try registering a new user
2. Check Logcat for any errors
3. Check Supabase dashboard → Authentication → Users (should see new user)
4. Check Supabase dashboard → Table Editor → app_users (should see user profile)

## Need Help?

Check Logcat output for detailed error messages. The app now logs:
- Configuration warnings
- Signup process steps
- Detailed error messages

Look for tags:
- `SupabaseConfig` - Configuration issues
- `SignUpUserUseCase` - Signup process
- `UserRepository` - Database operations
- `RegisterViewModel` - UI errors


