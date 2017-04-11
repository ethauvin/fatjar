
import com.beust.kobalt.buildScript
import com.beust.kobalt.plugin.application.application
import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.project

val bs = buildScript {
    repos()
}


val p = project {

    name = "fatjar"
    group = "com.example"
    artifactId = name
    version = "0.1"

    sourceDirectories {
        path("src/main/kotlin")
    }

    sourceDirectoriesTest {
        path("src/test/kotlin")
    }

    dependencies {
        compile("org.apache.commons:commons-compress:1.13")
        compile("commons-io:commons-io:2.5")
    }

    dependenciesTest {
        compile("org.testng:testng:6.11")

    }

    assemble {
        jar {
        }
    }

    application {
        mainClass = "com.example.MainKt"
    }


}
