package org.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.data.DataParser;


/**
 *
 * @author tgiunipero
 */
public class DataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        HashMap uploadedFiles = (HashMap) session.getAttribute("uploadedFiles");

        // Handle Ajax call for individual matches with a given confidence score.
        // (This request is made by mouseover functionality in prototype 3.)
        if (request.getServletPath().equals("/getMatches")) {

            ArrayList<String> entityPairs = new ArrayList<String>();

            // get request query string
            String queryString = request.getQueryString();

            // if a 'remaining' parameter exists, extract it
            String remaining = "";
            if (request.getParameter("remaining") != null) {
                remaining = request.getParameter("remaining");
                int idx = queryString.indexOf("&remaining=");
                queryString = queryString.substring(0, idx);
            }

            String[] tokens = queryString.split("&");
            entityPairs.addAll(Arrays.asList(tokens));

            // get URIs
            String uriData = "[";
            uriData += DataParser.getURIs(entityPairs);
            if (!remaining.isEmpty()) {
                uriData += ",{\"remaining\":" + remaining + "}]";
            } else {
                uriData += "]";
            }

            try {
                out.print(uriData);

            } finally {
                out.close();
            }

        } else if (request.getServletPath().equals("/parseData")) {

            DataParser dataParser = (DataParser) session.getAttribute("dataParser");

            if (dataParser == null) {

                // initialise the DataParser and attach it to user session
                dataParser = new DataParser();
                session.setAttribute("dataParser", dataParser);
            }

            ArrayList fileNames = new ArrayList();

            // get request query string
            String queryString = request.getQueryString();
            String[] tokens = queryString.split("&");

            // set grouping number
            int groupingNumber = Integer.parseInt(tokens[tokens.length-1]);
            dataParser.setGroupingNumber(groupingNumber);

            // collect file names
            for (int i=0; i<tokens.length-1; i++) {
                fileNames.add(tokens[i]);
            }

            // set confidence score thresholds
            dataParser.setConfScoreThresholds(fileNames, uploadedFiles);

            // parse file data
            dataParser.parseData(fileNames, uploadedFiles);

            // check if two files have been parsed,
            // if so, prepare file comparison data
            if (dataParser.getParsedData().size() == 2) {
                dataParser.generateFileComparisonData();
            }

            // get data
            String confScoreThresholds = dataParser.getConfScoreThresholds();
            String parsedData = dataParser.getSketchDataAsJSON();
            String fileComparisonData = "";

            if (dataParser.getParsedData().size() == 2) {
                fileComparisonData = dataParser.getComparisonData();
            }

            // prepare response
            // send confidence score thresholds and parsed data
            try {
                out.print("[" + confScoreThresholds + ",");
                out.print(parsedData);
                out.flush();

            // send file comparison data if it exists
            if (!fileComparisonData.isEmpty()) {
                out.print("," + fileComparisonData);
                out.flush();
            }

            out.print("]");

            } finally {
                out.close();
            }

        // If any other requests are made that lead to this servlet,
        // respond with an error message
        } else {

            out.close();

            // display error with a sensible message
            response.sendError(response.SC_NOT_FOUND,
                    "Not sure how you got here, but in any event there's no going" +
                    " forward with what you've given me :-(");
        }
    }
}
