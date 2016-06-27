<%@ page info="com.mooreb.config delete property page" %>
<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>

<% String uuid = request.getParameter("uuid"); %>


<%
        final Property property = DefaultPersistence.INSTANCE.getProperty(uuid);
        if(null == property) {
%>
                <h3>ERROR: could not find property to delete</h3>
<%
        }
        else {
%>

<form action="/config/properties/delete_receiver.jsp" method="POST" enctype="application/x-www-form-urlencoded">
  <input type="hidden" name="uuid" value="<%= uuid %>"></td></tr>
  <table>
    <tr><td>What is your name?:</td>                  <td><input type="text" name="author"       size="64"></td></tr>
    <tr><td>Why are you deleting this property?:</td> <td><input type="text" name="comments"     size="128"></td></tr>
  </table>
  <p>
  <input type="submit" value="Delete property!" />
</form>

<% } %>
<br />
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>