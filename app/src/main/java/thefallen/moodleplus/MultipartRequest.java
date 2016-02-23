package thefallen.moodleplus;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/*
    Submits a multipart file to the server
 */

public class MultipartRequest extends Request<String> {

    private HttpEntity mHttpEntity;

    private String fileName;
    private Response.Listener mListener;

    public MultipartRequest(String url, String filePath, String fileName,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        this.fileName = fileName;
        mListener = listener;
        mHttpEntity = buildMultipartEntity(filePath);
    }

    public MultipartRequest(String url, File file, String fileName,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        this.fileName = fileName;
        mListener = listener;
        mHttpEntity = buildMultipartEntity(file);
    }

    private HttpEntity buildMultipartEntity(String filePath) {
        File file = new File(filePath);
        return buildMultipartEntity(file);
    }

    private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        String fileName = file.getName();
        FileBody fileBody = new FileBody(file);
        builder.addPart("sub_file", fileBody);
        builder.addPart("_formname",new StringBody("sub_form", ContentType.APPLICATION_JSON));
        builder.addPart("sub_name",new StringBody(fileName, ContentType.APPLICATION_JSON));
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e(e+"IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success("Uploaded"+response.statusCode, getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}