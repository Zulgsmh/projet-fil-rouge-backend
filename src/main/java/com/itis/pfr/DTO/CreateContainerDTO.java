package com.itis.pfr.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateContainerDTO{

    private String image;
    private String version;
    private String cmd;
    private String name;
    private List<String> envVariables;
    private String hostname;
    private String internalPort;
    private String externalPort;
    private String internalData;
    private String externalData ;

}
