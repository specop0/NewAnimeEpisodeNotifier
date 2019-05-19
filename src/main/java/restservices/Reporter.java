package restservices;

import java.util.List;
import models.Episode;
import org.json.JSONObject;

public class Reporter extends RestBase implements IReporter {

    public Reporter(int port) {
        this(String.format("http://localhost:%d/mail/send", port));
    }

    public Reporter(String url) {
        this.Url = url;
    }

    protected final String Url;

    @Override
    public void NotifyNewEpisodes(List<Episode> episodes) {
        if (!episodes.isEmpty() && false) {
            JSONObject data = new JSONObject();
            data.put("subject", String.format("%d new episodes", episodes.size()));

            StringBuilder content = new StringBuilder();
            episodes.stream().forEach(x -> {
                content.append(x.GetName());
                content.append(System.lineSeparator());
                content.append(x.GetEpisode());
                content.append(System.lineSeparator());
                content.append(System.lineSeparator());
            });
            data.put("content", content.toString());

            this.Do(this.Url, "POST", data);
        }
    }

}
