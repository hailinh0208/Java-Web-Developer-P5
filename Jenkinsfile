pipeline {
    agent {
        label 'local' // Chạy pipeline trên Jenkins agent local
    }

    environment {
        // Đường dẫn thư mục local chứa dự án
        PROJECT_DIR = '/Users/linhcute/your-project-directory' // Thay bằng đường dẫn thực tế của bạn
        PATH = "/usr/local/bin:/usr/bin:$PATH" // Đường dẫn chứa Maven và Java
        JAVA_HOME = "/usr/bin" // Thư mục chứa Java
        MAVEN_HOME = "/usr/local/bin" // Thư mục chứa Maven
    }

    stages {
        stage('Prepare Environment') {
            steps {
                echo "Switching to project directory: ${PROJECT_DIR}"
                dir("${PROJECT_DIR}") {
                    sh 'pwd' // Kiểm tra đường dẫn hiện tại
                }
            }
        }

        stage('Build with Maven') {
            steps {
                dir("${PROJECT_DIR}") {
                    echo "Running Maven build using the correct paths..."
                    sh '${MAVEN_HOME}/mvn clean install'
                }
            }
        }
    }

    post {
        success {
            echo "Maven build completed successfully."
        }
        failure {
            echo "Maven build failed. Please check logs."
        }
    }
}
