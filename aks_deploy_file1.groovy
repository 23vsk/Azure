pipeline {
    agent any  // You can specify the agent as per your requirements

    environment {
        AZURE_CREDENTIALS_ID = 'your-azure-service-principal-credentials-id'
        NEXUS_CREDENTIALS_ID = 'your-nexus-credentials-id'
        KUBECONFIG = '/home/jenkins/.kube/config'  // Path to kubeconfig file
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm  // Checkout the source code from GitHub
            }
        }

        stage('Build') {
            steps {
                script {
                    // Replace this with your build commands, for example:
                    sh 'mvn clean package'
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                script {
                    // Replace these with your Nexus repository information and credentials
                    def nexusUrl = 'http://your-nexus-repository-url'
                    def nexusRepo = 'your-nexus-repository'
                    def nexusCreds = credentials(NEXUS_CREDENTIALS_ID)

                    // Example Maven command to push artifact to Nexus
                    sh "mvn deploy:deploy-file -Durl=${nexusUrl}/repository/${nexusRepo} -DrepositoryId=nexus -Dfile=target/your-service.jar -DgroupId=com.example -DartifactId=your-service -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true -DpomFile=pom.xml -DgeneratePom.description=\"Your service description\" -DgeneratePom.name=\"Your service name\" -DgeneratePom.url=\"Your service URL\" -DgeneratePom.inceptionYear=\"2024\" -DgeneratePom.license.name=\"Apache License, Version 2.0\" -DgeneratePom.license.url=\"http://www.apache.org/licenses/LICENSE-2.0\" -DgeneratePom.organization.name=\"Your organization\" -DgeneratePom.organization.url=\"Your organization URL\" -DgeneratePom.scm.url=\"Your SCM URL\" -DgeneratePom.scm.connection=\"scm:git:Your Git URL\" -DgeneratePom.scm.developerConnection=\"scm:git:Your Git Developer URL\" -DgeneratePom.scm.tag=\"HEAD\" -DgeneratePom.ciManagement.url=\"Your CI management URL\" -DgeneratePom.issueManagement.url=\"Your issue management URL\" -DgeneratePom.distributionManagement.snapshotRepository.url=\"Your distribution management snapshot repository URL\" -DgeneratePom.distributionManagement.snapshotRepository.id=\"Your distribution management snapshot repository ID\" -DgeneratePom.distributionManagement.snapshotRepository.name=\"Your distribution management snapshot repository name\" -DgeneratePom.distributionManagement.snapshotRepository.uniqueVersion=\"true\" -DgeneratePom.distributionManagement.snapshotRepository.layout=\"default\" -DgeneratePom.distributionManagement.repository.url=\"Your distribution management repository URL\" -DgeneratePom.distributionManagement.repository.id=\"Your distribution management repository ID\" -DgeneratePom.distributionManagement.repository.name=\"Your distribution management repository name\" -DgeneratePom.distributionManagement.repository.uniqueVersion=\"true\" -DgeneratePom.distributionManagement.repository.layout=\"default\" -DgeneratePom.distributionManagement.site.url=\"Your distribution management site URL\" -DgeneratePom.distributionManagement.site.id=\"Your distribution management site ID\" -DgeneratePom.distributionManagement.site.name=\"Your distribution management site name\" -DgeneratePom.distributionManagement.site.uniqueVersion=\"true\" -DgeneratePom.distributionManagement.site.layout=\"default\" -DgeneratePom.inceptionYear=\"2024\" -DgeneratePom.issueManagement.system=\"Your issue management system\" -DgeneratePom.issueManagement.url=\"Your issue management system URL\" -DgeneratePom.ciManagement.system=\"Your CI management system\" -DgeneratePom.ciManagement.url=\"Your CI management system URL\" -DgeneratePom.licenses.license.name=\"Apache License, Version 2.0\" -DgeneratePom.licenses.license.url=\"http://www.apache.org/licenses/LICENSE-2.0\" -DgeneratePom.licenses.license.distribution=\"repo\" -DgeneratePom.scm.connection=\"scm:git:Your Git URL\" -DgeneratePom.scm.developerConnection=\"scm:git:Your Git Developer URL\" -DgeneratePom.scm.url=\"Your SCM URL\" -DgeneratePom.scm.tag=\"HEAD\" -DgeneratePom.scm.connection=\"scm:git:Your Git URL\" -DgeneratePom.scm.developerConnection=\"scm:git:Your Git Developer URL\" -DgeneratePom.scm.tag=\"HEAD\""
                }
            }
        }

        stage('Pull from Nexus') {
            steps {
                script {
                    // Replace these with your Nexus repository information and credentials
                    def nexusUrl = 'http://your-nexus-repository-url'
                    def nexusRepo = 'your-nexus-repository'
                    def nexusCreds = credentials(NEXUS_CREDENTIALS_ID)

                    // Example Maven command to pull artifact from Nexus
                    sh "mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get -DrepoUrl=${nexusUrl}/repository/${nexusRepo} -Dartifact=com.example:your-service:1.0.0"
                }
            }
        }

        stage('Deploy to AKS') {
            steps {
                script {
                    // Log in to Azure CLI
                    def azureCreds = credentials(AZURE_CREDENTIALS_ID)
                    withCredentials([azureServicePrincipal(credentialsId: azureCreds.id, tenantId: azureCreds.tenantId, clientId: azureCreds.servicePrincipalId, clientSecret: azureCreds.servicePrincipalKey)]) {
                        sh "az login --service-principal -u ${azureCreds.servicePrincipalId} -p ${azureCreds.servicePrincipalKey} --tenant ${azureCreds.tenantId}"
                    }

                    // Set up kubectl with AKS cluster
                    sh "az aks get-credentials --resource-group your-aks-resource-group --name your-aks-cluster-name"

                    // Apply Kubernetes manifests
                    sh "kubectl apply -f path/to/your/kubernetes/manifests"
                }
            }
        }
    }
}
