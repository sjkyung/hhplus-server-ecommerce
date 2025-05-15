package kr.hhplus.be.server.support

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class RedissonTestConfig {
    companion object {
        val redis = GenericContainer(DockerImageName.parse("redis:7.4.2"))
            .apply {
                withExposedPorts(6379)
                start() // 명시적 start
            }
    }
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            useSingleServer()
                .setAddress("redis://${redis.host}:${redis.getMappedPort(6379)}")
        }
        return Redisson.create(config)
    }
}