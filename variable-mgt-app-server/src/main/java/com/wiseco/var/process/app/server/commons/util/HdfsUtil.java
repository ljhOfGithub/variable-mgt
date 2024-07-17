/*
 * Licensed to the Wiseco Software Corporation under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// /*
// * Licensed to the Wiseco Software Corporation under one or more
// * contributor license agreements. See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
// package com.wiseco.var.process.app.server.commons.util;
//
// import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
// import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.hadoop.conf.Configuration;
// import org.apache.hadoop.fs.FSDataInputStream;
// import org.apache.hadoop.fs.FileStatus;
// import org.apache.hadoop.fs.FileSystem;
// import org.apache.hadoop.fs.Path;
//
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.net.URI;
//
// /**
// * @author Asker.J
// * @since 2022/10/13
// */
// @Slf4j
// public final class HdfsUtil {
// private HdfsUtil() {
// throw new InternalDataServiceException("非法创建 HdfsUtil");
// }
//
// /**
// * 上传文件
// *
// * @param conf 配置信息
// * @param local 本地地址
// * @param remote 远程地址
// * @param uri 统一资源标识符
// * @throws IOException IO异常
// */
// public static void uploadFile(Configuration conf, String uri, String local, String remote) throws IOException {
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// fs.copyFromLocalFile(new Path(local), new Path(remote));
// log.info("hdfs copy file from: " + local + " to " + remote);
// fs.close();
// }
//
// /**
// * 上传文件
// *
// * @param conf 配置信息
// * @param local 本地地址
// * @param remote 远程地址
// * @param uri 统一资源标识符
// * @param user 用户名
// * @throws IOException IO异常
// * @throws InterruptedException 中断异常
// */
// public static void uploadFile(Configuration conf, String uri, String user, String local, String remote)
// throws IOException, InterruptedException {
// FileSystem fs = FileSystem.get(URI.create(uri), conf, user);
// fs.copyFromLocalFile(new Path(local), new Path(remote));
// log.info("hdfs copy file from: " + local + " to " + remote);
// fs.close();
// }
//
// /**
// * 获取hdfs上文件流
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param localFilePath 本地地址
// * @param remoteFile 远程地址
// * @throws IOException IO异常
// */
// public static void getFileStream(Configuration conf, String uri, String localFilePath, String remoteFile)
// throws IOException {
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// Path path = new Path(remoteFile);
// // 获取文件流
// FSDataInputStream in = fs.open(path);
// // 输出流
// FileOutputStream fos = new FileOutputStream(localFilePath);
// int ch = 0;
// while ((ch = in.read()) != MagicNumbers.MINUS_INT_1) {
// fos.write(ch);
// }
// log.info("-----");
// in.close();
// fos.close();
// }
//
// /**
// * 创建文件夹
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param remoteFile 远程地址
// * @throws IOException IO异常
// */
// public static void mkdirIfNotExist(Configuration conf, String uri, String remoteFile) throws IOException {
// // 获取文件系统
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// Path path = new Path(remoteFile);
// boolean exists = fs.exists(path);
// if (!exists) {
// fs.mkdirs(path);
// log.info("创建文件夹" + remoteFile);
// }
// fs.close();
// }
//
// /**
// * 创建文件夹
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param remoteFile 远程地址
// * @param user 用户名
// * @throws IOException IO异常
// * @throws InterruptedException 中断异常
// */
// public static void mkdirIfNotExist(Configuration conf, String uri, String user, String remoteFile)
// throws IOException, InterruptedException {
// // 获取文件系统
// FileSystem fs = FileSystem.get(URI.create(uri), conf, user);
// Path path = new Path(remoteFile);
// boolean exists = fs.exists(path);
// if (!exists) {
// fs.mkdirs(path);
// log.info("创建文件夹" + remoteFile);
// }
// fs.close();
// }
//
// /**
// * 判断文件夹是否存在
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param remoteFile 远程地址
// * @return 布尔值
// * @throws IOException IO异常
// */
// public static boolean isDirectoryExist(Configuration conf, String uri, String remoteFile) throws IOException {
// // 获取文件系统
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// Path path = new Path(remoteFile);
// boolean exists = fs.exists(path);
// boolean directory = fs.isDirectory(path);
// fs.close();
// return exists & directory;
// }
//
// /**
// * 判断文件是否存在
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param remoteFile 远程地址
// * @return 布尔值
// * @throws IOException IO异常
// */
// public static boolean isExist(Configuration conf, String uri, String remoteFile) throws IOException {
// // 获取文件系统
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// Path path = new Path(remoteFile);
// boolean exists = fs.exists(path);
// fs.close();
// return exists;
// }
//
// /**
// * 判断文件是否存在
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param remoteFile 远程地址
// * @param user 用户名
// * @return 布尔值
// * @throws IOException IO异常
// * @throws InterruptedException 中断异常
// */
// public static boolean isExist(Configuration conf, String uri, String user, String remoteFile) throws IOException,
// InterruptedException {
// // 获取文件系统
// FileSystem fs = FileSystem.get(URI.create(uri), conf, user);
// Path path = new Path(remoteFile);
// boolean exists = fs.exists(path);
// fs.close();
// return exists;
// }
//
// /**
// * 下载 hdfs上的文件
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param remote 远程地址
// * @param local 本地地址
// * @throws IOException IO异常
// */
// public static void download(Configuration conf, String uri, String remote, String local) throws IOException {
// Path path = new Path(remote);
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// fs.copyToLocalFile(path, new Path(local));
// log.info("download: from" + remote + " to " + local);
// fs.close();
// }
//
// /**
// * 删除文件或者文件夹
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param filePath 文件地址
// * @throws IOException IO异常
// */
// public static void delete(Configuration conf, String uri, String filePath) throws IOException {
// Path path = new Path(filePath);
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// fs.deleteOnExit(path);
// log.info("Delete: " + filePath);
// fs.close();
// }
//
// /**
// * 删除文件或者文件夹
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param filePath 文件路径
// * @param user 用户名
// * @throws IOException IO异常
// * @throws InterruptedException 中断异常
// */
// public static void delete(Configuration conf, String uri, String user, String filePath) throws IOException,
// InterruptedException {
// Path path = new Path(filePath);
// FileSystem fs = FileSystem.get(URI.create(uri), conf, user);
// fs.deleteOnExit(path);
// log.info("Delete: " + filePath);
// fs.close();
// }
//
// /**
// * 查看目录下面的文件
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param folder 文件夹
// * @throws IOException IO异常
// */
// public static void ls(Configuration conf, String uri, String folder) throws IOException {
// Path path = new Path(folder);
// FileSystem fs = FileSystem.get(URI.create(uri), conf);
// FileStatus[] list = fs.listStatus(path);
// log.info("ls: " + folder);
// log.info("==========================================================");
// for (FileStatus f : list) {
// log.info("name: " + f.getPath() + " ,folder: " + f.isDirectory() + " ,size: " + f.getPath());
// }
// log.info("==========================================================");
// fs.close();
// }
//
// /**
// * 查看目录下面的文件
// *
// * @param conf 配置信息
// * @param uri 统一资源标识符
// * @param folder 文件夹
// * @param user 用户名
// * @throws IOException IO异常
// * @throws InterruptedException 中断异常
// */
// public static void ls(Configuration conf, String uri, String user, String folder) throws IOException,
// InterruptedException {
// Path path = new Path(folder);
// FileSystem fs = FileSystem.get(URI.create(uri), conf, user);
// FileStatus[] list = fs.listStatus(path);
// log.info("ls: " + folder);
// log.info("==========================================================");
// for (FileStatus f : list) {
// log.info("name: " + f.getPath() + " ,folder: " + f.isDirectory() + " ,size: " + f.getLen());
// }
// log.info("==========================================================");
// fs.close();
// }
// }
