package com.matilda.git.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
@RequestMapping("entity")
public class PostHelloWorldController {

    ResponseClone responseClone = new ResponseClone();
    ObjectMapper objectMapper = new ObjectMapper();
    RequestClone requestClone = new RequestClone();
    Utilities utilities = new Utilities();
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public String postEcho(@RequestBody String entityVar) throws IOException {
        //TODO: process POST request
        //entity ="Priyesh";
        //return "{\"name\":\"My Name is Priyesh\"}";
        boolean isURLValid;
        boolean isReqJSONValid;
        boolean isLocalPathValid;

        isReqJSONValid = utilities.isJSONValid(entityVar);
        if(!isReqJSONValid) {
            return returnError();
        }

        requestClone = objectMapper.readValue(entityVar,RequestClone.class);

        isURLValid = utilities.isURLValid(requestClone.gitURL);
        if(!isURLValid){
            return returnError();
        }

        isLocalPathValid = utilities.isLocalPathValid(requestClone.localPath);
        if(!isLocalPathValid){
            return returnError();
        }
        ResponseClone respJSON = clone(entityVar);
        String returnSuccess = objectMapper.writeValueAsString(respJSON);
        return returnSuccess;
        }

    public ResponseClone clone(String reqJSONString) throws IOException {
        requestClone = objectMapper.readValue(reqJSONString,RequestClone.class);
        try {
            System.out.println("Cloning "+requestClone.gitURL+" into "+requestClone.gitURL);
            
            if(!StringUtils.isEmpty(requestClone.getUserName()) && !StringUtils.isEmpty(requestClone.getPassword())) {
            	Git.cloneRepository()
                .setURI(requestClone.gitURL)
                .setDirectory(Paths.get(requestClone.localPath).toFile())
                .call();
            }else {
            
	            Git.cloneRepository()
	                    .setURI(requestClone.gitURL)
	                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(requestClone.getUserName(), requestClone.getPassword()))
	                    .setDirectory(Paths.get(requestClone.localPath).toFile())
	                    .call();
            }
            responseClone.responseCode ="200";
            responseClone.responseDesc = "Cloning Completed";
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
            responseClone.responseCode ="2001";
            responseClone.responseDesc = "Error Occurred While Cloning";
            return responseClone;
        }
        return responseClone;
    }

    public String returnError() throws JsonProcessingException {
        String returnError = objectMapper.writeValueAsString(utilities.responseClone);
        return returnError;
    }
}
