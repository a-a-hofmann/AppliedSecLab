# Backdoor2

## Idea: Issue insecure certificates

In order to install the manipulated openssl:
- Copy the binary openssl into /usr/bin/ (you must override the openssl)
    1.For MAC OS you can find it in the directory MAC OS
    2.For Ubuntu you can find it in the directory Ubuntu
- Or you can use the openssl-1.0.2m and follow the installation process provided by OpenSSL (the source code is manipulated there aswell :))

In the hacking directory, you can find an example and how the private key can be extracted. A program that does the calculations for you is provided as well. With two different certificates that are issued from the CA (the certificates must be changed to PEM format) the program can extract the private key of the CA (the PEM certificates must be given as arguments in the jar file).

In order to use this backdoor in our project (CA Authority), the RSA CA certificate and the RSA key under the directory "etc/ssl/CA/private/" (cacert.pem & cakey.pem) must be replaced with the DSA CA certificate and DSA key, which can be found under the "CA DSA files"
    
    
