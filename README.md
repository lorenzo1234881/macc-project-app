# MACC Project App

The app is an attempt to follow the [Guide to app architecture](https://developer.android.com/jetpack/guide) from the andriod documentation, and so it is structured in the following way:

* **app**: App main module.

    * **api**: Auxiliar functions to contact APIs, exposed by the cloud part of the project.

    * **data**: Made of repositories that each can contain zero to many data sources.

    * **ui**: UI elements such as Activity and Fragment, and classes that produce, manipualte UI states, like ViewModel.

    * **domain**: Responsible for encapsulating complex business logic, or simple business logic that is reused by multiple ViewModels. It is used by **ui**, and has access to **data**.