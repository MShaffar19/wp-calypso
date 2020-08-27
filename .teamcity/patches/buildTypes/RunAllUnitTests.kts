package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'RunAllUnitTests'
in the root project, and delete the patch script.
*/
create(DslContext.projectId, BuildType({
    id("RunAllUnitTests")
    name = "Run all unit tests"
    description = "test"

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Prepare environment"
            scriptContent = """
                set -e
                export HOME="/calypso"
                export NODE_ENV="test"
                export CHROMEDRIVER_SKIP_DOWNLOAD=true
                export PUPPETEER_SKIP_DOWNLOAD=true
                export npm_config_cache=${'$'}(yarn cache dir)
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh" --install
                nvm use
                
                # Install modules
                yarn install
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "automattic/wp-calypso-ci:1.0.5"
            dockerRunParameters = "-u %env.UID%"
        }
        script {
            name = "Run all tests (1)"
            scriptContent = """
                set -e
                export JEST_JUNIT_OUTPUT_NAME="results.xml"
                export HOME="/calypso"
                export NODE_ENV="test"
                export CHROMEDRIVER_SKIP_DOWNLOAD=true
                export PUPPETEER_SKIP_DOWNLOAD=true
                export npm_config_cache=${'$'}(yarn cache dir)
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh" --install
                nvm use
                
                # Install modules
                yarn install
                
                # Run client tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/client" yarn test-client --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run packages tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/packages" yarn test-packages --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run server tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/server" yarn test-server --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run FSE tests
                cd apps/full-site-editing
                JEST_JUNIT_OUTPUT_DIR="../../test_results" yarn test:js --reporters=default --reporters=jest-junit  --maxWorkers=${'$'}JEST_MAX_WORKERS
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "automattic/wp-calypso-ci:1.0.5"
            dockerRunParameters = "-u %env.UID%"
        }
    }

    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "test_results/**/*.xml")
        }
        perfmon {
        }
    }
}))

