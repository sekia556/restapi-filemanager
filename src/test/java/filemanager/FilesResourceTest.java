package filemanager;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

public class FilesResourceTest {

  private static final String baseURL = "http://localhost:8080/filemanager/rest/files";


  // public void shouldGetAlreadyCreatedSchedule() throws Exception {
  // final URL url = new URL(base, "schedule/" + scheduleId);
  // final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());
  // final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
  // assertEquals(200, response.getStatus());
  // final JsonObject jsonObject = readJsonContent(response);
  // assertEquals(scheduleId, jsonObject.getString("id"));
  // assertEquals(TEST_SESSION, jsonObject.getString("sessionId"));
  // assertEquals(TEST_VENUE, jsonObject.getString("venue"));
  // }
  //
  //
  // public void shouldGetScheduledSessionsForVenue() throws Exception {
  // final String microprofile = String.valueOf(20);
  // final String javaeeNext = String.valueOf(40);
  // final String payaraMicro = String.valueOf(60);
  //
  // createScheduledSession(microprofile, "Hilton", String.valueOf(500), LocalDate.now(),
  // LocalTime.now());
  // createScheduledSession(javaeeNext, "Moscone", String.valueOf(600), LocalDate.now(),
  // LocalTime.now());
  // createScheduledSession(payaraMicro, "Hilton", String.valueOf(500), LocalDate.now(),
  // LocalTime.now());
  //
  // final URL url = new URL(base, "schedule/venue/500");
  // final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());
  // final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
  // assertEquals(200, response.getStatus());
  // final JsonArray jsonArray = readJsonArray(response);
  // assertEquals(2, jsonArray.size());
  // }
  //
  // @Test
  // public void shouldGetActiveScheduledSessions() throws Exception {
  // final String webServices = String.valueOf(100);
  // final String designPatterns = String.valueOf(120);
  // final String java = String.valueOf(140);
  //
  // createScheduledSession(webServices, "Moscone", String.valueOf(600), LocalDate.of(1995, 9, 20),
  // LocalTime.of(16, 0));
  // createScheduledSession(designPatterns, "Moscone", String.valueOf(600),
  // LocalDate.of(1995, 9, 20), LocalTime.of(17, 0));
  // createScheduledSession(java, "Moscone", String.valueOf(600), LocalDate.of(1995, 9, 21),
  // LocalTime.of(16, 0));
  //
  // final URL url = new URL(base, "schedule/active/" + LocalDateTime.of(1995, 9, 20, 17, 34, 29));
  // final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());
  // final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
  // assertEquals(200, response.getStatus());
  // final JsonArray jsonArray = readJsonArray(response);
  // assertEquals(1, jsonArray.size());
  // }
  //
  // @Test
  // public void shouldGetScheduledSessionsByDate() throws Exception {
  // final URL url = new URL(base, "schedule/all/" + LocalDate.of(1995, 9, 20));
  // final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());
  // final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
  // assertEquals(200, response.getStatus());
  // final JsonArray jsonArray = readJsonArray(response);
  // assertEquals(2, jsonArray.size());
  // }
  //
  // @Test
  // public void shouldRemoveSchedule() throws Exception {
  // final String removeMe = String.valueOf(200);
  // final Response createResponse = createScheduledSession(removeMe, "Far far away",
  // String.valueOf(666), LocalDate.now(), LocalTime.now());
  // final String removeId = getScheduleId(createResponse);
  //
  // final URL url = new URL(base, "schedule/" + removeId);
  // final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());
  // final Response deleteResponse = target.request(MediaType.APPLICATION_JSON_TYPE).delete();
  // assertEquals(204, deleteResponse.getStatus());
  //
  // final URL checkUrl = new URL(base, "schedule/" + removeId);
  // final WebTarget checkTarget = ClientBuilder.newClient().target(checkUrl.toExternalForm());
  // final Response checkResponse = checkTarget.request(MediaType.APPLICATION_JSON_TYPE).get();
  // assertEquals(404, checkResponse.getStatus());
  // }

