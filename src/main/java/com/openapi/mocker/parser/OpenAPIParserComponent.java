package com.openapi.mocker.parser;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAPIParserComponent  {

    @Autowired
    private ParseOptions parseOptions ;
    @Bean
    public OpenAPI  readAPI() {
        final OpenAPI openAPI = new OpenAPIV3Parser().read("PartyEnrollment.yaml", null, parseOptions);
        openAPI.getOpenapi();
        return openAPI;

    }


    private void printData(OpenAPI openAPI) {
        System.out.println(openAPI.getSpecVersion());

        Info info = openAPI.getInfo();
        System.out.println(info.getTitle());
        System.out.println(info.getVersion());

        List<Server> servers = openAPI.getServers();
        for (Server server : servers) {
            System.out.println(server.getUrl());
            System.out.println(server.getDescription());
        }

        Paths paths = openAPI.getPaths();
        paths.entrySet()
                .forEach(pathEntry -> {
                    System.out.println(pathEntry.getKey());

                    PathItem path = pathEntry.getValue();
                    printOperationDetails(path.getGet(), "GET");
                    printOperationDetails(path.getPost(), "POST");
                    printOperationDetails(path.getPut(), "PUT");
                    printOperationDetails(path.getDelete(), "DELETE");
                    printOperationDetails(path.getPatch(), "PATCH");
                });
    }

    private void printOperationDetails(Operation operation, String method) {
        if (operation != null) {
            System.out.println(method + " operation details:");
            System.out.println("Summary: " + operation.getSummary());
            System.out.println("Description: " + operation.getDescription());
            System.out.println("Operation ID: " + operation.getOperationId());

            ApiResponses responses = operation.getResponses();
            responses.entrySet()
                    .forEach(responseEntry -> {
                        System.out.println("Response Key: " + responseEntry.getKey());

                        ApiResponse response = responseEntry.getValue();
                        System.out.println("Response Description: " + response.getDescription());
                    });
        } else {
            System.out.println(method + " operation not defined for this path.");
        }
    }







}
