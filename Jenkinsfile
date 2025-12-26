pipeline {
    agent any

    tools {
        maven 'maven-3'
        jdk 'java-17'
    }

    environment {
        APP_SERVER = '13.53.197.75'
        SSH_CRED   = 'app-server-ssh'

        APP_DIR = '/opt/myapp'
        TMP_DIR = '/home/ec2-user/app'

        PORT_A = '8086'   // Blue
        PORT_B = '8087'   // Green
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Detect Active Port') {
            steps {
                sshagent(credentials: [SSH_CRED]) {
                    sh """
                    ssh ec2-user@${APP_SERVER} '
                      if systemctl is-active --quiet myapp@${PORT_A}; then
                        echo ${PORT_A} > /tmp/active_port
                      else
                        echo ${PORT_B} > /tmp/active_port
                      fi
                    '
                    """
                }
            }
        }

        stage('Decide Idle Port') {
            steps {
                sshagent(credentials: [SSH_CRED]) {
                    sh """
                    ssh ec2-user@${APP_SERVER} '
                      ACTIVE=\$(cat /tmp/active_port)
                      if [ "\$ACTIVE" = "${PORT_A}" ]; then
                        echo ${PORT_B} > /tmp/idle_port
                      else
                        echo ${PORT_A} > /tmp/idle_port
                      fi
                    '
                    """
                }
            }
        }

        stage('Deploy New Version to Idle Port') {
            steps {
                sshagent(credentials: [SSH_CRED]) {
                    sh """
                    scp target/*.jar ec2-user@${APP_SERVER}:${TMP_DIR}/

                    ssh ec2-user@${APP_SERVER} '
                      IDLE=\$(cat /tmp/idle_port)
                      sudo cp ${TMP_DIR}/*.jar ${APP_DIR}/myapp-\${IDLE}.jar
                      sudo systemctl restart myapp@\${IDLE}
                    '
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                sshagent(credentials: [SSH_CRED]) {
                    sh """
                    ssh ec2-user@${APP_SERVER} '
                      IDLE=\$(cat /tmp/idle_port)
                      sleep 15
                      curl -f http://127.0.0.1:\${IDLE}/ || exit 1
                    '
                    """
                }
            }
        }

        stage('Switch Nginx Traffic') {
            steps {
                sshagent(credentials: [SSH_CRED]) {
                    sh """
                    ssh ec2-user@${APP_SERVER} '
                      IDLE=\$(cat /tmp/idle_port)
                      sudo sed -i "s/127.0.0.1:[0-9]*/127.0.0.1:\${IDLE}/" /etc/nginx/conf.d/myapp.conf
                      sudo nginx -t
                      sudo systemctl reload nginx
                    '
                    """
                }
            }
        }

        stage('Stop Old Version') {
            steps {
                sshagent(credentials: [SSH_CRED]) {
                    sh """
                    ssh ec2-user@${APP_SERVER} '
                      ACTIVE=\$(cat /tmp/active_port)
                      sudo systemctl stop myapp@\${ACTIVE}
                    '
                    """
                }
            }
        }
    }

    post {
        success {
            echo '🚀 BLUE-GREEN DEPLOYMENT SUCCESSFUL'
        }
        failure {
            echo '❌ DEPLOYMENT FAILED'
        }
    }
}



// pipeline {
//     agent any
//
//     tools {
//         maven 'maven-3'
//         jdk 'java-17'
//     }
//
//     environment {
//         APP_SERVER = '13.53.197.75'
//         SSH_CRED   = 'app-server-ssh'
//         APP_DIR    = '/opt/myapp'
//         TMP_DIR    = '/home/ec2-user/app'
//
//         PORT_A     = '8086'   // Blue
//         PORT_B     = '8087'   // Green
//     }
//
//     stages {
//
//         stage('Checkout Code') {
//             steps {
//                 checkout scm
//             }
//         }
//
//         stage('Build & Test') {
//             steps {
//                 sh 'mvn clean package'
//             }
//         }
//
//         stage('Detect Active Port') {
//             steps {
//                 sshagent(credentials: [SSH_CRED]) {
//                     sh '''
//                     ssh ec2-user@${APP_SERVER} "
//                       if systemctl is-active --quiet myapp@8086; then
//                         echo 8086 > /tmp/active_port
//                       else
//                         echo 8087 > /tmp/active_port
//                       fi
//                     "
//                     '''
//                 }
//             }
//         }
//         stage('Decide Idle Port') {
//             steps {
//                 sh '''
//                 ACTIVE=$(ssh ec2-user@${APP_SERVER} cat /tmp/active_port)
//                 if [ "$ACTIVE" = "8086" ]; then
//                   echo 8087 > /tmp/idle_port
//                 else
//                   echo 8086 > /tmp/idle_port
//                 fi
//                 '''
//             }
//         }
//         stage('Deploy New Version') {
//             steps {
//                 sshagent(credentials: [SSH_CRED]) {
//                     sh '''
//                     IDLE=$(ssh ec2-user@${APP_SERVER} cat /tmp/idle_port)
//                     scp target/*.jar ec2-user@${APP_SERVER}:${APP_DIR}/myapp.jar
//                     ssh ec2-user@${APP_SERVER} "
//                       sudo systemctl restart myapp@${IDLE}
//                     "
//                     '''
//                 }
//             }
//         }
//         stage('Health Check') {
//             steps {
//                 sh '''
//                 IDLE=$(ssh ec2-user@${APP_SERVER} cat /tmp/idle_port)
//                 sleep 15
//                 curl -f http://${APP_SERVER}:${IDLE}/ || exit 1
//                 '''
//             }
//         }
//         stage('Switch Nginx Traffic') {
//             steps {
//                 sshagent(credentials: [SSH_CRED]) {
//                     sh '''
//                     IDLE=$(ssh ec2-user@${APP_SERVER} cat /tmp/idle_port)
//                     ssh ec2-user@${APP_SERVER} "
//                       sudo sed -i 's/[0-9][0-9][0-9][0-9]/'${IDLE}'/g' /etc/nginx/conf.d/myapp.conf
//                       sudo nginx -s reload
//                     "
//                     '''
//                 }
//             }
//         }
//         stage('Stop Old Version') {
//             steps {
//                 sshagent(credentials: [SSH_CRED]) {
//                     sh '''
//                     ACTIVE=$(ssh ec2-user@${APP_SERVER} cat /tmp/active_port)
//                     ssh ec2-user@${APP_SERVER} "
//                       sudo systemctl stop myapp@${ACTIVE}
//                     "
//                     '''
//                 }
//             }
//         }
//     }
//
//     post {
//         success {
//             echo '🚀 CI/CD PIPELINE SUCCESS'
//         }
//         failure {
//             echo '❌ CI/CD PIPELINE FAILED'
//         }
//     }
// }
