package restservices;

import restservices.RestCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Episode;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class RestCacheTests {

    @Test
    public void TestLastParsedEpisodes() {
        int port = 12345;
        spark.Spark.port(port);

        String baseUrl = String.format("http://localhost:%d/data", port);
        Map<String, JSONObject> savedData = new HashMap<>();

        // substitute URL via Spark to return known HTML pages
        RestCache testee = new RestCache(baseUrl);
        spark.Spark.get("/data/*", (rqst, rspns) -> {
            String key = rqst.splat()[0];
            rspns.type("application/json");
            JSONObject data = savedData.get(key);
            if (data == null) {
                return new JSONObject();
            }
            return data;
        });
        spark.Spark.put("/data/*", (rqst, rspns) -> {
            String key = rqst.splat()[0];
            String body = rqst.body();
            savedData.put(key, new JSONObject(body));
            rspns.type("application/json");
            return new JSONObject();
        });
        spark.Spark.awaitInitialization();

        // empty
        List<Episode> actualEpisodes;
        actualEpisodes = testee.GetLastParsedEpisodes();
        Assert.assertTrue(actualEpisodes.isEmpty());
        Assert.assertTrue(savedData.isEmpty());

        // set
        List<Episode> expectedEpisodes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Episode expectedEpisode = new Episode(
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10));
            expectedEpisodes.add(expectedEpisode);
        }
        testee.SetLastParsedEpisodes(expectedEpisodes);
        Assert.assertArrayEquals(new String[]{RestCache.LAST_PARSED_EPISODES_KEY}, savedData.keySet().toArray());

        // get
        actualEpisodes = testee.GetLastParsedEpisodes();
        Assert.assertEquals(expectedEpisodes.size(), actualEpisodes.size());
        for (int i = 0; i < expectedEpisodes.size(); i++) {
            Assert.assertEquals(expectedEpisodes.get(i), actualEpisodes.get(i));
        }

        // clear
        savedData.clear();
        actualEpisodes = testee.GetLastParsedEpisodes();
        Assert.assertTrue(actualEpisodes.isEmpty());

        spark.Spark.stop();
        spark.Spark.awaitStop();
    }

}
