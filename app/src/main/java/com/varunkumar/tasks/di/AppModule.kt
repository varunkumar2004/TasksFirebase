package com.varunkumar.tasks.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.varunkumar.tasks.notification.AndroidTaskScheduler
import com.varunkumar.tasks.notification.TaskSchedulerReceiver
import com.varunkumar.tasks.notification.TaskSchedulerService
import com.varunkumar.tasks.sign_in.GoogleAuthUiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(@ApplicationContext context: Context): GoogleAuthUiClient {
       return GoogleAuthUiClient(
           context = context,
           oneTapClient = Identity.getSignInClient(context)
       )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideTaskSchedulerService(@ApplicationContext context: Context): TaskSchedulerService {
        return TaskSchedulerService(context)
    }

    @Provides
    @Singleton
    fun provideAndroidTaskScheduler(@ApplicationContext context: Context): AndroidTaskScheduler {
        return AndroidTaskScheduler(context)
    }
}
