package com.icloud.common.ftp;
import com.icloud.basecommon.util.io.IOUtils;
import com.icloud.common.ConfigUtil;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class SFTPUtil {

    private transient Logger log = LoggerFactory.getLogger(this.getClass());

    private ChannelSftp sftp;

    private Session session;
    /** SFTP 登录用户名*/
    private static String username = ConfigUtil.get("user");
    /** SFTP 登录密码*/
    private static String password = ConfigUtil.get("password");
    /** 私钥 */
    private String privateKey;
    /** SFTP 服务器地址IP地址*/
    private static String host = ConfigUtil.get("server");
    /** SFTP 端口*/
    private static int port = Integer.valueOf(ConfigUtil.get("port"));


    /**
     * 构造基于密码认证的sftp对象
     */
    public SFTPUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SFTPUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtil(){}


    /**
     * 连接sftp服务器
     */
    public void login(){
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("[ip:").append(host);
            sb.append(", port:").append(port);
            sb.append(", userName:").append(username);
            sb.append(", password:").append(password);

            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);// 设置私钥
            }

            session = jsch.getSession(username, host, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
//            e.printStackTrace();
            log.warn("connect SFTP JSchException," + sb + "failed,"+e.getMessage());
        }catch(Exception ex){
            log.warn("connect SFTP Exception," + sb + "failed,"+ex.getMessage());
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout(){
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     * @param basePath  服务器的基础路径
     * @param directory  上传到该目录
     * @param sftpFileName  sftp端文件名
     * @param input   输入流
     */
    public void upload(String basePath, String directory, String sftpFileName, InputStream input) throws SftpException{
        try {
            sftp.cd(basePath);
            sftp.cd(directory);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String[] dirs=directory.split("/");
            String tempPath=basePath;
            for(String dir:dirs){
                if(null== dir || "".equals(dir)) continue;
                tempPath+="/"+dir;
                try{
                    sftp.cd(tempPath);
                }catch(SftpException ex){
                    sftp.mkdir(tempPath);
                    sftp.cd(tempPath);
                }
            }
        }
        sftp.put(input, sftpFileName);  //上传文件
    }


    /**
     * 下载文件。
     * @param directory 下载目录
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }

    /**
     * 下载文件
     * @param directory 下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);

        return fileData;
    }


    /**
     * 删除文件
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException{
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    /**
     * 重命名，或者移动文件
     * @param oldpath 原文件全路径
     * @param newpath   新文件全路径
     * @throws SftpException
     */
    public void mvFile(String oldpath, String newpath) throws SftpException{
        sftp.rename(oldpath,newpath);
    }

    /**
     * 文件复制
     * @param oldFilePath  源文件
     * @param directory  目标文件夹
     * @param fileName  目标文件
     */
    public boolean copyFile(String oldFilePath, String directory, String fileName){
        long start = System.currentTimeMillis();
        InputStream tInputStream = null;
        ByteArrayOutputStream baos = null;
        InputStream nInputStream = null;
        try{
            //如果文件夹不存在，则创建文件夹
            if(sftp.ls(directory) == null){
                sftp.mkdir(directory);
            }
            //切换到指定文件夹
            sftp.cd(directory);
        }catch (SftpException e){
            //创建不存在的文件夹，并切换到文件夹
            try {
                sftp.mkdir(directory);
                sftp.cd(directory);
            } catch (SftpException ex) {
                ex.printStackTrace();
            }
        }

//        String filePath = directory+"/"+fileName;
        try{
            tInputStream = sftp.get(oldFilePath);
            //拷贝读取到字节流
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = tInputStream.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            nInputStream = new ByteArrayInputStream(baos.toByteArray());
            sftp.put(nInputStream, fileName);
            return true;
        }catch (SftpException se){
            log.warn("copyFile SFTP SftpException"+se.getMessage());
        }catch (Exception e){
            log.warn("copyFile SFTP Exception"+e.getMessage());
        }finally {
            if(tInputStream!=null){
                try {
                    tInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(tInputStream!=null){
                try {
                    tInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("复制文件成功！！ 耗时：{}ms",(System.currentTimeMillis() - start));
        return false;
    }


    /**
     * 列出目录下的文件
     * @param directory 要列出的目录
     */
    public List<String> listFiles(String directory, List<String> pathList) throws SftpException {
        boolean flag=openDir(directory);
        if(flag){
            Vector vv = sftp.ls(directory);
            if(vv == null && vv.size() == 0){
                return null;
            }else{
                for(Object object : vv){
                    ChannelSftp.LsEntry entry=(ChannelSftp.LsEntry)object;
                    String filename=entry.getFilename();
                    if(".".equals(filename) || "..".equals(filename)){
                        continue;
                    }
                    if(openDir(directory+filename+"/")){
                        //能打开，说明是目录，接着遍历
                        listFiles(directory+filename+"/",pathList);
                    }else{
                        pathList.add(directory+filename);
                        log.info("directory+filename==="+directory+filename);
                    }
                }
            }
        }else{
            log.info("对应的目录"+directory+"不存在！");
        }
        return pathList;
    }

    /**
     * @param directory
     * @return
     */
    public boolean openDir(String directory){
        try{
             sftp.cd(directory);
            return true;
        }catch(SftpException e){
            log.error(e.getMessage());
             return false;
        }
   }


    //上传文件测试
//    public static void main(String[] args) throws SftpException, IOException {
//        SFTPUtil sftp = new SFTPUtil("用户名", "密码", "ip地址", 22);
//        sftp.login();
//        File file = new File("D:\\图片\\t0124dd095ceb042322.jpg");
//        InputStream is = new FileInputStream(file);
//
//        sftp.upload("基础路径","文件路径", "test_sftp.jpg", is);
//        sftp.logout();
//    }

}
