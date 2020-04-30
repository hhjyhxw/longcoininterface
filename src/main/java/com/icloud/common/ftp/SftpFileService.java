package com.icloud.common.ftp;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class SftpFileService {

    
    private static final Logger logger = Logger.getLogger(SftpFileService.class);

    /**
     * @param remoteDirectory 远程文件所在目录
     * @param downloadFile  待下载的文件
     * @param saveFile      本地保存路径
     * @throws Exception
     */
    public boolean download(String remoteDirectory, String downloadFile, String saveFile){
        SFTPUtil sftp = null;
        try {
            logger.info("remoteDirectory==="+remoteDirectory);
            sftp = new SFTPUtil();
//            sftp = new SFTPUtil(ConfigUtil.get("user"),  ConfigUtil.get("password"), ConfigUtil.get("server"), Integer.valueOf(ConfigUtil.get("port")));
            sftp.login();
            sftp.download(remoteDirectory,downloadFile,saveFile);
            return true;
        }catch (Exception e){
            logger.error("SFTP download file exception", e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
        return false;
    }


    /**
     * @param remoteDirectory 远程文件所在目录
     * @param downloadFile  待下载的文件
     * @throws Exception
     */
    public byte[] download(String remoteDirectory, String downloadFile){
        SFTPUtil sftp = null;
        try {
            sftp = new SFTPUtil();
//            sftp = new SFTPUtil(ConfigUtil.get("user"),  ConfigUtil.get("password"), ConfigUtil.get("server"), Integer.valueOf(ConfigUtil.get("port")));
            sftp.login();
          return  sftp.download(remoteDirectory,downloadFile);
        }catch (Exception e){
            logger.error("SFTP download file exception", e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
        return null;
    }

    /**
     * 修改文件目录（移动文件、重命名文件）
     * @param olddirectory 原文件目录
     * @param fileName 原文件名称
     * @param newdirectory 新文件目录
     * @param newfileName  新文件名称
     * @throws Exception
     */
    public void changeFileDir(String olddirectory, String fileName, String newdirectory, String newfileName) {
        SFTPUtil sftp = null;
        try {
            sftp = new SFTPUtil();
//            sftp = new SFTPUtil(ConfigUtil.get("user"),  ConfigUtil.get("password"), ConfigUtil.get("server"), Integer.valueOf(ConfigUtil.get("port")));
            sftp.login();
            if(sftp.copyFile(olddirectory+fileName,newdirectory,newfileName)){//复制原文件
                sftp.delete(olddirectory,fileName);//删除原文件
            }
        }catch (Exception e){
            logger.error("SFTP changeFileDir exception ;oldPath==="+(olddirectory+fileName)
                    +";newPath==="+(newdirectory+newfileName)+";newdirectory==="+newdirectory+";newfileName==="+newfileName, e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
    }

    /**
     *
     * @param fileDir 文件目录
     * @param fileName  文件名
     * @return
     * @throws Exception
     */
    public boolean deleteFile(String fileDir, String fileName) throws Exception {
        SFTPUtil sftp = null;
        try {
            sftp = new SFTPUtil();
            sftp.login();
            sftp.delete(fileDir,fileName);
            return true;
        }catch (Exception e){
            logger.error("SFTP deleteFile exception", e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
        return false;
    }

    /**
     * 文件上传
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     * @param basePath  服务器的基础路径
     * @param directory  上传到该目录
     * @param sftpFileName  sftp端文件名
     * @param in   输入流
     * @throws Exception
     */
    public boolean uploadFile(String basePath, String directory, String sftpFileName, InputStream in) throws Exception {
        SFTPUtil sftp = null;
        try {
            sftp = new SFTPUtil();
            sftp.login();
            sftp.upload(basePath,directory,sftpFileName,in);
            return true;
        }catch (Exception e){
            logger.error("SFTP uploadFile exception", e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
        return false;
    }


    public List<String> listDirectFile(String directory){
        SFTPUtil sftp = null;
        try {
            sftp = new SFTPUtil();
            sftp.login();
            return  sftp.listFiles(directory,new ArrayList<String>());
        }catch (Exception e){
            logger.error("SFTP listDirectFile exception", e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
        return null;
    }
    /**
     * 文件复制
     */
    public boolean copyFile(String fromDir, String toDir, String fileName) throws Exception {
        SFTPUtil sftp = null;
        try {
            sftp = new SFTPUtil();
            sftp.login();
            return sftp.copyFile(fromDir+fileName,toDir,fileName);
        }catch (Exception e){
            logger.error("SFTP SftpFileService exception", e);
        }finally {
            if(sftp!=null){
                sftp.logout();
            }
        }
        return false;
    }


}
