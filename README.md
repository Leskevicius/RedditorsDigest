### Redditors Digest

In order to run it

You must enable admob and firebase analytics, and paste the google-services.json to app/ folder.

Have a reddit account, and create an app to get a client_id and client_secret.
Once you have those, add a string resource:
```
<string name="client_id">CLIENT ID HERE</string>
<string name="client_secret">CLIENT SECRET HERE</string>
```
You must also get an app ad id from firbase and add it as a resource:
```
<string name="app_ad_id">APP AD ID HERE</string>
```
