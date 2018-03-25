package filemanager;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
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

  @Test
  public void shouldCreateReadDeleteFile() throws IOException {
    final String filename = "test.txt";
    File file = createTestFile(filename);

    // prepare: the test file must not exist
    deleteFile(filename);

    // register the file
    Response res1 = postFile(file);
    System.out.println("res1=" + res1);
    JsonObject jsonObj1 = getJsonObject(res1);
    System.out.println("res1 json=" + jsonObj1);
    assertEquals(200, res1.getStatus());
    assertEquals(filename, jsonObj1.getString("id"));

    // the file name must be in a file list
    Response res2 = getFiles();
    System.out.println("res2=" + res2);
    assertEquals(200, res2.getStatus());
    JsonObject jsonObj2 = getJsonObject(res2);
    System.out.println("res2 json=" + jsonObj2);
    assertEquals(true, jsonObj2.containsKey(filename));

    // a file get from the server must be the same as the registered
    Response res3 = getFile(filename);
    System.out.println("res3=" + res3);
    assertEquals(200, res3.getStatus());
    File tmpFile3 = res3.readEntity(File.class);
    System.out.println(tmpFile3);
    assertEquals(file.length(), tmpFile3.length());


    // a file which doesn't exist must not be got
    Response res4 = getFile("no-exists.txt");
    System.out.println("res4=" + res4);
    assertEquals(404, res4.getStatus());

    // a file must be deleted
    Response res5 = deleteFile(filename);
    System.out.println("res5=" + res5);
    assertEquals(204, res5.getStatus());

    // a file which doesn't exist must be deleted
    Response res6 = deleteFile(filename);
    System.out.println("res6=" + res6);
    assertEquals(404, res6.getStatus());

    // a file list doen't contain the file which has been deleted
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


  private static JsonObject getJsonObject(Response response) {
    String jsonStr = response.readEntity(String.class);
    StringReader reader = new StringReader(jsonStr);
    return Json.createReader(reader).readObject();
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
