package com.openapi.mocker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.openapi.mocker.parser.OpenAPIParserComponent;
import io.swagger.models.Response;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.*;

import static org.springframework.http.HttpMethod.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private OpenAPI openAPI;

    @Autowired
    private RestTemplate restTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    OpenAPIParserComponent openApiParser;

    @Autowired
    Faker faker;

    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE})
    @RequestMapping("/**")
    public ResponseEntity<String> handleDynamicRequest(
            HttpServletRequest request,
            @RequestParam Map<String, String> requestParams,
            @RequestBody(required = false) String requestBody,
            HttpMethod httpMethod) {
        String requestEndpointPath = request.getRequestURI().substring("/api".length());
        PathItem pathItem = openAPI.getPaths().get(requestEndpointPath);
        if (pathItem != null) {
            Operation operation = null;

            if (httpMethod.equals(GET)) {
                operation = pathItem.getGet();
            } else if (httpMethod.equals(POST)) {
                operation = pathItem.getPost();
            } else if (httpMethod.equals(PUT)) {
                operation = pathItem.getPut();
            } else if (httpMethod.equals(PATCH)) {
                operation = pathItem.getPatch();
            } else if (httpMethod.equals(DELETE)) {
                operation = pathItem.getDelete();
            }

            if (operation != null) {
                // Check if requestBody is required
                io.swagger.v3.oas.models.parameters.RequestBody openApiRequestBody = operation.getRequestBody();
                if (openApiRequestBody != null && openApiRequestBody.getRequired()) {
                    if (requestBody == null || requestBody.isEmpty()) {
                        return ResponseEntity.badRequest().body("Request body is required.");
                    }
                    else {
                        Content content = openApiRequestBody.getContent();
                        if (content != null) {
                            MediaType mediaType = content.get("application/json");
                            if (mediaType != null) {
                                Schema schema = mediaType.getSchema();
                                if (schema != null) {
                                    List<String> requiredFields = schema.getRequired();
                                    if (requiredFields != null && !requiredFields.isEmpty()) {
                                        JsonNode jsonNode;
                                        try {
                                            jsonNode = objectMapper.readTree(requestBody);
                                        } catch (JsonProcessingException e) {
                                            return ResponseEntity.badRequest().body("Invalid JSON format in request body.");
                                        }
                                        for (String field : requiredFields) {
                                            if (!jsonNode.has(field)) {
                                                return ResponseEntity.badRequest().body("Required field '" + field + "' is missing.");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                // handle the responses

                String responseCode = "400";
                ApiResponse response = operation.getResponses().get(responseCode);
                if (response != null) {
                    String description = response.getDescription();
                    Map<String, Header> headers = response.getHeaders();
                    Map<String, MediaType> content = response.getContent();
                    Map<String, Link> links = response.getLinks();
                    Map<String, Object> extensions = response.getExtensions();
                    String ref = response.get$ref();

                    if (ref != null) {
                        String schemaName = ref.substring(ref.lastIndexOf('/') + 1);
                        Schema<?> schema = openAPI.getComponents().getSchemas().get(schemaName);
                        if (schema != null) {
                            // Generate fake data based on the referenced schema
                            Object fakeData = generateFakeData(schema);
                        }
                    }
                    if (content!= null) {
                        MediaType mediaType = content.get("application/json");
                        if (mediaType != null) {
                            Schema schema = mediaType.getSchema();
                            if (schema != null) {

                            }
                        }
                    }

                }



                // Check if required parameters exists
                List<Parameter> parameters = operation.getParameters();
                Map<String, String> requestHeaders = getHeadersAsMap(request);
                if (parameters != null) {
                    for (Parameter parameter : parameters) {
                        if(parameter.getRequired()){
                            String paramName = parameter.getName();
                            if(requestHeaders.containsKey(paramName)) {
                                String paramValue = request.getHeader(paramName);
                                if (  paramName != null && !isValidParameterValue(paramName, paramValue, operation)) {
                                    return ResponseEntity.badRequest().body("Invalid parameter value: " + paramName);
                                }
                            }
                        }


                    }
                }
                return ResponseEntity.ok("Response for dynamic endpoint");
            }
        }
        return ResponseEntity.notFound().build();
    }

    private boolean isValidParameterValue(String paramName, String paramValue, Operation operation) {
        Parameter parameter = findParameterByName(paramName, operation);
        if (parameter != null) {
            Schema<?> schema = parameter.getSchema();
            if (schema != null) {
                // Check constraints such as minimum, maximum, pattern, etc.
                // You can access schema properties like schema.getMinimum(), schema.getMaximum(), schema.getPattern(), etc.
                // Implement your validation logic here
                // Return true if the parameter value is valid, otherwise false
            }
        }
        return true; // D
    }

    private Parameter findParameterByName(String paramName, Operation operation) {
            if (operation != null) {
                List<Parameter> parameters = operation.getParameters();
                if (parameters != null) {
                    for (Parameter parameter : parameters) {
                        if (paramName.equals(parameter.getName())) {
                            return parameter;
                        }
                    }
                }
            }
        return null;
    }

    private Map<String, String> getHeadersAsMap(HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headersMap.put(headerName, headerValue);
        }

        return headersMap;
    }

    private Object generateFakeData(Schema<?> schema) {
        if (schema.getType().equals("string")) {
            return faker.lorem().sentence();
        } else if (schema.getType().equals("integer")) {
            return faker.number().randomNumber();
        } else if (schema.getType().equals("boolean")) {
            return faker.bool().bool();
        } else if (schema.getType().equals("object")) {
            Map<String, Object> fakeObject = new HashMap<>();
            if (schema.getProperties() != null) {
                for (Map.Entry<String, Schema> propertyEntry : schema.getProperties().entrySet()) {
                    String propertyName = propertyEntry.getKey();
                    Schema<?> propertySchema = propertyEntry.getValue();
                    fakeObject.put(propertyName, generateFakeData(propertySchema));
                }
            }
            return fakeObject;
        } else if (schema.getType().equals("array")) {
            // Generate fake data for array type schema
            // You can recursively generate fake data for the items in the array schema
            // Return the generated array
        }

        return null; // Default case
    }


}
