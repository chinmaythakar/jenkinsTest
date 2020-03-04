pipeline {
    agent any
    tools {
        maven 'maven'
    }
    environment {
        registry = "iad.ocir.io/orasenatdpltoci01/thakarchinmay/crudapp"
    }
    stages {
        stage('Clean'){
            steps {
                sh 'mvn clean'
            }
        }
        stage('Install') {
            steps {
                sh 'mvn install:install-file -Dfile=lib/ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=18.3.0.0 -Dpackaging=jar -DgeneratePom=true'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Publish') {
            environment {
                registryCredential = '4a6efec0-dd7b-4bb5-b5f8-62e1c7b52b26'
            }
            steps{
                script {
                    def appimage = docker.build registry + ":$BUILD_NUMBER"
                    docker.withRegistry( '', registryCredential ) {
                        appimage.push('latest')
                    }
                }
            }
        }
        stage('Deploy') {
         steps {
                sh 'kubectl apply -f k8s.yaml'
            }
        }
    }
}