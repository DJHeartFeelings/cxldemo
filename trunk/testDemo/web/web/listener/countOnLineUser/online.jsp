<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="java.util.*"%>
<form action="online.jsp" method="post">
�û�����
<input type="text" name="name">
<input type="submit" value="��½">
<a href="logout.jsp">ע��</a>
</form>
<!-- ��session����������û��� -->
<%
	if(request.getParameter("name")!=null)
		session.setAttribute("uname",request.getParameter("name")) ;
%>
<h2>������Ա</h2>
<hr>
<%
	List l = (List)application.getAttribute("allUser");
	for(Object o : l)
		out.println(o);
%>
