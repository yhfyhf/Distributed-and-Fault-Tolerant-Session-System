```
Server = IP:Port
RPC message = flag;CallID;OperationCode;sessionId;
Cookie Value = sessionId__versionNumber__locationMetadata
sessionId = ServerId+rebootNum+sessionNum
sessionKey = sessionId#versionNumber
Servers = ip:port,ip:port,ip:port
```

```
Client read
Send:   callID;RPC.Conf.SESSION_READ;sessionID;versionNumber
Return: true;CallID;message
        true;NotExists
        false;SocketTimeout
        false;errorMessage
```

```
Client write
Send:   callID;RPC.Conf.SESSION_WRITE;sessionId;versionNumber;message;dicardTime
Return: true;server1IP!serverID1,server2IP!serverID2,server3IP!server
        false;SocketTimeout
```


### Test Cases
1. First visit, new a session
2. Locally not exists sid#3, retrieve from other servers
3. server1 already stores server2's session. server2 restarts, so send rpc read to server1 to retrieve previous session.
4. Test reboot number
