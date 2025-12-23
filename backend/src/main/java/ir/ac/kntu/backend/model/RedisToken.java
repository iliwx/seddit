package ir.ac.kntu.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash("RedisTokenEntity")
public class RedisToken implements Serializable {
    @Id
    private String id;
    private String tokenStr;
}
