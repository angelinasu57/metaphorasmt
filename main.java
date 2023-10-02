import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        String apiKeyMetaphor = "f17e7409-8c44-4189-a2c6-2aa3608bb8b5";
        String apiKeyGooglePlace = "";
        String query = "what are attractions in san diego? ";
        String location = "San Diego";

        OkHttpClient client = new OkHttpClient();

        Request metaphorRequest = new Request.Builder()
                .url("https://metaphor.ai/api/search")
                .addHeader("x-api-key", apiKeyMetaphor)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "{ \"query\": \"" + query + "\", \"numResults\": 10}"))
                .build();

        try {
            Response metaphorresponse = client.newCall(metaphorRequest).execute();

            if (metaphorresponse.isSuccessful()) {
                String responseBody = metaphorresponse.body().string();
 
                List<String> titles = extractTitlesFromResponse(responseBody);

                for (String title : titles) { // trying google api search
                    try {
                    String placesquery = title;
                    String encodedquery = java.net.URLEncoder.encode(placesquery, "UTF-8");
                    String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + encodedQuery + "&key=" + apiKeyGooglePlace;

                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String jsonResponse = response.toString();
                    System.out.println(jsonResponse); // response for that title query

                    connection.disconnect();

                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                }
                
                } else {
                // Handle unsuccessful response
                System.out.println("Metaphor Error: " + metaphorresponse.code());
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> extractTitlesFromResponse(String responseBody) {
        JSONObject jsonobject = new JSONObject(responseBody);
        JSONArray resultsArray = jsonObject.getJSONArray("results");
        List<String> titlesrv = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject resultObject = resultsArray.getJSONObject(i);
            String title = resultObject.getString("title"); // extract titles from each result listed
            // System.out.println("Title: " + title);
            titlesrv.add(title);
        }

    }
}
