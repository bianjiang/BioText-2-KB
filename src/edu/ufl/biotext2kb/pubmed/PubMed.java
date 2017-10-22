package edu.ufl.biotext2kb.pubmed;

import com.google.inject.AbstractModule;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;

public class PubMed  extends AbstractModule {

    @Override
    protected void configure(){

    }

    private static final Logger LOG = LoggerFactory.getLogger(PubMed.class);

    private final String db = "pubmed";
    private final String retMode = "xml";
    private final String retType = "abstract";

    private final OkHttpClient httpClient = new OkHttpClient();

    private HttpUrl.Builder newBaseHttpUrlBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("eutils.ncbi.nlm.nih.gov").addPathSegments("entrez/eutils/");
    }

    public String fetchAbstractTextByPMID (String pmid) throws IOException {

        HttpUrl url = newBaseHttpUrlBuilder()
                .addPathSegment("efetch.fcgi")
                .addQueryParameter("db", db)
                .addQueryParameter("retmode", retMode)
                .addQueryParameter("rettype", retType)
                .addQueryParameter("id", pmid)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//            Headers responseHeaders = response.headers();
//            for (int i = 0; i < responseHeaders.size(); i++) {
//                LOG.debug(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//            }

            return getAbstractText(response.body().string());
        }

    }

    private String getAbstractText (String xmlString) {

        try {
            DocumentBuilderFactory domFactory =
                    DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            XPath xpath = XPathFactory.newInstance().newXPath();
            // XPath Query for showing all nodes value
            XPathExpression expr = xpath.compile("//Article/Abstract/AbstractText/text()");

            Node result = (Node)expr.evaluate(doc, XPathConstants.NODE);

            return result.getNodeValue();

        }catch(Exception ex){
            return null;
        }
    }

}
