<%@ page info="com.mooreb.config comparator receiver page" %>

<%@ page import="com.mooreb.configcomparator.HTMLGenerator" %>

<% String first_url = request.getParameter("first_dropdown"); %>
<% String second_url = request.getParameter("second_dropdown"); %>
<%= HTMLGenerator.generate_html_diff(first_url, second_url, false) %>

<br />
<hr>
<br />
<a href="/config/index.jsp">back home</a>
<br />
<jsp:include page="/includes/footer.jsp" />
</body>
</html>
