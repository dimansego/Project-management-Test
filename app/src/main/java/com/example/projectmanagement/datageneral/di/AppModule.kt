package com.example.projectmanagement.datageneral.di

import com.example.projectmanagement.datageneral.core.config.SupabaseConfig
import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.data.repository.meeting.*
import com.example.projectmanagement.datageneral.data.repository.project.*
import com.example.projectmanagement.datageneral.data.repository.task.*
import com.example.projectmanagement.datageneral.data.repository.user.*
import com.example.projectmanagement.datageneral.domain.usecase.meeting.*
import com.example.projectmanagement.datageneral.domain.usecase.project.*
import com.example.projectmanagement.datageneral.domain.usecase.task.*
import com.example.projectmanagement.datageneral.domain.usecase.user.*

class AppContainer(
    val signUpUserUseCase: SignUpUserUseCase,
    val signInUserUseCase: SignInUserUseCase,
    val signOutUserUseCase: SignOutUserUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val getUserByIdUseCase: GetUserByIdUseCase,
    val viewProjectsUseCase: ViewProjectsUseCase,
    val createProjectUseCase: CreateProjectUseCase,
    val joinProjectUseCase: JoinProjectUseCase,
    val assignLeaderUseCase: AssignLeaderUseCase,
    val removeMemberUseCase: RemoveMemberUseCase,
    val updateProjectUseCase: UpdateProjectUseCase,
    val viewTasksUseCase: ViewTasksUseCase,
    val addTaskDependencyUseCase: AddTaskDependencyUseCase,
    val assignTaskUseCase: AssignTaskUseCase,
    val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    val createTaskUseCase: CreateTaskUseCase,
    val deleteTaskUseCase: DeleteTaskUseCase,
    val updateTaskUseCase: UpdateTaskUseCase,
    val scheduleMeetingUseCase: ScheduleMeetingUseCase,
    val editMeetingNoteUseCase: EditMeetingNoteUseCase,
    val viewMeetingsUseCase: ViewMeetingsUseCase,
    val addAttachmentUseCase: AddAttachmentUseCase
)

fun appModule(): AppContainer {
    val supabaseClient = SupabaseClient(SupabaseConfig.SUPABASE_URL, SupabaseConfig.SUPABASE_ANON_KEY)

    val authRepository = AuthRepository(supabaseClient)
    val userRepository = UserRepository(supabaseClient, authRepository)
    val projectRepository = ProjectRepository(supabaseClient)
    val projectMemberRepository = ProjectMemberRepository(supabaseClient)
    val taskRepository = TaskRepository(supabaseClient)
    val taskDependencyRepository = TaskDependencyRepository(supabaseClient)
    val meetingRepository = MeetingRepository(supabaseClient)
    val meetingInvitationRepository = MeetingInvitationRepository(supabaseClient)
    val attachmentRepository = AttachmentRepository(supabaseClient)

    return AppContainer(
        signUpUserUseCase = SignUpUserUseCase(authRepository, userRepository),
        signInUserUseCase = SignInUserUseCase(authRepository, userRepository),
        signOutUserUseCase = SignOutUserUseCase(authRepository),
        getCurrentUserUseCase = GetCurrentUserUseCase(userRepository),
        getUserByIdUseCase = GetUserByIdUseCase(userRepository),
        viewProjectsUseCase = ViewProjectsUseCase(projectRepository),
        createProjectUseCase = CreateProjectUseCase(projectRepository, projectMemberRepository),
        joinProjectUseCase = JoinProjectUseCase(projectRepository, projectMemberRepository, userRepository),
        assignLeaderUseCase = AssignLeaderUseCase(projectMemberRepository, userRepository),
        removeMemberUseCase = RemoveMemberUseCase(projectMemberRepository),
        updateProjectUseCase = UpdateProjectUseCase(projectRepository),
        viewTasksUseCase = ViewTasksUseCase(taskRepository),
        addTaskDependencyUseCase = AddTaskDependencyUseCase(taskDependencyRepository),
        assignTaskUseCase = AssignTaskUseCase(taskRepository),
        changeTaskStatusUseCase = ChangeTaskStatusUseCase(taskRepository, taskDependencyRepository),
        createTaskUseCase = CreateTaskUseCase(taskRepository),
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository),
        updateTaskUseCase = UpdateTaskUseCase(taskRepository),
        scheduleMeetingUseCase = ScheduleMeetingUseCase(meetingRepository),
        editMeetingNoteUseCase = EditMeetingNoteUseCase(meetingRepository),
        addAttachmentUseCase = AddAttachmentUseCase(attachmentRepository),
        viewMeetingsUseCase = ViewMeetingsUseCase(meetingRepository)
    )

}