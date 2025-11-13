pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://192.168.50.4:9000'
        SONAR_AUTH_TOKEN = credentials('sonarqube')
    }

    stages {
        stage('GIT') {
            steps {
                git branch: 'main',
                    changelog: false,
                    credentialsId: 'jenkins-github',
                    url: 'https://github.com/khawlaGuizani/devsecops.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean install -B -DskipTests'
            }
        }

        stage('Secret Scan - Gitleaks') {
            steps {
                sh '''
                echo "=== Scan de secrets avec Gitleaks ==="
                gitleaks detect --source . --report-format json --report-path gitleaks-report.json || true
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                echo "=== Construction de l'image Docker ==="
                docker build -t devsecops-app:latest . || true
                '''
            }
        }

        stage('Docker Scan - Trivy') {
            steps {
                sh '''
                echo "=== Scan de l'image Docker avec Trivy ==="
                trivy image --format table --output trivy-image-scan.txt devsecops-app:latest || true
                cat trivy-image-scan.txt
                '''
            }
        }

        stage('DAST - OWASP ZAP Scan') {
            steps {
                sh '''
                echo "=== DAST scan avec OWASP ZAP (installé localement) ==="

                # Créer un dossier pour les rapports ZAP
                mkdir -p /var/jenkins_home/zap-report
                chmod 777 /var/jenkins_home/zap-report

                # Lancer ZAP en mode baseline scan
                # Remplacer l'URL par celle de ton application
                zaproxy -cmd -quickurl http://192.168.50.4:8090 \
                        -quickout /var/jenkins_home/zap-report/zap_report.html || true

                # Vérifier que le rapport a été généré
                if [ -f /var/jenkins_home/zap-report/zap_report.html ]; then
                    cat /var/jenkins_home/zap-report/zap_report.html
                else
                    echo "⚠️ Le rapport ZAP n'a pas été généré !"
                fi
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'zap-report/zap_report.html', fingerprint: true
                }
            }
        }

        stage('SCA - Dependency Analysis') {
            steps {
                sh '''
                echo "=== Analyse SCA avec OWASP Dependency-Check ==="
                dependency-check.sh --project devsecops \
                    --scan . \
                    --format HTML \
                    --out dependency-check-report || true
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh '''
                echo "=== Analyse SonarQube ==="
                mvn sonar:sonar \
                    -Dsonar.projectKey=devops_git \
                    -Dsonar.host.url=${SONAR_HOST_URL} \
                    -Dsonar.login=${SONAR_AUTH_TOKEN} || true
                '''
            }
        }
    }
}
