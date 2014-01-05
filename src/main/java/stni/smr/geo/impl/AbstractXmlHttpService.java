package stni.smr.geo.impl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 *
 */
public class AbstractXmlHttpService {
    private final HttpClient client;
    private final XPathFactory factory = XPathFactory.newInstance();

    public AbstractXmlHttpService() {
        client = HttpClientBuilder.create().build();
    }

    protected InputSource sourceFromHttpGet(URI url) throws IOException {
        return new InputSource(streamFromHttpGet(url));
    }

    protected InputStream streamFromHttpGet(URI url) throws IOException {
        final HttpGet get = new HttpGet(url);

        final HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException(response.getStatusLine().getReasonPhrase());
        }

        return response.getEntity().getContent();
    }

    protected XPath xPath() {
        return factory.newXPath();
    }
}
