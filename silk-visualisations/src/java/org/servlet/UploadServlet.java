package org.servlet;

//import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.alignment.AlignmentFile;
import org.alignment.AlignmentParser;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;


/**
 *
 * @author tgiunipero
 */
public class UploadServlet extends HttpServlet {

    private File applicationDir;
    private File destinationDir;
    private File tmpDir;

    private static final int SIZE_THRESHOLD = (5 * 1024 * 1024);        //   5 MB
    private static final long MAX_FILE_SIZE = (1000 * 1024 * 1024);     // 100 MB
    private static final long MAX_REQUEST_SIZE = (2000 * 1024 * 1024);  // 200 MB
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String APPLICATION_DIR_PATH = System.getProperty("user.home") +
                                                       FILE_SEPARATOR + ".silk-visualisations";
    private static final String DESTINATION_DIR_PATH = APPLICATION_DIR_PATH +
                                                       FILE_SEPARATOR + "data";
    private static final String TMP_DIR_PATH = APPLICATION_DIR_PATH +
                                                       FILE_SEPARATOR + "temp";

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        boolean status;

// WRITING FILE TO DISK DISABLED
//
//        // create hidden application folder in user's home directory
//        applicationDir = new File(APPLICATION_DIR_PATH);
//        if (!applicationDir.exists()) {
//            status = applicationDir.mkdir();
//            if (!status) {
//                throw new ServletException("Unable to create '.silk-visualisations'" +
//                                           " folder in user directory");
//            }
//        }
//
//        // create folder for temporary files
//        tmpDir = new File(TMP_DIR_PATH);
//        if (!tmpDir.exists()) {
//            status = tmpDir.mkdir();
//            if (!status) {
//                throw new ServletException("Unable to create 'temp' folder");
//            }
//        }
//
//        // create folder for data
//        destinationDir = new File(DESTINATION_DIR_PATH);
//        if (!destinationDir.exists()) {
//            status = destinationDir.mkdir();
//            if (!status) {
//                throw new ServletException("Unable to create 'data' folder");
//            }
//        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { }

