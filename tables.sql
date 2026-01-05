-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.app_users (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  auth_id uuid NOT NULL UNIQUE,
  email text NOT NULL UNIQUE CHECK (email ~* '^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$'::text),
  name text,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT app_users_pkey PRIMARY KEY (id),
  CONSTRAINT app_users_auth_id_fkey FOREIGN KEY (auth_id) REFERENCES auth.users(id)
);
CREATE TABLE public.attachments (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  meeting_id uuid,
  url text NOT NULL,
  uploaded_by uuid NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  project_id uuid NOT NULL,
  CONSTRAINT attachments_pkey PRIMARY KEY (id),
  CONSTRAINT attachments_project_id_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(id),
  CONSTRAINT attachments_project_id_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(project_id),
  CONSTRAINT attachments_project_id_meeting_id_fkey FOREIGN KEY (project_id) REFERENCES public.meetings(id),
  CONSTRAINT attachments_project_id_meeting_id_fkey FOREIGN KEY (project_id) REFERENCES public.meetings(project_id),
  CONSTRAINT attachments_uploaded_by_fkey FOREIGN KEY (uploaded_by) REFERENCES public.app_users(id)
);
CREATE TABLE public.meeting_invitations (
  meeting_id uuid NOT NULL,
  user_id uuid NOT NULL,
  status USER-DEFINED NOT NULL DEFAULT 'invited'::invitation_status,
  invited_at timestamp with time zone DEFAULT now(),
  responded_at timestamp with time zone,
  project_id uuid NOT NULL,
  CONSTRAINT meeting_invitations_pkey PRIMARY KEY (meeting_id, user_id, project_id),
  CONSTRAINT meeting_invitations_project_id_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(id),
  CONSTRAINT meeting_invitations_project_id_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(project_id),
  CONSTRAINT meeting_invitations_project_id_meeting_id_fkey FOREIGN KEY (project_id) REFERENCES public.meetings(id),
  CONSTRAINT meeting_invitations_project_id_meeting_id_fkey FOREIGN KEY (project_id) REFERENCES public.meetings(project_id),
  CONSTRAINT meeting_invitations_project_id_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.project_members(project_id),
  CONSTRAINT meeting_invitations_project_id_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.project_members(user_id),
  CONSTRAINT meeting_invitations_project_id_user_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(project_id),
  CONSTRAINT meeting_invitations_project_id_user_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(user_id),
  CONSTRAINT meeting_invitations_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(id),
  CONSTRAINT meeting_invitations_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.app_users(id)
);
CREATE TABLE public.meeting_notes (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  meeting_id uuid NOT NULL,
  author_id uuid,
  notes text,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now(),
  project_id uuid NOT NULL,
  CONSTRAINT meeting_notes_pkey PRIMARY KEY (id),
  CONSTRAINT meeting_notes_author_id_fkey FOREIGN KEY (author_id) REFERENCES public.app_users(id),
  CONSTRAINT meeting_notes_project_id_author_id_fkey FOREIGN KEY (author_id) REFERENCES public.project_members(project_id),
  CONSTRAINT meeting_notes_project_id_author_id_fkey FOREIGN KEY (author_id) REFERENCES public.project_members(user_id),
  CONSTRAINT meeting_notes_project_id_author_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(project_id),
  CONSTRAINT meeting_notes_project_id_author_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(user_id),
  CONSTRAINT meeting_notes_project_id_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(id),
  CONSTRAINT meeting_notes_project_id_meeting_id_fkey FOREIGN KEY (meeting_id) REFERENCES public.meetings(project_id),
  CONSTRAINT meeting_notes_project_id_meeting_id_fkey FOREIGN KEY (project_id) REFERENCES public.meetings(id),
  CONSTRAINT meeting_notes_project_id_meeting_id_fkey FOREIGN KEY (project_id) REFERENCES public.meetings(project_id)
);
CREATE TABLE public.meetings (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL,
  title text NOT NULL,
  description text,
  start_time timestamp with time zone NOT NULL,
  end_time timestamp with time zone NOT NULL,
  location text NOT NULL,
  created_by uuid NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now(),
  notes text,
  notes_updated_at timestamp with time zone DEFAULT now(),
  notes_updated_by uuid,
  CONSTRAINT meetings_pkey PRIMARY KEY (id),
  CONSTRAINT meetings_notes_updated_by_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(project_id),
  CONSTRAINT meetings_notes_updated_by_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(user_id),
  CONSTRAINT meetings_notes_updated_by_project_id_fkey FOREIGN KEY (notes_updated_by) REFERENCES public.project_members(project_id),
  CONSTRAINT meetings_notes_updated_by_project_id_fkey FOREIGN KEY (notes_updated_by) REFERENCES public.project_members(user_id),
  CONSTRAINT meetings_project_id_created_by_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(project_id),
  CONSTRAINT meetings_project_id_created_by_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(user_id),
  CONSTRAINT meetings_project_id_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.project_members(project_id),
  CONSTRAINT meetings_project_id_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.project_members(user_id),
  CONSTRAINT meetings_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.app_users(id),
  CONSTRAINT meetings_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id)
);
CREATE TABLE public.pending_notifications (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  user_id uuid NOT NULL,
  type text NOT NULL,
  payload jsonb NOT NULL,
  CONSTRAINT pending_notifications_pkey PRIMARY KEY (id),
  CONSTRAINT pending_notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.project_members (
  project_id uuid NOT NULL,
  user_id uuid NOT NULL,
  role USER-DEFINED NOT NULL DEFAULT 'member'::member_role,
  joined_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT project_members_pkey PRIMARY KEY (project_id, user_id),
  CONSTRAINT project_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.app_users(id),
  CONSTRAINT project_members_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id)
);
CREATE TABLE public.projects (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  title text NOT NULL,
  description text,
  invite_code text NOT NULL DEFAULT SUBSTRING(encode(gen_random_bytes(6), 'hex'::text) FROM 1 FOR 10) UNIQUE,
  invite_expires_at timestamp with time zone NOT NULL DEFAULT (now() + '7 days'::interval),
  is_invite_active boolean DEFAULT true,
  owner_id uuid NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT projects_pkey PRIMARY KEY (id),
  CONSTRAINT projects_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.app_users(id)
);
CREATE TABLE public.task_categories (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL,
  name text NOT NULL,
  color text NOT NULL DEFAULT '#FFFFFF'::text CHECK (color ~ '^#[0-9A-Fa-f]{6}$'::text),
  CONSTRAINT task_categories_pkey PRIMARY KEY (id),
  CONSTRAINT task_categories_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id)
);
CREATE TABLE public.task_dependencies (
  project_id uuid NOT NULL,
  predecessor_id uuid NOT NULL,
  successor_id uuid NOT NULL,
  dependency_type USER-DEFINED NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  lag bigint DEFAULT '288000'::bigint CHECK (lag >= 0 AND lag <= 7776000),
  CONSTRAINT task_dependencies_pkey PRIMARY KEY (project_id, predecessor_id, successor_id),
  CONSTRAINT task_dependencies_project_id_predecessor_id_fkey FOREIGN KEY (project_id) REFERENCES public.tasks(id),
  CONSTRAINT task_dependencies_project_id_predecessor_id_fkey FOREIGN KEY (project_id) REFERENCES public.tasks(project_id),
  CONSTRAINT task_dependencies_project_id_predecessor_id_fkey FOREIGN KEY (predecessor_id) REFERENCES public.tasks(id),
  CONSTRAINT task_dependencies_project_id_predecessor_id_fkey FOREIGN KEY (predecessor_id) REFERENCES public.tasks(project_id),
  CONSTRAINT task_dependencies_project_id_successor_id_fkey FOREIGN KEY (project_id) REFERENCES public.tasks(id),
  CONSTRAINT task_dependencies_project_id_successor_id_fkey FOREIGN KEY (project_id) REFERENCES public.tasks(project_id),
  CONSTRAINT task_dependencies_project_id_successor_id_fkey FOREIGN KEY (successor_id) REFERENCES public.tasks(id),
  CONSTRAINT task_dependencies_project_id_successor_id_fkey FOREIGN KEY (successor_id) REFERENCES public.tasks(project_id)
);
CREATE TABLE public.tasks (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  project_id uuid NOT NULL,
  title text NOT NULL,
  description text,
  assignee_id uuid,
  status USER-DEFINED NOT NULL DEFAULT 'todo'::task_status,
  priority integer NOT NULL DEFAULT 2 CHECK (priority = ANY (ARRAY[1, 2, 3])),
  estimated_hours integer NOT NULL DEFAULT 8 CHECK (estimated_hours > 0),
  due_date date NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT tasks_pkey PRIMARY KEY (id),
  CONSTRAINT tasks_assignee_id_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(project_id),
  CONSTRAINT tasks_assignee_id_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.project_members(user_id),
  CONSTRAINT tasks_assignee_id_project_id_fkey FOREIGN KEY (assignee_id) REFERENCES public.project_members(project_id),
  CONSTRAINT tasks_assignee_id_project_id_fkey FOREIGN KEY (assignee_id) REFERENCES public.project_members(user_id),
  CONSTRAINT tasks_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id)
);