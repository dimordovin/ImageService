package com.dimordovin.imageservice.controller;

import com.dimordovin.imageservice.dto.CollectionDTO;
import com.dimordovin.imageservice.dto.CollectionsDTO;
import com.dimordovin.imageservice.dto.ImageDTO;
import com.dimordovin.imageservice.dto.ImagesDTO;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final Map<String, String> mimeTypeExtensions = new HashMap<>();

    static {
        mimeTypeExtensions.put("image/jpeg", ".jpeg");
        mimeTypeExtensions.put("image/jpg", ".jpg");
        mimeTypeExtensions.put("image/png", ".png");
        mimeTypeExtensions.put("image/gif", ".gif");
    }

    public static String IMAGE_SERVICE_URL = System.getenv("IMAGE_SERVICE_URL");
    public static String IMAGES_URL = IMAGE_SERVICE_URL + "/api/images";
    public static String COLLECTIONS_URL = IMAGE_SERVICE_URL + "/api/collections";

    public static String getPrevHref(String url, Integer offset, Integer limit) {
        if (limit != null && offset != null && ! offset.equals(0)) {
            return url + "?offset=" + Math.max(0, offset - limit) + "&limit=" + limit;
        } else {
            return null;
        }
    }

    public static String getNextHref(String url, Integer offset, Integer limit) {
        if (limit != null) {
            if (offset == null) {
                offset = 0;
            }
            return url + "?offset=" + (offset + limit) + "&limit=" + limit;
        } else {
            return null;
        }
    }

    public static void addExtensionToName(ImageDTO body) {
        if (body.getName() != null) {
            String ext = mimeTypeExtensions.get(body.getType());
            String currName = body.getName();
            if (! currName.endsWith(ext)) {
                body.setName(body.getName() + mimeTypeExtensions.get(body.getType()));
            }
        }
    }

    public static void setCollectionHref(CollectionDTO collection) {
        collection.setHref(COLLECTIONS_URL + "/" + collection.getId());
    }

    public static void setImageHref(ImageDTO image) {
        image.setHref(IMAGES_URL + "/" + image.getId());
    }

    public static void setAllImagesHrefs(ImagesDTO images, Integer offset, Integer limit) {
        for (ImageDTO i : images.getImages()) {
            setImageHref(i);
        }

        images.setHref(IMAGES_URL + getPaginationString(offset, limit));
    }

    public static void setAllCollectionsHrefs(CollectionsDTO collections, Integer offset, Integer limit) {
        for (CollectionDTO c : collections.getCollections()) {
            setCollectionHref(c);
        }

        collections.setHref(COLLECTIONS_URL + getPaginationString(offset, limit));
    }

    public static String getPaginationString(Integer offset, Integer limit) {
        return "?offset=" + (offset == null ? 0 : offset) + (limit ==  null ? "" : "&limit=" + limit);
    }
}
