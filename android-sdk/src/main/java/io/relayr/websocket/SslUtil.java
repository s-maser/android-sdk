package io.relayr.websocket;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.relayr.model.MqttChannel;

public class SslUtil {

    private static final String PROPERTIES_FILE_NAME = "ssl.properties";
    private static final String CERTIFICATE_FILE_NAME = "relayr.crt";

    private static SslUtil sSslUtil;
    private static Certificate sCertificate;
    private static boolean sCertRefreshed = false;

    private final Context mContext;
    private final Properties properties = new Properties();

    static SslUtil instance() {
        return sSslUtil;
    }

    static void init(Context context) {
        sSslUtil = new SslUtil(context);
    }

    private SslUtil(Context context) {
        mContext = context;
        sCertificate = loadCertificate();

        try {
            properties.load(context.getAssets().open(PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getBroker() {
        return properties.getProperty("connection") + "://" +
                properties.getProperty("host") + ":" +
                properties.getProperty("port");
    }

    MqttConnectOptions getConnectOptions(MqttChannel.MqttCredentials credentials) {
        MqttConnectOptions connOpts = new MqttConnectOptions();

        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(3);
        connOpts.setKeepAliveInterval(60);

        connOpts.setSocketFactory(createSocketFactory());

        connOpts.setUserName(credentials.getUser());
        connOpts.setPassword(credentials.getPassword().toCharArray());

        connOpts.setServerURIs(new String[]{});
        return connOpts;
    }

    SSLSocketFactory createSocketFactory() {
        TrustManagerFactory tmf = null;
        try {
            tmf = createTrustManagerFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        
        if (tmf == null) return null;

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(properties.getProperty("ssl_type"));
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        if (sslContext == null) return null;

        return sslContext.getSocketFactory();
    }

    TrustManagerFactory createTrustManagerFactory() throws CertificateException {
        TrustManagerFactory tmf = null;

        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            if (sCertificate == null)
                sCertificate = downloadCertificate(properties.getProperty("cert_url"));
            tmf.init(createKeyStore(sCertificate));
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

        return tmf;
    }

    void refreshCertificate() {
        if (sCertificate == null || !sCertRefreshed)
            try {
                downloadCertificate(properties.getProperty("cert_url"));
            } catch (CertificateException e) {
                e.printStackTrace();
            }
    }

    Certificate downloadCertificate(String url) throws CertificateException {
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        if (cf == null) throw new CertificateException("CertificateFactory creation failed!");

        try {
            InputStream caInput = new URL(url).openStream();
            sCertificate = cf.generateCertificate(caInput);
            sCertRefreshed = true;
            persistCertificate(sCertificate);
        } catch (CertificateException | IOException e) {
            sCertRefreshed = false;
            e.printStackTrace();
        }

        return sCertificate;
    }

    Certificate loadCertificate() {
        FileInputStream fis;
        try {
            fis = mContext.openFileInput(CERTIFICATE_FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            return (Certificate) is.readObject();
        } catch (FileNotFoundException e) {
            Log.w("SslUtil", "Certificate not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("SslUtil", "Certificate can't be read: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("SslUtil", "Can not cast certificate object: " + e.getMessage());
        }

        return null;
    }

    void persistCertificate(Certificate certificate) {
        FileOutputStream fos;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ObjectOutputStream(out).writeObject(certificate);

            fos = mContext.openFileOutput(CERTIFICATE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(out.toByteArray());
            fos.close();
        } catch (FileNotFoundException e) {
            Log.w("SslUtil", "Certificate file not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("SslUtil", "Certificate can't be written: " + e.getMessage());
        }
    }

    KeyStore createKeyStore(Certificate certificate) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return keyStore;
    }

    Properties getProperties() {
        return properties;
    }
}
