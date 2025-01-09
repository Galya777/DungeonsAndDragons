pipeline {
    agent any

    tools {
        jdk 'TomCat' // Replace with your configured JDK name
        maven 'Maven3' // Replace with your configured Maven installation name
    }

    environment {
        APP_NAME = "dungeons-and-dragons"
        VERSION = "1.0.0"
        DOCKER_CREDENTIALS_ID ='docker-hub-credentialsS'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Galya777/DungeonsAndDragons.git'
            }
        }

        stage('Build and Package') {
            steps {
                sh 'mvn clean package'
                archiveArtifacts artifacts: "target/*.jar", fingerprint: true
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Dockerize') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${APP_NAME}:${VERSION} ."
            }
        }

         stage('Push to Registry') {
            steps {
                echo 'Pushing Docker image...'
                withCredentials([usernamePassword(credentialsId:'docker-hub-credentialsS', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    script {
                        echo "Logging in to Docker..."
                        sh "docker login --username $DOCKER_USERNAME --password $DOCKER_PASSWORD"
                        echo "Tagging Docker image..."
                        sh "docker tag ${APP_NAME}:${VERSION} galya777/${APP_NAME}:${VERSION}"
                        echo "Pushing Docker image..."
                        sh "docker push galya777/${APP_NAME}:${VERSION}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo 'Deploying to Kubernetes...'
                withKubeConfig([credentialsId: 'kubeconfig-credentials']) {
                     sh 'mkdir -p /var/lib/jenkins/.minikube'
                sh 'minikube start --alsologtostderr -v=0 --profile jenkins'
                sh 'minikube kubectl config use-context jenkins'
                    sh 'kubectl apply -f deployment.yaml'
                    sh 'kubectl apply -f service.yaml'
                    sh 'kubectl get services'
                }
            }
        }



    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }

}
