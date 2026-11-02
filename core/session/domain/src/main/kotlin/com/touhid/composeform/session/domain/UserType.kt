package com.touhid.composeform.session.domain

// Which kind of user is signed in. Decides the home screen (:app's splash routes on it).
enum class UserType {
    // Sees only acquisition approvals.
    Approver,

    // Sees everything else: leads, the specific form, the form demo.
    Agent,
}
