package com.dimordovin.imageservice.entity;


import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "height")
    private Integer height;

    @Column(name = "width")
    private Integer width;

    @Column(name = "type")
    private String type;

    @Column(name = "url")
    private String url;

    @Column(name = "creation_time")
    @Type(type="org.hibernate.type.ZonedDateTimeType")
    private ZonedDateTime creationTime;

    @Column(name = "size")
    private Integer size;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "images")
    private Set<Collection> collections = new HashSet<>();

    public Image() {
    }

    public Image(String id, String name, Integer height, Integer width, String type, String url,
                    ZonedDateTime creationTime, Integer size) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.width = width;
        this.type = type;
        this.url = url;
        this.creationTime = creationTime;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Set<Collection> getCollections() {
        return collections;
    }

    public void setCollections(Set<Collection> collections) {
        this.collections = collections;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (o.getClass() != getClass()) {
            return false;
        }

        Image i = (Image) o;

        return getId().equals(i.getId());
    }
}