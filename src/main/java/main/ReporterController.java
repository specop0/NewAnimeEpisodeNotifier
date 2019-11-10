package main;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import models.Anime;
import models.Episode;
import restservices.ICache;
import restservices.IReporter;
import webdriver.IAnimesProvider;

public class ReporterController implements IReporterController {

    public ReporterController(ICache Cache, IAnimesProvider AnimesProvider, IReporter Reporter) {
        this.Cache = Cache;
        this.AnimesProvider = AnimesProvider;
        this.Reporter = Reporter;
    }

    protected final ICache Cache;
    protected final IAnimesProvider AnimesProvider;
    protected final IReporter Reporter;

    @Override
    public void NotifyNewEpisodes(List<Episode> episodes) {
        if (!episodes.isEmpty()) {
            List<Episode> newEpisodes = this.FilterSeasonAnimes(episodes);
            String subject = String.format("%d new episodes", newEpisodes.size());

            StringBuilder content = new StringBuilder();
            newEpisodes.stream().forEach(x -> {
                content.append(x.GetName());
                content.append(System.lineSeparator());
                content.append(x.GetEpisode());
                content.append(System.lineSeparator());
                content.append(System.lineSeparator());
            });

            List<Episode> seasonAnimeEpisodes = episodes.stream().filter(x -> !newEpisodes.contains(x)).collect(Collectors.toList());
            if (!seasonAnimeEpisodes.isEmpty()) {
                content.append(System.lineSeparator());
                content.append("============================================");
                content.append(System.lineSeparator());
                content.append(String.format("Season animes: %d new epsiodes", seasonAnimeEpisodes.size()));
                content.append(System.lineSeparator());
                content.append(System.lineSeparator());
                
                seasonAnimeEpisodes.stream().forEach(x -> {
                    content.append(x.GetName());
                    content.append(System.lineSeparator());
                    content.append(x.GetEpisode());
                    content.append(System.lineSeparator());
                    content.append(System.lineSeparator());
                });
            }

            this.Reporter.Notify(subject, content.toString());
        }
    }

    public List<Episode> FilterSeasonAnimes(List<Episode> episodes) {
        return this.FilterSeasonAnimes(episodes, true);
    }

    private List<Episode> FilterSeasonAnimes(List<Episode> episodes, boolean firstRun) {
        if (episodes.isEmpty()) {
            return episodes;
        }

        List<Anime> seasonAnimes = this.Cache.GetSeasonAnimes();
        if (seasonAnimes.isEmpty() || !firstRun) {
            seasonAnimes = this.AnimesProvider.GetSeasonAnime();
            this.Cache.SetSeasonAnimes(seasonAnimes);
        }

        if (seasonAnimes.isEmpty()) {
            return episodes;
        }

        Set<String> animeNames = seasonAnimes.stream().map(x -> x.GetName()).collect(Collectors.toSet());

        List<Episode> episodesExceptSeasonAnimes = episodes.stream().filter(x -> !animeNames.contains(x.GetName())).collect(Collectors.toList());

        if (!firstRun) {
            return episodesExceptSeasonAnimes;
        }

        // check if amount of season animes is low and get new season animes
        int seasonAnimesCount = episodes.size() - episodesExceptSeasonAnimes.size();
        if ((episodes.size() >= 5 && seasonAnimesCount == 0)
                || (episodes.size() >= 10 && seasonAnimesCount <= 1)
                || episodes.size() > 2 * seasonAnimesCount && seasonAnimesCount >= 10) {
            return this.FilterSeasonAnimes(episodesExceptSeasonAnimes, false);

        }

        return episodesExceptSeasonAnimes;
    }
}
