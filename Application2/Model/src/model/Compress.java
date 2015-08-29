package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Compress extends HttpServlet{
    public Compress() {
        super();
    }
    //压缩文件名的编码格式
    private static final String ENCODE = "GBK";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException,
                                                            IOException {
        try {
            zip("");
            String downFilename = "提交材料.zip";
            //获取项目的绝对地址，下载附件包存放的地址
            String filepath = this.getServletConfig().getServletContext().getRealPath("/");
            response.setContentType("application/x-zip-compressed");
            response.setHeader("Location", downFilename);
            //对下载文件的文件名进行编码格式转换
            response.setHeader("Content-Disposition",
                               "attachment; filename=" + new String(downFilename.getBytes("GBK"),"ISO8859_1"));
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream =
                new FileInputStream(filepath + downFilename);
            byte[] buffer = new byte[2048];
            int i = -1;
            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            System.out.println("没有找到您要的文件");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("系统错误，请及时与管理员联系");
        }
    }
    
    
    public void zip(String result) {
        try {
            //服务器压缩文件的地址
            String zipFileName = "C:\\Users\\Administrator\\Desktop\\压缩文件\\多级文件压缩.zip";
            //压缩包
            File basetarZipFile = new File(new String(zipFileName.getBytes(ENCODE))).getParentFile();
            if (!basetarZipFile.exists() && !basetarZipFile.mkdirs())
                throw new RuntimeException("目标文件无法创建!!!");
            BufferedOutputStream bos = null;
            FileOutputStream out = null;
            org.apache.tools.zip.ZipOutputStream zOut = null;
            JSONObject fileList = new JSONObject(result);
            JSONArray folderList =
                fileList.getJSONArray("folders");
            // 创建文件输出对象out,提示:注意中文支持
            out =
    new FileOutputStream(new String(zipFileName.getBytes(ENCODE)));
            bos = new BufferedOutputStream(out);
            zOut = new ZipOutputStream(bos);
            //系统参数sun.jnu.encoding表示获取当前系统中的文件名的编码方式.这里将ZipOutputStream的文件名编码方式设置成系统的文件名编码方式.
            zOut.setEncoding(System.getProperty("sun.jnu.encoding"));
            zip(zOut, folderList);

            zOut.close();
            bos.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zip(ZipOutputStream zOut, JSONArray folderList) {
        try {
            for (int i = 0; i < folderList.length(); i++) {
                JSONObject folder = folderList.getJSONObject(i);
                String folderName = folder.getString("folderName");
                try {
                    zOut.putNextEntry(new ZipEntry(folderName + "/"));
                    folderName += "/";
                    //获取附件
                    JSONArray files =
                        folder.getJSONArray("row");
                    for (int j = 0; j < files.length(); j++) {
                        JSONObject file = files.getJSONObject(j);
                        File f =new File("C:\\Users\\Administrator\\Desktop\\压缩文件\\"+file.getString("fileName"));
                        // 填入文件句柄
                        zOut.putNextEntry(new ZipEntry(folderName + file.getString("fileName")));
                        // 开始压缩，从文件入流读,写入ZIP 出流
                        writeFile(zOut, f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(ZipOutputStream zOut,
                                  File file) throws IOException {
        FileInputStream in = null;
        BufferedInputStream bis = null;
        in = new FileInputStream(file);
        bis = new BufferedInputStream(in);
        int len = 0;
        byte[] buff = new byte[2048];
        while ((len = bis.read(buff)) != -1)
            zOut.write(buff, 0, len);
        zOut.flush();
        bis.close();
        in.close();
    }

    public static void main(String[] args) {
        try{
            Compress compress = new Compress();
            JSONArray folderList = new JSONArray();
            JSONObject folder1 = new JSONObject();
            JSONObject folder2 = new JSONObject();
            JSONArray fileList1 = new JSONArray();
            JSONArray fileList2 = new JSONArray();
            JSONObject file1 = new JSONObject();
            JSONObject file2 = new JSONObject();
            JSONObject file3 = new JSONObject();
            JSONObject file4 = new JSONObject();
            folder1.put("folderName", "demo");
            folder2.put("folderName","示例文件夹");
            file1.put("fileName","a.xml");
            file2.put("fileName","b.jpg");
            fileList1.put(file1);
            fileList1.put(file2);
            file3.put("fileName", "示例文件1.xml");
            file4.put("fileName", "示例文件2.jpg");
            fileList2.put(file3);
            fileList2.put(file4);
            folder1.put("row", fileList1);
            folder2.put("row", fileList2);
            folderList.put(folder1);
            folderList.put(folder2);
            JSONObject data = new JSONObject();
            data.put("folders", folderList);
            compress.zip(data.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
