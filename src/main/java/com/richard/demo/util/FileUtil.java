package com.richard.demo.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {
    /**
     * compress multiple files into one zip file
     *
     * @param srcfile source file list
     * @param zipfile
     */
    public static void zipFiles(List<File> srcfile, File zipfile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipfile);
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            for (int i = 0; i < srcfile.size(); i++) {
                zip(srcfile.get(i), zipOutputStream);
            }
        } catch (Exception e) {
            log.error("Error occurs when zip file", e);
        }
    }

    private static void zip(File srcFile, ZipOutputStream zipOutputStream) {
        byte[] buf = new byte[1024];
        try (FileInputStream in = new FileInputStream(srcFile)) {
            zipOutputStream.putNextEntry(new ZipEntry(srcFile.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, len);
            }
            zipOutputStream.closeEntry();
        } catch (FileNotFoundException e) {
            log.error("{} not found", srcFile.getName());
        } catch (IOException e) {
            log.error("Error occurred while zip file {} ", srcFile.getName());
        }
    }

    /**
     * @param content file content
     * @param path file path
     */
    public static void writeFile(String content, String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            log.error("An error occurred while creating file {} ", path, e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            log.error("An error occurred while writing to  file {} ", path, e);
        }
    }

    public static String getTempLoaction() {
        String location = null;

        try {
            location = ResourceUtils.getURL("classpath:").getPath();
        } catch (FileNotFoundException var2) {
            log.error("file not found");
        }

        return location;
    }

    public static boolean createDirectory(File dir) {
        boolean result = false;
        try {
            result = dir.exists() ? true : dir.mkdir();
        } catch (Exception e) {
            log.error("An error occurred while creating dir {} ", dir.getPath(), e);
        }
        return result;
    }

    public static Resource test1() {
        // test sr hierarchy
        // parent folder
        String rootFolder = FileUtil.getTempLoaction() + "ServiceRegistry";
        FileUtil.createDirectory(new File(rootFolder));
        String serviceMappingPath = rootFolder + "/ServiceMapping.json";
        String errorPath = rootFolder + "/Error.json";

        File srcFile = new File(rootFolder);
        String fileName = new Date().toString();
        File outFile = new File(FileUtil.getTempLoaction() + fileName + ".zip");

        try {
            String mapping = prepareMapping();
            FileUtil.writeFile(mapping, serviceMappingPath);
            FileUtil.writeFile(new JsonObject().toString(), errorPath);

            // sub folder
            Gson gson = new Gson();
            String serviceFolder = rootFolder + "/Services";
            FileUtil.createDirectory(new File(serviceFolder));
            String serviceJson = serviceFolder + "/service1.json";
            String service = prepareService();
            FileUtil.writeFile(gson.toJson(service), serviceJson);


            String cdtFolder = rootFolder + "/ComplexDataTypes";
            FileUtil.createDirectory(new File(cdtFolder));
            String cdtJson = cdtFolder + "/cdt1.json";
            String cdt = prepareCdt();
            FileUtil.writeFile(gson.toJson(cdt), cdtJson);
            log.info("completing hierarchy....");

            log.info("starting zipping....");

            CompactAlgorithm.zipFiles(srcFile, outFile);
            log.info("completing zipping....");
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            // FileUtils.deleteQuietly(srcFile);
            // FileUtils.deleteQuietly(outFile);
        }
        return new FileSystemResource(outFile.getPath());
    }

    /**
     * 将Resource 写回磁盘，进行解压缩
     * 
     * @param srResource
     * @return
     */
    public static String decompression(Resource srResource) {
        String dateStr = new Date().toString();
        String tempSrZipFileName = FileUtil.getTempLoaction() + "SR_" + dateStr + ".zip";
        String tempSrFolder = FileUtil.getTempLoaction() + "SR_" + dateStr;
        try {
            // 输出文件到磁盘，检查文件数据内容及结构，方便测试。
            OutputStream os = null;
            InputStream in = null;
            os = new FileOutputStream(tempSrZipFileName);

            // 如果restful返回的resource，可能需要转成ByteArrayResource
            // byte[] bytes = ((ByteArrayResource) srResource).getByteArray();
            // in = new ByteArrayInputStream(bytes);
            in = srResource.getInputStream();
            byte[] bs = new byte[1024];
            int len = 0;
            while ((len = in.read(bs)) != -1) {
                os.write(bs, 0, len);
                os.flush();
            }
            in.close();
            os.close();
            FileUtil.decompression(tempSrZipFileName, tempSrFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempSrFolder;
    }

    public static void decompression(String targetFileName, String parent) {
        System.out.println("正在解压...");
        try {
            // 1.构建解压输出流
            ZipInputStream zIn = new ZipInputStream(new FileInputStream(targetFileName));
            ZipEntry entry = null;
            File file = null;
            // 2.循环取值 有下一级 且为文件不是目录
            while ((entry = zIn.getNextEntry()) != null && !entry.isDirectory()) {
                file = new File(parent, entry.getName()); // 父目录+当前条目名字
                // 如果文件不存在
                if (!file.exists()) {
                    new File(file.getParent()).mkdirs();// 创建此文件的上级目录
                }
                // 3.写文件
                OutputStream out = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(out);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = zIn.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);
                }
                bos.close();
                System.out.println(file.getAbsolutePath() + "解压成功！");
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public static void main(String[] args) {
        Resource file = test1();
        log.info(file.getFilename());
        String tempFolder = decompression(file);
        log.info(tempFolder);
        FileUtils.deleteQuietly(new File(tempFolder));
        FileUtils.deleteQuietly(new File(tempFolder + ".zip"));
    }


    public static String prepareCdt() {
        Gson gson = new Gson();
        Object1 car = Object1.builder().number("01").result("aa").build();
        return gson.toJson(car);
    }

    public static String prepareService() {
        Gson gson = new Gson();
        Car car = Car.builder().color("yellow").type("Cadilac").build();
        return gson.toJson(car);
    }

    public static String prepareMapping() {
        com.google.gson.JsonObject mapping = new com.google.gson.JsonObject();

        JsonArray serviceArray = new JsonArray();
        com.google.gson.JsonObject obj1 = new com.google.gson.JsonObject();
        obj1.addProperty("serviceId", 1);
        obj1.addProperty("metadataId", 1);
        obj1.addProperty("name", "testService");
        obj1.addProperty("groupPath", "/user defined/test");
        serviceArray.add(obj1);

        JsonArray cdtArray = new JsonArray();
        com.google.gson.JsonObject obj2 = new com.google.gson.JsonObject();
        obj2.addProperty("id", 2);
        obj2.addProperty("name", "testCDT");
        obj2.addProperty("groupPath", "/user defined/test/cdt");
        cdtArray.add(obj2);

        mapping.add("ServiceMapping", serviceArray);
        mapping.add("CDTMapping", cdtArray);

        return mapping.toString();
    }

    @Test
    public void test() throws Exception {
        // 表示classpath的路径，就是bin的绝对路径名
        System.out.println(FileUtil.class.getResource("/"));
        System.out.println(FileUtil.class.getClassLoader().getResource(""));
        // System.out.println(ResourceUtils.getURL("classpath:").getPath());

        // 表示当前类的folder的名字
        System.out.println(FileUtil.class.getResource(""));

        // 尽量不要使用user.dir,因为得出的结果各不相同
        System.out.println(System.getProperty("user.dir"));


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Car {

        private String color;
        private String type;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Object1 {
        private String number;
        private String result;
    }
}



