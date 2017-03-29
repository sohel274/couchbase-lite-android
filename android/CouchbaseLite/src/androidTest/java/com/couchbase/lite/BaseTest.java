package com.couchbase.lite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.couchbase.lite.internal.support.JsonUtils;
import com.couchbase.lite.utils.FileUtils;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

public class BaseTest {
    protected final static String kDatabaseName = "testdb";

    protected Context context;
    protected File dir;
    protected Database db = null;


    @Before
    public void setUp() {
        Log.e("BaseTest", "setUp");
        context = InstrumentationRegistry.getContext();
        dir = new File(context.getFilesDir(), "CouchbaseLite");

        FileUtils.cleanDirectory(dir);

        openDB();
    }

    @After
    public void tearDown() {
        Log.e("BaseTest", "tearDown");
        if (db != null) {
            db.close();
            db = null;
        }
    }

    protected void openDB() {
        assertNull(db);

        DatabaseOptions options = DatabaseOptions.getDefaultOptions();
        options.setDirectory(dir);
        db = new Database(kDatabaseName, options);
        assertNotNull(db);
    }

    protected void closeDB() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    protected void reopenDB() {
        closeDB();
        openDB();
    }

    protected InputStream getAsset(String name) {
        return this.getClass().getResourceAsStream("/assets/" + name);
    }

    protected void loadJSONResource(String name) throws Exception {
        InputStream is = getAsset(name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        int n = 0;
        while((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            n += 1;
            JSONObject json = new JSONObject(line);
            Map<String, Object> props = JsonUtils.fromJson(json);
            String docId = String.format(Locale.ENGLISH, "doc-%03d", n);
            Document doc = db.getDocument(docId);
            doc.setProperties(props);
            doc.save();
        }
    }
}
