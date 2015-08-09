(EduServer)服务端:
1.将EduServer倒入能开发 j2ee 的eclipse
2.修改 com.gdufs.edu.constant.Constant.java中ROOT_BASE为"本机中tomcat的目录"/webapps/ROOT/
3.在"本机中tomcat的目录"/webapps/ROOT/ 下,创建 image和 files两个目录
4.运行EduServer,待启动有停止,将EduServer工程以 .war的形式导出.
5.将EduServer.war放在 "本机中tomcat的目录"/webapps,启动tomcat,服务端开启


客户端:
修改  移动学习平台/src/com/gdufs/studyplatform/config/Constants.java 中IP常年为 本机的ip地址,
运行即可 

