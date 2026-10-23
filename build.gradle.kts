plugins {
    application
}

val swtVersion = "3.135.0"
val jfaceVersion = "3.40.0"

// SWT ships one artifact per platform (windowing system, OS, architecture)
val swtPlatform: String = run {
    val os = System.getProperty("os.name").lowercase()
    val arch = when (val a = System.getProperty("os.arch")) {
        "amd64", "x86_64" -> "x86_64"
        "aarch64", "arm64" -> "aarch64"
        else -> a
    }
    when {
        os.contains("mac") -> "cocoa.macosx.$arch"
        os.contains("win") -> "win32.win32.$arch"
        else -> "gtk.linux.$arch"
    }
}
val isMac = System.getProperty("os.name").lowercase().contains("mac")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.eclipse.platform:org.eclipse.swt.$swtPlatform:$swtVersion")
    implementation("org.eclipse.platform:org.eclipse.jface:$jfaceVersion") {
        // the generic SWT artifact selects the platform via Maven profiles, which Gradle ignores
        exclude(group = "org.eclipse.platform", module = "org.eclipse.swt")
    }
    // JFace loads some of its icons as SVG
    runtimeOnly("org.eclipse.platform:org.eclipse.swt.svg:3.133.100") {
        exclude(group = "org.eclipse.platform", module = "org.eclipse.swt")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets {
    main {
        java.setSrcDirs(listOf("app/src"))
        resources.setSrcDirs(listOf("app/resources"))
        // the 2004 manifest with its ext/*.jar Class-Path is kept for reference only
        resources.exclude("META-INF/MANIFEST.MF")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val appJvmArgs = buildList {
    // SWT must run on the main thread on macOS
    if (isMac) add("-XstartOnFirstThread")
    // SWT loads its native libraries via JNI
    add("--enable-native-access=ALL-UNNAMED")
}

application {
    mainClass = "com.groovemanager.app.mc909se.Starter"
    applicationDefaultJvmArgs = appJvmArgs
}

// The app loads its icons from ./root, so run it from the app directory
tasks.named<JavaExec>("run") {
    workingDir = file("app")
}

tasks.register<JavaExec>("runSse") {
    group = "application"
    description = "Runs the Simple Sample Editor (basic version)."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.groovemanager.app.sse.Starter"
    jvmArgs = appJvmArgs
    workingDir = file("app")
    javaLauncher = javaToolchains.launcherFor(java.toolchain)
}
