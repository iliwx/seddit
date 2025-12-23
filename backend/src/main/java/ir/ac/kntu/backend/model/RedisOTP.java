package ir.ac.kntu.backend.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@RedisHash(value = "OTP", timeToLive = 300)
public class RedisOTP implements Serializable {

    @Id
    private final String id;
    private final String otp;
    private Integer attempts;
}

