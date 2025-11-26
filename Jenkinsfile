pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://192.168.50.4:9000'
        SONAR_AUTH_TOKEN = credentials('sonarqube')
    }

    stages {
        stage('Checkout SCM') {
            steps {
                git branch: 'main',
                    changelog: false,
                    credentialsId: 'jenkins-github',
                    url: 'https://github.com/khawlaGuizani/devsecops.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean install -B -DskipTests || true'
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

        stage('Secret Scan - Gitleaks') {
            steps {
                sh '''
echo "=== Scan de secrets avec Gitleaks ==="
gitleaks detect --source . --report-format json --report-path gitleaks-report.json || true

if [ -f gitleaks-report.json ]; then
    cat gitleaks-report.json
else
    echo "⚠️ Le rapport Gitleaks n'a pas été généré !"
fi
'''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json', fingerprint: true
                }
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
trivy image \
  --timeout 10m0s \
  --scanners vuln \
  --format table \
  --output trivy-image-scan.txt \
  devsecops-app:latest || true

if [ -f trivy-image-scan.txt ]; then
    cat trivy-image-scan.txt
else
    echo "⚠️ Le rapport Trivy n'a pas été généré !"
fi
'''
            }
            post {
                always {
                    script {
                        if (fileExists('trivy-image-scan.txt')) {
                            archiveArtifacts artifacts: 'trivy-image-scan.txt', fingerprint: true
                        } else {
                            echo '⚠️ Aucun rapport Trivy à archiver (Trivy a probablement échoué ou expiré)'
                        }
                    }
                }
            }
        }


        stage('DAST - OWASP ZAP Scan') {
            steps {
                sh '''
echo "=== DAST scan avec OWASP ZAP (installé localement) ==="

mkdir -p "$WORKSPACE/zap-report"

# Change ZAP proxy port to avoid Jenkins 8080 conflict
zaproxy -cmd -port 8095 \
    -quickurl http://192.168.50.4:8090 \
    -quickout "$WORKSPACE/zap-report/zap_report.html" || true

REPORT="$WORKSPACE/zap-report/zap_report.html"
if [ -f "$REPORT" ]; then
    echo "✅ Rapport ZAP généré : $REPORT"
    if command -v w3m >/dev/null 2>&1; then
        w3m -dump "$REPORT"
    elif command -v lynx >/dev/null 2>&1; then
        lynx -dump "$REPORT"
    else
        echo "⚠️ Installer w3m ou lynx pour afficher le rapport dans la console"
    fi
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

REPORT="$WORKSPACE/dependency-check-report/dependency-check-report.html"
if [ -f "$REPORT" ]; then
    if command -v w3m >/dev/null 2>&1; then
        w3m -dump "$REPORT"
    elif command -v lynx >/dev/null 2>&1; then
        lynx -dump "$REPORT"
    else
        echo "⚠️ Installer w3m ou lynx pour afficher le rapport Dependency-Check"
    fi
else
    echo "⚠️ Le rapport Dependency-Check n'a pas été généré !"
fi
'''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'dependency-check-report/**', fingerprint: true
                }
            }
        }


    }
}
