<%@ page info="com.mooreb.config list auditlog page" %>

<%@ page import="com.mooreb.config.common.AuditLogEntry" %>
<%@ page import="com.mooreb.config.common.Property" %>
<%@ page import="com.mooreb.config.service.DefaultPersistence" %>
<%@ page import="java.util.List" %>

<%!
public String renderBriefProperty(final Property property) {
    if(null == property) {
      return "";
    }
    else {
      return("name=" + property.getPropertyName() + "<br>" + "context=" + property.getContext() + "<br>" + "value=" + property.getValue());
    }
}
%>

<% List<AuditLogEntry> auditLogEntries = DefaultPersistence.INSTANCE.getAuditLog(); %>
<table>
  <tr>
    <th>timestamp</th>
    <th>action</th>
    <th>author</th>
    <th>old</th>
    <th>new</th>
    <th>comments</th>
  </tr>
<% for(final AuditLogEntry ale : auditLogEntries) { %>
  <tr>
    <td>
      <%= ale.getModificationTimestamp() %>
    </td>
    <td>
      <%= ale.getAction() %>
    </td>
    <td>
      <%= ale.getAuthor() %>
    </td>
    <td>
      <%= renderBriefProperty(ale.getOldProperty()) %>
    </td>
    <td>
      <%= renderBriefProperty(ale.getNewProperty()) %>
    </td>
    <td>
      <%= ale.getComments() %>
    </td>
  </tr>
<% } %>
</table>
<br />
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>
