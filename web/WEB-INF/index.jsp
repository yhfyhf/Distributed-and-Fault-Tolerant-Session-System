<%--
  Created by IntelliJ IDEA.
  User: yhf
  Date: 3/17/16
  Time: 21:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" href="data:;base64,=">
    <title>Session Management</title>
    <style>
        span {
            margin-right: 80px;
        }
    </style>
</head>

<body>
<div class="main">
    <p>
        <span>NetID: hy456, yx339</span>
        <span>Session: ${sessionId}</span>
        <span>Version: ${versionNumber}</span>
        <span>Current Date: ${curTime}</span>
    </p>

    <h1>${message}</h1>

    <form method="post" action="">
        <div>
            <input type="submit" value="Replace" name="replace">
            <input type="text" name="message">
        </div>
        <div>
            <input type="submit" value="Refresh" name="refresh">
        </div>
        <div>
            <input type="submit" value="Logout" name="logout">
        </div>
    </form>

    <p>
        <span>Cookie: ${serialCookie}</span>
        <span>Expires: ${expireAt}</span>
    </p>
    <p>The server excuting the request  Server ID: ${localServerId}, Reboot number: ${localServerRebootNum}</p>
    <p>The server where session data found  Server ID: ${exutedServerId}</p>
    <p>Metadata: ${metadata}</p>
    <p>Cookie domain: ${cookieDomain}</p>

</div>
</body>
</html>
