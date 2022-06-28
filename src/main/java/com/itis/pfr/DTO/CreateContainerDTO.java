package com.itis.pfr.DTO;

import java.util.List;

public record CreateContainerDTO(String image,
                                 String version,
                                 String cmd,
                                 String name,
                                 List<String> envVariables,
                                 String hostname ,
                                 String internalPort,
                                 String externalPort,
                                 String internalData,
                                 String externalData


                                 ) { }
