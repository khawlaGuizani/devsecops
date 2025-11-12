pipeline {
    agent any

    stages {
        stage('GIT') {
            steps {
                git branch: 'main',
                    changelog: false,
                    credentialsId: 'jenkins-github',
                    url: 'https://github.com/khawlaGuizani/devsecops.git'
            }
        }

        stage('Maven build') {
            steps {
                sh 'mvn clean install -B -DskipTests'
            }
        }

        

        stage('SONAR') {
            environment {
                SONAR_HOST_URL = 'http://192.168.50.4:9000'
                SONAR_AUTH_TOKEN = credentials('sonarqube')
            }
            steps {
                sh '''
                  mvn sonar:sonar \
                    -Dsonar.projectKey=devops_git \
                    -Dsonar.host.url=${SONAR_HOST_URL} \
                    -Dsonar.login=${SONAR_AUTH_TOKEN}
                '''
            }
        }
    }
}
