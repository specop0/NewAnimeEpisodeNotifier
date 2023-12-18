# Searches for new anime and sends an e-mail if new found.

## Configuration in JSON file needed

cache entry is a [specop0/LocalRestServer](https://github.com/specop0/LocalRestServer)

reporter entry is a [specop0/MailRestServer](https://github.com/specop0/MailRestServer)

```json
{
    "cache": {
        "ipAddress": "localrestserver",
        "port": 6491,
        "authorization": "secret of LocalRestServer"
    },
    "reporter": {
        "ipAddress": "mailrestserver",
        "port": 6897
    }
}
```