    /*
     * This method returns an HTTP status 200 message to the client
     * regardless of whether the file is successfully deleted on the
     * server-side. This enables the client UI to function as anticipated.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        HttpSession session = request.getSession();
        HashMap uploadedFiles = (HashMap) session.getAttribute("uploadedFiles");

        String fileName = request.getParameter("file");

        // Remove file from uploadedFiles
        if (uploadedFiles.containsKey(fileName)) {

            uploadedFiles.remove(fileName);

        } else {
            log("Strangely, " + fileName + " was not found in the uploadedFiles HashMap");
        }

        // Delete file if it has been saved to disk
        if (fileName != null) {

            File goner = new File(DESTINATION_DIR_PATH + FILE_SEPARATOR + fileName);

            boolean success = goner.delete();
            if (!success) {
                log("Unable to delete file from disk: " + fileName);
            }
        }
    }

//    EXPERIMENTAL - Use of the PUT method is currently not used
//
//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//
//        InputStream stream = request.getInputStream();
//        InputStreamReader isr = new InputStreamReader(stream);
//        BufferedReader br = new BufferedReader(isr);
//
//        String line;
//        String fileName = "";
//
//        // relies on Content-Disposition header displayed inline
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//            if (line.contains("filename")) {
//                int start = line.indexOf("filename") + 10;
//                int end = line.indexOf("\"", start);
//                fileName = line.substring(start, end);
//                break;
//            }
//        }
//
//        AlignmentFile alignmentFile = AlignmentParser.parse(stream);
//
//        // prepare response
//        try {
//            out.write("[{\"name\":\"" + fileName + "\",\"size\": \"\"" +
//                      ",\"url\":\"\",\"thumbnail_url\":\"\",\"delete_url\":\"delete?file=" +
//                      "output1.xml" + "\",\"delete_type\":\"DELETE\"},");
//
////            out.write("{\"name\":\"" + "output2.xml" + "\",\"size\": \"\"" +
////                      ",\"url\":\"\",\"thumbnail_url\":\"\",\"delete_url\":\"delete?file=" +
////                      "output1.xml" + "\",\"delete_type\":\"DELETE\"},");
//
//            out.write(alignmentFile.getMatchingsAsString(3) + "]");
//        } catch (Exception e) {
//            // display error with a sensible message
//            response.sendError(response.SC_INTERNAL_SERVER_ERROR,
//                               "Unable to parse file");
//        }
//    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");  // ensures that user input is
                                                // interpreted as 8-bit Unicode

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        HashMap uploadedFiles = (HashMap) session.getAttribute("uploadedFiles");

        // check to ensure that this is a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            // create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory(SIZE_THRESHOLD, tmpDir);

            // track temporary files so they may be deleted automatically
            FileCleaningTracker fileCleaningTracker =
                    FileCleanerCleanup.getFileCleaningTracker(request.getServletContext());
            factory.setFileCleaningTracker(fileCleaningTracker);

            // create a new file upload handler
            ServletFileUpload uploadHandler = new ServletFileUpload(factory);

            // set file size constraint
            uploadHandler.setFileSizeMax(MAX_FILE_SIZE);
            // set overall request size constraint
            uploadHandler.setSizeMax(MAX_REQUEST_SIZE);

            try {
                // parse the request
                List items = uploadHandler.parseRequest(request);
                Iterator itr = items.iterator();

                // Begin preparing JSON response for successful uploads
                String fileData = "[";

                // handle the uploaded files
                while (itr.hasNext()) {

                    FileItem item = (FileItem) itr.next();
                    InputStream is = item.getInputStream();
                    AlignmentFile alignmentFile = AlignmentParser.parse(is);

                    // If user is uploading a file for the first time,
                    // create a HashMap and attach it to the user session.
                    // The HashMap will retain data from files uploaded by user.
                    // File names are used as keys; AlignmentFile objects are values.
                    if (uploadedFiles == null) {

                        // initialise the HashMap so that it is ready to receive new files
                        uploadedFiles = new HashMap();
                        session.setAttribute("uploadedFiles", uploadedFiles);
                    }

                    String fileName = getUniqueFileName(item.getName(), uploadedFiles);
                    uploadedFiles.put(fileName, alignmentFile);

// WRITING FILE TO DISK DISABLED
boolean success = true;
//                    // Write file to disk
//                    String location = destinationDir + FILE_SEPARATOR + item.getName();
//                    boolean success = AlignmentFileWriter.writeToDisk(alignmentFile, location);

                    // prepare response
                    if (success) {
                        fileData += "{\"name\":\"" + fileName +
                                    "\",\"size\":" + item.getSize() +
                                    ",\"url\":\"\",\"thumbnail_url\":\"\",\"delete_url\":\"delete?file=" +
                                    fileName + "\",\"delete_type\":\"DELETE\"},";

                    } else {
                        // display error with a sensible message
                        response.sendError(response.SC_INTERNAL_SERVER_ERROR,
                                           "Unable to parse file");
                    }
                }
                // remove final comma
                fileData = fileData.substring(0, fileData.length()-1);
                fileData += "]";

                out.write(fileData);
                out.close();
            } catch (FileUploadException ex) {
                log("Error encountered while parsing the request", ex);
            }
        }
    }


    /*
     * Returns a file name which can be used as a unique identifier.
     *
     * If a file with the same name already exists, append '_1'
     * to the name (or if '_1' already exists, increment the number).
     */
    private String getUniqueFileName(String name, HashMap uploadedFiles) {

        String fileName = name;
        int i, xmlIdx, underScoreIdx;

        while (uploadedFiles.containsKey(fileName)) {
            if (fileName.endsWith(".xml")) {
                xmlIdx = fileName.indexOf(".xml");

                if (fileName.matches(".*_\\d+\\.xml")) {
                    underScoreIdx = fileName.lastIndexOf("_");

                    // Note: if unable to correctly parse file name,
                    // the uploadedFiles HashMap will simply overwrite
                    // a value whose key already exists
                    try {
                        i = Integer.parseInt(fileName.substring(underScoreIdx+1, xmlIdx));
                    } catch (NumberFormatException nfe) {
                        log("Unable to parse file name: " + fileName);
                        return fileName;
                    }

                    i++;
                    fileName = fileName.substring(0, underScoreIdx+1) +
                               String.valueOf(i) +
                               fileName.substring(xmlIdx);

                } else {
                    fileName = fileName.substring(0, xmlIdx) +
                           "_1" + fileName.substring(xmlIdx);
                }

            // else, if file name does not end in '.xml'
            // Note: The client-side does not allow user to proceed if file extension
            // is not '.xml'.  The else clause below is added just in case...
            } else {

                if (fileName.matches(".*_\\d+")) {
                    underScoreIdx = fileName.lastIndexOf("_");

                    try {
                        i = Integer.parseInt(fileName.substring(underScoreIdx+1));
                    } catch (NumberFormatException nfe) {
                        log("Unable to parse file name: " + fileName);
                        return fileName;
                    }

                    i++;
                    fileName = fileName.substring(0, underScoreIdx+1) +
                               String.valueOf(i);
                } else {
                    fileName = fileName.concat("_1");
                }
            }
        }

        return fileName;
    }
}
