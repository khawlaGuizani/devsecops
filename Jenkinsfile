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
                docker build -t devsecops-app:latest .
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
                echo "=== DAST scan avec OWASP ZAP ==="
                
                # Créer un dossier temporaire pour les rapports ZAP
                mkdir -p $WORKSPACE/zap-report
                chmod 777 $WORKSPACE/zap-report

                # Lancer ZAP en mode baseline scan
                docker run --rm \
                    -v $WORKSPACE/zap-report:/zap/wrk/:rw \
                    -p 8090:8090 \
                    ghcr.io/zaproxy/zaproxy:stable \
                    zap-baseline.py -t http://192.168.50.4:8090 -r zap_report.html || true

                # Afficher le rapport
                cat $WORKSPACE/zap-report/zap_report.html
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
                    --out dependency-check-report
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
                    -Dsonar.login=${SONAR_AUTH_TOKEN}
                '''
            }
        }
    }
}
