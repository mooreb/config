<%@ page info="com.mooreb.config delete property receiver page" %>

<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>

<% String uuid = request.getParameter("uuid"); %>
<% String author = request.getParameter("author"); %>
<% String comments = request.getParameter("comments"); %>

<% Property property = DefaultPersistence.INSTANCE.getProperty(uuid); %>

<% boolean success = DefaultPersistence.INSTANCE.deleteProperty(author, comments, property); %>
<p>
<% if(!success) { %>
  <h3>ERROR: trouble deleting property. Were one of author/comments null?</h3>
<% }
   else {
%>
success! the property was deleted.
<% } %>
</p>
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
