package main;

import java.util.Collection;
import models.Episode;

public interface IReporterController {
    
    void NotifyNewEpisodes(Collection<Episode> episodes);
}
