<%@ page info="com.mooreb.config create property page" %>

<p><a href="/config/properties/help.jsp" target="_blank">Help</a></p>
<form action="/config/properties/create_receiver.jsp" method="POST" enctype="application/x-www-form-urlencoded">
<table>
  <tr><td>Input a property name:</td>                   <td><input type="text" name="propertyName" size="64"></td></tr>
  <tr><td>Input the context:</td>                       <td><input type="text" name="context"      size="64"></td></tr>
  <tr><td>Input the value:</td>                         <td><input type="text" name="value"        size="64"></td></tr>
  <tr><td>What is your name?:</td>                      <td><input type="text" name="author"       size="64"></td></tr>
  <tr><td>Why are you creating this new property?:</td> <td><input type="text" name="comments"     size="128"></td></tr>
</table>
<p>
  <input type="submit" value="Create a new property!" />
</form>
<br />
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>