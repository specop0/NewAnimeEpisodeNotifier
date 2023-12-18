package webdriver;

import java.util.ArrayList;
import java.util.List;
import models.Anime;
import models.File;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class MyAnimeListTests {

    @After
    public void TearDown() {
        spark.Spark.stop();
        spark.Spark.awaitStop();
    }

    @Test
    public void TestParsingSeasonAnime() {
        int port = 12345;
        spark.Spark.port(port);

        String baseUrl = String.format("http://localhost:%d", port);

        // load HTML page to parse and expected information (from resources)
        String pageResource = "webdriver/Season.html";
        String pagePath = this.getClass().getClassLoader().getResource(pageResource).getPath();
        String page = File.ReadAllText(pagePath);

        String expectedResource = String.format("webdriver/Season.json");
        String expectedPath = this.getClass().getClassLoader().getResource(expectedResource).getPath();
        JSONObject expectedInformation = new JSONObject(File.ReadAllText(expectedPath));

        List<Anime> expectedAnimes = new ArrayList<>();
        expectedInformation.getJSONArray("$data").forEach(x -> {
            JSONObject jsonObject = (JSONObject) x;
            expectedAnimes.add(Anime.fromJSON(jsonObject));
        });

        // substitue URL via Spark to return known HTML page
        MyAnimeList testee = new MyAnimeList(baseUrl);
        spark.Spark.get("/anime/season", (rqst, rspns) -> page);
        spark.Spark.awaitInitialization();

        // parse season anime and compare with expected
        List<Anime> actualAnimes = testee.GetSeasonAnime();
        Assert.assertEquals(expectedAnimes.size(), actualAnimes.size());
        for (int i = 0; i < expectedAnimes.size(); i++) {
            Anime expectedAnime = expectedAnimes.get(i);
            Anime actualAnime = actualAnimes.get(i);

            Assert.assertEquals(expectedAnime.GetId(), actualAnime.GetId());
            Assert.assertEquals(expectedAnime.GetName(), actualAnime.GetName());
            Assert.assertEquals(expectedAnime.GetUrl(), actualAnime.GetUrl());

            Assert.assertEquals(expectedAnime, actualAnime);
            Assert.assertEquals(expectedAnime.toString(), actualAnime.toString());
        }
    }
}
