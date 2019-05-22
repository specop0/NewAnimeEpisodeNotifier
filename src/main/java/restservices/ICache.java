package restservices;

import java.util.List;
import models.Anime;
import models.Episode;

public interface ICache {

    void SetLastParsedEpisodes(List<Episode> episodes);

    List<Episode> GetLastParsedEpisodes();
    
    void SetSeasonAnimes(List<Anime> animes);
    
    List<Anime> GetSeasonAnimes();
}
