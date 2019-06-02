package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import models.Anime;
import models.Episode;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import restservices.ICache;
import restservices.IReporter;
import webdriver.IAnimesProvider;

public class ReporterControllerTests {

    @Before
    public void SetUp() {
        this.Cache = Mockito.mock(ICache.class);
        this.Reporter = Mockito.mock(IReporter.class);
        this.AnimesProvider = Mockito.mock(IAnimesProvider.class);

        this.Testee = new ReporterController(this.Cache, this.AnimesProvider, this.Reporter);
    }

    protected ReporterController Testee;
    protected ICache Cache;
    protected IReporter Reporter;
    protected IAnimesProvider AnimesProvider;

    protected List<Episode> CreateEpisodes(int length) {
        List<Episode> episodes = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Episode episode = new Episode(
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10));
            episodes.add(episode);
        }
        return episodes;
    }

    @Test
    public void TestFilterSeasonAnimes() {
        List<String> animeNames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            animeNames.add(RandomStringUtils.randomAlphabetic(10));
        }

        List<Anime> seasonAnimes = new ArrayList<>();
        List<Episode> episodes = new ArrayList<>();
        List<Episode> expectedEpisodes = new ArrayList<>();
        for (String animeName : animeNames) {
            seasonAnimes.add(new Anime(0, animeName, ""));
            seasonAnimes.add(new Anime(0, RandomStringUtils.randomAlphabetic(10), ""));

            Episode expectedEpisode = new Episode(
                    RandomStringUtils.randomAlphabetic(10),
                    animeName + RandomStringUtils.randomAlphabetic(10),
                    RandomStringUtils.randomAlphabetic(10));
            episodes.add(expectedEpisode);
            expectedEpisodes.add(expectedEpisode);

            Episode matchingEpisode = new Episode(
                    RandomStringUtils.randomAlphabetic(10),
                    animeName,
                    RandomStringUtils.randomAlphabetic(10));
            episodes.add(matchingEpisode);
        }
        
        Mockito.when(this.AnimesProvider.GetSeasonAnime()).thenReturn(seasonAnimes);
        
        List<Episode> actualEpisodes = this.Testee.FilterSeasonAnimes(episodes);
        Assert.assertArrayEquals(expectedEpisodes.toArray(), actualEpisodes.toArray());
        
        Mockito.verify(this.Cache).SetSeasonAnimes(seasonAnimes);
    }
}
