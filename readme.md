The file SRA file upload is a java web start swing application using the signed certificate.
Currently deployed https://enasequence.github.io/sub-file-uploader/WebinUploader.jnlp

Possible user experience while running WebinUploader.jnlp:

Case #1 User is on OSX and is using Chrome -> Warning msg is displayed advising them to use another browser as applet and webstart will not launch. Webstart will launch after if and when we have Apple developer Id that will allow us the sign the jar for OSX.

Case #2 -> Windows OS using Chrome -> Webstart will launch.

case #3 -> OSX/Windows not using chrome -> applet will launch.

/**********************************************/
/* To build and sign the jar then cvreate war */ENA
/**********************************************/
1. gradlew clean jar
2. Create a .p12 keystore with the private key used to obtain certificate(.pem/pkcs7) from certificate authority(goDaddy)
   Note: please remember the password of keystore and ./gradlew of the privatekey
3. To list all the keys in keystore
   keytool -keystore ENA-2019.p12 -list -v
   [ Enter Keystore password  ]
4. Import the certificate obtained from goDaddy into keystore using the same alias as privateKey
   keytool -importcert -noprompt -alias  codesigncert -file <something>-SHA2.pem -keystore ENA-2019.p12 -storepass pass1234
   [ Enter key password for <codesigncert> ]

5. sign the jar jarsigner -storetype pkcs12 -keystore ENA-2019.p12 sub-file-uploader/build/libs/webin-file-uploader-1.0.4.jar codesigncert(alias)

/*************/
/* To deploy */
/*************/

1: Create a new webin-file-uploader.jar and sign it using the above process
2: Create a git tag
3: Release the new jar in github
4: Update the WebinUploader.jnlp to point to new jar in the github release directory
5: Commit and push WebinUploader.jnlp 



