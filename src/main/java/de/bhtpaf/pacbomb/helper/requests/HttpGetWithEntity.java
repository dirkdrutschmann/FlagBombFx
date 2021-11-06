package de.bhtpaf.pacbomb.helper.requests;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase
{
    public final static String METHOD_NAME = "GET";

    public HttpGetWithEntity(String url)
    {
        super();
        setURI(URI.create(url));
    }

    @Override
    public String getMethod()
    {
        return METHOD_NAME;
    }
}
