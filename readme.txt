The file SRA file upload is now a java web start swing application using the signed certificate. The appletr has been removed. Currenly deployed (temporary) under ves-hx-d0:8080/ena/upload/WebinUploader.jnlp for testing puposes. I have made a request to have the following servers created so it can be deployed there (this follows the SRA Wenin servers used as documented under http://www.ebi.ac.uk/seqdb/confluence/display/EMBL/Webin+on+VMs

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
/* To build and sign the jar then cvreate war */
/**********************************************/
1: gradlew clean jar
2: jarsigner -storetype pkcs12 -keystore ENA-2016.p12 build/libs/webin-file-uploader-1.0.1.jar "1" (you will be prompted for passcode, enter pass1234)
3: gradlew war

/*************/
/* To deploy */
/*************/

1: ssh ebi-cli-001
2: become ena_adm
3: ssh ves-hx-5a/b
4: cd /nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/bin
5: ./stop
6: ./clean_dirs
7: rsync /homes/username/webin-file-uploader-1.0.1.war ves-hx-5a:/nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/deploy
8: change /nfs/public/rw/webadmin/tomcat/bases/ena/tc-ena-file_upload_prod/conf/Catalina/localhost/ena#upload.xml to point to latest version os war file
9: ./start

