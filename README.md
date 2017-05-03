# scala Impala JDBC

Just enough code to run `SHOW DATABASES` via the Impala JDBC Driver.

The core of this is stolen from the [Scala Cookbook](http://shop.oreilly.com/product/0636920026914.do).
(See this page for more: http://alvinalexander.com/scala/scala-jdbc-connection-mysql-sql-select-example)

Setup
-----
You need to get a copy of the Impala JDBC driver from the Cloudera website:
https://www.cloudera.com/downloads/connectors/impala/jdbc/2-5-37.html

This needs to be copied into this repo under the `lib` directory (assumes you are in the root of this repo):
```bash
mkdir -p /tmp/impalajdbc/unpack
unzip /tmp/impalajdbc/impala_jdbc_2.5.37.1057.zip -d /tmp/impalajdbc/unpack

mkdir -p lib
unzip /tmp/impalajdbc/unpack/2.5.37.1057\ GA/Cloudera_ImpalaJDBC41_2.5.37.zip -x "*.pdf" -d lib/
```

After that you should be able to build the JAR:
```bash
sbt clean assembly
```

This creates the JAR file as `target/scala-2.10/scalaImpalaJDBC-assembly-0.1.0.jar`

Usage
-----
The only argument is an JDBC URL to connect to. See the [Impala JDBC Install Guide](http://www.cloudera.com/documentation/other/connectors/impala-jdbc/latest/Cloudera-JDBC-Driver-for-Impala-Install-Guide.pdf) for all options.
```bash
java -jar target/scala-2.10/scalaImpalaJDBC-assembly-0.1.0.jar "jdbc:impala://clquick:21050/default;auth=noSasl"
```

TLS
---
In order to use this with an Impala cluster using TLS/SSL you first need to take the CA certificate that signed the Impala server certificates and create a JKS trust store for it.
```bash
mkdir jks
JKSPASS=passW0rdH3r3
keytool -importcert -noprompt -keystore jks/truststore.jks \
-alias coreCA -file x509/coreCA.pem -storepass $JKSPASS
```
The URL can then refer to this:
```bash
java -jar target/scala-2.10/scalaImpalaJDBC-assembly-0.1.0.jar "jdbc:impala://impala.example.com.au:21050/default;AuthMech=3;SSL=1;SSLTrustStore=jks/truststore.jks;SSLTrustStorePwd=passW0rdH3r3;UID=impala_user;PWD=********"
```

Kerberos
---
For a real application you'll need to get this working with [JAAS](http://docs.oracle.com/javase/7/docs/technotes/guides/security/jgss/tutorials/) but this shows at least how to connect using a keytab.
```bash
kinit -kt krb/impala_user.keytab impala_user
java -jar scalaImpalaJDBC-assembly-0.1.0.jar "jdbc:impala://impala.example.com.au:21050/default;AuthMech=1;SSL=1;SSLTrustStore=jks/truststore.jks;SSLTrustStorePwd=passW0rdH3r3;KrbHostFQDN=impala.example.com.au;KrbServiceName=impala"
```
