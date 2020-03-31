import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kevem.evm.EIP

import org.kevem.evm.HardFork

class HardForkTest {

    @Test
    internal fun `check TangerineWhistle has EIPS from previous hard forks`() {
        val homestead = HardFork.Homestead.eips()
        val tangerineWhistle = HardFork.TangerineWhistle.eips()

        assertThat(tangerineWhistle).containsAll(homestead)
    }

    @Test
    internal fun `check SpuriousDragon has EIPS from previous hard forks`() {
        val homestead = HardFork.Homestead.eips()
        val spuriousDragon = HardFork.SpuriousDragon.eips()
        val tangerineWhistle = HardFork.TangerineWhistle.eips()

        assertThat(spuriousDragon).containsAll(homestead)
        assertThat(spuriousDragon).containsAll(tangerineWhistle)
    }

    @Test
    internal fun `check Byzantium has EIPS from previous hard forks`() {
        val homestead = HardFork.Homestead.eips()
        val spuriousDragon = HardFork.SpuriousDragon.eips()
        val tangerineWhistle = HardFork.TangerineWhistle.eips()
        val byzantium = HardFork.Byzantium.eips()

        assertThat(byzantium).containsAll(homestead)
        assertThat(byzantium).containsAll(spuriousDragon)
        assertThat(byzantium).containsAll(tangerineWhistle)
    }

    @Test
    internal fun `check Homestead hard fork lists all required EIPs`() {
        val allEips = HardFork.Homestead.eips()

        assertThat(allEips).isEqualTo(listOf(EIP.EIP606, EIP.EIP2, EIP.EIP7, EIP.EIP8).sorted())
    }

    @Test
    internal fun `check Spurious Dragon hard fork lists all required EIPs`() {
        val allEips = HardFork.SpuriousDragon.eips()

        assertThat(allEips).isEqualTo(
            listOf(
                EIP.EIP607,
                EIP.EIP155,
                EIP.EIP160,
                EIP.EIP161,
                EIP.EIP170,
                EIP.EIP608,
                EIP.EIP150,
                EIP.EIP779,
                EIP.EIP606,
                EIP.EIP2,
                EIP.EIP7,
                EIP.EIP8
            ).sorted()
        )
    }

    @Test
    internal fun `check Byzantium hard fork lists all required EIPs`() {
        val allEips = HardFork.Byzantium.eips()

        assertThat(allEips).isEqualTo(
            listOf(
                EIP.EIP609,
                EIP.EIP100,
                EIP.EIP140,
                EIP.EIP196,
                EIP.EIP197,
                EIP.EIP198,
                EIP.EIP211,
                EIP.EIP214,
                EIP.EIP607,
                EIP.EIP649,
                EIP.EIP658,
                EIP.EIP155,
                EIP.EIP160,
                EIP.EIP161,
                EIP.EIP170,
                EIP.EIP608,
                EIP.EIP140,
                EIP.EIP150,
                EIP.EIP779,
                EIP.EIP606,
                EIP.EIP2,
                EIP.EIP7,
                EIP.EIP8
            ).sorted()
        )
    }

    @Test
    internal fun `check Byzantium hard fork can be parsed in lowercase`() {
        val parsed = HardFork.fromStringOrNull("byzantium")

        assertThat(parsed).isEqualTo(HardFork.Byzantium)
    }

    @Test
    internal fun `check Istanbul hard fork can be parsed in mixed case`() {
        val parsed = HardFork.fromStringOrNull("istanbul")

        assertThat(parsed).isEqualTo(HardFork.Istanbul)
    }
}