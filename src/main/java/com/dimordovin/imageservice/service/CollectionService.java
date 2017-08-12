package com.dimordovin.imageservice.service;

import com.dimordovin.imageservice.dto.CollectionDTO;
import com.dimordovin.imageservice.dto.CollectionsDTO;
import com.dimordovin.imageservice.dto.ImagesDTO;
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

public class CollectionService {

    private static final SessionFactory sessionFactory = SessionFactoryHelper.sessionFactory;

    public CollectionDTO createAndSaveCollection(CollectionDTO dto) {

        if (dto.getName() == null) {
            throw new NullPointerException("name");
        }

        Session session = sessionFactory.openSession();

        String newId = null;
        Transaction tx = null;
        try {

            do {
                newId = RandomStringUtils.randomAlphanumeric(10);
            } while (session.get(Collection.class, newId) != null);

            tx = session.beginTransaction();
            Collection collection = new Collection(newId, dto.getName(), ZonedDateTime.now(ZoneId.of("Z")));

            session.save(collection);
            tx.commit();

            return getDtoFromEntity(collection);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during collection creation", e);
        } finally {
            session.close();
        }
    }

    public CollectionDTO getCollection(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        try {
            Collection collection = session.get(Collection.class, id);
            return collection == null ? null : getDtoFromEntity(collection);
        } finally {
            session.close();
        }
    }

    public CollectionDTO deleteCollection(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Collection collection = null;
        try {
            tx = session.beginTransaction();

            collection = session.get(Collection.class, id);

            if (collection != null) {
                for (Image i : collection.getImages()) {
                    i.getCollections().remove(collection);
                    session.update(i);
                }
                collection.getImages().clear();

                session.update(collection);
                session.remove(collection);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during collection deletion", e);
        } finally {
            session.close();
        }

        return collection == null ? null : getDtoFromEntity(collection);
    }

    public CollectionsDTO getCollections(Integer offset, Integer limit) {
        Session session = sessionFactory.openSession();
        List<Collection> collections;
        try {
            Query query = session.createQuery("FROM Collection ORDER BY creationTime");

            if (offset != null) {
                query.setFirstResult(offset);
            }

            if (limit != null) {
                query.setMaxResults(limit);
            }

            collections = query.list();
        } finally {
            session.close();
        }

        CollectionsDTO collectionsDTO = new CollectionsDTO();
        collectionsDTO.setCollections(new ArrayList<>());

        for (Collection c : collections) {
            collectionsDTO.getCollections().add(getDtoFromEntity(c));
        }

        return collectionsDTO;
    }

    public CollectionDTO updateCollection(String id, CollectionDTO dto) {
        if (id == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Collection collection;
        try {
            tx = session.beginTransaction();

            collection = session.get(Collection.class, id);

            if (collection == null) {
                return null;
            }

            if (dto.getName() != null) {
                collection.setName(dto.getName());
            }

            session.update(collection);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during collection update", e);
        } finally {
            session.close();
        }

        return getDtoFromEntity(collection);
    }

    public boolean addImage(String collectionId, String imageId) {
        if (collectionId == null || imageId == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Collection collection = session.get(Collection.class, collectionId);

            if (collection == null) {
                return false;
            }

            Image image = session.get(Image.class, imageId);

            if (image == null) {
                return false;
            }

            collection.getImages().add(image);
            image.getCollections().add(collection);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during image addition", e);
        } finally {
            session.close();
        }

        return true;
    }

    public boolean deleteImage(String collectionId, String imageId) {
        if (collectionId == null || imageId == null) {
            throw new NullPointerException("id");
        }

        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Collection collection = session.get(Collection.class, collectionId);

            if (collection == null) {
                return false;
            }

            Image image = session.get(Image.class, imageId);

            if (image == null) {
                return false;
            }

            collection.getImages().remove(image);
            image.getCollections().remove(collection);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            throw new RuntimeException("error during image addition", e);
        } finally {
            session.close();
        }

        return true;
    }

    public ImagesDTO getImages(String collectionId, Integer offset, Integer limit) {
        Session session = sessionFactory.openSession();
        List<Image> images;
        try {
            Query query = session.createQuery("SELECT i FROM Image i JOIN i.collections c WHERE c.id = :id")
                    .setParameter("id", collectionId);

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
            imagesDTO.getImages().add(ImageService.getDtoFromEntity(i));
        }

        return imagesDTO;
    }

    private CollectionDTO getDtoFromEntity(Collection collection) {

        CollectionDTO dto = new CollectionDTO(collection.getId(), collection.getName(),
                collection.getCreationTime().toString());

        return dto;
    }
}
