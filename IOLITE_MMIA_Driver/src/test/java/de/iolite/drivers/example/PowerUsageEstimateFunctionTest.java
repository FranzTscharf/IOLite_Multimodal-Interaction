
package de.iolite.drivers.example;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Tests the {@link PowerUsageEstimateFunction}.
 *
 * @author Niels Johannes
 * @since 17.11
 */
public class PowerUsageEstimateFunctionTest {

	private static final double DECLARED_MAXIMUM_POWER_VALUE = 5.4;
	private static final double DIMMING_LEVEL = 50;
	private static final double RESULT_WHEN_DIMMED = DECLARED_MAXIMUM_POWER_VALUE * DIMMING_LEVEL / 100;

	/**
	 * Test for correct PowerUsageEstimate value being calculated, when OnOffProperty is set to true and DimmingLevel is set.
	 */
	@Test
	public final void powerUsageEstimateReturnsReducedPowerUsageWhenDimmingLevelSetAndOn() {
		// Mocked consumer for PowerUsageEstimate
		final Consumer<Double> consumer = Mockito.mock(Consumer.class);

		// create testee
		final PowerUsageEstimateFunction testee = new PowerUsageEstimateFunction(() -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE), consumer);

		// simulate on property with true value
		testee.newOnValue(true);

		Mockito.reset(consumer);
		// simulate reporting of DIMMING_LEVEL
		testee.newDimmingLevel(DIMMING_LEVEL);

		// new PowerUsageEstimate value, depending of on property and DimmingLevel should be reported
		Mockito.verify(consumer, Mockito.only()).accept(RESULT_WHEN_DIMMED);
	}

	/**
	 * Test for PowerUsageEstimate value being 0, when OnOffProperty is set to false.
	 */
	@Test
	public final void powerUsageEstimateReturnsZeroWhenDimmingLevelSetAndOff() {
		// Mocked consumer for PowerUsageEstimate
		final Consumer<Double> consumer = Mockito.mock(Consumer.class);

		// create testee
		final PowerUsageEstimateFunction testee = new PowerUsageEstimateFunction(() -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE), consumer);

		// simulate on property with true value
		testee.newOnValue(false);

		Mockito.reset(consumer);
		// simulate reporting of dimming level DIMMING_LEVEL
		testee.newDimmingLevel(DIMMING_LEVEL);

		// reported PowerUsageEstimate value should be 0. Expect at most 2 invocations (1 for each property).
		Mockito.verify(consumer, Mockito.only()).accept(0.);
	}

	/**
	 * Test for PowerUsageEstimate value being DECLARED_MAXIMUM_POWER_VALUE, when OnOffProperty is set to true and no DIMMING_LEVEL defined.
	 */
	@Test
	public final void powerUsageEstimateReturnsMaximumPowerWhenNoDimmingLevelSetAndOn() {
		// Mocked consumer for PowerUsageEstimate
		final Consumer<Double> consumer = Mockito.mock(Consumer.class);

		// create testee
		final PowerUsageEstimateFunction testee = new PowerUsageEstimateFunction(() -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE), consumer);

		// simulate on property with true value
		testee.newOnValue(true);

		// reported PowerUsageEstimate value should be DECLARED_MAXIMUM_POWER_VALUE. Expect exactly 1 invocation.
		Mockito.verify(consumer, Mockito.only()).accept(DECLARED_MAXIMUM_POWER_VALUE);
	}

	/**
	 * Test for PowerUsageEstimate value being 0, when OnOffProperty is set to false and no DIMMING_LEVEL defined.
	 */
	@Test
	public final void powerUsageEstimateReturnsZeroWhenNoDimmingLevelSetAndOff() {
		// Mocked consumer for PowerUsageEstimate
		final Consumer<Double> consumer = Mockito.mock(Consumer.class);

		// create testee
		final PowerUsageEstimateFunction testee = new PowerUsageEstimateFunction(() -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE), consumer);

		// simulate on property with true value
		testee.newOnValue(false);

		// reported PowerUsageEstimate value should be 0. Expect exactly 1 invocation.
		Mockito.verify(consumer, Mockito.only()).accept(0.);
	}

	/**
	 * Assert that the function does not generated a power usage estimate when the 'on' value is unknown.
	 */
	@Test
	public final void powerUsageEstimateIsNotCalculatedWithoutOnValue() {
		// Mocked consumer for PowerUsageEstimate
		final Consumer<Double> consumer = Mockito.mock(Consumer.class);

		// create testee
		final PowerUsageEstimateFunction testee = new PowerUsageEstimateFunction(() -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE), consumer);

		// simulate on property with true value
		testee.newDimmingLevel(45.);

		// reported PowerUsageEstimate value should be 0. Expect exactly 1 invocation.
		Mockito.verify(consumer, Mockito.never()).accept(Matchers.anyDouble());
	}
}
