package restservices;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import models.Anime;
import models.Episode;
import org.json.JSONObject;

public class RestCache extends RestBase implements ICache {

    public RestCache(String ipAddress, int port, String authorization) {
        this(String.format("http://%s:%d/data/%s", ipAddress, port, authorization));
    }

    public RestCache(String baseUrl) {
        this.BaseUrl = baseUrl;
    }

    public static final String KEY_LAST_PARSED_EPISODES = "LastParsedEpisodes";
    public static final String KEY_SEASON_ANIMES = "SeasonAnimes";
    public static final String KEY_SINGLE_ENTRY = "$data";

    protected String BaseUrl;

    protected String GetUrl(String key) {
        return String.format("%s/%s", this.BaseUrl, key);
    }

    @Override
    public void SetLastParsedEpisodes(List<Episode> episodes) {
        this.SetItems(episodes, this.GetUrl(KEY_LAST_PARSED_EPISODES), x -> x.toJSON());
    }

    @Override
    public List<Episode> GetLastParsedEpisodes() {
        return this.GetItems(this.GetUrl(KEY_LAST_PARSED_EPISODES), x -> Episode.fromJSON(x));
    }

    @Override
    public void SetSeasonAnimes(List<Anime> animes) {
        this.SetItems(animes, this.GetUrl(KEY_SEASON_ANIMES), x -> x.toJSON());
    }

    @Override
    public List<Anime> GetSeasonAnimes() {
        return this.GetItems(this.GetUrl(KEY_SEASON_ANIMES), x -> Anime.fromJSON(x));
    }

    protected <T> void SetItems(List<T> items, String url, Function<T, JSONObject> jsonFunc) {
        JSONObject obj = new JSONObject();
        List<JSONObject> serializedItems = items.stream()
                .map(jsonFunc)
                .collect(Collectors.toList());
        obj.put(KEY_SINGLE_ENTRY, serializedItems);

        this.Do(url, "PUT", obj);
    }

    protected <T> List<T> GetItems(String url, Function<JSONObject, T> jsonFunc) {
        JSONObject jsonResult = this.Do(url, "GET");
        List<T> items = new ArrayList<>();
        if (jsonResult.keySet().contains(KEY_SINGLE_ENTRY)) {
            jsonResult.getJSONArray(KEY_SINGLE_ENTRY).forEach(x -> {
                JSONObject jsonObject = (JSONObject) x;
                items.add(jsonFunc.apply(jsonObject));
            });
        }
        return items;
    }
}
