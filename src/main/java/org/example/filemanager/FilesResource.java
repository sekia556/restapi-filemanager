package org.example.filemanager;

import static javax.ws.rs.core.Response.Status.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Resource class for /files. This class supplies file create, read and delete.
 *
 * @author sekia
 */
@Path("files")
public class FilesResource {

  private static String filesPath = "./files";

  static {
    // check filesPath existance, and create it if it doesn't exist
    File file = new File(filesPath);
    if (file.exists() && file.isFile()) {
      throw new RuntimeException("files folder cannot be created: " + filesPath);
    }
    if (!file.exists()) {
      if (!file.mkdirs()) {
        throw new RuntimeException("file folder cannot be created partially: " + filesPath);
      }
    }
  }

  /**
   * Get list of files.
   *
   * @return Response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getList() {

    File curDir = new File(filesPath);
    List<File> files = getAllFiles(curDir, new ArrayList<File>());

    JsonObjectBuilder builder = Json.createObjectBuilder();
    for (File f : files) {
      LocalDateTime date =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());
      String datestr = date.format(DateTimeFormatter.ISO_DATE_TIME);

      builder.add(f.getName(),
          Json.createObjectBuilder().add("size", f.length()).add("date", datestr).build());
    }

    JsonObject value = builder.build();

    return Response.ok().entity(value).build();
  }

  // get all files recursively
  private List<File> getAllFiles(File curDir, List<File> files) {

    File[] filesList = curDir.listFiles();
    for (File file : filesList) {
      if (file.isDirectory()) {
        getAllFiles(file, files);
      } else if (file.isFile()) {
        files.add(file);
      }
    }
    return files;
  }


  /**
   * Get a file of {id}.
   *
   * @param id file's ID.
   * @return Response.
   */
  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile(@PathParam("id") String id) {

    File target = new File(Paths.get(filesPath, id).toString());
    if (!target.exists() || !target.isFile()) {
      return Response.status(404).entity("{\"message\": \"Not Found\"}").build();
    }

    return Response.ok(target, MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=\"" + target.getName() + "\"").build();

  }

  /**
   * Post a file with mime-multipart.
   *
   * @param file InputStream of "file" data.
   * @param fileDisposition FormDataContentDisposition of "file" data
   * @return id of the registered file.
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response registerFile(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition) {

    try {
      String fileName = fileDisposition.getFileName();
      java.nio.file.Path filePath = Paths.get(filesPath, fileName);

      if (new File(filePath.toString()).exists()) {
        JsonObject conflictionMsg =
            Json.createObjectBuilder().add("message", fileName + " exists.").build();
        return Response.status(CONFLICT).entity(conflictionMsg).build();
      }

      java.nio.file.Files.copy(file, filePath);

      JsonObject successMsg =
          Json.createObjectBuilder().add("message", "Success").add("id", fileName).build();

      return Response.ok().entity(successMsg).build();

    } catch (IOException e) {
      return Response.serverError().build();
    }
  }

  /**
   * Delete a file of {id}.
   *
   * @param id file's id.
   * @return Response.
   */
  @DELETE
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeFile(@PathParam("id") String id) {

    File target = new File(Paths.get(filesPath, id).toString());
    if (target.exists() && target.isFile()) {
      target.delete();
    } else {
      return Response.status(NOT_FOUND).entity("{\"message\": \"Not Found\"}")
          .build();
    }

    return Response.noContent().build();
  }

}
