<%@ page info="com.mooreb.config comparator page" %>

<form action="/config/comparator/compare_receiver.jsp" method="POST" enctype="application/x-www-form-urlencoded">
Environment on the left:
<select name="first_dropdown">
  <option value="put your env here">your env 1</option>
  <option value="put your env here">your env 2</option>
  <option value="put your env here" selected> your env 3</option>
</select>

Environment on the right:
<select name="second_dropdown">
  <option value="put your env here">your env 1</option>
  <option value="put your env here">your env 2</option>
  <option value="put your env here" selected> your env 3</option>
</select>
  <input type="submit" value="Compare Environments!" />
</form>
<br />
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>
