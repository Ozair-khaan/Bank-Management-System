<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fund Transfer</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f7f6; }
        .card { background: white; padding: 30px; border-radius: 8px; max-width: 500px; margin: auto; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="number"] { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn { width: 100%; padding: 12px; background: #004d99; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .success { color: green; padding: 10px; background: #e6ffe6; border: 1px solid green; margin-bottom: 15px; }
        .error { color: red; padding: 10px; background: #ffe6e6; border: 1px solid red; margin-bottom: 15px; }
        .back-link { display: block; text-align: center; margin-top: 15px; text-decoration: none; color: #004d99; }
    </style>
</head>
<body>

<div class="card">
    <h2>Initiate Transfer</h2>

    <c:if test="${not empty successMessage}">
        <div class="success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="error">${errorMessage}</div>
    </c:if>

    <form action="/app/transfer" method="post">
        <div class="form-group">
            <label>From Account:</label>
            <input type="text" value="${fromAccount}" disabled />
        </div>
        <div class="form-group">
            <label>To Account Number:</label>
            <input type="text" name="toAccount" required placeholder="e.g., ACC999888777" />
        </div>
        <div class="form-group">
            <label>Amount ($):</label>
            <input type="number" name="amount" step="0.01" required placeholder="0.00" />
        </div>
        <div class="form-group">
            <label>Description / Purpose:</label>
            <input type="text" name="description" required placeholder="e.g., Invoice Payment" />
        </div>
        <button type="submit" class="btn">Confirm Transfer</button>
    </form>

    <a href="/app/dashboard" class="back-link">Return to Dashboard</a>
</div>

</body>
</html>