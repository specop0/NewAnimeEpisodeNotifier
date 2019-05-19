package restservices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.Episode;
import org.json.JSONObject;

public class RestCache extends RestBase implements ICache {

    public RestCache(int port, String authorization) {
        this(String.format("http://localhost:%d/data/%s", port, authorization));
    }

    public RestCache(String baseUrl) {
        this.BaseUrl = baseUrl;
    }

    public static final String LAST_PARSED_EPISODES_KEY = "LastParsedEpisodes";
    public static final String SINGLE_ENTRY_KEY = "$data";

    protected String BaseUrl;

    protected String GetUrl(String key) {
        return String.format("%s/%s", this.BaseUrl, key);
    }

    @Override
    public void SetLastParsedEpisodes(List<Episode> episodes) {
        JSONObject obj = new JSONObject();
        List<JSONObject> serializedEpisodes = episodes.stream()
                .map(x -> x.toJSON())
                .collect(Collectors.toList());
        obj.put(SINGLE_ENTRY_KEY, serializedEpisodes);

        this.Do(this.GetUrl(LAST_PARSED_EPISODES_KEY), "PUT", obj);
    }

    @Override
    public List<Episode> GetLastParsedEpisodes() {
        String url = this.GetUrl(LAST_PARSED_EPISODES_KEY);

        JSONObject jsonResult = this.Do(url, "GET");
        List<Episode> episodes = new ArrayList<>();
        if (jsonResult.keySet().contains(SINGLE_ENTRY_KEY)) {
            jsonResult.getJSONArray(SINGLE_ENTRY_KEY).forEach(x -> {
                JSONObject jsonObject = (JSONObject) x;
                episodes.add(Episode.fromJSON(jsonObject));
            });
        }
        return episodes;
    }
}
