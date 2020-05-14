# ftproxy
Java-Netty asynchronous FTP proxy - thoughts and updates in chronological order

1. Work in Progress.

2. One day, some day, this will act very similar to a fast, asynchronous
   netty-based ftp proxy.

3. Update: a considerable amount of functionality is in place.

4. Here's how to bring up the FTP Proxy at a specified port (8081) on the
   local machine (localhost), while also specifying a specific ftp host/port
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

8. Updated configuration interface; list of supported configuration parameters
   and their default values are as follows:

   - host 127.0.0.1             // Default host for the FTP proxy
   - port 8080                  // Default port for the FTP proxy
   - remote-host 127.0.0.1      // Default host for the remote/backend FTP server
   - remote-host 14646          // Default port for the remote/backend FTP server
   - server-backlog 128         // The default maximum queue length for
                                // incoming connections
   - read-timeout 30            // Default read timeout, in seconds
   - terminate-ssl false        // Default value which indicates whether SSL
                                // should be terminated at the FTP proxy or not
   - implicit-ssl false         // Default flag for whether SSL should be
                                // enabled implicitly at the FTP proxy or not

   All of these can be independently modified using the -D switch for the java
   commandline. As an example:

mvn exec:java -Dexec.mainClass="four.six.ftproxy.server.FTProxy" -Dhost=localhost -Dport=8081 -Dremote-host=ftphost -Dremote-port=9091

9. The mentioned default values, as well as any overriding command line parameters,
   will in turn be overridden by the contents of a properties file (ftproxy.properties),
   if one is present in the working directory of the FTP proxy program. Example
   format follows:

   host=localhost<br />
   port=8181<br />
   implicit-ssl=true<br />
   read-timeout=120<br />
   remote-host=someftphost<br />
   remote-port=9192<br />

10. The -Dpath-to-properties=fullfilepath overrides the default
    location (i.e. the current working directory) of the properties file. As
    an example:

mvn exec:java -Dpath-to-properties=D:\\cygwin64\\tmp\\ftproxy.properties -Dexec.mainClass="four.six.ftproxy.server.FTProxy"


