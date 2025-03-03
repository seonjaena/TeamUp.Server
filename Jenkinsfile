pipeline {
    agent any

    tools {
        gradle 'gradle-8.6'
        jdk 'jdk-21'
    }

    stages {

        stage('Build Docker') {
            steps {
                script {
                    if(env.BRANCH_NAME == "master") {
                        profile = "prod"
                    }else {
                        profile = "prod"
                    }
                    sh "./build-docker.sh ${profile}"
                }
            }
        }

        stage('Upload Docker') {

            steps {

                ///////////////////////////////////////////////
                // AWS 개발 ECR에 업로드
                ///////////////////////////////////////////////
                script {
                    def projectVersion = sh(script: "./gradlew properties | grep -Po '(?<=version: ).*'", returnStdout: true)
                    projectVersion = projectVersion.trim()

                    VER="${projectVersion}-${env.BRANCH_NAME}"

                    //이미 생성된 이미지 찾기 위함
                    builtTag = "teamup.server:${VER}"

                    ecrRepositoryDev = "339713136341.dkr.ecr.ap-northeast-2.amazonaws.com"
                    ecrRepositoryProd = "339713136341.dkr.ecr.ap-northeast-2.amazonaws.com"
                    ecrRepository = (env.BRANCH_NAME == "master") ? ecrRepositoryProd : ecrRepositoryDev

                    // 로그인
                    sh "aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${ecrRepository}"

                    //태깅
                    sh "docker tag ${builtTag} ${ecrRepository}/${builtTag}"

                    //upload
                    sh "docker push ${ecrRepository}/${builtTag}"
                }
            }
        }

    }
}
