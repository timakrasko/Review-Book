<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="sumdu.edu.ua.core.domain.Book" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Каталог книг</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 2rem; }
        h1 { color: #333; }
        .book-list { list-style: none; padding: 0; }
        .book-item {
            border: 1px solid #ddd;
            margin: 1rem 0;
            padding: 1rem;
            border-radius: 4px;
        }
        .book-item:hover { background-color: #f5f5f5; }
        .book-title { font-size: 1.2em; font-weight: bold; color: #0066cc; }
        .book-author { color: #666; }
        .book-year { color: #999; font-size: 0.9em; }
        a { text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<h1>📚 Каталог книг</h1>

<%
    @SuppressWarnings("unchecked")
    List<Book> books = (List<Book>) request.getAttribute("books");

    if (books == null || books.isEmpty()) {
%>
<p>Книг поки немає.</p>
<%
} else {
%>
<ul class="book-list">
    <%
        for (Book book : books) {
    %>
    <li class="book-item">
        <a href="<%= request.getContextPath() %>/comments?bookId=<%= book.getId() %>">
            <div class="book-title"><%= book.getTitle() %></div>
            <div class="book-author">Автор: <%= book.getAuthor() %></div>
            <div class="book-year">Рік видання: <%= book.getPubYear() %></div>
        </a>
    </li>
    <%
        }
    %>
</ul>
<%
    }
%>
</body>
</html>