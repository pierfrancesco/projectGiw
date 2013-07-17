package it.uniroma3.dia.giw.model.twitter.data;

public class Place {
    
    public Attribute attributes;
    public BoundingBox boundingBox;
    public String country;
    public String countryCode;
    public String fullname;
    public String id;
    public String name;
    public String placeType;
    public String url;
    
    public Place() {
    
    }
    
    public Attribute getAttributes() {
    
        return attributes;
    }
    
    public void setAttributes(Attribute attributes) {
    
        this.attributes = attributes;
    }
    
    public BoundingBox getBoundingBox() {
    
        return boundingBox;
    }
    
    public void setBoundingBox(BoundingBox boundingBox) {
    
        this.boundingBox = boundingBox;
    }
    
    public String getCountry() {
    
        return country;
    }
    
    public void setCountry(String country) {
    
        this.country = country;
    }
    
    public String getCountryCode() {
    
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
    
        this.countryCode = countryCode;
    }
    
    public String getFullname() {
    
        return fullname;
    }
    
    public void setFullname(String fullname) {
    
        this.fullname = fullname;
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
    
    public String getPlaceType() {
    
        return placeType;
    }
    
    public void setPlaceType(String placeType) {
    
        this.placeType = placeType;
    }
    
    public String getUrl() {
    
        return url;
    }
    
    public void setUrl(String url) {
    
        this.url = url;
    }
    
    @Override
    public int hashCode() {
    
        final int prime = 31;
        int result = 1;
        result = prime * result + (attributes == null ? 0 : attributes.hashCode());
        result = prime * result + (boundingBox == null ? 0 : boundingBox.hashCode());
        result = prime * result + (country == null ? 0 : country.hashCode());
        result = prime * result + (countryCode == null ? 0 : countryCode.hashCode());
        result = prime * result + (fullname == null ? 0 : fullname.hashCode());
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (placeType == null ? 0 : placeType.hashCode());
        result = prime * result + (url == null ? 0 : url.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
    
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Place other = (Place) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (boundingBox == null) {
            if (other.boundingBox != null) {
                return false;
            }
        } else if (!boundingBox.equals(other.boundingBox)) {
            return false;
        }
        if (country == null) {
            if (other.country != null) {
                return false;
            }
        } else if (!country.equals(other.country)) {
            return false;
        }
        if (countryCode == null) {
            if (other.countryCode != null) {
                return false;
            }
        } else if (!countryCode.equals(other.countryCode)) {
            return false;
        }
        if (fullname == null) {
            if (other.fullname != null) {
                return false;
            }
        } else if (!fullname.equals(other.fullname)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (placeType == null) {
            if (other.placeType != null) {
                return false;
            }
        } else if (!placeType.equals(other.placeType)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    
        return "Place [attributes=" + attributes + ", boundingBox=" + boundingBox + ", country="
                + country + ", countryCode=" + countryCode + ", fullname=" + fullname + ", id="
                + id + ", name=" + name + ", placeType=" + placeType + ", url=" + url + "]";
    }
    
}
