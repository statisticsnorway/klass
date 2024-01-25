pipeline {

    agent any

    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    parameters {
        booleanParam(name: "RELEASE",
                description: "Build a release from current commit.",
                defaultValue: false)

        choice(name: 'ARTIFACT',
                choices: ['klass-api', 'klass-forvaltning'],
                description: 'Name of artifact to build and deploy.'
        )
    }

    tools {
        maven 'Maven 3.5.2'
        git 'Default'
    }

    stages {

        stage("Build & deploy SNAPSHOT of klass-api to Nexus") {
            when {
                expression { params.ARTIFACT == 'klass-api' }
            }
            tools {
                jdk 'OpenJDK Java 17'
            }
            steps {
                sh "mvn -B clean deploy -Pdocumentation -pl :klass-api -am"
            }
        }

        stage("Build & deploy SNAPSHOT of klass-forvaltning to Nexus") {
            when {
                expression { params.ARTIFACT == 'klass-forvaltning' }
            }
            tools {
                jdk 'Oracle Java 8'
            }
            steps {
                sh "mvn -B clean deploy -Pdocumentation -Djava.version=1.8 -pl :klass-forvaltning -am"
            }
        }

        stage("Build & deploy RELEASE of klass-api to Nexus") {
            when {
                expression { params.RELEASE && params.ARTIFACT == 'klass-api' }
            }
            tools {
                jdk 'OpenJDK Java 17'
            }
            steps {
                sh "mvn -B -DpushChanges=false release:prepare -P nexus -pl :klass-api -am"
                sshagent(['605c16cc-7c0c-4d39-8c8a-6d190e2f98b1']) {
                    sh('git push --follow-tags') 
                }
                sh "mvn -B release:perform -P nexus"
            }
        }

        stage("Build & deploy RELEASE of klass-forvaltning to Nexus") {
            when {
                expression { params.RELEASE && params.ARTIFACT == 'klass-forvaltning' }
            }
            tools {
                jdk 'Oracle Java 8'
            }
            steps {
                sh "mvn -B -DpushChanges=false release:prepare -P nexus -Djava.version=1.8 -pl :klass-forvaltning -am"
                sshagent(['605c16cc-7c0c-4d39-8c8a-6d190e2f98b1']) {
                    sh('git push --follow-tags')
                }
                sh "mvn -B release:perform -Djava.version=1.8 -pl :klass-forvaltning -am"
            }
        }

    }
}