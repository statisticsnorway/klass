# HTTP Client tests

This folder contains files for Jetbrains' 
[HTTP Client plugin](https://www.jetbrains.com/help/idea/2023.1/http-client-in-product-code-editor.html). With these 
you can create, edit, and execute HTTP requests directly in the IntelliJ IDEA code editor.

To be able to run these requests you should have an environment file called `http-client.env.json` 
inside this folder. The file should have
the following structure:

```
{
  "local": {
    "base_url": "http://localhost:8080/api/klass/v1",
    "other": "..."
  },
  "prod": {
    "base_url": "https://data.ssb.no/api/klass/v1",
    "other": "..."
}
```
