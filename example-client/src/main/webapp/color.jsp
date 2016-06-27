<%@ page info="com.mooreb.config example client example color page" %>

<%@ page import="com.mooreb.config.client.fastproperty.StringProperty" %>
<% final String defaultColor = "blue"; %>
<% final String colorPropertyName = "exampleConfigClient.color"; %>
<% final StringProperty colorProperty = StringProperty.findOrCreate(colorPropertyName, defaultColor); %>

<!DOCTYPE html>
<html>
<body style="background-color:<%= colorProperty.get()%>">

<h1>
  Hello world!<br>
  The default color is <%= defaultColor %> <br>
  The current color is <%= colorProperty.get() %> <br>
  It is controlled by property named <%= colorPropertyName %><br>
  <% final String uuid = colorProperty.getUUID(); %>
  <% if (null == uuid) { %>
    It is currently set at its default.
  <% } else { %>
    It is overridden by the service (uuid= <%= uuid %>)
  <% } %>
  <br>
</h1>
</body>
</html>
