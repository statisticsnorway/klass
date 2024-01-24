pipeline {

    agent any

    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    parameters {
        choice(name: 'Artifact',
                choices: ['klass-api', 'klass-forvaltning'],
                description: 'Name of artifact to build and deploy.'
        )
    }

    parameters {
        booleanParam(name: "RELEASE",
                description: "Build a release from current commit.",
                defaultValue: false)
    }

    tools {
        if (${params.Artifact} == 'klass-api') {
            jdk 'OpenJDK Java 17'
        } else {
            jdk 'Oracle Java 8'
        }
        maven 'Maven 3.5.2'
        git 'Default'
    }

    stages {

        stage("Build & deploy SNAPSHOT to Nexus") {
            steps {
                sh "mvn -B clean deploy -Pdocumentation -pl :${params.Artifact} -am"
            }
        }

        stage("Build & deploy RELEASE to Nexus") {
            when {
                expression { params.RELEASE }
            }
            steps {
                sh "mvn -B -DpushChanges=false release:prepare"
                sshagent(['605c16cc-7c0c-4d39-8c8a-6d190e2f98b1']) {
                    sh('git push --follow-tags') 
                }
                sh "mvn -B release:perform -pl :${params.Artifact} -am"
            }
        }

    }
}