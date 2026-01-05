package com.example.projectmanagement

import android.app.Application
import androidx.room.Room
import com.example.projectmanagement.data.database.ProjectDatabase
import com.example.projectmanagement.data.repository.ProjectRepository as RoomProjectRepository
import com.example.projectmanagement.datageneral.core.SupabaseClient
import com.example.projectmanagement.datageneral.core.config.SupabaseConfig
import com.example.projectmanagement.datageneral.data.repository.meeting.MeetingRepository
import com.example.projectmanagement.datageneral.data.repository.project.ProjectRepository as SupabaseProjectRepository
import com.example.projectmanagement.datageneral.data.repository.task.TaskRepository as SupabaseTaskRepository
import com.example.projectmanagement.datageneral.data.repository.user.AuthRepository
import com.example.projectmanagement.datageneral.data.repository.user.UserRepository
import com.example.projectmanagement.datageneral.di.appModule
import com.example.projectmanagement.datageneral.domain.usecase.user.SignUpUserUseCase
import com.example.projectmanagement.datageneral.repository.SupabaseSyncRepository

class ProjectApplication : Application() {
    val database: ProjectDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            ProjectDatabase::class.java,
            ProjectDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigrationOnDowngrade() // Allow downgrades
            .fallbackToDestructiveMigration() // ⚠️ DEVELOPMENT ONLY: Clears database on schema change
        // TODO: Remove fallbackToDestructiveMigration() in production and add proper migrations
        .build()
    }

    // Supabase setup
    private val supabaseClient: SupabaseClient by lazy {
        SupabaseClient(SupabaseConfig.SUPABASE_URL, SupabaseConfig.SUPABASE_ANON_KEY)
    }
    
    val authRepository: AuthRepository by lazy {
        AuthRepository(supabaseClient)
    }
    
    val userRepository: UserRepository by lazy {
        UserRepository(supabaseClient, authRepository)
    }
    
    // Use cases
    val signUpUserUseCase: SignUpUserUseCase by lazy {
        SignUpUserUseCase(authRepository, userRepository)
    }
    
    val signInUserUseCase: com.example.projectmanagement.datageneral.domain.usecase.user.SignInUserUseCase by lazy {
        com.example.projectmanagement.datageneral.domain.usecase.user.SignInUserUseCase(authRepository, userRepository)
    }
    
    // Sync repository
    val syncRepository: SupabaseSyncRepository by lazy {
        val roomRepo = RoomProjectRepository(
            database.projectDao(),
            database.taskDao()
        )
        val supabaseProjectRepo = SupabaseProjectRepository(supabaseClient)
        val supabaseTaskRepo = SupabaseTaskRepository(supabaseClient)
        val meetingRepo = MeetingRepository(supabaseClient)
        SupabaseSyncRepository(roomRepo, supabaseProjectRepo, supabaseTaskRepo, meetingRepo, authRepository, userRepository = userRepository)
    }
}

