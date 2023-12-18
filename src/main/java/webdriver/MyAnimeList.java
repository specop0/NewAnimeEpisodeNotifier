package webdriver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import models.Anime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyAnimeList implements IAnimesProvider {

    public MyAnimeList() {
        this("https://myanimelist.net");
    }

    public MyAnimeList(String baseUrl) {
        this.BaseUrl = baseUrl;
    }

    protected String BaseUrl;

    @Override
    public List<Anime> GetSeasonAnime() {
        try {
            Document doc = Jsoup.connect(this.GetSeasonAnimeUrl()).get();

            Elements animeTitles = doc.select(".link-title");

            return animeTitles.stream()
                    .map(x -> this.ParseSeasonAnime(x))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            Logger.getLogger(Gogoanime.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<>();
    }

    protected Anime ParseSeasonAnime(Element element) {
        String name = element.text();
        String url = element.attr("href");
        String[] splittedUrl = url.split("/");
        String idString = splittedUrl[splittedUrl.length - 2];
        int id = Integer.parseInt(idString);
        return new Anime(id, name, url);
    }

    protected String GetSeasonAnimeUrl() {
        return String.format("%s/anime/season", this.BaseUrl);
    }
}
