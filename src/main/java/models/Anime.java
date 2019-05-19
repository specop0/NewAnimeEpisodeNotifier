package models;

import java.util.Objects;
import org.json.JSONObject;

public class Anime {

    public Anime(int id, String name, String url) {
        this.Id = id;
        this.Name = name;
        this.Url = url;
    }

    private final int Id;

    private final String Name;

    private final String Url;

    public int GetId() {
        return Id;
    }

    public String GetName() {
        return Name;
    }

    public String GetUrl() {
        return Url;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.GetId());
        json.put("name", this.GetName());
        json.put("url", this.GetUrl());
        return json;
    }

    public static Anime fromJSON(JSONObject json) {
        return new Anime(
                json.getInt("id"),
                json.getString("name"),
                json.getString("url"));
    }

    @Override
    public String toString() {
        return "Anime{" + "Id=" + Id + ", Name=" + Name + ", Url=" + Url + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.Id;
        hash = 13 * hash + Objects.hashCode(this.Name);
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
        final Anime other = (Anime) obj;
        if (this.Id != other.Id) {
            return false;
        }
        if (!Objects.equals(this.Name, other.Name)) {
            return false;
        }
        return true;
    }
}
