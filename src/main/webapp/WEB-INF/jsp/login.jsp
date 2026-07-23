<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bank Login</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f7f6; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-card { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); width: 350px; text-align: center; }
        .login-card h2 { margin-bottom: 25px; color: #004d99; }
        .form-group { margin-bottom: 20px; text-align: left; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #333; }
        input[type="text"], input[type="password"] { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn { width: 100%; padding: 12px; background: #004d99; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; }
        .btn:hover { background: #003366; }
        .alert { color: red; background: #ffe6e6; padding: 10px; border-radius: 4px; margin-bottom: 15px; border: 1px solid red; }
        .success { color: green; background: #e6ffe6; padding: 10px; border-radius: 4px; margin-bottom: 15px; border: 1px solid green; }
    </style>
</head>
<body>

<div class="login-card">
    <h2>Enterprise Banking</h2>

    <c:if test="${param.error != null}">
        <div class="alert">Invalid Account Number or Password.</div>
    </c:if>
    <c:if test="${param.logout != null}">
        <div class="success">You have been logged out successfully.</div>
    </c:if>

    <form action="/login" method="post">
        <div class="form-group">
            <label>Account Number (Username)</label>
            <!-- The name attribute MUST be "username" -->
            <input type="text" name="username" required placeholder="Enter Account Number" />
        </div>
        <div class="form-group">
            <label>Password</label>
            <!-- The name attribute MUST be "password" -->
            <input type="password" name="password" required placeholder="Enter Password" />
        </div>
        <button type="submit" class="btn">Secure Login</button>
    </form>
</div>

</body>
</html>