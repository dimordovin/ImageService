package com.dimordovin.imageservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "collections")
public class CollectionsDTO {

    private String nextHref;

    private String prevHref;

    private String href;

    @XmlElement(name =  "collections", type = CollectionDTO.class)
    private List<CollectionDTO> collections = null;

    public String getNextHref() {
        return nextHref;
    }

    public void setNextHref(String nextHref) {
        this.nextHref = nextHref;
    }

    public String getPrevHref() {
        return prevHref;
    }

    public void setPrevHref(String prevHref) {
        this.prevHref = prevHref;
    }

    public List<CollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionDTO> collections) {
        this.collections = collections;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}