import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class PlaylistTest {

    String userId = "";
    String playListId = "";
    String authToken = "BQApkBt7An80Gb4x1hXWmjrJZpsbF3GgAbK3AFnGxoxDlKvnZkqlR3GoaXyvLavj6qHqNND0ugR53wl3EmkPY6_FEUeEpCTVMZR1MPGJIaJcuRvfcVL2vd5pi_roLcDRiJ3Jd3V6saHcTss6KwspakDXQ_MNs-4GggPm3f3saFh-jFoOVIWHGiaD2_Dy3kUmu6Y5UgwHy3quP6QZd4kjE8Rk-wnEKvQSYwglISxSMNK_29JW3n7Xd3Nn0eaP7q_ELfuWSjijPCMxf-ejQb7zGvSNED8DajTdEbtCbh2M";


    @BeforeMethod
    public void StartUp(){
        RestAssured.baseURI = "https://api.spotify.com/v1";

    }
    @Test
    public void SpotifyApiTest() throws IOException {

        getUserId();
        createPlayList();

        checkPlaylistIsAvailableInAllPlaylists();

        //isPlayListEmpty();

        addtoPlayList(searchTrack("Unforgiven"));

        //assertEquals(addtoPlayList(),isTrackVisibleInPlayList());



        }
    public void getUserId() {
        Response response =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                .when()
                        .get("/me")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();
        userId = response.getBody().jsonPath().getString("id");

        System.out.println("User ID: " + userId);

    }

    public void createPlayList() throws IOException {
        URL file = Resources.getResource("playListBody.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);

        Response playListResponse =
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
                .body(json.toString())
        .when()
                .post("/users/{user_id}/playlists",userId)
        .then()
                .statusCode(201)
                .extract()
                .response();
        playListId = playListResponse.getBody().jsonPath().getString("id");
        System.out.println(playListId);

    }


    public void checkPlaylistIsAvailableInAllPlaylists(){


        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
        .when()
                .get("playlists/{playlist_id}",playListId)
        .then()
                .statusCode(200);

    }

    public void isPlayListEmpty(){


        Response isEmptyResponse =
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
        .when()
                .get("playlists/{playlist_id}/tracks",playListId)
        .then()
                .statusCode(200)
                .extract()
                .response();
        ArrayList arraylist = isEmptyResponse.path("items");
        Boolean answer = arraylist.isEmpty();
        System.out.println(answer);
    }

    public String searchTrack(String trackName){

        Response searchTrackResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("q",trackName )
                        .queryParam("market", "US")
                        .queryParam("limit","1")
                        .queryParam("type", "track")
                .when()
                        .get("search")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList arrayList = searchTrackResponse.path("tracks.items.uri");
        return arrayList.get(0).toString();
    }

    public void addtoPlayList(String trackId){

        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
                .queryParam("playlist_id",playListId)
                .queryParam("uris",trackId)
        .when()
                .post("playlists/{playlist_id}/tracks",playListId)
        .then()
                .statusCode(201);
    }

    public String isTrackVisibleInPlayList(){

        Response isVisibleBody =
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
                .queryParam("playlist_id",playListId)
                .queryParam("market","TR")
                .queryParam("limit","1")
        .when()
                .get("playlists/{playlist_id}/tracks",playListId)
        .then()
                .statusCode(200)
                .extract()
                .response();
        ArrayList array = isVisibleBody.path("tracks.items.uri");
        return array.get(0).toString();

    }

}
