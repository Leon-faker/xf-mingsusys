package com.xf_mingsu.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.Date;

/**
 * Base64与图片之间的转换
 *
 */

public class Base64ImageConverter {
	
	private static Logger logger = LoggerFactory.getLogger(Base64ImageConverter.class);

	/**
	 * 将图片转换为Base64
	 * @param imgFile 图片的绝对路径（如：d:\\222\\999.jpg）
	 * @return 返回Base64编码后的图片字符串
	 */
	public static String imageToBase64(String imgFile) {
		String base64Image = null;
		try {
			//读取图片字节数组
			InputStream in = new FileInputStream(imgFile);
			byte[] data = new byte[in.available()];//将图片文件转化为字节数组字符串
			in.read(data);
			in.close();

			BASE64Encoder encoder = new BASE64Encoder();
			base64Image = encoder.encode(data);//返回Base64编码过的字节数组字符串
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return base64Image;
	}
	/**
	 * 将图片转换为Base64
	 * @param in 图片的绝对路径（如：d:\\222\\999.jpg）
	 * @return 返回Base64编码后的图片字符串
	 */
	public static String imageInputStreamToBase64(InputStream in) {
		String base64Image = null;
		try {
			//读取图片字节数组
			byte[] data = new byte[in.available()];//将图片文件转化为字节数组字符串
			in.read(data);
			in.close();

			BASE64Encoder encoder = new BASE64Encoder();
			base64Image = encoder.encode(data);//返回Base64编码过的字节数组字符串


		} catch (IOException e) {
			e.printStackTrace();
		}
		return base64Image;
	}


	/**
	 * base64字符串转化成图片
	 * @param imgData 图片数据
	 * @param imgFilePath 图片存放的路径（格式如下 d:\\111\\222\\333）
	 * @param imgSuffix 图片后缀名（取值：jpg,jpeg,png,gif,bmp;不传默认值为png格式）
	 * @return 图片的名称
	 */
	public static String base64ToImage(String imgData, String imgFilePath, String imgSuffix) {
		String imageName = null; // 图片名称
		String[] imgSuffixs = new String[] { "jpg", "jpeg", "png", "gif", "bmp"};
		try {
			//对字节数组字符串进行Base64解码并生成图片
			if (imgData == null) {
				return imageName;
			}

			// 字符后缀名
			if (imgSuffix == null) {
				imgSuffix = "png";
			} else {
				imgSuffix = imgSuffix.toLowerCase();
				boolean isEqual = false;
				for (int i = 0; i < imgSuffixs.length; i++) {
					if (imgSuffixs[i].equals(imgSuffix) == true) {
						isEqual = true;
						break;
					}
				}

				if (isEqual == false) {
					imgSuffix = "png";
				}
			}

			//Base64解码
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(imgData);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {//调整异常数据
					b[i] += 256;
				}
			}

			//生成图片
			File file = new File(imgFilePath);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			} else {
				imageName = new Date().getTime() + "." + imgSuffix;
				imgFilePath = imgFilePath + "//" + imageName;
			}
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return imageName;
		} catch (Exception e) {
			e.printStackTrace();
			return imageName;
		}
	}

	/**
	 * Base64字符串转换成图片文件存储本地
	 * @param imagePath
	 * @param filePath
	 */
	@SuppressWarnings("restriction")
	public static boolean saveUrlAs(String sessionId,String imageStr,String filePath, String fileName){
		try{
			File file = new File(filePath);
			if(!file.exists() && !file.isDirectory()){
				logger.info(sessionId + ":要保存的图片目录不存在，开始创建图片目录：" + filePath);
				file.mkdirs();
			}
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] bytes = base64Decoder.decodeBuffer(imageStr);
			FileOutputStream fos = new FileOutputStream(filePath + "/" + fileName);
			fos.write(bytes);
			fos.close();
			logger.info(sessionId + ":图片缓存成功！");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		// String de = imageToBase64("d:\\111.jpg");
		String de = "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";
		String imageName = base64ToImage(de, "d:\\222", "jpg");
		System.out.println(imageName);
	}
}
