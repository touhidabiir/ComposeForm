package com.touhid.composeform.feature.specificform.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint

// The specific form's host Activity: the entry point SpecificFormHandler opens. Shows
// SpecificFormFragment as the only step (1 of 1) until a real multi-step wizard hosts it.
//
// A FragmentActivity, since the form is a Fragment, and an @AndroidEntryPoint because Hilt only
// injects a Fragment whose Activity is one too. The long @AndroidEntryPoint(FragmentActivity::class)
// form, extending the generated Hilt_SpecificFormActivity directly: only :app applies the Hilt
// Gradle plugin, and that plugin's bytecode rewrite is what lets the short form work.
@AndroidEntryPoint(FragmentActivity::class)
class SpecificFormActivity : Hilt_SpecificFormActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // A fixed id (res/values/ids.xml), so the FragmentManager finds the same container when it
        // restores the fragment after a configuration change or process death.
        setContentView(FragmentContainerView(this).apply { id = R.id.specificform_container })
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.specificform_container, SpecificFormFragment.newInstance(position = 1, totalCount = 1))
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, SpecificFormActivity::class.java)
    }
}
