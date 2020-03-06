[Dashboard]
(http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login)
hasdfbkasdf
- K8s token - token:      
```
eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJva2UtYWRtaW4tdG9rZW4tZ2Zyc2ciLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoib2tlLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiM2U3NzFhMmMtNGVhOC0xMWVhLThjNTgtMGE1ODBhZWQ1NDk0Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50Omt1YmUtc3lzdGVtOm9rZS1hZG1pbiJ9.4RVqUKMI3XTNxmnCCcmCPEBOVcfDodGnAG3fOrcWedJ6VLs4z0IDYLA83h9Bkc_DoTYvuCxajZiZAFTtXbKRsJ5AHNyVDB9eTbCzhtKao-QoML6k0iO5XSWAU2zmF-3CO1EgVH3fIdN_LMza5WV-BMMaMmuQTbBB0drROQsV-6ORPrkui59AblfUeit5EMl-ij7QRti_6-KunLSzQFICoPmevZKNMg8FB7FKv9FhP8mdfQJfUpHWh_YUGWTYNSU9Ks8QWBJFnA0HeWVhoz9eHskcfMtrKYZeUJ-ElnBx0H5eYQJxhtc97_6Xn6xtnYv315QBN3Q6n2v7i_EsdlXQ6Q
```

### Asset - CI/CD

Pre-requisites: 
- OCI-CLI downloaded and configured for tenancy - https://docs.cloud.oracle.com/en-us/iaas/Content/API/SDKDocs/cliinstall.htm 
-  Download and install kubectl locally -https://kubernetes.io/docs/tasks/tools/install-kubectl/
- Download and install docker locally - https://docs.docker.com/install/linux/docker-ce/centos/
- 


Steps:

1. Create VCN for Jenkins VCN, open port 80
2. Create Jenkins Server compute instance from the marketplace - https://docs.bitnami.com/oci/get-started-oci-partner/
3. get credentials - ssh into the instance and then - run  command cat ./bitnami_credentials
4. Reach Jenkins server from the public IP of the instance. And login using the credentials from above.
5. Create OKE cluster on OCI 
	- quick create ->name, Public, VMstd2.2, 3 replica set, enable add ons -> create
6. Follow the Quickstart options within the console to set up the cluster. Dashboard is optional
7. On the OCI dashboard click on the profile icon at top right and then click your profile. Click on Auth token -> Generate Token -> write the descriptions and then record the token for future use.
8. Have your git repositiry ready along with the docker and k8s manifiest files 
	- Go to your github repo -> settings -> webhook -> add a new webhook
    -	payload url: 
    ``` 
    http://<jenkins server public IP>/jenkins/github-webhook/ 
    ```
    - content type: applciation/json.
	- add webhook
9. Execute the following chain of commands on the Jenkins dashboard.
	- Login to Jenkins -> Manage Jenkins -> Manage Plugins -> Docker Build and Publish, BlueOcean update existing -> restart
	 - Manage Jenkins -> Global Tool Configuration -> Add Maven -> name: maven -> install automatically -> apply/save
    - New Item -> Enter name and select freestyle project -> ok
    -  General -> GitHub Project add the following url:
    ```
    https://github.com/chinmaythakar/jenkinsTest/
    ```
    -	Source Code Management-> Git -> add the following repository url:
    ```
    https://github.com/chinmaythakar/jenkinsTest.git
    ```
    -	Build Triggers -> GitHub hook trigger for GITScm polling
    -	Build -> Add build step -> Invoke top level maven targets -> select maven from drop down for maven version -> goals: clean
    -	Add build step -> Execute shell -> paste this command there
    ```
    /opt/bitnami/apps/jenkins/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven/bin/mvn install:install-file -Dfile=lib/ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=18.3.0.0 -Dpackaging=jar -DgeneratePom=true
    ```
    -	Add build step -> Invoke top level maven targets -> select maven from drop down for maven version -> goals: package
    - Add build step -> Docker Build and Publish
        - Repo name: 
        ```
        iad.ocir.io/orasenatdpltoci01/thakarchinmay/crudapp
        ```
        - Tag: 1.0
        - Docker registry URL
        ```
        https://iad.ocir.io/v2/
        ```
        - Registry Credential -> Add -> Jenkins -> add your oci username 
        ```
        <tenancy-namespace>/oracleidentitycloudservice/<youremail@oracle.com>
        ```
        and auth token created from OCI console in step 7.
        Click Add and then select that from the drop down.
    - Add build step -> Execute shell -> paste this command there
    ```
    kubectl apply -f k8s.yaml
    ```
    -	Save
10. ssh into the jenkins instance and perform the follwoing steps
	- Download Docker
	```
    sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
                  
    sudo yum install -y yum-utils \
  	device-mapper-persistent-data \
  	lvm2
    
  	sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
    
    sudo yum install docker-ce docker-ce-cli containerd.io
    
    sudo systemctl start docker
    
    sudo usermod -aG docker tomcat
    
    sudo chmod 777 /var/run/docker.sock 
    
    sudo setenforce 0
    
    sudo systemctl restart docker
    ```
    - Download Kubectl
    ```
    curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
    
    chmod +x ./kubectl
    
    sudo mv ./kubectl /usr/local/bin/kubectl
    
    kubectl version --client
    ```
    - Download OCI-CLI for tomcat and set up the k8s cluster quickstart from OKE console.
    ```
    sudo su - tomcat
    
    bash -c "$(curl -L https://raw.githubusercontent.com/oracle/oci-cli/master/scripts/install/install.sh)"
    (accept the default prompts for oci cli installation)
    
    oci -version (to check if oci cli has been installed)
    
    oci setup config (follow promts to setup oci-cli with tenancy)
    
    mkdir -p $HOME/.kube
    
	oci ce cluster create-kubeconfig --cluster-id <cluster-ocid> --file $HOME/.kube/config --region <region-id e.g. us-ashburn-1> --token-version 2.0.0 
    
    export KUBECONFIG=$HOME/.kube/config
    
    kubectl create secret docker-registry password123 --docker-server=iad.ocir.io --docker-username='<tenancy-namespace>/<oci-username>' --docker-password='<oci-auth-token>' --docker-email='<email-address>'
    
    exit
    
    exit
    ```
