import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

public class PDF { /*

public Map<String, Object> processRecord(String url) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    Map<String, Object> map = new HashMap<String, Object>();
    try {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream input = null;
                if (entity != null) {
                    try{
                        input = entity.getContent();
                        BodyContentHandler handler = new BodyContentHandler();
                        Metadata metadata = new Metadata();
                        AutoDetectParser parser = new AutoDetectParser();
                        ParseContext parseContext = new ParseContext();
                        parser.parse(input, handler, metadata, parseContext);
                        map.put("text", handler.toString().replaceAll("\n|\r|\t", " "));
                        map.put("title", metadata.get(TikaCoreProperties.TITLE));
                        map.put("pageCount", metadata.get("xmpTPg:NPages"));
                        map.put("status_code", response.getStatusLine().getStatusCode() + "");
                } catch (Exception e) {                     
                    e.printStackTrace();
                }finally{
                    if(input != null){
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                }
            }catch (Exception exception) {
                exception.printStackTrace();
            }
    return map;
}

 public static void main(String arg[]){
    WebPagePdfExtractor webPagePdfExtractor = new WebPagePdfExtractor();
    Map<String, Object> extractedMap = webPagePdfExtractor.processRecord("http://math.about.com/library/q20.pdf");
    System.out.println(extractedMap.get("text"));
}
*/
}