
package de.iolite.drivers.example;

import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.Mockito;

import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.exception.IllegalValueException;

/**
 * Tests the {@link NumericalValueForkingCallbackDecorator}.
 *
 * @author Niels Johannes
 * @since 17.11
 */
public class NumericalValueForkingCallbackDecoratorTest {

	/**
	 * Expect double values being passed to consumer.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newDoubleValueDoubleCallsArePassedToConsumer()
			throws IllegalValueException {
		final Consumer<Double> doubleValueConsumer = Mockito.mock(Consumer.class);

		final NumericalValueForkingCallbackDecorator testee =
				new NumericalValueForkingCallbackDecorator(Mockito.mock(DataPointValueCallback.class), doubleValueConsumer);

		// execute code
		testee.newDoubleValue(5.4);

		// test execution
		Mockito.verify(doubleValueConsumer).accept(5.4);
	}

	/**
	 * Expect double values being passed to consumer.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newDoubleValueDoubleCallsArePassedToDelegate()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final NumericalValueForkingCallbackDecorator testee = new NumericalValueForkingCallbackDecorator(delegateMock, Mockito.mock(Consumer.class));

		// execute code
		testee.newDoubleValue(5.4);

		// test execution
		Mockito.verify(delegateMock).newDoubleValue(5.4);
	}

	/**
	 * Expect {@code DataPointValueCallback#newDoubleValue(double, long)} are passed to delegated {@link DataPointValueCallback}.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newDoubleValueDoubleLongCallsArePassedToDelegate()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final NumericalValueForkingCallbackDecorator testee = new NumericalValueForkingCallbackDecorator(delegateMock, Mockito.mock(Consumer.class));

		// execute code
		final long timestamp = System.currentTimeMillis();
		testee.newDoubleValue(7.3, timestamp);

		// test execution
		Mockito.verify(delegateMock).newDoubleValue(7.3, timestamp);
	}

	/**
	 * Expect integer values being passed to consumer.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newIntValueIntCallsArePassedToConsumer()
			throws IllegalValueException {
		final Consumer<Double> valueConsumer = Mockito.mock(Consumer.class);

		final NumericalValueForkingCallbackDecorator testee =
				new NumericalValueForkingCallbackDecorator(Mockito.mock(DataPointValueCallback.class), valueConsumer);

		// execute code
		testee.newIntValue(50);

		// test execution
		Mockito.verify(valueConsumer).accept(50.);
	}

	/**
	 * Expect integer values being passed to delegate.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newIntValueIntCallsArePassedToDelegate()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final NumericalValueForkingCallbackDecorator testee = new NumericalValueForkingCallbackDecorator(delegateMock, Mockito.mock(Consumer.class));

		// execute code
		testee.newIntValue(50);

		// test execution
		Mockito.verify(delegateMock).newIntValue(50);
	}

	/**
	 * Expect {@code DataPointValueCallback#newIntValue(int, long)} are passed to delegated {@link DataPointValueCallback}.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newIntValueIntLongCallsArePassedToDelegate()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final NumericalValueForkingCallbackDecorator testee = new NumericalValueForkingCallbackDecorator(delegateMock, Mockito.mock(Consumer.class));

		// execute code
		final long timestamp = System.currentTimeMillis();
		testee.newIntValue(60, timestamp);

		// test execution
		Mockito.verify(delegateMock).newIntValue(60, timestamp);
	}
}
