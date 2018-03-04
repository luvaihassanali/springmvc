/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: jetty/9.4.8.v20171121
 * Generated at: 2018-03-04 22:49:18 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.WEB_002dINF.views;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent,
                 org.apache.jasper.runtime.JspSourceImports {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private static final java.util.Set<java.lang.String> _jspx_imports_packages;

  private static final java.util.Set<java.lang.String> _jspx_imports_classes;

  static {
    _jspx_imports_packages = new java.util.HashSet<>();
    _jspx_imports_packages.add("javax.servlet");
    _jspx_imports_packages.add("javax.servlet.http");
    _jspx_imports_packages.add("javax.servlet.jsp");
    _jspx_imports_classes = null;
  }

  private volatile javax.el.ExpressionFactory _el_expressionfactory;
  private volatile org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public java.util.Set<java.lang.String> getPackageImports() {
    return _jspx_imports_packages;
  }

  public java.util.Set<java.lang.String> getClassImports() {
    return _jspx_imports_classes;
  }

  public javax.el.ExpressionFactory _jsp_getExpressionFactory() {
    if (_el_expressionfactory == null) {
      synchronized (this) {
        if (_el_expressionfactory == null) {
          _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
        }
      }
    }
    return _el_expressionfactory;
  }

  public org.apache.tomcat.InstanceManager _jsp_getInstanceManager() {
    if (_jsp_instancemanager == null) {
      synchronized (this) {
        if (_jsp_instancemanager == null) {
          _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
        }
      }
    }
    return _jsp_instancemanager;
  }

  public void _jspInit() {
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
      throws java.io.IOException, javax.servlet.ServletException {

    final java.lang.String _jspx_method = request.getMethod();
    if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method) && !javax.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "JSPs only permit GET POST or HEAD");
      return;
    }

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\r\n");
      out.write("<title>Web Socket Ex</title>\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("\t//Open the web socket connection to the server\r\n");
      out.write("\tvar socketConn = new WebSocket('ws://localhost:8080/socketHandler');\r\n");
      out.write("\t\r\n");
      out.write("\tsocketConn.onopen = function (event) {\r\n");
      out.write("\t\t  socketConn.send(\"Client attempting to connect\"); \r\n");
      out.write("\t};\r\n");
      out.write("\t//send name to server\r\n");
      out.write("\tfunction send() {\r\n");
      out.write("\t\tvar clientMsg = document.getElementById('clientMsg');\r\n");
      out.write("\t\tif (clientMsg.value) {\r\n");
      out.write("\t\t\tsocketConn.send(\"Name:\" + clientMsg.value);\r\n");
      out.write("\t\t\tdocument.getElementById('name').innerHTML = clientMsg.value + \"'s View\";\r\n");
      out.write("\t\t\tclientMsg.value = '';\r\n");
      out.write("\t\t}\r\n");
      out.write("\t}\r\n");
      out.write("\t\r\n");
      out.write("\t//print all clients connected\r\n");
      out.write("\tfunction print() {\r\n");
      out.write("\t\tsocketConn.send(\"Print\");\r\n");
      out.write("\t}\r\n");
      out.write("\t\r\n");
      out.write("\t//proof of connection\r\n");
      out.write("\tfunction proof() {\r\n");
      out.write("\t\tsocketConn.send(\"Proof\");\r\n");
      out.write("\t}\r\n");
      out.write("\t\r\n");
      out.write("\t// print server message to console\r\n");
      out.write("\tsocketConn.onmessage = function(event) {\r\n");
      out.write("\t\tvar serverMsg = document.getElementById('serverMsg');\r\n");
      out.write("\t\tserverMsg.value = event.data;\r\n");
      out.write("\t}\r\n");
      out.write("</script>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");
      out.write("   <h1>Spring MVC WebSocket</h1>\r\n");
      out.write("   <hr />\r\n");
      out.write("   <label id=\"name\">Enter name</label>\r\n");
      out.write("   <br>\r\n");
      out.write("   <textarea rows=\"8\" cols=\"50\" id=\"clientMsg\"></textarea>\r\n");
      out.write("   <br>\r\n");
      out.write("   <button onclick=\"send()\">Send</button>\r\n");
      out.write("   <button onclick=\"print()\">Print</button>\r\n");
      out.write("   <button onclick=\"proof()\">Proof</button>\r\n");
      out.write("   <button onclick=\"window.location.href='/view2'\">View2</button>\r\n");
      out.write("   <br>\r\n");
      out.write("   <label>Dealer</label>\r\n");
      out.write("   <br>\r\n");
      out.write("   <textarea rows=\"8\" cols=\"50\" id=\"serverMsg\" readonly=\"readonly\"></textarea>\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
