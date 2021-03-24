pipeline {

    agent any

    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    tools {
        jdk 'Oracle Java 8'
        maven 'Maven 3.5.2'
        git 'Default'
    }

    parameters {
        booleanParam(name: "RELEASE",
            description: "Build a release from current commit.",
            defaultValue: true)
    }

    stages {

        stage("Build & Deploy SNAPSHOT") {
            steps {
                sh "mvn -B clean deploy -Pdocumentation"
            }
        }

        stage("Release") {
            when {
                expression { params.RELEASE }
            }
            steps {
                sh "mvn -B -DdryRun=false -DpushChanges=false release:prepare"
                sshagent(['0dbe3a0a-6b80-451f-a60d-e65527dc9085']) {
                    sh('git push') 
                }
                sh "mvn -B -DdryRun=false release:perform"
            }
        }

    }
}