## Containerize Spring-Boot app
1. Kubernetes
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES) - can be used with initializing bean
to detect the cloud platform on which application wants to run
<code>
@Bean
InitializingBean initializingBean(){
              return () -> System.out.println("Hello k8s ");
          }</code>

Also in application.properties
<code>spring.main.cloud-platform=kubernetes</code>
2. Create a docker image

<code>
@Bean
   
    InitializingBean initializingBean(){
        return () -> System.out.println("Hello containers from " + BootifulK8sApplication.class.getName());
    }
</code>

Use maven command to submit the jar to packeto buildpack to create docker image
<code> mvn clean spring-boot:build-image</code>
<code>docker images</code>
<code> docker run <<image id>></code>





