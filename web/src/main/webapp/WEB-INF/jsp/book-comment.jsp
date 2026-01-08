<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="sumdu.edu.ua.core.domain.Book" %>
<%@ page import="sumdu.edu.ua.core.domain.Comment" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Відгуки про книгу</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 2rem; max-width: 800px; }
        h1 { color: #333; }
        .book-info {
            background: #f5f5f5;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 2rem;
        }
        .back-link { display: inline-block; margin-bottom: 1rem; }
        .comment-form {
            background: #fff;
            border: 2px solid #0066cc;
            padding: 1.5rem;
            border-radius: 4px;
            margin-bottom: 2rem;
        }
        .form-group { margin-bottom: 1rem; }
        .form-group label { display: block; margin-bottom: 0.5rem; font-weight: bold; }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .form-group textarea { min-height: 100px; resize: vertical; }
        button {
            background: #0066cc;
            color: white;
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
        }
        button:hover { background: #0052a3; }
        .comments-list { list-style: none; padding: 0; }
        .comment-item {
            border: 1px solid #ddd;
            padding: 1rem;
            margin-bottom: 1rem;
            border-radius: 4px;
        }
        .comment-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }
        .comment-author { font-weight: bold; color: #0066cc; }
        .comment-date { color: #999; font-size: 0.9em; }
        .comment-text { color: #333; line-height: 1.5; }
        .delete-btn {
            background: #dc3545;
            color: white;
            padding: 0.25rem 0.75rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9em;
            margin-top: 0.5rem;
        }
        .delete-btn:hover { background: #c82333; }
    </style>
</head>
<body>
<a href="<%= request.getContextPath() %>/books" class="back-link">← Назад до каталогу</a>

<%
    Book book = (Book) request.getAttribute("book");
    if (book == null) {
%>
<p>Книгу не знайдено.</p>
<%
} else {
%>
<div class="book-info">
    <h1><%= book.getTitle() %></h1>
    <p><strong>Автор:</strong> <%= book.getAuthor() %></p>
    <p><strong>Рік видання:</strong> <%= book.getPubYear() %></p>
</div>

<div class="comment-form">
    <h2>Додати відгук</h2>
    <form method="post" action="<%= request.getContextPath() %>/comments">
        <input type="hidden" name="bookId" value="<%= book.getId() %>">
        <div class="form-group">
            <label for="author">Ваше ім'я:</label>
            <input type="text" id="author" name="author" required maxlength="64">
        </div>
        <div class="form-group">
            <label for="text">Ваш відгук:</label>
            <textarea id="text" name="text" required maxlength="1000"></textarea>
        </div>
        <button type="submit">Надіслати відгук</button>
    </form>
</div>

<h2>Відгуки</h2>
<%
    @SuppressWarnings("unchecked")
    List<Comment> comments = (List<Comment>) request.getAttribute("comments");

    if (comments == null || comments.isEmpty()) {
%>
<p>Відгуків поки немає. Будьте першим!</p>
<%
} else {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
%>
<ul class="comments-list">
    <%
        for (Comment comment : comments) {
    %>
    <li class="comment-item">
        <div class="comment-header">
            <span class="comment-author"><%= comment.getAuthor() %></span>
            <span class="comment-date"><%= formatter.format(comment.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()) %></span>
        </div>
        <div class="comment-text"><%= comment.getText() %></div>

        <form method="post" action="<%= request.getContextPath() %>/comments" style="display: inline;">
            <input type="hidden" name="_method" value="delete">
            <input type="hidden" name="bookId" value="<%= book.getId() %>">
            <input type="hidden" name="commentId" value="<%= comment.getId() %>">
            <input type="hidden" name="createdAt" value="<%= comment.getCreatedAt().toString() %>">
            <button type="submit" class="delete-btn"
                    onclick="return confirm('Видалити цей відгук?')">
                🗑️ Видалити
            </button>
        </form>
    </li>
    <%
        }
    %>
</ul>
<%
    }
%>
<%
    }
%>
</body>
</html>