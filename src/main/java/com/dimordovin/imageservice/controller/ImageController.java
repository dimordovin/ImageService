package com.dimordovin.imageservice.controller;

import com.dimordovin.imageservice.dto.ImageDTO;
import com.dimordovin.imageservice.dto.ImagesDTO;
import com.dimordovin.imageservice.service.FileService;
import com.dimordovin.imageservice.service.ImageService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import static com.dimordovin.imageservice.controller.Utils.*;

@Path("/images")
public class ImageController {

    private ImageService imageService = new ImageService();
    private FileService fileService = new FileService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createImage(ImageDTO body) throws URISyntaxException {
        if (body.getName() == null || body.getName().equals("")) {
            return Response.status(400).entity("name is empty").build();
        }

        if (body.getType() == null || ! mimeTypeExtensions.containsKey(body.getType())) {
            return Response.status(400).entity("type must be one of the following: "
                + "{image/jpeg, image/jpg, image/png, image/gif}").build();
        }

        addExtensionToName(body);

        ImageDTO image = imageService.createAndSaveImage(body);

        URI uri = UriBuilder.fromUri(FileService.FILE_SERVICE_URL + "/api/images")
                .queryParam("id", image.getId()).queryParam("filename", image.getName()).build();
        setImageHref(image);
        return Response.ok(uri).entity(image).build();
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateImage(@PathParam("id") String id, ImageDTO body) {
        if (body.getName() != null && body.getName().equals("")) {
            return Response.status(400).entity("name is empty").build();
        }

        if (body.getType() != null && ! mimeTypeExtensions.containsKey(body.getType())) {
            return Response.status(400).entity("type must be one of the following: "
                    + "{image/jpeg, image/jpg, image/png, image/gif}").build();
        }

        ImageDTO currImage = imageService.getImage(id);

        if (currImage == null) {
            return Response.status(404).entity("image not found").build();
        }

        if (body.getType() == null) {
            body.setType(currImage.getType());
        }

        addExtensionToName(body);

        ImageDTO updatedImage = imageService.updateImage(id, body);

        setImageHref(updatedImage);

        return Response.ok().entity(updatedImage).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImage(@PathParam("id") String id) throws SQLException {
        if (id == null) {
            return Response.status(400).entity("id is empty").build();
        }

        ImageDTO image = imageService.getImage(id);

        if (image == null) {
            return Response.status(404).entity("image not found").build();
        }

        setImageHref(image);

        return Response.ok().entity(image).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteImage(@PathParam("id") String id) throws SQLException {
        if (id == null) {
            return Response.status(400).entity("id is empty").build();
        }

        ImageDTO image = imageService.deleteImage(id);
        if (image == null) {
            return Response.status(404).entity("image not found").build();
        }

        if (image.getUrl() != null) {
            String[] segments = image.getUrl().split("[/]");
            fileService.deleteImage(segments[segments.length - 1]);
        }

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImages(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit)
            throws SQLException {

        if (offset != null && offset < 0 || limit != null && limit < 0) {
            return Response.status(400).entity("offset or limit is negative").build();
        }

        ImagesDTO images = imageService.getImages(offset, limit);
        images.setNextHref(getNextHref(IMAGES_URL, offset, limit));
        images.setPrevHref(getPrevHref(IMAGES_URL, offset, limit));
        setAllImagesHrefs(images, offset, limit);

        return Response.ok().entity(images).build();
    }


}
