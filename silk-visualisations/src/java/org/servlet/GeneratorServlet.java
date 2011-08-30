package org.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.alignment.AlignmentFileWriter;

/**
 *
 * @author tgiunipero
 */
public class GeneratorServlet extends HttpServlet {

	private static final long serialVersionUID = -3668718128910810851L;
	private static final String DESTINATION_DIR = System.getProperty("user.home") +
                                                      System.getProperty("file.separator") +
                                                      "alignmentOutput" +
                                                      System.getProperty("file.separator");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // get request parameters
        String outputFile1 = request.getParameter("name1");
        String linkQuantity1 = request.getParameter("links1");
        String outputFile2 = request.getParameter("name2");
        String linkQuantity2 = request.getParameter("links2");
        String numSharedLinks = request.getParameter("sharedLinks");
        String numEqualConfScores = request.getParameter("equalConfScores");

        // write files to disk
        boolean success = AlignmentFileWriter.writeToDisk(outputFile1,
                                                          outputFile2,
                                                          linkQuantity1,
                                                          linkQuantity2,
                                                          numSharedLinks,
                                                          numEqualConfScores,
                                                          DESTINATION_DIR);

        // if all is well, redirect to the file generator page,
        // otherwise a generic status 500 message will appear
        if (success) {
            response.sendRedirect("index.html");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}