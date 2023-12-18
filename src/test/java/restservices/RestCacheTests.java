package restservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import models.Anime;
import models.Episode;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestCacheTests {

    protected int Port;
    protected String BaseUrl;
    protected final Map<String, JSONObject> SavedData = new HashMap<>();
    protected RestCache Testee;

    @Before
    public void SetUp() throws Exception {
        this.Port = 12345;
        this.BaseUrl = String.format("http://localhost:%d/data", this.Port);
        this.SavedData.clear();

        // substitute URL via Spark to return known HTML pages
        spark.Spark.port(this.Port);
        spark.Spark.get("/data/*", (rqst, rspns) -> {
            String key = rqst.splat()[0];
            rspns.type("application/json");
            JSONObject data = this.SavedData.get(key);

            spark.Spark.awaitInitialization();
            if (data == null) {
                return new JSONObject();
            }
            return data;
        });
        spark.Spark.put("/data/*", (rqst, rspns) -> {
            String key = rqst.splat()[0];
            String body = rqst.body();
            this.SavedData.put(key, new JSONObject(body));
            rspns.type("application/json");
            return new JSONObject();
        });
        spark.Spark.awaitInitialization();

        this.Testee = new RestCache(this.BaseUrl);
    }

    @After
    public void TearDown() throws Exception {
        spark.Spark.stop();
        spark.Spark.awaitStop();
    }

    @Test
    public void TestLastParsedEpisodes() {
        // empty
        List<Episode> actualEpisodes;
        actualEpisodes = this.Testee.GetLastParsedEpisodes();
        Assert.assertTrue(actualEpisodes.isEmpty());
        Assert.assertTrue(this.SavedData.isEmpty());

        // set
        List<Episode> expectedEpisodes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Episode expectedEpisode = new Episode(
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10));
            expectedEpisodes.add(expectedEpisode);
        }
        this.Testee.SetLastParsedEpisodes(expectedEpisodes);
        Assert.assertArrayEquals(
                new String[] { RestCache.KEY_LAST_PARSED_EPISODES },
                this.SavedData.keySet().toArray());

        // get
        actualEpisodes = this.Testee.GetLastParsedEpisodes();
        Assert.assertEquals(expectedEpisodes.size(), actualEpisodes.size());
        for (int i = 0; i < expectedEpisodes.size(); i++) {
            Assert.assertEquals(expectedEpisodes.get(i), actualEpisodes.get(i));
        }

        // clear
        this.SavedData.clear();
        actualEpisodes = this.Testee.GetLastParsedEpisodes();
        Assert.assertTrue(actualEpisodes.isEmpty());
    }

    @Test
    public void TestSeasonAnimes() {
        // empty
        List<Anime> actualItems;
        actualItems = this.Testee.GetSeasonAnimes();
        Assert.assertTrue(actualItems.isEmpty());
        Assert.assertTrue(this.SavedData.isEmpty());

        // set
        List<Anime> expectedItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Anime expectedItem = new Anime(
                    ThreadLocalRandom.current().nextInt(),
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10));
            expectedItems.add(expectedItem);
        }
        this.Testee.SetSeasonAnimes(expectedItems);
        Assert.assertArrayEquals(new String[] { RestCache.KEY_SEASON_ANIMES }, this.SavedData.keySet().toArray());

        // get
        actualItems = this.Testee.GetSeasonAnimes();
        Assert.assertEquals(expectedItems.size(), actualItems.size());
        for (int i = 0; i < expectedItems.size(); i++) {
            Assert.assertEquals(expectedItems.get(i), actualItems.get(i));
        }

        // clear
        this.SavedData.clear();
        actualItems = this.Testee.GetSeasonAnimes();
        Assert.assertTrue(actualItems.isEmpty());
    }

}