  private static JsonObject readJsonContent(final Response response) {
    final JsonReader jsonReader = readJsonStringFromResponse(response);
    return jsonReader.readObject();
  }

  private static JsonArray readJsonArray(final Response response) {
    final JsonReader jsonReader = readJsonStringFromResponse(response);
    return jsonReader.readArray();
  }

  private static JsonReader readJsonStringFromResponse(final Response response) {
    final String competitionJson = response.readEntity(String.class);
    final StringReader stringReader = new StringReader(competitionJson);
    return Json.createReader(stringReader);
  }

  private static JsonObject getJsonObject(Response response) {
    String jsonStr = response.readEntity(String.class);
    StringReader reader = new StringReader(jsonStr);
    return Json.createReader(reader).readObject();
  }


  @Test
  public void shouldCreateReadDeleteFile() throws IOException {
    final String filename = "test.txt";
    File file = createTestFile(filename);

    //prepare: the test file doesn't exist
    deleteFile(filename);

    //register the file
    Response res1 = postFile(file);
    System.out.println("res1=" + res1);
    assertEquals(200, res1.getStatus());

    //confirm the file is in a file list
    Response res2 = getFiles();
    System.out.println("res2=" + res2);
    assertEquals(200, res2.getStatus());
    JsonObject jsonObj1 = getJsonObject(res2);
    System.out.println("res2 json=" + jsonObj1);
    assertEquals(true, jsonObj1.containsKey(filename));
    // assertEquals(filename, jsonObject.getString("id"));

    Response res3 = getFile(filename);
    System.out.println("res3=" + res3);
    assertEquals(200, res3.getStatus());
    System.out.println(res3.readEntity(File.class));
    // java.nio.file.Files.isSameFile(path, path2)


    
    Response res4 = getFile("no-exists.txt");
    System.out.println("res4=" + res4);
    assertEquals(404, res4.getStatus());

    Response res5 = deleteFile(filename);
    System.out.println("res5=" + res5);
    assertEquals(204, res5.getStatus());

    Response res6 = deleteFile(filename);
    System.out.println("res6=" + res6);
    assertEquals(404, res6.getStatus());

    Response res7 = getFiles();
    System.out.println("res7=" + res7);
    assertEquals(200, res7.getStatus());
    JsonObject jsonObj7 = getJsonObject(res7);
    System.out.println("res7 json=" + jsonObj7);
    assertEquals(false, jsonObj7.containsKey(filename));
  }

  private File createTestFile(String filename) throws IOException {
    File file = new File(filename);
    if (file.exists()) {
      return file;
    } else {
      file.createNewFile();
    }

    try (FileWriter filewriter = new FileWriter(file);) {
      filewriter.write("This is a test file!!");
      return file;
    } catch (IOException e) {
      throw e;
    }
  }


  private Response getFiles() throws MalformedURLException {
    final URL url = new URL(baseURL);
    final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());

    return target.request(MediaType.APPLICATION_JSON_TYPE).get();
  }

  private Response getFile(final String id) throws MalformedURLException {
    final URL url = new URL(baseURL + "/" + id);
    final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());

    return target.request(MediaType.APPLICATION_OCTET_STREAM_TYPE).get();
  }

  private Response postFile(final File file) throws MalformedURLException {

    Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
    WebTarget webTarget = client.target(baseURL);
    MultiPart multiPart = new MultiPart();
    multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

    FileDataBodyPart fileDataBodyPart =
        new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
    multiPart.bodyPart(fileDataBodyPart);

    Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(multiPart, multiPart.getMediaType()));

    System.out.println(response.getStatus() + " " + response.getStatusInfo() + " " + response);


    return response;
  }

  private Response deleteFile(final String id) throws MalformedURLException {
    final URL url = new URL(baseURL + "/" + id);
    final WebTarget target = ClientBuilder.newClient().target(url.toExternalForm());

    return target.request(MediaType.APPLICATION_JSON_TYPE).delete();
  }

}
