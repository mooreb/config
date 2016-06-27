<%@ page info="com.mooreb.config edit property page" %>
<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>

<% String uuid = request.getParameter("uuid"); %>


<%
        final Property property = DefaultPersistence.INSTANCE.getProperty(uuid);
        if(null == property) {
%>
                <h3>ERROR: could not find property to edit</h3>
<%
        }
        else {
%>

<%
        final String oldPropertyName = property.getPropertyName();
        final String oldContext = property.getContext();
        final String oldValue = property.getValue();
%>

<p><a href="/config/properties/help.jsp" target="_blank">Help</a></p>
<form action="/config/properties/edit_receiver.jsp" method="POST" enctype="application/x-www-form-urlencoded">
  <input type="hidden" name="uuid" value="<%= uuid %>"></td></tr>
  <table>
    <tr><td>Input a property name:</td>                   <td><input type="text" name="newPropertyName" value="<%= oldPropertyName %>" size="64"></td></tr>
    <tr><td>Input the context:</td>                       <td><input type="text" name="newContext"      value="<%= oldContext %>" size="64"></td></tr>
    <tr><td>Input the value:</td>                         <td><input type="text" name="newValue"        value="<%= oldValue %>" size="64"></td></tr>
    <tr><td>What is your name?:</td>                      <td><input type="text" name="newAuthor"       size="64"></td></tr>
    <tr><td>Why are you editing this property?:</td> <td><input type="text" name="newComments"     size="128"></td></tr>
  </table>
  <p>
  <input type="submit" value="Edit property!" />
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