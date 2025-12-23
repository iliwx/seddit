package ir.ac.kntu.backend.repository;

import ir.ac.kntu.backend.model.RedisOTP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisOTPRepository extends CrudRepository<RedisOTP, String> {
}
