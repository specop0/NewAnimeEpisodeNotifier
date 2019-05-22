package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import models.Episode;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import restservices.ICache;
import restservices.IReporter;
import webdriver.IEpisodesProvider;

public class ControllerTests {

    @Before
    public void SetUp() {
        this.Cache = Mockito.mock(ICache.class);
        this.EpisodesProvider = Mockito.mock(IEpisodesProvider.class);
        this.Reporter = Mockito.mock(IReporter.class);

        this.Testee = new Controller(this.Cache, this.EpisodesProvider, this.Reporter);
    }

    protected Controller Testee;
    protected ICache Cache;
    protected IEpisodesProvider EpisodesProvider;
    protected IReporter Reporter;

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
    public void TestNoEpisodes() {
        List<Episode> emptyList = new ArrayList<>();
        Mockito.when(this.EpisodesProvider.GetNextEpisodes()).thenReturn(emptyList);
        Mockito.when(this.EpisodesProvider.HasNextEpisodes()).thenReturn(true, true, false);

        List<Episode> knownEpisodes = this.CreateEpisodes(30);
        Mockito.when(this.Cache.GetLastParsedEpisodes()).thenReturn(knownEpisodes);

        this.Testee.Start();

        Mockito.verify(this.EpisodesProvider, Mockito.times(1)).HasNextEpisodes();
        Mockito.verify(this.EpisodesProvider, Mockito.times(1)).GetNextEpisodes();
        Mockito.verifyNoMoreInteractions(this.EpisodesProvider);

        List<Episode> thinnedKnownEpisodes = knownEpisodes.stream().limit(20).collect(Collectors.toList());
        Mockito.verify(this.Reporter).NotifyNewEpisodes(Mockito.argThat(x -> x.isEmpty()));

        Mockito.verify(this.Cache).SetLastParsedEpisodes(thinnedKnownEpisodes);
    }

    @Test
    public void TestNewEpisodes() {
        List<Episode> providedEpisodesA = this.CreateEpisodes(20);
        List<Episode> providedEpisodesB = this.CreateEpisodes(20);
        Mockito.when(this.EpisodesProvider.GetNextEpisodes()).thenReturn(providedEpisodesA, providedEpisodesB);
        Mockito.when(this.EpisodesProvider.HasNextEpisodes()).thenReturn(true, true, false);

        List<Episode> knownEpisodes = this.CreateEpisodes(20);
        Mockito.when(this.Cache.GetLastParsedEpisodes()).thenReturn(knownEpisodes);

        this.Testee.Start();

        Mockito.verify(this.EpisodesProvider, Mockito.times(2)).GetNextEpisodes();
        Mockito.verify(this.EpisodesProvider, Mockito.times(3)).HasNextEpisodes();
        Mockito.verifyNoMoreInteractions(this.EpisodesProvider);

        List<Episode> allKnownEpisodes = Stream.concat(providedEpisodesA.stream(), providedEpisodesB.stream()).collect(Collectors.toList());
        Mockito.verify(this.Reporter).NotifyNewEpisodes(allKnownEpisodes);

        Mockito.verify(this.Cache).SetLastParsedEpisodes(providedEpisodesA);
    }

    @Test
    public void TestNewEpisodesOnTwoPages() {
        List<Episode> providedEpisodesA = this.CreateEpisodes(20);
        List<Episode> providedEpisodesB = this.CreateEpisodes(20);
        Mockito.when(this.EpisodesProvider.GetNextEpisodes()).thenReturn(providedEpisodesA, providedEpisodesB, this.CreateEpisodes(20));
        Mockito.when(this.EpisodesProvider.HasNextEpisodes()).thenReturn(true);

        List<Episode> knownEpisodes = Stream.concat(providedEpisodesA.stream().limit(10), providedEpisodesB.stream().limit(10)).collect(Collectors.toList());
        Mockito.when(this.Cache.GetLastParsedEpisodes()).thenReturn(knownEpisodes);

        this.Testee.Start();

        Mockito.verify(this.EpisodesProvider, Mockito.times(2)).GetNextEpisodes();
        Mockito.verify(this.EpisodesProvider, Mockito.times(2)).HasNextEpisodes();
        Mockito.verifyNoMoreInteractions(this.EpisodesProvider);

        List<Episode> newEpisodes = providedEpisodesA.stream().skip(10).collect(Collectors.toList());
        Mockito.verify(this.Reporter).NotifyNewEpisodes(newEpisodes);

        Mockito.verify(this.Cache).SetLastParsedEpisodes(providedEpisodesA);
    }

    @Test
    public void TestNewEpisodesOnThreePages() {
        List<Episode> providedEpisodesA = this.CreateEpisodes(20);
        List<Episode> providedEpisodesB = this.CreateEpisodes(20);
        List<Episode> providedEpisodesC = this.CreateEpisodes(20);
        Mockito.when(this.EpisodesProvider.GetNextEpisodes()).thenReturn(providedEpisodesA, providedEpisodesB, providedEpisodesC, this.CreateEpisodes(20));
        Mockito.when(this.EpisodesProvider.HasNextEpisodes()).thenReturn(true);

        List<Episode> knownEpisodes = Stream.concat(providedEpisodesA.stream().skip(10), providedEpisodesC.stream().skip(10)).collect(Collectors.toList());
        Mockito.when(this.Cache.GetLastParsedEpisodes()).thenReturn(knownEpisodes);

        this.Testee.Start();

        Mockito.verify(this.EpisodesProvider, Mockito.times(3)).GetNextEpisodes();
        Mockito.verify(this.EpisodesProvider, Mockito.times(3)).HasNextEpisodes();
        Mockito.verifyNoMoreInteractions(this.EpisodesProvider);

        List<Episode> allProvidedEpisodes = Stream.concat(
                Stream.concat(providedEpisodesA.stream(), providedEpisodesB.stream()),
                providedEpisodesC.stream())
                .collect(Collectors.toList());
        List<Episode> newEpisodes = allProvidedEpisodes.stream().filter(x -> !knownEpisodes.contains(x)).collect(Collectors.toList());
        Mockito.verify(this.Reporter).NotifyNewEpisodes(newEpisodes);

        Mockito.verify(this.Cache).SetLastParsedEpisodes(providedEpisodesA);
    }

    @Test
    public void TestNewEpisodesAbortsAtKnownEpisodesReached() {
        List<Episode> providedEpisodes = this.CreateEpisodes(40);
        Mockito.when(this.EpisodesProvider.GetNextEpisodes()).thenReturn(providedEpisodes);
        Mockito.when(this.EpisodesProvider.HasNextEpisodes()).thenReturn(true);

        List<Episode> newEpisodes = new ArrayList<>();
        List<Episode> knownEpisodes = new ArrayList<>();
        List<Episode> allEpisodes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Episode episode = providedEpisodes.get(i);
            if (i % 2 == 0) {
                newEpisodes.add(episode);
            } else {
                knownEpisodes.add(episode);
            }
            allEpisodes.add(episode);
        }
        Mockito.when(this.Cache.GetLastParsedEpisodes()).thenReturn(knownEpisodes);

        this.Testee.Start();

        Mockito.verify(this.EpisodesProvider, Mockito.times(1)).GetNextEpisodes();
        Mockito.verify(this.EpisodesProvider, Mockito.times(1)).HasNextEpisodes();
        Mockito.verifyNoMoreInteractions(this.EpisodesProvider);

        Mockito.verify(this.Reporter).NotifyNewEpisodes(newEpisodes);
        Mockito.verify(this.Cache).SetLastParsedEpisodes(allEpisodes);
    }
}
