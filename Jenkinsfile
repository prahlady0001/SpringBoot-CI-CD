pipeline {
    agent any

    tools {
        maven 'maven-3'
        jdk 'java-17'
    }

    environment {
        APP_SERVER = '13.53.197.75'
        SSH_CRED   = 'app-server-ssh'
        APP_DIR    = '/opt/myapp'
        TMP_DIR    = '/home/ec2-user/app'
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

        stage('Deploy to EC2-2') {
            steps {
                sshagent(credentials: ["${SSH_CRED}"]) {
                    sh """
                        scp -o StrictHostKeyChecking=no target/*.jar ec2-user@${APP_SERVER}:${TMP_DIR}/
                        ssh ec2-user@${APP_SERVER} '
                            sudo systemctl stop myapp || true
                            sudo cp ${TMP_DIR}/*.jar ${APP_DIR}/myapp.jar
                            sudo systemctl start myapp
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '🚀 CI/CD PIPELINE SUCCESS'
        }
        failure {
            echo '❌ CI/CD PIPELINE FAILED'
        }
    }
}
