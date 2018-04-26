package pers.husen.highdsa.common.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import pers.husen.highdsa.common.exception.StackTrace2Str;

/**
 * @Desc 读取服务器的html文件
 *
 * @Author 何明胜
 *
 * @Created at 2018年2月28日 下午12:11:39
 * 
 * @Version 1.0.1
 */
public class ReadHtmlFile {
	private static final Logger logger = LogManager.getLogger(ReadHtmlFile.class);

	/**
	 * 读取html文件
	 * 
	 * @param htmlQualifiedName
	 * @return
	 */
	public static String readHtmlByName(String htmlQualifiedName) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(htmlQualifiedName)));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			logger.error(StackTrace2Str.exceptionStackTrace2Str(e));
		} catch (IOException e) {
			logger.error(StackTrace2Str.exceptionStackTrace2Str(e));
		}
		return sb.toString();
	}

	/**
	 * 根据文件路径读取文件并输出至浏览器
	 * 
	 * @param htmlQualifiedName
	 * @param response
	 * @throws IOException
	 */
	public static void writeHtmlByName(String htmlQualifiedName, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");

		OutputStream outStream = response.getOutputStream();
		try {
			FileInputStream fip = new FileInputStream(htmlQualifiedName);
			// 建立缓冲区
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fip.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			fip.close();
			outStream.close();
			// 关闭输入流,释放系统资源
		} catch (Exception e) {
			logger.error(StackTrace2Str.exceptionStackTrace2Str(e));
		}
	}

	/**
	 * 修改html的关键字,并返回html内容
	 * 
	 * @param htmlQualifiedName
	 * @param keywords
	 */
	public static String modifyHtmlKeywords(String htmlQualifiedName, String keywords) {
		// 空格变为英文逗号
		String cmmma = ",";
		if (keywords != null && keywords != "" && keywords.indexOf(cmmma) == -1) {
			keywords = keywords.replaceAll("\\s", ",");
		}

		File file = new File(htmlQualifiedName);
		Document doc = null;
		try {
			doc = Jsoup.parse(file, "UTF-8");
		} catch (IOException e) {
			logger.error(StackTrace2Str.exceptionStackTrace2Str(e));
		}

		Element keywordsElement = doc.select("meta[name=keywords]").first();

		keywordsElement.attr("content", keywords);

		return doc.html();
	}
}