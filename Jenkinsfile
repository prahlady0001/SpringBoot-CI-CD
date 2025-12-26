pipeline {
    agent any

    tools {
        maven 'maven-3'
        jdk 'java-17'
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }
    }

    post {
        success {
            echo '✅ CI Pipeline SUCCESS'
        }
        failure {
            echo '❌ CI Pipeline FAILED'
        }
    }
}
