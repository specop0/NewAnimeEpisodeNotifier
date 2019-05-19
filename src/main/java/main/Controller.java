package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.Episode;
import restservices.ICache;
import restservices.IReporter;
import webdriver.IEpisodesProvider;

public class Controller {

    public Controller(ICache cache, IEpisodesProvider episodesProvider, IReporter reporter) {
        this.Cache = cache;
        this.EpisodesProvider = episodesProvider;
        this.Reporter = reporter;
    }

    protected ICache Cache;
    protected IEpisodesProvider EpisodesProvider;
    protected IReporter Reporter;

    public void Start() {
        List<Episode> knownEpisodes = this.Cache.GetLastParsedEpisodes();
        List<Episode> allNewEpisodes = new ArrayList<>();

        int knownEpisodesCount = 0;
        for (int i = 0; i < 10; i++) {
            // no episodes available
            if (!this.EpisodesProvider.HasNextEpisodes()) {
                break;
            }

            List<Episode> foundEpisodes = this.EpisodesProvider.GetNextEpisodes();
            List<Episode> newEpisodes = foundEpisodes.stream()
                    .filter(x -> !knownEpisodes.contains(x))
                    .collect(Collectors.toList());
            allNewEpisodes.addAll(newEpisodes);

            knownEpisodesCount += foundEpisodes.size() - newEpisodes.size();

            // no episodes found
            if (newEpisodes.isEmpty()) {
                break;
            }

            // all known episodes were found
            if (!knownEpisodes.isEmpty() && knownEpisodes.size() == knownEpisodesCount) {
                break;
            }
        }

        this.Reporter.NotifyNewEpisodes(allNewEpisodes);

        // save the last 20 elements to the cache
        final int cacheSize = 20;
        if (allNewEpisodes.size() < cacheSize) {
            for (int i = 0; i < knownEpisodes.size() && allNewEpisodes.size() < cacheSize; i++) {
                allNewEpisodes.add(knownEpisodes.get(i));
            }
        }

        if (!allNewEpisodes.isEmpty()) {
            this.Cache.SetLastParsedEpisodes(allNewEpisodes.subList(0, allNewEpisodes.size() < cacheSize ? allNewEpisodes.size() : cacheSize));
        }
    }
}
