pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Código obtenido desde GitHub'
            }
        }

        stage('Build') {
            steps {
                echo 'Aquí ejecutaremos Maven'
            }
        }

        stage('Test') {
            steps {
                echo 'Aquí ejecutaremos las pruebas'
            }
        }
    }
}