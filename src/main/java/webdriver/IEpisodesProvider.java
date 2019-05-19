package webdriver;

import java.util.List;
import models.Episode;

public interface IEpisodesProvider {

    boolean HasNextEpisodes();

    List<Episode> GetNextEpisodes();
}
