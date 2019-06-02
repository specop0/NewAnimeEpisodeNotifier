package main;

import java.util.List;
import models.Episode;

public interface IReporterController {
    
    void NotifyNewEpisodes(List<Episode> episodes);
}
