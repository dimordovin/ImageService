package com.dimordovin.imageservice.controller;

import com.dimordovin.imageservice.dto.CollectionDTO;
import com.dimordovin.imageservice.dto.CollectionsDTO;
import com.dimordovin.imageservice.dto.ImagesDTO;
import com.dimordovin.imageservice.service.CollectionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import static com.dimordovin.imageservice.controller.Utils.*;

@Path("/collections")
public class CollectionController {

    private CollectionService collectionService = new CollectionService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCollection(CollectionDTO body) throws URISyntaxException {
        if (body.getName() == null || body.getName().equals("")) {
            return Response.status(400).entity("name is empty").build();
        }

        CollectionDTO collection = collectionService.createAndSaveCollection(body);
        setCollectionHref(collection);
        return Response.created(new URI(IMAGE_SERVICE_URL + "/api/collections/" + collection.getId()))
                .entity(collection).build();
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCollection(@PathParam("id") String id, CollectionDTO body) {
        if (id == null || id.isEmpty()) {
            return Response.status(400).entity("id is empty").build();
        }

        if (body.getName() != null && body.getName().equals("")) {
            return Response.status(400).entity("name is empty").build();
        }

        CollectionDTO collection = collectionService.updateCollection(id, body);

        if (collection == null) {
            return Response.status(404).entity("collection not found").build();
        }

        setCollectionHref(collection);
        return Response.ok().entity(collection).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCollection(@PathParam("id") String id) throws SQLException {
        if (id == null || id.isEmpty()) {
            return Response.status(400).entity("id is empty").build();
        }

        CollectionDTO collection = collectionService.getCollection(id);

        if (collection == null) {
            return Response.status(404).entity("collection not found").build();
        }

        setCollectionHref(collection);

        return Response.ok().entity(collection).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCollection(@PathParam("id") String id) throws SQLException {
        if (id == null || id.isEmpty()) {
            return Response.status(400).entity("id is empty").build();
        }

        CollectionDTO collection = collectionService.deleteCollection(id);
        if (collection == null) {
            return Response.status(404).entity("collection not found").build();
        }

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCollections(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit)
            throws SQLException {

        if (offset != null && offset < 0 || limit != null && limit < 0) {
            return Response.status(400).entity("offset or limit is negative").build();
        }

        CollectionsDTO collections = collectionService.getCollections(offset, limit);
        collections.setNextHref(getNextHref(COLLECTIONS_URL, offset, limit));
        collections.setPrevHref(getPrevHref(COLLECTIONS_URL, offset, limit));

        setAllCollectionsHrefs(collections, offset, limit);

        return Response.ok().entity(collections).build();
    }

    @PUT
    @Path("/{collectionId}/images/{imageId}")
    public Response addImage(@PathParam("collectionId") String collectionId,
                             @PathParam("imageId") String imageId) {
        if (collectionId == null || imageId == null || collectionId.isEmpty() || imageId.isEmpty()) {
            return Response.status(400).entity("collectionId or imageId is empty").build();
        }

        if (! collectionService.addImage(collectionId, imageId)) {
            return Response.status(404).entity("collection or image not found").build();
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{collectionId}/images/{imageId}")
    public Response deleteImage(@PathParam("collectionId") String collectionId,
                             @PathParam("imageId") String imageId) {
        if (collectionId == null || imageId == null || collectionId.isEmpty() || imageId.isEmpty()) {
            return Response.status(400).entity("collectionId or imageId is empty").build();
        }

        if (! collectionService.deleteImage(collectionId, imageId)) {
            return Response.status(404).entity("collection or image not found").build();
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/{collectionId}/images")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImages(@PathParam("collectionId") String collectionId,
                              @QueryParam("offset") Integer offset,
                              @QueryParam("limit") Integer limit) throws SQLException {

        if (collectionId == null || collectionId.isEmpty()) {
            return Response.status(400).entity("id is empty").build();
        }

        if (collectionService.getCollection(collectionId) == null) {
            return Response.status(404).entity("collection not found").build();
        }

        ImagesDTO images = collectionService.getImages(collectionId, offset, limit);
        images.setNextHref(
                getNextHref(COLLECTIONS_URL + "/" + collectionId + "/images", offset, limit));
        images.setPrevHref(
                getPrevHref(COLLECTIONS_URL + "/" + collectionId + "/images", offset, limit));
        setAllImagesHrefs(images, offset, limit);
        images.setHref(COLLECTIONS_URL + "/" + collectionId + "/images" + getPaginationString(offset, limit));

        return Response.ok().entity(images).build();
    }
}
