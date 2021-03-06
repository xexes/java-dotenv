package tests

import io.github.cdimascio.dotenv.DotEnvException
import io.github.cdimascio.dotenv.Dotenv
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test as test

class DotEnvTest() {
    private val envVars = mapOf(
            "MY_TEST_EV1" to "my test ev 1",
            "MY_TEST_EV2" to "my test ev 2"
    )

    @test(expected = DotEnvException::class)

    fun dotenvMalformed() {
        Dotenv.configure()
                .directory("./src/test/resources")
                .load()
    }

    @test
    fun dotenvIgnoreMalformed() {
        val dotEnv = Dotenv.configure().apply {
            directory("./src/test/resources")
            ignoreIfMalformed()
        }.load()

        envVars.forEach {
            val expected = it.value
            val actual = dotEnv[it.key]
            assertEquals(expected, actual)
        }

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotEnv.get("HOME")
        assertEquals(expectedHome, actualHome)
    }

    @test
    fun resourceRelative() {
        val dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .load()
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv["HOME"]
        assertEquals(expectedHome, actualHome)
    }

    @test
    fun resourceCurrent() {
        val dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .load()
        assertEquals("my test ev 1", dotenv["MY_TEST_EV1"])

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv["HOME"]
        assertEquals(expectedHome, actualHome)
    }

    @test(expected = DotEnvException::class)
    fun dotenvMissing() {
        Dotenv.configure()
                .directory("/missing/.env")
                .load()
    }

    @test
    fun dotenvIgnoreMissing() {
        val dotenv = Dotenv.configure()
                .directory("/missing/.env")
                .ignoreIfMissing()
                .load()

        val expectedHome = System.getProperty("user.home")
        val actualHome = dotenv.get("HOME")
        assertEquals(expectedHome, actualHome)

        assertNull(dotenv["MY_TEST_EV1"])
    }
}