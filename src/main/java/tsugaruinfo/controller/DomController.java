package tsugaruinfo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DomController {

	@Autowired
	ResourceLoader resourceLoader;
	
	@RequestMapping(value="/readable")
	public String readable() {
		
		//ファイル取得
		Resource resource = resourceLoader.getResource("classpath:sample.xml");
		
		Document doc = null;
		Element root = null;
		
		//Xml取得
		try {
			doc = new SAXBuilder().build(resource.getFile());
			root = doc.getRootElement();
			
			} catch (JDOMException e) {
				System.out.println(e);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println(e);
			}
	
		//Xmlから子要素を取得（孫要素は取得していない）
		List<Element> list = root.getChildren();
		
		List<String> result = new ArrayList<String>();
		
		//Xmlから孫要素を取得するループ
		for(Element e : list) {
			if(e.getName().compareTo("employee") == 0) {
				result.add(e.getChild("name").getValue());
			}
		}
		
		//取得した要素をコンソールに吐き出すループ
		for(String name : result) {
			System.out.println(name);
		}
	
        return null;
	}
	
	@RequestMapping(value="/download")
	public String donwload(HttpServletResponse response) {
		
		Resource resource = resourceLoader.getResource("classpath:sample.xml");
		
		byte[] fileContent = null;
		
		fileContent = StreamToByte(resource);
        
//		//ファイル書き込み
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + "sample.xml");
        response.setContentLength(fileContent.length);
		
        OutputSreamWrite(response, fileContent);
        
        return null;
	}
	
	/**
	 * InputStream から　バイト文字列に変換
	 * @param filepath
	 * @return
	 */
	private byte[] StreamToByte(Resource resource) {
		
	int nRead;
    InputStream is = null;
    byte[] fileContent = new byte[16384];
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	
    //ファイルをバイト形式に変換
    try {
        is = new FileInputStream(resource.getFile().toString());
        
        while ((nRead = is.read(fileContent, 0, fileContent.length)) != -1) {
        	  buffer.write(fileContent, 0, nRead);
        	}

        	buffer.flush();
        	
        	return buffer.toByteArray();
    } catch (FileNotFoundException e) {
    	e.getStackTrace();
    } catch (IOException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}
    return null;
	}
	
	/**
	 * ダウンロードファイル書き込み
	 * @param response
	 * @param fileContent
	 */
	public void OutputSreamWrite(HttpServletResponse response, byte[] fileContent) {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(fileContent);
            os.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
	}
}
