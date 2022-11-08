# Sub file uploader

The SRA file uploader is a java web start swing application deployed using the signed certificate.

Currently deployed in https://enasequence.github.io/sub-file-uploader/WebinUploader.jnlp

#Possible user experience while running WebinUploader.jnlp:

1. User is on OSX and is using Chrome -> Warning msg is displayed advising them to use another browser as applet and webstart will not launch. Webstart will launch after if and when we have Apple developer Id that will allow us the sign the jar for OSX.

2. Windows OS using Chrome -> Webstart will launch.

3. OSX/Windows not using chrome -> applet will launch.

#To create a certificate
1. Create a CSR file and privatekey using the below command. The CSR file must contain the details as per this document https://intranet.ebi.ac.uk/article/requesting-ssl-certificates

   `openssl req -newkey rsa:2048 -keyout privatekey.key -out openssl-ena.csr` 

   Note: The privatekey.key will be used for sining the jar

2. Create a service now ticket in (https://embl.service-now.com/) and attach the openssl-ena.csr file. This file will be used by IT service to get the certificate from certificate authority.

3. After IT request, it will take some time to get the link to the certificate from the IT team.

#To build and sign the jar 

1. gradlew clean jar
2. Create a .p12 keystore using private key (used to obtain certificate) and the certificate received from certificate authority

   Note: please remember the password of keystore

   `openssl pkcs12 -export -in cert/ena_ebi_2022_08_Nov.pem -inkey cert/privatekey.key -name codesigncert -out ENA-2022.p12`

   [ Enter Keystore password  ]

3. Sign the jar using the below command

   `jarsigner -storetype pkcs12 -keystore ENA-2022.p12 build/libs/webin-file-uploader-1.0.17.jar codesigncert(alias)`
4. To verify the jar validity use the below command

   `jarsigner -verify -verbose -certs build/libs/webin-file-uploader-1.0.17.jar`


# To deploy sub-file-uploader

webin-file-uploader.jar is deployed in git hub.

1. Create a new webin-file-uploader.jar and sign it using the above process
2. Create a git tag
3. Release the new jar in github
4. Update the WebinUploader.jnlp to point to new jar in the github release directory
5. Commit and push WebinUploader.jnlp 



