<html>
<head>
    <title>File Upload</title>
</head>
<body>
    <h1>Distributed File System</h1>
    <div class="messages">
        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
    </div>

    <form method="post" action="${createLink(controller: 'fileSystem', action: 'uploadFile')}" enctype="multipart/form-data">
        <label for="file">Choose a file:</label>
        <input type="file" name="file" id="file" required>
        <br>
        <input type="submit" value="Upload File">
    </form>

    <h2>Uploaded Files:</h2>
    <ul>
        <g:each in="${fileList}" var="file">
            <li>
                ${file.getName()} |
                <a href="${createLink(controller: 'fileSystem', action: 'downloadFile', params: [fileName: file.getName()])}">Download</a>
            </li>
        </g:each>
    </ul>
</body>
</html>
