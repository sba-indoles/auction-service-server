pipeline {
    agent any

    environment {
        BRANCH = 'dev'
        AWS_REGION = 'ap-northeast-2' // ECR 리전 설정
        ECR_REPO_NAME = "${BRANCH}-auction" // ECR 리포지토리 이름 설정
        IMAGE_TAG = "${env.BUILD_NUMBER}" // 이미지 태그 (빌드 번호)
        AWS_ACCOUNT_ID = '703671902880' // AWS 계정 ID
        ECR_REPO_URL = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}"
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/sba-fourMan/auction-service-server', branch: 'dev'
            }
        }

        stage('Modify application.yml') {
            steps {
                script {
                    writeFile file: 'src/main/resources/application.yml', text: '''\

server:
  port: ${PORT}

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    open-in-view: off
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true

  data:
    redis:
      host: ${REDIS_HOST}
      port: 6379

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
  header: Authorization
  prefix: Bearer
  type: JWT
'''
                }
            }
        }

        stage('Build JAR File') {
            steps {
                sh './gradlew build -x test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${ECR_REPO_URL}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS Jenkins Access Key']]) {
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO_URL}"
                }
            }
        }

        stage('Push Docker Image to ECR') {
            steps {
                script {
                    sh "docker push ${ECR_REPO_URL}:${IMAGE_TAG}"
                }
            }
        }

        stage('Remove Docker Image and .jar File') {
            steps {
                script {
                    sh "docker rmi ${ECR_REPO_URL}:${IMAGE_TAG} || true"
                    sh "rm -rf build/libs/*.jar"
                }
            }
        }
    }
}