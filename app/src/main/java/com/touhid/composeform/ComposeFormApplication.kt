package com.touhid.composeform

import android.app.Application
import com.touhid.composeform.session.SessionGuard
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ComposeFormApplication : Application() {

    @Inject
    lateinit var sessionGuard: SessionGuard

    override fun onCreate() {
        super.onCreate()
        // Watches for a session that has ended (refresh token rejected, new day) for as long as
        // the process lives.
        sessionGuard.start()
    }
}
