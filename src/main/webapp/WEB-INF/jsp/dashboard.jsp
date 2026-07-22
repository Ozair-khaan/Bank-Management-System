<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Enterprise Banking Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f7f6; }
        .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #004d99; color: white; }
        .btn { display: inline-block; padding: 10px 15px; background: #004d99; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>

<div class="card">
    <h2>Welcome, ${dashboard.accountHolderName}</h2>
    <p><strong>Account Number:</strong> ${dashboard.accountNumber}</p>
    <p><strong>Account Type:</strong> ${dashboard.accountType}</p>
    <h3 style="color: #2e8b57;">Available Balance: $${dashboard.availableBalance}</h3>
    <a href="/app/transfer" class="btn">Initiate Fund Transfer</a>
</div>

<div class="card">
    <h3>Recent Transactions</h3>
    <table>
        <tr>
            <th>Date</th>
            <th>Ref Number</th>
            <th>Type</th>
            <th>Description</th>
            <th>Amount</th>
        </tr>
        <c:forEach var="txn" items="${dashboard.recentTransactions}">
            <tr>
                <td>${txn.date}</td>
                <td>${txn.referenceNumber}</td>
                <td>${txn.type}</td>
                <td>${txn.description}</td>
                <td style="color: ${txn.type == 'TRANSFER_OUT' || txn.type == 'WITHDRAWAL' ? 'red' : 'green'};">
                    $${txn.amount}
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

</body>
</html>