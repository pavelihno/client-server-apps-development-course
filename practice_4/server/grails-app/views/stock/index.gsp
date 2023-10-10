<!DOCTYPE html>
<html>
<head>
    <title>List of Stocks</title>
</head>
<body>
    <h1>List of Stocks</h1>
    <table>
        <thead>
            <tr>
                <th>Company Name</th>
                <th>Price</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${stocks}" var="stock">
                <tr>
                    <td>${stock.companyName}</td>
                    <td>${stock.price}</td>
                </tr>
            </g:each>
        </tbody>
    </table>
</body>
</html>
