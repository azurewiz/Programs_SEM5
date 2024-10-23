package com.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/session")
public class SessionServlet extends HttpServlet {

  private static final String SESSION_DIR = "../../../hidden_sessions/";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Get or create session ID
    HttpSession session = request.getSession(true);
    String sessionId = session.getId();

    // File to track session data
    File sessionFile = new File(SESSION_DIR + "/." + sessionId);

    // Load or create session data
    if (!sessionFile.exists()) {
      createNewSessionFile(sessionFile, sessionId);
    }

    // Read session file and retrieve stored data
    String userData = readSessionFile(sessionFile);

    // Respond to client
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html><body>");
    out.println("<h1>Session ID: " + sessionId + "</h1>");
    out.println("<h2>Stored Data: " + userData + "</h2>");
    out.println("<form method='POST'>");
    out.println("<label>Enter Data: </label>");
    out.println("<input type='text' name='userData'/>");
    out.println("<input type='submit' value='Update'/>");
    out.println("</form>");
    out.println("</body></html>");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Get session ID
    HttpSession session = request.getSession(false);
    if (session == null) {
      response.getWriter().println("Session expired.");
      return;
    }
    String sessionId = session.getId();
    File sessionFile = new File(SESSION_DIR + "/." + sessionId);

    // Update session data
    String newUserData = request.getParameter("userData");
    updateSessionFile(sessionFile, newUserData);

    // Redirect to show updated session data
    response.sendRedirect("session");
  }

  private void createNewSessionFile(File sessionFile, String sessionId) throws IOException {
    if (!sessionFile.getParentFile().exists()) {
      sessionFile.getParentFile().mkdirs();
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(sessionFile))) {
      writer.write("Session created with ID: " + sessionId);
    }
  }

  private String readSessionFile(File sessionFile) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
      return reader.readLine();
    }
  }

  private void updateSessionFile(File sessionFile, String userData) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(sessionFile))) {
      writer.write(userData);
    }
  }
}
