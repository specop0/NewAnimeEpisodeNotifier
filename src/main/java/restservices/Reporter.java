package restservices;

import org.json.JSONObject;

public class Reporter extends RestBase implements IReporter {

    public Reporter(String ipAddress, int port) {
        this(String.format("http://%s:%d/mail/send", ipAddress, port));
    }

    public Reporter(String url) {
        this.Url = url;
    }

    protected final String Url;

    @Override
    public void Notify(String subject, String content) {
        if (null != subject && null != content) {
            JSONObject data = new JSONObject();
            data.put("subject", subject);
            data.put("content", content);

            this.Do(this.Url, "POST", data);
        }
    }

}
