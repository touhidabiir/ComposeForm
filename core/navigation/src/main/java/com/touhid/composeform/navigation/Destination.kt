package com.touhid.composeform.navigation

// A screen another feature may open. Each one is declared in its owning feature's `navigation`
// module, the only module another feature is allowed to depend on. It says *what* to open, never
// *how*: whether it's a Compose screen, an XML Activity or a Fragment flow stays inside the
// owning feature's `ui` module.
interface Destination
