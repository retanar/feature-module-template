plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.android.plugin)
    implementation(libs.kotlin.plugin)

    // Workaround to use libs in convention plugin scripts (and possibly other)
    // Refer to https://stackoverflow.com/questions/67795324/gradle7-version-catalog-how-to-use-it-with-buildsrc
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
