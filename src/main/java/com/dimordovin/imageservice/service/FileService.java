package com.dimordovin.imageservice.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

public class FileService {

    public static final String FILE_SERVICE_URL = System.getenv("FILE_SERVICE_URL");

    private Client client;

    public FileService() {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        client = Client.create(cc);
    }

    public void deleteImage(String s3Filename) {
        WebResource deleteResource =
                client.resource( FILE_SERVICE_URL + "/api/images/" + s3Filename);

        ClientResponse response = deleteResource.delete(ClientResponse.class);

        if (response.getStatus() != 204) {
            throw new RuntimeException("file delete failed");
        }
    }
}
