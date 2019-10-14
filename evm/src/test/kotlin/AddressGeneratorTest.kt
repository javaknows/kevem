import com.gammadex.kevin.DefaultAddressGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AddressGeneratorTest {

    private val underTest = DefaultAddressGenerator()

    @Test
    fun nextAddressIsGenerated() {
        val generated = underTest.nextAddress()

        val addressString = generated.toString()
        assertThat(addressString.length).isEqualTo(42)
        assertThat(addressString).startsWith("0x")
    }

    @Test
    fun twoSequentialAddressesAreDifferent() {
        val generated = underTest.nextAddress()
        val generated2 = underTest.nextAddress()

        assertThat(generated).isNotEqualTo(generated2)
    }
}