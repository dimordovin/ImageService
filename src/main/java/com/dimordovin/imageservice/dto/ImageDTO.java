package com.dimordovin.imageservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "image")
public class ImageDTO {

    private String id;

    private String name;

    @XmlElement(name = "resolution", type = Resolution.class, nillable = false)
    private Resolution resolution;

    private String type;

    private String url;

    private String creationTime;

    private Integer size;

    private String href;

    public ImageDTO() {
    }

    public ImageDTO(String id, String name, Resolution resolution, String type, String url, String creationTime,
                    Integer size) {
        this.id = id;
        this.name = name;
        this.resolution = resolution;
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

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
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

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}