rem ��bat��������java����(���߿����Դ��jar����ʽ����)�����ʼ���
rem ��echo off����ʾ����ʾ����
@echo off 
rem FastMailer�����ڰ�testJavaMail�У�������������һ����tool.GetDnsIp,
rem ���ԣ�Ŀ¼�ṹ����
rem d 
rem  - testJavaMail
rem     -FastMailer.java  
rem  - tool
rem     - GetDnsIp.class
d:
rem ��������jar��,������d����
set classpath=.;activation-1.1.jar;mail-1.4.jar 
rem Դ�ļ���utf-8����,FastMailer.javaλ��d:\testJavaMail\FastMailer.java
javac -encoding utf-8 testJavaMail\FastMailer.java 
rem ��start javaw������ʾdos���ڣ�javaw��java�Ĳ�ͬ����javaw�ص���dos���ڳ���Ҳ����GetDnsIp
start javaw testJavaMail.FastMailer 
rem ��pause�� �����ʾֹͣ�����bat�ļ�һ�����������Լ����������