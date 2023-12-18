package main;

import models.File;
import org.json.JSONObject;
import restservices.ICache;
import restservices.IReporter;
import restservices.Reporter;
import restservices.RestCache;
import webdriver.Gogoanime;
import webdriver.IAnimesProvider;
import webdriver.IEpisodesProvider;
import webdriver.MyAnimeList;

public class Main {

    public static void main(String[] args) {
        String configurationFilename = "/mnt/configuration.json";
        if (args.length > 0) {
            configurationFilename = args[0];
        }

        JSONObject configuration = new JSONObject(File.ReadAllText(configurationFilename));

        JSONObject cacheConfiguration = configuration.getJSONObject("cache");
        ICache cache = new RestCache(
                cacheConfiguration.getString("ipAddress"),
                cacheConfiguration.getInt("port"),
                cacheConfiguration.getString("authorization"));
        IEpisodesProvider episodeProvider = new Gogoanime();
        JSONObject reporterConfiguration = configuration.getJSONObject("reporter");
        IReporter reporter = new Reporter(
                reporterConfiguration.getString("ipAddress"),
                reporterConfiguration.getInt("port"));
        IAnimesProvider animesProvider = new MyAnimeList();
        IReporterController reporterController = new ReporterController(cache, animesProvider, reporter);

        Controller controller = new Controller(cache, episodeProvider, reporterController, animesProvider);
        System.out.println(String.format("Starting controller..."));
        controller.Start();
        System.out.println(String.format("...finished"));
    }
}
