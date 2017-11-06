

PASSWORD=aaa  # {a}nnika, {a}lex, {a}rd :)
HOSTNAME=core-ca
CLIENTNAME=authorization-server

all:	clean create-directories create-serial-number create-truststore add-client

create-directories:
	# Generate a certificate authority (CA)
	sudo mkdir etc/ssl/CA
	sudo mkdir etc/ssl/CA/certs
	sudo mkdir etc/ssl/CA/newcerts
	sudo mkdir etc/ssl/CA/newcerts/db
	sudo mkdir etc/ssl/CA/newcerts/
	sudo mkdir etc/ssl/CA/private

create-serial-number:
	# Generate a file to keep track of the serial numbers
	sudo bash -c "echo ’01’ > etc/ssl/CA/serial"
	    
create-record-certificates:
	# Generate a file to record all issued certificates
	sudo touch etc/ssl/CA/index.txt

	# configuration provided

create-ca
	sudo openssl req -new -x509 -extensions v3 ca -keyout cakey.pem -out cacert.pem -days 3650
	
install-CA
	sudo mv cakey.pem etc/ssl/CA/private/
	sudo mv cacert.pem etc/ssl/CA/

create-CRL
	sudo mkdir etc/ssl/CA/crl
	sudo bash -c "echo ’01’ > /etc/ssl/CA/crlnumber"





