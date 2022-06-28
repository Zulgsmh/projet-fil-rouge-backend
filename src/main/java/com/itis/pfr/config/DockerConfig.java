package com.itis.pfr.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DockerConfig {

    @Value("${application.test.instance.ip}")
    private String LOCAL_INSTANCE_IP;

    @Value("${application.test.instance.port}")
    private String LOCAL_INSTANCE_PORT;

    public String getLOCAL_INSTANCE_IP() {return LOCAL_INSTANCE_IP;}

    public String getLOCAL_INSTANCE_PORT() { return  LOCAL_INSTANCE_PORT;}

    public DockerClientConfig dockerClientConfig() {
       return DefaultDockerClientConfig.createDefaultConfigBuilder()
               .withDockerHost("tcp://"+getLOCAL_INSTANCE_IP()+":"+ getLOCAL_INSTANCE_PORT())
               .build();
    }

    public DockerClient getDockerClient(){
        return DockerClientBuilder.getInstance(dockerClientConfig()).build();
    }

}
