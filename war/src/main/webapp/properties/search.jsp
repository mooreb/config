<%@ page info="com.mooreb.config search property page" %>

<form action="/config/properties/search_receiver.jsp" method="POST" enctype="application/x-www-form-urlencoded">
  Input a pattern to match propertyName: <input type="text" name="pattern" size="64">
  <input type="submit" value="Search for a property!" />
</form>
<br />
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>
