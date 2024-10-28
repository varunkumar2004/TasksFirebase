package com.varunkumar.tasks.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
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
}