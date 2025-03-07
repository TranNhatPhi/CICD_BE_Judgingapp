pipeline {
    agent any

    stages {
        stage('ssh to server with user ubuntu') {
            steps {
                sshagent(['dd']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -l ubuntu 51.79.161.34 "cd /home/ubuntu && ./deploy_be_judgingapp.sh"
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline deploy frontend identity service execution finished.'
        }
        success {
            echo 'Code checkout completed successfully!'
        }
        failure {
            echo 'Code checkout failed!'
        }
    }
}