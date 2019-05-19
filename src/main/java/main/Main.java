package main;

import models.File;
import org.json.JSONObject;
import restservices.ICache;
import restservices.IReporter;
import restservices.Reporter;
import restservices.RestCache;
import webdriver.Gogoanime;
import webdriver.IEpisodesProvider;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            JSONObject configuration = new JSONObject(File.ReadAllText(args[0]));

            JSONObject cacheConfiguration = configuration.getJSONObject("cache");
            ICache cache = new RestCache(cacheConfiguration.getInt("port"), cacheConfiguration.getString("authorization"));
            IEpisodesProvider episodeProvider = new Gogoanime();
            JSONObject reporterConfiguration = configuration.getJSONObject("reporter");
            IReporter reporter = new Reporter(reporterConfiguration.getInt("port"));

            Controller controller = new Controller(cache, episodeProvider, reporter);
            controller.Start();
        } else {
            throw new IllegalArgumentException("No configuration file provided");
        }
    }
}
