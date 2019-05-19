package restservices;

import java.util.List;
import models.Episode;

public interface IReporter {

    void NotifyNewEpisodes(List<Episode> episodes);
}
