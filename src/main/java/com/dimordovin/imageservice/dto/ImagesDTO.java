package com.dimordovin.imageservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "images")
public class ImagesDTO {

    private String nextHref;

    private String prevHref;

    private String href;

    @XmlElement(name =  "images", type = ImageDTO.class)
    private List<ImageDTO> images = null;

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

    public List<ImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
