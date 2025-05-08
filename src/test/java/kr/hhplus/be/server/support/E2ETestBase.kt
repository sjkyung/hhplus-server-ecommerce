package kr.hhplus.be.server.support


import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Import(RedissonTestConfig::class)
abstract class E2ETestBase {

    @LocalServerPort
    var port: Int = 8080

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate


    @BeforeEach
    fun cleanDatabase() {

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0")

        val tableNames = jdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = (SELECT DATABASE())",
            String::class.java
        )

        tableNames.forEach { tableName ->
            jdbcTemplate.execute("TRUNCATE TABLE $tableName")
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1")

    }


    companion object {
        @Container
        val mysql = MySQLContainer(DockerImageName.parse("mysql:8.0")).apply {
            withDatabaseName("hhplus")
            withUsername("application")
            withPassword("application")
        }

        @Container
        val redis = GenericContainer(DockerImageName.parse("redis:7.4.2")).apply{
            withExposedPorts(6379)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mysql::getJdbcUrl)
            registry.add("spring.datasource.username", mysql::getUsername)
            registry.add("spring.datasource.password", mysql::getPassword)

            registry.add("spring.redis.host") {redis.host}
            registry.add("spring.redis.port") {redis.getMappedPort(6379).toString()}
        }
    }
}