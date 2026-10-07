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
                withCredentials([
                    usernamePassword(
                        credentialsId: 'coregs-db',
                        usernameVariable: 'DB_USER',
                        passwordVariable: 'DB_PASSWORD'
                    )
                ]) {
                    sh '''
                        docker run --rm \
                          -v jenkins_home:/jenkins_home \
                          -w /jenkins_home/workspace/CoreGS-Pipeline \
                          -e SPRING_DATASOURCE_URL="jdbc:mysql://docker-mysql:3306/coregs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
                          -e SPRING_DATASOURCE_USERNAME="$DB_USER" \
                          -e SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
                          --network devops-net \
                          coregs-build:java23 \
                          ./mvnw test
                    '''
                }
            }
        }

        stage('SonarQube') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'coregs-db',
                        usernameVariable: 'DB_USER',
                        passwordVariable: 'DB_PASSWORD'
                    ),
                    string(
                        credentialsId: 'sonarqube-token',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            docker run --rm \
                              -v jenkins_home:/var/jenkins_home \
                              -w /var/jenkins_home/workspace/CoreGS-Pipeline \
                              --network devops-net \
                              -e SPRING_DATASOURCE_URL="jdbc:mysql://docker-mysql:3306/coregs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
                              -e SPRING_DATASOURCE_USERNAME="$DB_USER" \
                              -e SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
                              -e SONAR_TOKEN="$SONAR_TOKEN" \
                              coregs-build:java23 \
                              ./mvnw verify sonar:sonar \
                              -Dsonar.host.url=http://sonarqube:9000 \
                              -Dsonar.token="$SONAR_TOKEN"
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'coregs-db',
                        usernameVariable: 'DB_USER',
                        passwordVariable: 'DB_PASSWORD'
                    )
                ]) {
                    sh '''
                        docker build -t coregs:latest .

                        docker rm -f coregs || true

                        docker run -d \
                          --name coregs \
                          --network devops-net \
                          -p 7070:8080 \
                          -e SPRING_DATASOURCE_URL="jdbc:mysql://docker-mysql:3306/coregs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
                          -e SPRING_DATASOURCE_USERNAME="$DB_USER" \
                          -e SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
                          coregs:latest
                    '''
                }
            }
        }
    }
}