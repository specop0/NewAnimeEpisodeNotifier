package main;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import models.Episode;
import restservices.ICache;
import webdriver.IAnimesProvider;
import webdriver.IEpisodesProvider;

public class Controller {

    public Controller(ICache cache, IEpisodesProvider episodesProvider, IReporterController reporter, IAnimesProvider animesProvider) {
        this.Cache = cache;
        this.EpisodesProvider = episodesProvider;
        this.Reporter = reporter;
        this.AnimesProvider = animesProvider;
    }

    protected ICache Cache;
    protected IEpisodesProvider EpisodesProvider;
    protected IReporterController Reporter;
    protected IAnimesProvider AnimesProvider;

    public void Start() {
        List<Episode> knownEpisodes = this.Cache.GetLastParsedEpisodes();
        HashSet<Episode> allEpisodes = new HashSet<>();
        HashSet<Episode> newEpisodes = new HashSet<>();

        int knownEpisodesCount = 0;
        for (int i = 0; i < 10; i++) {
            // no episodes available
            if (!this.EpisodesProvider.HasNextEpisodes()) {
                break;
            }

            List<Episode> foundEpisodes = this.EpisodesProvider.GetNextEpisodes();

            // no episodes found
            if (foundEpisodes.isEmpty()) {
                break;
            }
            allEpisodes.addAll(foundEpisodes);

            // filter new episodes until all are known
            for (int j = 0; j < foundEpisodes.size(); j++) {

                // all known episodes were found
                if (!knownEpisodes.isEmpty() && knownEpisodes.size() == knownEpisodesCount) {
                    break;
                }

                Episode foundEpisode = foundEpisodes.get(j);

                if (knownEpisodes.contains(foundEpisode)) {
                    knownEpisodesCount++;
                } else {
                    newEpisodes.add(foundEpisode);
                }

            }

            // all known episodes were found
            if (!knownEpisodes.isEmpty() && knownEpisodes.size() == knownEpisodesCount) {
                break;
            }
        }

        this.Reporter.NotifyNewEpisodes(newEpisodes);

        // save the last 200 latests episodes to the cache
        List<Episode> latestEpisodes = Stream.concat(allEpisodes.stream(), knownEpisodes.stream())
                .distinct()
                .limit(200)
                .collect(Collectors.toList());
        if (!latestEpisodes.isEmpty()) {
            this.Cache.SetLastParsedEpisodes(latestEpisodes);
        }
    }
}
