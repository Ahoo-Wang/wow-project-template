plugins {
    // me.ahoo.wow.api: brings wow-api, assembles the contract of the domain module declared below and compiles
    // `DemoContract` and the `<State>View`s from it.
    id("wow.api-conventions")
}

dependencies {
    // The domain module whose contract this module carries.
    wowContract(project(":domain"))
    // The commands' Jakarta constraints, which the host checks at admission (`wow.command.invalid`, 400).
    api("jakarta.validation:jakarta.validation-api")
    // The query fields of the generated contract code (`DemoContract.Demo.STATE__*`).
    api("me.ahoo.wow:wow-query-api")
}
