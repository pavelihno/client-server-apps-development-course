<!DOCTYPE html>
<html>
<head>
    <title>List of Stocks</title>
</head>
<body>
    <h1>Search by Company Name</h1>
    
    <form action="${createLink(controller: 'stock', action: 'search')}" method="get">
        <label for="companyName">Company Name:</label>
        <input type="text" name="companyName" id="companyName">
        <button type="submit">Search</button>
    </form>

    <h2>Search Results</h2>
    <table>
        <tr>
            <th>Company Name</th>
            <th>Price</th>
        </tr>
        <g:each in="${stocks}" var="stock">
            <tr>
                <td>${stock.companyName}</td>
                <td>${stock.price}</td>
            </tr>
        </g:each>
    </table>
</body>
</html>