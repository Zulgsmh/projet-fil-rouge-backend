package com.itis.pfr.services;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PortBinding;
import com.itis.pfr.DTO.CreateContainerDTO;
import com.itis.pfr.config.DockerConfig;
import com.itis.pfr.exceptions.NotFoundException;
import com.itis.pfr.models.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DockerService {

    @Autowired
    DockerConfig dockerConfig;

    @Autowired
    UserService userService;

    public void pingDockerDaemon() {
        dockerConfig.getDockerClient().pingCmd().exec();
    }

    public void customQuery(String query){
        //TODO: Run query
    }

    public List<Container> getAllContainersOnOurDaemon() {
        log.info("fetching containers");
        return dockerConfig.getDockerClient().listContainersCmd().withShowAll(true).exec();
    }

    public String deleteContainer(String id) {
        dockerConfig.getDockerClient().removeContainerCmd(id).exec();
        return "Container with id " + id + "has been removed.";
    }

    public List<Image> getAllImagesAvailable() {
        return dockerConfig.getDockerClient().listImagesCmd().exec();
    }

    public void startContainer(String containerId){
        dockerConfig.getDockerClient().startContainerCmd(containerId).exec();
        log.info("Container : {} has been started.", containerId);
    }

    public void stopContainer(String containerId){
        dockerConfig.getDockerClient().stopContainerCmd(containerId).exec();
        log.info("Container : {} has been stopped.", containerId);
    }

    public void killContainer(String containerId){
        dockerConfig.getDockerClient().killContainerCmd(containerId).exec();
        log.info("Container : {} has been killed.", containerId);
    }

    public Optional<Container> getContainerById(String id) {
        return dockerConfig.getDockerClient()
                .listContainersCmd()
                .withIdFilter(List.of(id))
                .exec()
                .stream()
                .findFirst();
    }

    public List<Container> getUserContainers(String userId) {
        Users user = userService.getUserById(userId).orElseThrow(() -> new NotFoundException("User does not exists"));
        List<Container> containerList = new ArrayList<>();
        user.getContainer().forEach((container) -> {
            Optional<Container> searchContainer = getContainerById(container.getId());
            searchContainer.ifPresent(containerList::add);
        });
        return containerList;
    }

    public String createContainer (CreateContainerDTO createContainerDTO) {
        try {
            return dockerConfig.getDockerClient().createContainerCmd(createContainerDTO.getImage()+ ":" + createContainerDTO.getVersion())
                    .withCmd(createContainerDTO.getCmd())
                    .withName(createContainerDTO.getName())
                    .withHostName(createContainerDTO.getHostname())
                    .withEnv(createContainerDTO.getEnvVariables())
                    .withPortBindings(PortBinding.parse(createContainerDTO.getExternalPort() + ":"+ createContainerDTO.getInternalPort()))
                    //.withBinds(Bind.parse(createContainerDTO.internalData() + ":" + createContainerDTO.externalData()))
                    .exec()
                    .getId();

        } catch( Exception e) {
            throw new RuntimeException(e);
        }
    }


}
