import org.junit.Rule
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

class ExecutorTest {

    @Rule
    var mockitoRule = MockitoJUnit.rule()

    @InjectMocks
    private val underTest: Executor? = null

    @Test
    internal fun execute() {
    }
}