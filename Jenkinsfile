pipeline {
    agent any

    tools {
        jdk 'TomCat' // Replace with your configured JDK name
        maven 'Maven3' // Replace with your configured Maven installation name
    }

    environment {
        APP_NAME = "DungeonsName"
        VERSION = "1.0.0"
        BUILD_DIR = "target"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Galya777/DungeonsAndDragons/'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
                archiveArtifacts artifacts: "${BUILD_DIR}/${APP_NAME}-${VERSION}.jar", fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying the application..."
                // Add deployment steps here if needed (e.g., copy JAR to a server or directory)
            }
        }
    }

    post {
        success {
            echo 'Build and deployment succeeded!'
        }
        failure {
            echo 'Build or deployment failed.'
        }
    }
}