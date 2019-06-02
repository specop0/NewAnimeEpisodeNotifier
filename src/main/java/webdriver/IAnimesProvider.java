package webdriver;

import java.util.List;
import models.Anime;

public interface IAnimesProvider {
    
    List<Anime> GetSeasonAnime();
}
