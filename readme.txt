The file SRA file upload is now a java web start swing application using the signed certificate. The appletr has been removed.
Currently deployed (temporary) under ves-hx-d0:8080/ena/upload/WebinUploader.jnlp for testing puposes.
I have made a request to have the following servers created so it can be deployed there (this follows the SRA Wenin servers used as documented under http://www.ebi.ac.uk/seqdb/confluence/display/EMBL/Webin+on+VMs

DEV
vm: ves-ebi-5b
home: /nfs/web-hx/webadmin/tomcat/bases/ena/tc-ena-file_upload_dev
port: 8160

TEST
vm: ves-ebi-5a
home: /nfs/web-hx/webadmin/tomcat/bases/ena/tc-ena-file_upload_test
port: 8160

PROD
vm: ves-hx-5a and ves-hx-5b
home: /nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod
port: 8160

SRA Webin has been modified to the following:

Case #1 User is on OSX and is using Chrome -> Warning msg is displayed advising them to use another browser as applet and webstart will not launch. Webstart will launch after if and when we have Apple developer Id that will allow us the sign the jar for OSX.

Case #2 -> Windows OS using Chrome -> Webstart will launch.

case #3 -> OSX/Windows not using chrome -> applet wil launch.

/**********************************************/
/* To build and sign the jar then cvreate war */ENA
/**********************************************/
1. Change webapp/WebinUploader.jnlp to point to new jar
2. gradlew clean jar
3. Create a .p12 keystore with the private key used to obtain certificate(.pem/pkcs7) from certificate authority(goDaddy)
   Note: please remember the password of keystore and ./gradlew of the privatekey
4. To list all the keys in keystore
    keytool -keystore ENA-2019.p12 -list -v
5. Import the certificate obtained from goDaddy into keystore using the same alias as privateKey
    keytool -importcert -noprompt -alias  codesigncert -file <something>-SHA2.pem -keystore ENA-2019.p12 -storepass pass1234

6. sign the jar jarsigner -storetype pkcs12 -keystore ENA-2019.p12 sub-file-uploader/build/libs/webin-file-uploader-1.0.4.jar codesigncert(alias)

8: gradlew war

/*************/
/* To deploy */
/*************/

1: ssh ebi-cli-001
2: become ena_adm
3: ssh ves-hx-5a/b
4: cd
/nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/bin
5: ./stop
6: ./clean_dirs
7: rsync /homes/username/webin-file-uploader-1.0.1.war ves-hx-5a/5b:
/nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/deploy
8: rsync /homes/username/webin-file-uploader-1.0.1.jar ves-hx-5a/5b:/nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/deploy
8: change
/nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/conf/Catalina/localhost/ena#upload.xml to point to latest version of the war file
9. copy webin-file-uploader-1.0.1.jar to
/nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/webapps/ena#upload
directory(same location as WebinUploader.jnlp)
9: ./start

