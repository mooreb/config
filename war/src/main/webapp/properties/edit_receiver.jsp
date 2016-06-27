<%@ page info="com.mooreb.config edit property receiver page" %>
<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>

<% String uuid = request.getParameter("uuid"); %>
<% String newPropertyName = request.getParameter("newPropertyName"); %>
<% String newContext = request.getParameter("newContext"); %>
<% String newValue = request.getParameter("newValue"); %>
<% String newAuthor = request.getParameter("newAuthor"); %>
<% String newComments = request.getParameter("newComments"); %>

<% Property oldProperty = DefaultPersistence.INSTANCE.getProperty(uuid); %>
<% Property newProperty = new Property(uuid, newPropertyName, newContext, newValue, newAuthor, newComments); %>

<% boolean success = DefaultPersistence.INSTANCE.editProperty(oldProperty, newProperty); %>
<p>
<% if(!success) { %>
  <h3>ERROR: trouble editing property. Were one of newPropertyName/newValue/newAuthor/newComments null?</h3>
<% }
   else {
     String uri = newProperty.getUri().toString();
%>
success! the property was edited. It can be fetched at:
<a href="<%= uri %>"><%= uri %></a>
<% } %>
</p>
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>