/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhg.scai.rp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author TCKB
 */
@WebServlet(name = "RPUpload", urlPatterns = {"/RPUpload"})
@MultipartConfig(fileSizeThreshold = 1024 * 10240, maxFileSize = 1024 * 10240 * 20, maxRequestSize = 1024 * 10240 * 5 * 5, location = "/Users/tckb/Desktop")
public class RPUpload extends HttpServlet {

    String fileType = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        HttpSession sess = req.getSession();

        String fileText = retrieveFile(req);

        File tempFile = File.createTempFile("inputFile", fileType);
        tempFile.createNewFile();
        saveTextToFile(fileText, tempFile.getAbsolutePath());

        sess.setAttribute("type", fileType);
        out.println("file uploaded in server at : "+tempFile.getAbsolutePath());

    }

    /**
     *
     * @return 1 - for simple text file, 2- for pdf file, -1 if illegal file
     *
     */
    private String getFileType(Part part) {
        System.out.println("part name:" + part.getName());

        String acceptedFilefileTypes[] = new String[]{"chemical/x-mdl-sdfile", "text/plain"};
        String partfileType = part.getContentType();
        if (partfileType.equalsIgnoreCase(acceptedFilefileTypes[1])) {
            return ".txt";
        }

        if (partfileType.equalsIgnoreCase(acceptedFilefileTypes[0])) {
            return ".sdf";
        }
        return null;
    }

    private String retrieveFile(HttpServletRequest req) {
        StringBuilder str = new StringBuilder();

        try {

            Part part = req.getPart("file");

            
            fileType = getFileType(part);

            InputStream is = part.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            char[] buf = new char[10];

            while ((br.read(buf)) != -1) {
                str.append(buf);
            }
            is.close();
            br.close();

            Collection<Part> parts = req.getParts();
            StringBuilder sb = new StringBuilder();

            for (Part p : parts) {

                if (!"iFile".equals(p.getName())) {
                    Scanner scanner = new Scanner(p.getInputStream());

                    while (scanner.hasNext()) {
                        sb.append(scanner.nextLine()).append(":");
                    }

                }

            }
            req.getSession().setAttribute("aEngine", sb.toString());

        } catch (IOException | ServletException ex) {
            Logger.getLogger(RPUpload.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return str.toString();

        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RPUpload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RPUpload at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void saveTextToFile(String text, String fname) throws IOException {

        FileWriter fr = new FileWriter(fname);
        fr.write(text);

        fr.close();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
