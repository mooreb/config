<%@ page info="com.mooreb.config create property receiver page" %>

<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>

<% String propertyName = request.getParameter("propertyName"); %>
<% String context = request.getParameter("context"); %>
<% String value = request.getParameter("value"); %>
<% String author = request.getParameter("author"); %>
<% String comments = request.getParameter("comments"); %>

<% Property property = new Property(propertyName, context, value, author, comments); %>

<% boolean success = DefaultPersistence.INSTANCE.createNewProperty(property); %>
<p>
<% if(!success) { %>
  <h3>ERROR: trouble creating new property. Were one of propertyName/value/author/comments null?</h3>
<% }
   else {
     String uri = property.getUri().toString();
%>
success! the new property can be fetched at:
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