package com.dimordovin.imageservice.controller.exceptionmappers;

import org.codehaus.jackson.map.JsonMappingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.EOFException;

@Provider
public class EOFExceptionMapper implements ExceptionMapper<EOFException> {
    @Override
    public Response toResponse(EOFException ex) {
        return Response.status(400).entity("broken JSON").type("text/plain").build();
    }
}
