package ir.ac.kntu.backend;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cookie-props")
public class Properties {

    private Security security = new Security();

    @Getter
    @Setter
    public static class Security {
        private ETokenMedia tokenMedia = ETokenMedia.header;
        private String tokenKey = "Authorization";
    }

    public enum ETokenMedia {
        header, cookie
    }
}
