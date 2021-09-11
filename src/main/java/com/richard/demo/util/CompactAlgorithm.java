package com.richard.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * 压缩算法类
 * 实现文件压缩，文件夹压缩，以及文件和文件夹的混合压缩
 */
@Slf4j
public class CompactAlgorithm {


    /**
     * compress files
     *
     * @param srcfile source directory
     * @param targetFile target zip file name
     */
    public static void zipFiles(File srcfile, File targetFile) {
        if (targetFile.exists()) {
            targetFile.delete();
        }

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFile))) {
            if (srcfile.isFile()) {
                zipFile(srcfile, out, "");
            } else {
                File[] list = srcfile.listFiles();
                for (int i = 0; i < list.length; i++) {
                    compress(list[i], out, "");
                }
            }
            log.info("[zipFiles] compress folder {} completed", srcfile.getPath());
        } catch (Exception e) {
            log.error("An error occurred while zipping files");
        }
    }

    /**
     * Iteratively compress a directory
     * 
     * @param file
     * @param out
     * @param basedir
     */
    private static void compress(File file, ZipOutputStream out, String basedir) {
        if (file.isDirectory()) {
            zipDirectory(file, out, basedir);
        } else {
            zipFile(file, out, basedir);
        }
    }

    /**
     * compress single file
     *
     * @param srcfile
     */
    public static void zipFile(File srcfile, ZipOutputStream out, String basedir) {
        if (!srcfile.exists()) {
            return;
        }

        byte[] buf = new byte[1024];
        try (FileInputStream in = new FileInputStream(srcfile)) {
            int len;
            out.putNextEntry(new ZipEntry(basedir + srcfile.getName()));
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * zip directory
     * 
     * @param dir
     * @param out
     * @param basedir
     */
    public static void zipDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }


    public static void main(String[] args) {
        String root = FileUtil.getTempLoaction() + "ServiceRegistry";
        File src = new File(root);
        File target = new File(root + ".zip");
        zipFiles(src, target);
    }

}
