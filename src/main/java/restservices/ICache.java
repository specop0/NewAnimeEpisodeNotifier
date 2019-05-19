package restservices;

import java.util.List;
import models.Episode;

public interface ICache {

    void SetLastParsedEpisodes(List<Episode> episodes);

    List<Episode> GetLastParsedEpisodes();
}
