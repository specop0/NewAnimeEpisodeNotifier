package webdriver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import models.Episode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Gogoanime implements IEpisodesProvider {

    public Gogoanime() {
        this("https://www3.gogoanime.io/?page=%d");
    }

    public Gogoanime(String baseUrl) {
        this.BaseUrl = baseUrl;
    }

    protected int CurrentPage = 0;
    protected String BaseUrl;

    @Override
    public boolean HasNextEpisodes() {
        return this.CurrentPage < 5;
    }

    @Override
    public List<Episode> GetNextEpisodes() {
        this.CurrentPage++;

        try {
            Document doc = Jsoup.connect(this.GetCurrentUrl()).get();

            Element episodeListElement = doc.selectFirst(".items");

            return episodeListElement.children().stream()
                    .map(x -> this.ParseEpisode(x))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            Logger.getLogger(Gogoanime.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<>();
    }

    public String GetCurrentUrl() {
        return String.format(BaseUrl, this.CurrentPage);
    }

    protected Episode ParseEpisode(Element element) {
        Element nameElement = element.selectFirst(".name a");
        String url = nameElement.attr("href");
        String name = nameElement.text();

        Element episodeElement = element.selectFirst(".episode");
        String episode = episodeElement.text();

        return new Episode(url, name, episode);
    }
}
