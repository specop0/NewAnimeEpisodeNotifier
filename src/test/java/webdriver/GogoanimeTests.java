package webdriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import models.Episode;
import models.File;
import org.json.JSONObject;
import org.junit.Assert;

public class GogoanimeTests {

    @Test
    public void TestParsing() {
        int port = 12345;
        spark.Spark.port(port);

        String baseUrl = String.format("http://localhost:%d/gogoanime/?page=", port);
        baseUrl += "%d";

        // load HTML pages to parse and expected Episodes (from resources)
        Map<String, String> pages = new HashMap<>();
        Map<String, List<Episode>> expectedResults = new HashMap<>();
        for (int page = 1; page <= 5; page++) {
            String pageResource = String.format("webdriver/GogoanimePage%d.html", page);
            String pagePath = this.getClass().getClassLoader().getResource(pageResource).getPath();

            String expectedResource = String.format("webdriver/GogoanimePage%d.json", page);
            String expectedPath = this.getClass().getClassLoader().getResource(expectedResource).getPath();

            String key = String.format("%d", page);
            pages.put(key, File.ReadAllText(pagePath));

            JSONObject expectedInformation = new JSONObject(File.ReadAllText(expectedPath));
            List<Episode> expectedEpisodes = expectedInformation.keySet().stream()
                    .map(x -> Integer.parseInt(x))
                    .sorted()
                    .map(x -> String.format("%d", x))
                    .map(x -> expectedInformation.getJSONObject(x))
                    .map(x -> {
                        String expectedUrl = x.getString("href");
                        String expectedName = x.getString("title");
                        String expectedEpisode = x.getString("episode");
                        Episode expected = new Episode(expectedUrl, expectedName, expectedEpisode);
                        return expected;
                    })
                    .collect(Collectors.toList());
            expectedResults.put(key, expectedEpisodes);
        }

        // substitue URL via Spark to return known HTML pages
        Gogoanime testee = new Gogoanime(baseUrl);
        spark.Spark.get("/gogoanime/", (rqst, rspns) -> {
            String pageParameter = rqst.queryParams("page");
            if (pages.containsKey(pageParameter)) {
                return pages.get(pageParameter);
            }
            return "";
        });
        spark.Spark.awaitInitialization();

        // parse all 5 pages and compare with expected episodes
        for (int i = 1; i <= 5; i++) {
            List<Episode> actualEpisodes = testee.GetNextEpisodes();

            String key = String.format("%d", i);
            List<Episode> expectedEpisodes = expectedResults.get(key);

            Assert.assertEquals(expectedEpisodes.size(), actualEpisodes.size());
            Assert.assertEquals(20, expectedEpisodes.size());
            for (int j = 0; j < expectedEpisodes.size(); j++) {
                Episode expectedEpisode = expectedEpisodes.get(j);
                Episode actualEpisode = actualEpisodes.get(j);

                Assert.assertEquals(expectedEpisode.GetUrl(), actualEpisode.GetUrl());
                Assert.assertEquals(expectedEpisode.GetName(), actualEpisode.GetName());
                Assert.assertEquals(expectedEpisode.GetEpisode(), actualEpisode.GetEpisode());
                
                Assert.assertEquals(expectedEpisode, actualEpisode);
                Assert.assertEquals(expectedEpisode.toString(), actualEpisode.toString());
            }
        }

        Assert.assertFalse(testee.HasNextEpisodes());
        Assert.assertTrue(testee.GetNextEpisodes().isEmpty());

        spark.Spark.stop();
        spark.Spark.awaitStop();
    }
}
