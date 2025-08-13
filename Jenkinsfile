pipeline {
    agent any

    environment {
        APP_NAME = 'reporting'
        DOCKER_IMAGE = "emiliofloresdiaz/reporting:latest"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/EmilioF-KS/cfo-backend---Git.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'emiliofloresdiaz', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE% .'
                bat 'docker push %DOCKER_IMAGE%'
            }
        }

        stage('Docker Run') {
            steps {
                bat 'docker run -d --name %APP_NAME% -p 8080:8080 %DOCKER_IMAGE%'
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Build or deployment failed.'
        }
    }
}
