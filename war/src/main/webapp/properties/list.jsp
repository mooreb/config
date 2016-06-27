<%@ page info="com.mooreb.config list property page" %>

<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>
<%@ page import="java.net.URI" %>
<table>
  <tr>
    <!-- <th>uuid</th> -->
    <th>property name</th>
    <th>context</th>
    <th>value</th>
    <th>last modified</th>
    <th>author</th>
    <th>comments</th>
    <th>links</th>
  </tr>
<% for(final Property property : DefaultPersistence.INSTANCE.getAllProperties()) { %>
<% String uuid = property.getUuid().toString(); %>
  <tr>

<!--
    <td>
      <%= uuid %>
    </td>
-->
    <td>
      <%= property.getPropertyName() %>
    </td>
    <td>
      <%= property.getContext() %>
    </td>
    <td>
      <%= property.getValue() %>
    </td>
    <td>
      <%= property.getLastModified() %>
    </td>
    <td>
      <%= property.getAuthor() %>
    </td>
    <td>
      <%= property.getComments() %>
    </td>
    <td>
      <% URI uri = property.getUri(); %>
      <a href="/config/properties/edit.jsp?uuid=<%= uuid %>">edit</a>
      <a href="/config/properties/delete.jsp?uuid=<%= uuid %>">delete</a>
    </td>
  </tr>
<% } %>
</table>
<p><hr></p>
<p><a href="/config/index.jsp">back home</a></p>
<jsp:include page="/includes/footer.jsp" />
</body>
</html>