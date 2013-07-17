package it.uniroma3.dia.giw.model.twitter.data;

public class Attribute {
    
    public String streetAddress;
    public String locality; // the city the place is in
    public String region; // the administrative region the place is in
    public String iso3; // the country code
    public String postalCode; // in the preferred local format for the place
    public String phone; // in the preferred local format for the place, include
                         // long distance code
    public String twitter; // twitter screen-name, without @
    public String url; // official/canonical URL for place
    
    // public String app:id //An ID or comma separated list of IDs representing
    // the place in the applications place database.
    
    public Attribute() {
    
    }
    
    public String getStreetAddress() {
    
        return streetAddress;
    }
    
    public void setStreetAddress(String streetAddress) {
    
        this.streetAddress = streetAddress;
    }
    
    public String getLocality() {
    
        return locality;
    }
    
    public void setLocality(String locality) {
    
        this.locality = locality;
    }
    
    public String getRegion() {
    
        return region;
    }
    
    public void setRegion(String region) {
    
        this.region = region;
    }
    
    public String getIso3() {
    
        return iso3;
    }
    
    public void setIso3(String iso3) {
    
        this.iso3 = iso3;
    }
    
    public String getPostalCode() {
    
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
    
        this.postalCode = postalCode;
    }
    
    public String getPhone() {
    
        return phone;
    }
    
    public void setPhone(String phone) {
    
        this.phone = phone;
    }
    
    public String getTwitter() {
    
        return twitter;
    }
    
    public void setTwitter(String twitter) {
    
        this.twitter = twitter;
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
        result = prime * result + (iso3 == null ? 0 : iso3.hashCode());
        result = prime * result + (locality == null ? 0 : locality.hashCode());
        result = prime * result + (phone == null ? 0 : phone.hashCode());
        result = prime * result + (postalCode == null ? 0 : postalCode.hashCode());
        result = prime * result + (region == null ? 0 : region.hashCode());
        result = prime * result + (streetAddress == null ? 0 : streetAddress.hashCode());
        result = prime * result + (twitter == null ? 0 : twitter.hashCode());
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
        Attribute other = (Attribute) obj;
        if (iso3 == null) {
            if (other.iso3 != null) {
                return false;
            }
        } else if (!iso3.equals(other.iso3)) {
            return false;
        }
        if (locality == null) {
            if (other.locality != null) {
                return false;
            }
        } else if (!locality.equals(other.locality)) {
            return false;
        }
        if (phone == null) {
            if (other.phone != null) {
                return false;
            }
        } else if (!phone.equals(other.phone)) {
            return false;
        }
        if (postalCode == null) {
            if (other.postalCode != null) {
                return false;
            }
        } else if (!postalCode.equals(other.postalCode)) {
            return false;
        }
        if (region == null) {
            if (other.region != null) {
                return false;
            }
        } else if (!region.equals(other.region)) {
            return false;
        }
        if (streetAddress == null) {
            if (other.streetAddress != null) {
                return false;
            }
        } else if (!streetAddress.equals(other.streetAddress)) {
            return false;
        }
        if (twitter == null) {
            if (other.twitter != null) {
                return false;
            }
        } else if (!twitter.equals(other.twitter)) {
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
    
        return "Attribute [street_address=" + streetAddress + ", locality=" + locality
                + ", region=" + region + ", iso3=" + iso3 + ", postal_code=" + postalCode
                + ", phone=" + phone + ", twitter=" + twitter + ", url=" + url + "]";
    }
    
}
