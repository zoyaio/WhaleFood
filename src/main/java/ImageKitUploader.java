import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ImageKitUploader {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/config.properties"));

            String urlEndpoint = props.getProperty("UrlEndpoint");
            String privateKey = props.getProperty("PrivateKey");
            String publicKey = props.getProperty("PublicKey");

            ImageKit imageKit = ImageKit.getInstance();
            Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
            imageKit.setConfig(config);

            System.out.println("✅ ImageKit SDK initialized successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}