-- ============================================
-- FIX ROW LEVEL SECURITY (RLS) POLICIES
-- ============================================
-- Run this SQL in Supabase SQL Editor to fix the registration issue
-- ============================================

-- Step 1: Drop existing policies (if any)
DROP POLICY IF EXISTS "Users can read own data" ON app_users;
DROP POLICY IF EXISTS "Users can insert own data" ON app_users;
DROP POLICY IF EXISTS "Users can update own data" ON app_users;
DROP POLICY IF EXISTS "Users can delete own data" ON app_users;

-- Step 2: Create correct policies that allow users to manage their own data

-- Policy 1: Allow users to INSERT their own profile
-- This is the critical one for registration!
CREATE POLICY "Users can insert own profile" ON app_users
  FOR INSERT
  WITH CHECK (auth.uid() = auth_id);

-- Policy 2: Allow users to SELECT their own profile
CREATE POLICY "Users can read own profile" ON app_users
  FOR SELECT
  USING (auth.uid() = auth_id);

-- Policy 3: Allow users to UPDATE their own profile
CREATE POLICY "Users can update own profile" ON app_users
  FOR UPDATE
  USING (auth.uid() = auth_id)
  WITH CHECK (auth.uid() = auth_id);

-- Policy 4: Allow users to DELETE their own profile (optional)
CREATE POLICY "Users can delete own profile" ON app_users
  FOR DELETE
  USING (auth.uid() = auth_id);

-- ============================================
-- VERIFICATION
-- ============================================
-- After running the above, verify RLS is enabled:
-- SELECT tablename, rowsecurity FROM pg_tables WHERE tablename = 'app_users';
-- Should show: rowsecurity = true

-- Check policies exist:
-- SELECT * FROM pg_policies WHERE tablename = 'app_users';
-- Should show 4 policies

-- ============================================
-- ALTERNATIVE: If you want to disable RLS temporarily for testing
-- ============================================
-- ⚠️ WARNING: Only for development/testing!
-- ALTER TABLE app_users DISABLE ROW LEVEL SECURITY;
-- Then re-enable it later:
-- ALTER TABLE app_users ENABLE ROW LEVEL SECURITY;

