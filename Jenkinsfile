pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh '''
                    docker run --rm \
                      -v jenkins_home:/jenkins_home \
                      -w /jenkins_home/workspace/CoreGS-Pipeline \
                      coregs-build:java23 \
                      ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    docker run --rm \
                      -v jenkins_home:/jenkins_home \
                      -w /jenkins_home/workspace/CoreGS-Pipeline \
                      coregs-build:java23 \
                      ./mvnw test
                '''
            }
        }
    }
}