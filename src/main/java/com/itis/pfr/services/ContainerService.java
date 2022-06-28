package com.itis.pfr.services;

import com.github.dockerjava.core.DockerClientConfig;
import com.itis.pfr.DTO.CreateContainerDTO;
import com.itis.pfr.DTO.GetUserContainerDTO;
import com.itis.pfr.exceptions.NotFoundException;
import com.itis.pfr.models.Container;
import com.itis.pfr.models.Users;
import com.itis.pfr.models.records.ContainerRequestBody;
import com.itis.pfr.repositories.ContainerRepository;
import com.itis.pfr.repositories.UsersRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final UsersRepository usersRepository;
    private final DockerService dockerService;

    public ContainerService(ContainerRepository containerRepository, UsersRepository usersRepository, DockerService dockerService) {
        this.containerRepository = containerRepository;
        this.usersRepository = usersRepository;
        this.dockerService = dockerService;
    }

    @Transactional
    public String createContainer(ContainerRequestBody containerRequestBody) {
        Users user = usersRepository.findById(containerRequestBody.userId())
                .orElseThrow(() -> new IllegalStateException(String.format("User %s does not exists.", containerRequestBody.userId())));



        containerRequestBody.servicesInstalled().forEach(service -> {
            //TODO: For volumes use user name and user id to make unique path

            CreateContainerDTO createContainerDTO =  checkServiceAndGetCreateContainerDTO(service);

/*
                    "/Users/"+ user.getId() + "/" + service + "/volume/data/db",
                    "/data/"+ service +"/testvolume"

             */

            String newContainerId = dockerService.createContainer(createContainerDTO);

            assert createContainerDTO != null;
            Container container = new Container(
                    service.replace("/", "_").concat("_from_front"),
                    containerRequestBody.servicesInstalled(),
                    "dns",
                    newContainerId,
                    "privateKey",
                    "0.0.0.0",
                    Integer.parseInt(createContainerDTO.getExternalPort()),
                    user,
                    LocalDateTime.now()
            );
            containerRepository.save(container);
        });

        return "All containers saved";
    }

    //TODO: NETWORK BETWEEN POSTGRES PGADMIN4 / MONGO MONGO-EXPRESS
    private CreateContainerDTO checkServiceAndGetCreateContainerDTO (String service) {
        CreateContainerDTO createContainerDTO = new CreateContainerDTO();
        createContainerDTO.setName(service.replace("/", "_").concat("_from_front"));
        createContainerDTO.setImage(service);
        createContainerDTO.setCmd("--bind_ip_all");
        createContainerDTO.setHostname("my_easy_container");
        createContainerDTO.setExternalPort(getNextPortAvailable());
        createContainerDTO.setVersion("latest");
        switch(service) {
            case "openzipkin/zipkin":
                createContainerDTO.setEnvVariables(new ArrayList<>());
                createContainerDTO.setInternalPort("9411");
                return createContainerDTO;
            case "postgres":
                break;
            case "dpage/pgadmin4":
                break;
            case "mongo":
                break;
            case "mongo-express":
                break;
            default:
                break;
        }

        return null;
    }

    private String getNextPortAvailable() {
        List<Container> containers =  containerRepository.findAll();
        if(containers.size() == 0) return "4000";
        else if (containers.size() == 1) return "4001";
        Optional<Container> lastContainer =  containers.stream().max(Comparator.comparing(Container::getPortOccuped));
        return lastContainer.map(container -> Integer.toString(container.getPortOccuped() + 1)).orElse("4000");
    }

    public List<GetUserContainerDTO> getAllContainersOfUser(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        List<Container> userContainers = containerRepository.getContainersByUsers(user);

        List<com.github.dockerjava.api.model.Container> dockerContainers = dockerService.getAllContainersOnOurDaemon();

        List<GetUserContainerDTO> result = new ArrayList<>();

        dockerContainers.stream().forEach(container -> {
            userContainers.stream().forEach(container1 -> {
                if (container1.getDockerContainerId().equals(container.getId()))
                    result.add(new GetUserContainerDTO(container1, container));
            });
        });

        return result;
    }

    public Container getContainerById(String id){
        return containerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Container %s not found.", id)));
    }

    public String startContainer(String containerId, String userId, String dockerContainerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new NotFoundException(String.format("Container %s not found.", containerId)));
        if(container.getUsers().getId().equals(userId) && container.getDockerContainerId().equals(dockerContainerId)){
            dockerService.startContainer(dockerContainerId);
            return "Container " + containerId + " started.";
        }
        throw new IllegalStateException("This user is not the possessor of the container");
    }

    public String stopContainer(String containerId, String userId, String dockerContainerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new NotFoundException(String.format("Container %s not found.", containerId)));
        if(container.getUsers().getId().equals(userId) && container.getDockerContainerId().equals(dockerContainerId)){
            dockerService.stopContainer(dockerContainerId);
            return "Container " + containerId + " stopped.";
        }
        throw new IllegalStateException("This user is not the possessor of the container");
    }

    @Transactional
    public Container deleteContainer(String userId, String containerId, String dockerContainerId) {
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new NotFoundException(String.format("Container %s not found.", containerId)));
        if(container.getUsers().getId().equals(userId)){
            dockerService.deleteContainer(dockerContainerId);
            containerRepository.deleteById(containerId);
            return container;
        }
        throw new IllegalStateException("This user is not the possessor of the container");
    }

}
