# ftproxy
Java-Netty asynchronous FTP proxy

1. Work in Progress.

2. One day, some day, this will act very similar to a fast, asynchronous
   netty-based ftp proxy.

3. Update: a considerable amount of functionality is in place.

4. Here's how to bring up the FTP Proxy at a specified port (8081) on the
   local machine (localhost), while also specfifying a specific ftp host/port
   (ftphost:9091) as the target backend:

mvn exec:java -Dexec.mainClass="four.six.ftproxy.server.FTProxy" -Dhost=localhost -Dport=8081 -Dremote-host=ftphost -Dremote-port=9091

5. A fair amount of testing code has been added to cover the broad/key
   functionalities involved.

6. Updates planned:

   - A bit of 'internal' code review to smooth out those rough edges
   - Improve code coverage in terms of test cases
   - The configuration interface is neanderthal at best; do something about
     that
   - Add more tuning options. In fact as of now, there's bloody zilch 
   - Clean up the logging framework
   - The coding style needs to toe some standard line and be consistent
     while at it

7. Go for a long flipping run - without a flipping face mask - once this COVID mess
   clears up. Then, perhaps, since hell would have long frozen over, it might be a
   good idea to take up ice skating too.
