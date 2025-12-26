pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
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
