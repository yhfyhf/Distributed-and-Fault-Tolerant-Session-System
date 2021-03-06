# A Distributed, Scalable and Fault-tolerant Session System


### Flow

1. The server launches. The server will get the information of all servers from disk, which is retrieved from SimpleDB.
2. User enters the url and send a request. If no cookie is sent with the request, then it is a new session between server and client. The server initializes a new session, saves it in memory, and returns a cookie to client. If the session is already existed specified by cookie value, the server will get session id, version and location metadata from cookie, and then 
tries to retrive it locally. If the session does not exists locally, the server will send ReadSession RPC request to R servers, and wait for just one response, retrieving the session, and bring back the data to user.

	Also, once a session is newly created or updated, the server will send WriteSession RPC request to W other servers, and wait for WQ responses, which means at least W servers duplicate the session. If it receives less than WQ responses when socket timeouts, it returns to the user with a failure page.

3. It a server crashes and reboots, it will update the reboot number stored in the file system, and then send ReadSession RPC requests to other servers. These server information can be accessed from the metadata in the cookie value. After first server responds, it retrieves the duplicated session and sends it back to the client. If no server responds affirmatively, the server initializes a new session and follows the new session flow.
4. Garbage collection. It runs in a separate thread. When the time reaches to the expired time, the session will be deleted in the server.

### Format
**CookieValue:** "sessionId\_\_versionNumber\_\_locationMetadata"

**locationMetadata:** "serverId1,serverId2,..."

**sessionId:** "serverId$rebootnum$sessionnum"

### RPC message format
#### ReadSession 
**request message:** "callID;1;sessionID;versionNumber"

**response message:** "callID;1;sessionID;versionNumber;serverID"

#### WriteSession
**request message:** "callID"

**response message:** "callID;serverID"



# Source File Functionality

### SessionServlet.java
Extends from Servlet that handles all the HTTP requests and responses.

### Session.java
Defines the session class and all the data associated with session.

### SessionCookie.java 
It is a class extends from the Cookie class. It has some additional methods to fulfill this project requirements.

### RPCClient.java
The client for RPC. It implements both reading and writing session from other servers.

### RPCServer.java
The server for RPC. It receives the RPC request from client, handles the RPC logics.

### Group.java 
It is a class to manage and keep track of all servers.

### Server.java 
It is a class to store the server information and functions, the information which can be read from the disk.

### Utils.java 
It has some common used functions which are used by different classes.

### Conf.java 
Defines the configurations used by the whole project.

### rebootnum.txt 
It is a txt file to store the reboot times.

### servers.txt 
It is a txt file to store all the server information.

### NF.txt
Stores values of N and F.

### index.jsp
It is a jsp file to render HTML file, displaying the session information.

###launch.sh
Launches all the instances.

### install-my-app.sh
Installs all the tools needed on each server, and generates the configuration files.

Node: All txt files and scripts will be at the root directory on each EC2 instance.

