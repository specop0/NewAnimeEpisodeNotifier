package models;

import java.util.Objects;
import org.json.JSONObject;

public class Episode {

    public Episode(String url, String name, String episode) {
        this.Url = url;
        this.Name = name;
        this.Episode = episode;
    }

    private final String Url;

    private final String Name;

    private final String Episode;

    public String GetUrl() {
        return Url;
    }

    public String GetName() {
        return Name;
    }

    public String GetEpisode() {
        return Episode;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("url", this.GetUrl());
        json.put("name", this.GetName());
        json.put("episode", this.GetEpisode());
        return json;
    }

    public static Episode fromJSON(JSONObject json) {
        return new Episode(
                json.getString("url"),
                json.getString("name"),
                json.getString("episode"));
    }

    @Override
    public String toString() {
        return "Episode{" + "Url=" + this.Url + ", Name=" + this.Name + ", Episode=" + this.Episode + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.Url);
        hash = 79 * hash + Objects.hashCode(this.Name);
        hash = 79 * hash + Objects.hashCode(this.Episode);
        return hash;
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
        final Episode other = (Episode) obj;
        if (!Objects.equals(this.Url, other.Url)) {
            return false;
        }
        if (!Objects.equals(this.Name, other.Name)) {
            return false;
        }
        if (!Objects.equals(this.Episode, other.Episode)) {
            return false;
        }
        return true;
    }
}
