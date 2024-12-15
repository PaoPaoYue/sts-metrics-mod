plugins {
    id("java")
}

group = "com.github.paopaoyue"
version = "0.1.0-local"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(files("C:\\Users\\LENOVO\\Desktop\\projects\\mtsLib\\BaseMod.jar"))
    implementation(files("C:\\Users\\LENOVO\\Desktop\\projects\\mtsLib\\desktop-1.0.jar"))
    implementation(files("C:\\Users\\LENOVO\\Desktop\\projects\\mtsLib\\ModTheSpire.jar"))
    implementation(files("C:\\Users\\LENOVO\\Desktop\\projects\\mtsLib\\StSLib.jar"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Delete>("cleanDevFolder") {
    delete(fileTree("E:\\Steam Games\\steamapps\\common\\SlayTheSpire\\mods") { include("*.jar") })
}

tasks.register<Delete>("cleanPublishFolder") {
    delete(fileTree("E:\\Steam Games\\steamapps\\common\\SlayTheSpire\\metrics-mod\\content") { include("*.jar") })
}

tasks.register<Copy>("deployToDevFolder") {
    dependsOn(tasks.clean, tasks.jar)
    from(layout.buildDirectory.dir("libs"))
    include("*.jar")
    into(layout.buildDirectory.dir("E:\\Steam Games\\steamapps\\common\\SlayTheSpire\\mods"))
}

tasks.register<Copy>("deployToPublishFolder") {
    dependsOn(tasks.clean, tasks.jar)
    from(layout.buildDirectory.dir("libs"))
    include("*.jar")
    into(layout.buildDirectory.dir("E:\\Steam Games\\steamapps\\common\\SlayTheSpire\\metrics-local-mod\\content"))
}

tasks.register<Exec>("runGame") {
    dependsOn(":deployToTestFolder")
    workingDir("E:\\Steam Games\\steamapps\\common\\SlayTheSpire")
    commandLine("cmd", "/c", "java -jar mts-launcher.jar")
}

tasks.register<Exec>("publishMod") {
    dependsOn(":cleanPublishFolder", ":deployToPublishFolder")
    workingDir("E:\\Steam Games\\steamapps\\common\\SlayTheSpire")
    commandLine("cmd", "/c", "java -jar mod-uploader.jar upload -w metrics-mod")
}