package com.dimordovin.imageservice.service;

import com.dimordovin.imageservice.dto.ImageDTO;
import com.dimordovin.imageservice.dto.ImagesDTO;
import com.dimordovin.imageservice.dto.Resolution;
import com.dimordovin.imageservice.entity.Collection;
import com.dimordovin.imageservice.entity.Image;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImageService {

    private static final SessionFactory sessionFactory = SessionFactoryHelper.sessionFactory;

    public ImageDTO createAndSaveImage(ImageDTO dto) {

        if (dto.getName() == null) {
            throw new NullPointerException("name");
        }

        Session session = sessionFactory.openSession();

        String newId = null;
        Transaction tx = null;
        try {

            do {
                newId = RandomStringUtils.randomAlphanumeric(10);
            } while (session.get(Image.class, newId) != null);

            tx = session.beginTransaction();
            Image image = new Image(newId, dto.getName(),
                    dto.getResolution() == null ? null : dto.getResolution().getHeight(),
                    dto.getResolution() == null ? null : dto.getResolution().getWidth(),
                    dto.getType(),
                    dto.getUrl(),
                    ZonedDateTime.now(ZoneId.of("Z")),
                    dto.getSize());

            session.save(image);
            tx.commit();

            return getDtoFromEntity(image);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during image creation", e);
        } finally {
            session.close();
        }
    }

    public ImageDTO updateImage(String id, ImageDTO dto) {
        if (id == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Image image;
        try {
            tx = session.beginTransaction();

            image = session.get(Image.class, id);

            if (image == null) {
                return null;
            }

            if (dto.getResolution() != null) {
                image.setWidth(dto.getResolution().getWidth());
                image.setHeight(dto.getResolution().getHeight());
            }

            if (dto.getName() != null) {
                image.setName(dto.getName());
            }

            if (dto.getSize() != null) {
                image.setSize(dto.getSize());
            }

            if (dto.getType() != null) {
                image.setType(dto.getType());
            }

            if (dto.getUrl() != null) {
                image.setUrl(dto.getUrl());
            }

            session.update(image);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during image update", e);
        } finally {
            session.close();
        }

        return getDtoFromEntity(image);
    }

    public ImageDTO getImage(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        try {
            Image image = session.get(Image.class, id);
            return image == null ? null : getDtoFromEntity(image);
        } finally {
            session.close();
        }
    }

    public ImageDTO deleteImage(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Image image = null;
        try {
            tx = session.beginTransaction();

            image = session.get(Image.class, id);

            if (image != null) {

                for (Collection c : image.getCollections()) {
                    c.getImages().remove(image);
                }

                session.remove(image);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during image deletion", e);
        } finally {
            session.close();
        }

        return image == null ? null : getDtoFromEntity(image);
    }

    public ImagesDTO getImages(Integer offset, Integer limit) {
        Session session = sessionFactory.openSession();
        List<Image> images;
        try {
            Query query = session.createQuery("FROM Image ORDER BY creationTime");

            if (offset != null) {
                query.setFirstResult(offset);
            }

            if (limit != null) {
                query.setMaxResults(limit);
            }

            images = query.list();
        } finally {
            session.close();
        }

        ImagesDTO imagesDTO = new ImagesDTO();
        imagesDTO.setImages(new ArrayList<>());

        for (Image i : images) {
            imagesDTO.getImages().add(getDtoFromEntity(i));
        }

        return imagesDTO;
    }

    public static ImageDTO getDtoFromEntity(Image image) {

        Resolution resolution = null;
        if (image.getHeight() != null && image.getWidth() != null) {
            resolution = new Resolution(image.getWidth(), image.getHeight());
        }

        ImageDTO dto = new ImageDTO(image.getId(), image.getName(), resolution, image.getType(), image.getUrl(),
                image.getCreationTime().toString(), image.getSize());

        return dto;
    }
}
